package app.krafted.fitlog.presentation.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import app.krafted.fitlog.MainActivity
import app.krafted.fitlog.R
import app.krafted.fitlog.presentation.timer.RestTimerManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.max

@AndroidEntryPoint
class RestTimerService : Service() {

    @Inject
    lateinit var restTimerManager: RestTimerManager

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var notificationManager: NotificationManager? = null
    private var timerJob: Job? = null

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "rest_timer_channel"
        const val ACTION_STOP = "app.krafted.fitlog.ACTION_STOP_TIMER"
        const val EXTRA_TARGET_TIMESTAMP = "target_timestamp"
        const val EXTRA_TOTAL_SECONDS = "total_seconds"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                restTimerManager.stopTimer()
                stopSelf()
                return START_NOT_STICKY
            }
            else -> {
                // Start or update the timer
                val targetTimestamp = intent?.getLongExtra(EXTRA_TARGET_TIMESTAMP, 0L) ?: 0L
                val totalSeconds = intent?.getIntExtra(EXTRA_TOTAL_SECONDS, 90) ?: 90
                
                if (targetTimestamp > 0) {
                    startTimerLoop(targetTimestamp, totalSeconds)
                }
            }
        }
        return START_STICKY
    }

    private fun startTimerLoop(targetTimestamp: Long, totalSeconds: Int) {
        // Cancel any existing timer
        timerJob?.cancel()
        
        // Start as foreground
        startSelfForeground()
        
        // Start the timer loop - this runs IN THE SERVICE, so it survives backgrounding
        timerJob = serviceScope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                val remainingMs = targetTimestamp - now
                val remainingSeconds = max(0, ceil(remainingMs / 1000.0).toInt())
                
                // Update the manager's state (this will also update the UI when app is in foreground)
                restTimerManager.updateFromService(remainingSeconds, totalSeconds, remainingMs <= 0)
                
                // Update notification
                if (remainingMs <= 0) {
                    // Timer complete!
                    updateNotification("Rest Complete!", true)
                    delay(2000) // Show "Complete" for 2 seconds
                    stopSelf()
                    break
                } else {
                    updateNotification("Rest: ${formatTime(remainingSeconds)}", false)
                }
                
                delay(500L) // Update every 500ms for smoother display
            }
        }
    }

    private fun startSelfForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                buildNotification("Starting..."),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE
            )
        } else {
            startForeground(NOTIFICATION_ID, buildNotification("Starting..."))
        }
    }

    private fun updateNotification(content: String, isFinished: Boolean) {
        notificationManager?.notify(NOTIFICATION_ID, buildNotification(content, isFinished))
    }

    private fun buildNotification(content: String, isFinished: Boolean = false): Notification {
        val launchIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, launchIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = Intent(this, RestTimerService::class.java).apply { action = ACTION_STOP }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Rest Timer")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setOngoing(!isFinished)
            .apply {
                if (!isFinished) {
                    addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent)
                }
            }
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Rest Timer",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows active rest timer"
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun formatTime(seconds: Int): String {
        val min = seconds / 60
        val sec = seconds % 60
        return "%02d:%02d".format(min, sec)
    }

    override fun onDestroy() {
        timerJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }
}
