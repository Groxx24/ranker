package com.ranker.countries.ui.rankings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranker.countries.domain.model.DailyRankings
import com.ranker.countries.domain.usecase.GetDailyRankingsUseCase
import com.ranker.countries.domain.usecase.PublishSeedStatsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface RankingsUiState {
    data object Loading : RankingsUiState
    data class Ready(val day: DailyRankings) : RankingsUiState
    /** The source has nothing to rank yet, e.g. a Firestore nobody has published to. */
    data object Empty : RankingsUiState
    data object Failed : RankingsUiState
}

/** The debug seeding button's progress, and the outcome to tell about once. */
sealed interface PublishState {
    data object Idle : PublishState
    /** Waiting for the publisher's password; [checking] while signing in, [wrong] after a miss. */
    data class Unlocking(val checking: Boolean = false, val wrong: Boolean = false) : PublishState
    data object Publishing : PublishState
    data class Published(val count: Int) : PublishState
    data object Failed : PublishState
}

/**
 * Today's rankings, loaded once and again on [onRetry]. With [publishSeedStats] (debug builds),
 * [onPublishSeedStats] asks for the publisher's password if needed, seeds the source and loads
 * again from it.
 */
class RankingsViewModel(
    private val getDailyRankings: GetDailyRankingsUseCase,
    private val publishSeedStats: PublishSeedStatsUseCase? = null,
) : ViewModel() {

    private val _state = MutableStateFlow<RankingsUiState>(RankingsUiState.Loading)
    val state: StateFlow<RankingsUiState> = _state.asStateFlow()

    private val _publish = MutableStateFlow<PublishState>(PublishState.Idle)
    val publish: StateFlow<PublishState> = _publish.asStateFlow()

    val canPublishSeedStats: Boolean get() = publishSeedStats != null

    init {
        load()
    }

    fun onRetry() = load()

    fun onPublishSeedStats() {
        val publishSeedStats = publishSeedStats ?: return
        if (_publish.value != PublishState.Idle) return
        if (publishSeedStats.isUnlocked) publish(publishSeedStats) else _publish.value = PublishState.Unlocking()
    }

    fun onUnlock(passphrase: String) {
        val publishSeedStats = publishSeedStats ?: return
        val current = _publish.value
        if (current !is PublishState.Unlocking || current.checking) return
        _publish.value = PublishState.Unlocking(checking = true)
        viewModelScope.launch {
            if (publishSeedStats.unlock(passphrase)) {
                publish(publishSeedStats)
            } else {
                _publish.value = PublishState.Unlocking(wrong = true)
            }
        }
    }

    fun onUnlockDismissed() {
        val current = _publish.value
        if (current is PublishState.Unlocking && !current.checking) _publish.value = PublishState.Idle
    }

    fun onPublishResultShown() {
        val current = _publish.value
        if (current is PublishState.Published || current is PublishState.Failed) _publish.value = PublishState.Idle
    }

    private fun publish(publishSeedStats: PublishSeedStatsUseCase) {
        _publish.value = PublishState.Publishing
        viewModelScope.launch {
            _publish.value = try {
                PublishState.Published(publishSeedStats()).also { load() }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                PublishState.Failed
            }
        }
    }

    private fun load() {
        _state.value = RankingsUiState.Loading
        viewModelScope.launch {
            _state.value = try {
                val day = getDailyRankings()
                if (day.rankings.isEmpty()) RankingsUiState.Empty else RankingsUiState.Ready(day)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                RankingsUiState.Failed
            }
        }
    }
}
