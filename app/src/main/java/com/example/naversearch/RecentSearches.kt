package com.example.naversearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_recent_searches.*

class RecentSearches : AppCompatActivity() {

    private var hisDb : HistoryDatabase? = null
    private var hisList = ArrayList<HistoryEntity>()
    lateinit var mAdapter : RecentAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_searches)

        hisDb = HistoryDatabase.getInstance(this)
        mAdapter = RecentAdapter(this, hisList)



        val r = Runnable {
            try {
                var count = hisDb?.hisDao()?.getCount()
                hisList = hisDb?.hisDao()?.getAll() as ArrayList<HistoryEntity>
                Log.d("count","$count")
                if (count != null) {
                    if(count > 10){
                        val len: Int = count - 11
                        for(i in 0..len){
                            val delHis = HistoryEntity(hisList[i].id,hisList[i].title)
                            hisDb?.hisDao()?.deleteTitle(delHis)
                        }
                    }
                }
                hisList = hisDb?.hisDao()?.getAll() as ArrayList<HistoryEntity>
                mAdapter = RecentAdapter(this, hisList)

                recyclerView_recent.adapter = mAdapter
                val manager = LinearLayoutManager(this)
                manager.reverseLayout = true
                manager.stackFromEnd = true
                recyclerView_recent.layoutManager = manager
                recyclerView_recent.setHasFixedSize(true)
            } catch (e: Exception) {
                Log.d("tag", "Error - $e")
            }
        }

        val thread = Thread(r)
        thread.start()

    }

    override fun onDestroy() {
        HistoryDatabase.destroyInstance()
        hisDb = null
        super.onDestroy()
    }

}