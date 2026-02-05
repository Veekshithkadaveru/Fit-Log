package app.krafted.fitlog.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel class that provides common functionality for all ViewModels in the app
 */
abstract class BaseViewModel<TState : UiState, TEvent : UiEvent> : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(initializeState())
    val uiState: StateFlow<TState> = _uiState.asStateFlow()
    
    // UI Events (one-time events like navigation, showing snackbar, etc.)
    private val _uiEvent = MutableSharedFlow<TEvent>()
    val uiEvent: SharedFlow<TEvent> = _uiEvent.asSharedFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Error handling
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Exception handler for coroutines
    protected val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        handleError(exception)
    }
    
    /**
     * Initialize the initial state of the ViewModel
     */
    protected abstract fun initializeState(): TState
    
    /**
     * Update the UI state
     */
    protected fun updateState(update: (TState) -> TState) {
        _uiState.value = update(_uiState.value)
    }
    
    /**
     * Emit a one-time UI event
     */
    protected fun emitEvent(event: TEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
    
    /**
     * Set loading state
     */
    protected fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
    
    /**
     * Handle errors and display error messages
     */
    protected open fun handleError(throwable: Throwable) {
        _isLoading.value = false
        _errorMessage.value = throwable.message ?: "An unknown error occurred"
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Execute a suspending function with loading state and error handling
     */
    protected fun executeWithLoading(block: suspend () -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            _isLoading.value = true
            try {
                block()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

/**
 * Base interface for UI state classes
 */
interface UiState

/**
 * Base interface for UI events (one-time events)
 */
interface UiEvent