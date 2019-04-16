package com.github.shichi18.rssreader

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context

class PollingJob : JobService() {
    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Thread {
            val response = httpGet("https://www.sbbit.jp/rss/HotTopics.rss")
            if (response != null) {
                val rss = parseRss(response)
                val prefs = getSharedPreferences("pref_polling", Context.MODE_PRIVATE)
                val lastFetchTime = prefs.getLong("last_publish_time", 0L)
                if (lastFetchTime > 0 && lastFetchTime < rss.pubDate.time) {
                    notifyUpdate(this)//通知
                }

                prefs.edit().putLong("last_publish_time", rss.pubDate.time).apply()
            }

        }.start()
        return true
    }
}
