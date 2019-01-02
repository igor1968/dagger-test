package com.igordanilchik.daggertest.app

import android.app.Application
import android.content.Context
import com.igordanilchik.daggertest.BuildConfig
import com.igordanilchik.daggertest.common.di.ApplicationComponent
import com.igordanilchik.daggertest.common.di.ApplicationModule
import com.igordanilchik.daggertest.common.di.DaggerApplicationComponent
import com.igordanilchik.daggertest.common.di.RepositoryModule
import com.igordanilchik.daggertest.common.log.ReleaseTree
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber

class DaggerApplication : Application() {

    lateinit var appComponent: ApplicationComponent
        private set

    override fun onCreate() {
        super.onCreate()
        initInjector()
        initLeakDetection()
        initTimber()
    }

    private fun initLeakDetection() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        if (BuildConfig.DEBUG) {
            LeakCanary.install(this)

//            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
//                    .detectAll()
//                    .penaltyLog()
//                    .build())
//            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
//                    .detectAll()
//                    .penaltyLog()
//                    //.penaltyDeath()
//                    .build())
        }
    }

    private fun initInjector() {
        appComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .repositoryModule(RepositoryModule())
                .build()
    }

    /**
     * Plant proper Timber for Logging for Debug
     * or Sending Errors as Crashes for Release
     * Pretends as Fail!
     */
    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }

    companion object {

        operator fun get(context: Context?): DaggerApplication {
            return context?.applicationContext as DaggerApplication
        }
    }

}
