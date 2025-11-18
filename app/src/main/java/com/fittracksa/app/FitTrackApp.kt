package com.fittracksa.app

import android.app.Application
import androidx.work.Configuration
import com.fittracksa.app.data.AppContainer
import com.fittracksa.app.data.DefaultAppContainer
import com.fittracksa.app.data.sync.SyncScheduler
import com.google.firebase.FirebaseApp

class FitTrackApp : Application(), Configuration.Provider {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        container = DefaultAppContainer(applicationContext)
        SyncScheduler.schedulePeriodicSync(applicationContext)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
