package app.krafted.fitlog.presentation.timer

import android.content.Context
import android.content.Intent
import android.os.Build
import app.krafted.fitlog.presentation.services.RestTimerService
import app.krafted.fitlog.presentation.viewmodel.RestTimerState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton manager for the rest timer.
 * The actual timer tick logic runs in RestTimerService (Foreground Service)
 * to ensure it continues when the app is backgrounded.
 */
@Singleton
class RestTimerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val _restTimerState = MutableStateFlow(RestTimerState())
    val restTimerState: StateFlow<RestTimerState> = _restTimerState.asStateFlow()

    private var targetTimestamp: Long = 0L
    private var totalDurationSeconds: Int = 0

    /**
     * Start a new rest timer with the given duration.
     * This starts the Foreground Service which will handle the actual countdown.
     */
    fun startTimer(durationSeconds: Int) {
        targetTimestamp = System.currentTimeMillis() + (durationSeconds * 1000L)
        totalDurationSeconds = durationSeconds
        
        _restTimerState.value = RestTimerState(
            isActive = true,
            remainingSeconds = durationSeconds,
            totalSeconds = durationSeconds,
            isPaused = false,
            isComplete = false,
            targetTimestamp = targetTimestamp
        )

        // Start the Foreground Service with the target timestamp
        val intent = Intent(context, RestTimerService::class.java).apply {
            putExtra(RestTimerService.EXTRA_TARGET_TIMESTAMP, targetTimestamp)
            putExtra(RestTimerService.EXTRA_TOTAL_SECONDS, durationSeconds)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    /**
     * Called by RestTimerService to update the state.
     * This ensures the UI stays in sync with the service's timer.
     */
    fun updateFromService(remainingSeconds: Int, totalSeconds: Int, isComplete: Boolean) {
        val currentState = _restTimerState.value
        if (!currentState.isActive) return
        
        _restTimerState.value = currentState.copy(
            remainingSeconds = remainingSeconds,
            totalSeconds = totalSeconds,
            isComplete = isComplete
        )
    }

    /**
     * Pause or resume the timer.
     */
    fun pauseResumeTimer() {
        val currentState = _restTimerState.value
        if (!currentState.isActive) return
        
        if (currentState.isPaused) {
            // Resume: Calculate new target timestamp
            targetTimestamp = System.currentTimeMillis() + (currentState.remainingSeconds * 1000L)
            _restTimerState.value = currentState.copy(
                isPaused = false,
                targetTimestamp = targetTimestamp
            )
            
            // Restart the service with new timestamp
            val intent = Intent(context, RestTimerService::class.java).apply {
                putExtra(RestTimerService.EXTRA_TARGET_TIMESTAMP, targetTimestamp)
                putExtra(RestTimerService.EXTRA_TOTAL_SECONDS, currentState.totalSeconds)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } else {
            // Pause: Stop the service, keep state
            _restTimerState.value = currentState.copy(
                isPaused = true,
                targetTimestamp = null
            )
            context.stopService(Intent(context, RestTimerService::class.java))
        }
    }

    /**
     * Add time to the current timer.
     */
    fun addTime(seconds: Int) {
        val current = _restTimerState.value
        if (!current.isActive || current.isPaused) return
        
        val newRemaining = current.remainingSeconds + seconds
        val newTotal = current.totalSeconds + seconds
        targetTimestamp += (seconds * 1000L)
        
        _restTimerState.value = current.copy(
            remainingSeconds = newRemaining,
            totalSeconds = newTotal,
            targetTimestamp = targetTimestamp
        )
        
        // Restart service with updated timestamp
        val intent = Intent(context, RestTimerService::class.java).apply {
            putExtra(RestTimerService.EXTRA_TARGET_TIMESTAMP, targetTimestamp)
            putExtra(RestTimerService.EXTRA_TOTAL_SECONDS, newTotal)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    /**
     * Stop and reset the timer.
     */
    fun stopTimer() {
        _restTimerState.value = RestTimerState()
        targetTimestamp = 0L
        totalDurationSeconds = 0
        
        // Stop the service
        context.stopService(Intent(context, RestTimerService::class.java))
    }
}
