package com.ranker.countries.ui.rankings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranker.countries.domain.model.DailyRankings
import com.ranker.countries.domain.usecase.GetDailyRankingsUseCase
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
    data object Publishing : PublishState
    data class Published(val count: Int) : PublishState
    data object Failed : PublishState
}

/**
 * Today's rankings, loaded once and again on [onRetry]. With [publishSampleStats] (debug builds),
 * [onPublishSampleStats] seeds the source and loads again from it.
 */
class RankingsViewModel(
    private val getDailyRankings: GetDailyRankingsUseCase,
    private val publishSampleStats: (suspend () -> Int)? = null,
) : ViewModel() {

    private val _state = MutableStateFlow<RankingsUiState>(RankingsUiState.Loading)
    val state: StateFlow<RankingsUiState> = _state.asStateFlow()

    private val _publish = MutableStateFlow<PublishState>(PublishState.Idle)
    val publish: StateFlow<PublishState> = _publish.asStateFlow()

    val canPublishSampleStats: Boolean get() = publishSampleStats != null

    init {
        load()
    }

    fun onRetry() = load()

    fun onPublishSampleStats() {
        val publishSampleStats = publishSampleStats ?: return
        if (_publish.value == PublishState.Publishing) return
        _publish.value = PublishState.Publishing
        viewModelScope.launch {
            _publish.value = try {
                PublishState.Published(publishSampleStats()).also { load() }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                PublishState.Failed
            }
        }
    }

    fun onPublishResultShown() {
        if (_publish.value != PublishState.Publishing) _publish.value = PublishState.Idle
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
