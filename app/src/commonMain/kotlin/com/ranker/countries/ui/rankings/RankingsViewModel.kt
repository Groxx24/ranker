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
    data object Failed : RankingsUiState
}

/** Today's rankings, loaded once and again on [onRetry]. */
class RankingsViewModel(
    private val getDailyRankings: GetDailyRankingsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<RankingsUiState>(RankingsUiState.Loading)
    val state: StateFlow<RankingsUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun onRetry() = load()

    private fun load() {
        _state.value = RankingsUiState.Loading
        viewModelScope.launch {
            _state.value = try {
                RankingsUiState.Ready(getDailyRankings())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                RankingsUiState.Failed
            }
        }
    }
}
