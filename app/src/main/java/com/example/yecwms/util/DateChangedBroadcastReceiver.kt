package com.example.yecwms.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.util.*


abstract class DateChangedBroadcastReceiver : BroadcastReceiver() {
    private var curDate = Calendar.getInstance()

    /**called when the receiver detected the date has changed. You should still check it yourself, because you might already be synced with the new date*/
    abstract fun onDateChanged(previousDate: Calendar, newDate: Calendar)

    companion object {
        fun toString(cal: Calendar): String {
            return "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}-${cal.get(Calendar.DAY_OF_MONTH)}"
        }

        fun resetDate(date: Calendar) {
            date.set(Calendar.HOUR_OF_DAY, 0)
            date.set(Calendar.MINUTE, 0)
            date.set(Calendar.SECOND, 0)
            date.set(Calendar.MILLISECOND, 0)
        }

        fun areOfSameDate(date: Calendar, otherDate: Calendar) =
            date.get(Calendar.DAY_OF_YEAR) == otherDate.get(Calendar.DAY_OF_YEAR) &&
                    date.get(Calendar.YEAR) == otherDate.get(Calendar.YEAR)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun register(context: Context, date: Calendar) {
        curDate = date.clone() as Calendar
        resetDate(curDate)
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_TIME_CHANGED)
        filter.addAction(Intent.ACTION_DATE_CHANGED)
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED)
        context.registerReceiver(this, filter)
        val newDate = Calendar.getInstance()
        resetDate(newDate)
        if (!areOfSameDate(newDate, curDate)) {
            val previousDate = curDate.clone() as Calendar
            curDate = newDate
            onDateChanged(previousDate, curDate)
        }
    }

    /**a convenient way to auto-unregister when activity/fragment has stopped. This should be called on the onResume method of the fragment/activity*/
    fun registerOnResume(activity: AppCompatActivity, date: Calendar, fragment: Fragment? = null) {
        Log.wtf("DATES", "BROADCAST REGISTERED")
        register(activity as Context, date)
        val lifecycle = fragment?.lifecycle ?: activity.lifecycle
        lifecycle.addObserver(object : LifecycleObserver {
            @Suppress("unused")
            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause() {
//                Log.d("AppLog", "onPause, so unregistering")
                lifecycle.removeObserver(this)
                activity.unregisterReceiver(this@DateChangedBroadcastReceiver)
            }
        })
    }

    override fun onReceive(context: Context, intent: Intent) {
        val newDate = Calendar.getInstance()
        resetDate(newDate)
        Log.d(
            "AppLog",
            "got intent:${intent.action} curDate:${toString(curDate)} newDate:${toString(newDate)}"
        )
        if (!areOfSameDate(newDate, curDate)) {
            Log.d("AppLog", "cur date is different, so posting event")
            val previousDate = curDate.clone() as Calendar
            curDate = newDate
            onDateChanged(previousDate, newDate)
        }
    }

}