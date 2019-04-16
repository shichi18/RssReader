package com.github.shichi18.rssreader


import android.app.LoaderManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Loader
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Rss> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ローダーを呼び出す
        loaderManager.initLoader(1, null, this)

        createChannel(this)
        val fetchJob = JobInfo.Builder(
            1, ComponentName(this,PollingJob::class.java))
            .setPeriodic(TimeUnit.HOURS.toMillis(6))//6h
            .setPersisted(true)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .build()

        getSystemService(JobScheduler::class.java).schedule(fetchJob)

    }


    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Rss> = RssLoader(this)

    override fun onLoadFinished(loader: Loader<Rss>, data: Rss?) {

        if (data != null) {
            val recyclerView = findViewById<RecyclerView>(R.id.articles)

            val adapter = ArticlesAdapter(this, data.articles) { article ->
                //記事をタップした時の処理
                val intent =CustomTabsIntent.Builder().build()
                intent.launchUrl(this, Uri.parse(article.link))
            }

            recyclerView.adapter = adapter

            val layoutManager = GridLayoutManager(this, 2)
            recyclerView.layoutManager = layoutManager
        }

    }

    override fun onLoaderReset(loader: Loader<Rss>) {
    }


}
