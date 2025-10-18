package com.fittracksa.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fittracksa.app.data.AppContainer
import com.fittracksa.app.data.local.MealEntity
import com.fittracksa.app.domain.FitTrackRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SharedDataViewModel(
    private val repository: FitTrackRepository
) : ViewModel() {

    val activities = repository.activities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val meals = repository.meals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val achievements = repository.achievements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _events = MutableSharedFlow<UiEvent>()
    val events: kotlinx.coroutines.flow.SharedFlow<UiEvent> = _events

    fun logActivity(type: String, duration: Int) {
        viewModelScope.launch {
            try {
                repository.logActivity(type, duration)
                _events.emit(UiEvent.Success("Activity stored offline and queued for sync"))
            } catch (e: Exception) {
                _events.emit(UiEvent.Error("Failed to log activity"))
            }
        }
    }

    fun logMeal(description: String, calories: Int) {
        viewModelScope.launch {
            try {
                repository.logMeal(description, calories)
                _events.emit(UiEvent.Success("Meal stored offline and queued for sync"))
            } catch (e: Exception) {
                _events.emit(UiEvent.Error("Failed to log meal"))
            }
        }
    }

    fun updateMeal(meal: MealEntity) {
        viewModelScope.launch {
            try {
                repository.updateMeal(meal)
                _events.emit(UiEvent.Success("Meal update saved offline"))
            } catch (e: Exception) {
                _events.emit(UiEvent.Error("Unable to update meal"))
            }
        }
    }

    fun deleteMeal(meal: MealEntity) {
        viewModelScope.launch {
            try {
                repository.deleteMeal(meal)
                _events.emit(UiEvent.Success("Meal removed locally"))
            } catch (e: Exception) {
                _events.emit(UiEvent.Error("Unable to delete meal"))
            }
        }
    }

    fun joinChallenge(id: Long) {
        viewModelScope.launch {
            try {
                repository.joinChallenge(id)
                _events.emit(UiEvent.Success("Challenge joined"))
            } catch (e: Exception) {
                _events.emit(UiEvent.Error("Unable to join challenge"))
            }
        }
    }

    fun syncNow() {
        viewModelScope.launch {
            try {
                repository.syncPending()
                _events.emit(UiEvent.Success("Synced with cloud"))
            } catch (e: Exception) {
                _events.emit(UiEvent.Error("Sync failed"))
            }
        }
    }

    sealed interface UiEvent {
        data class Success(val message: String) : UiEvent
        data class Error(val message: String) : UiEvent
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repo = container.repository
                    @Suppress("UNCHECKED_CAST")
                    return SharedDataViewModel(repo) as T
                }
            }
    }
}
