package com.example.naversearch

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException
import java.net.URL
import java.net.URLEncoder


class MainActivity : AppCompatActivity() {

    val clientId = "scOL8PYDyvG1H6LTbLrv"
    val clientSecret = "dVM9w1tz4q"

    // 1페이지에 10개씩 데이터를 불러온다
    var page = 1
    var limit = 20
    var edit = ""
    var pageCheck = false
    var adapter: RecyclerViewAdapter? = null
    var limitTotal = 0

    private var hisDb : HistoryDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intentTitle = intent.getStringExtra("title")

        if(intentTitle == null){
            Log.d("title","nope")
        }else{
            //레이아웃매니저 설정
            recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            recyclerView.setHasFixedSize(true)
            edit = intentTitle

            fetchJson(page, limit, intentTitle)
            editText.setText("$intentTitle")
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    Log.d("hise","hise")

                    if (!recyclerView.canScrollVertically(-1) && pageCheck) {
                        if(page < 20){
                            return
                        }
                        pageCheck = false
                        page -= 20
                        progress_bar.visibility = View.VISIBLE
                        fetchJson(page, limit, edit)
                    } else if (!recyclerView.canScrollVertically(1)) {
                        pageCheck = false
                        page += 20
                        progress_bar.visibility = View.VISIBLE
                        fetchJson(page, limit, edit)
                    } else {
                        pageCheck = true
                    }
                }
            })
        }

        recent_searches_btn.setOnClickListener {
            val intent = Intent(this,RecentSearches::class.java)
            startActivity(intent)
        }

        search_btn.setOnClickListener {
            if(editText.text.isEmpty()){
                //검색텍스트 박스를 불러와 값의 유무를 따진다
                return@setOnClickListener
            }

            edit = editText.text.toString()
            hisDb = HistoryDatabase.getInstance(this)

            /* 새로운 객체를 생성, id 이외의 값을 지정 후 DB에 추가 */
            val addRunnable = Runnable {
                val newHis = HistoryEntity(null,edit)
                newHis.title = edit.toString()
                hisDb?.hisDao()?.saveTitle(newHis)
            }

            val addThread = Thread(addRunnable)
            addThread.start()


            //레이아웃매니저 설정
            recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            recyclerView.setHasFixedSize(true)

            fetchJson(page, limit, edit)

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    Log.d("hise","hise")

                    if (!recyclerView.canScrollVertically(-1) && pageCheck) {
                        if(page < 20){
                            return
                        }
                        pageCheck = false
                        page -= 20
                        progress_bar.visibility = View.VISIBLE
                        fetchJson(page, limit, edit)
                    } else if (!recyclerView.canScrollVertically(1)) {
                        pageCheck = false
                        page += 20
                        progress_bar.visibility = View.VISIBLE
                        fetchJson(page, limit, edit)
                    } else {
                        pageCheck = true
                    }
                }
            })

            //API 함수 호출
//            fetchJson(editText.text.toString())

            //키보드를 내린다.
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
        }

    }

    fun fetchJson(page : Int, limit : Int, vararg p0: String) {
        //OkHttp로 요청하기
        val text = URLEncoder.encode("${p0[0]}", "UTF-8")
//        val url = URL("https://openapi.naver.com/v1/search/movie.json?query=${text}&display=10&start=2&genre=")
        val url = URL("https://openapi.naver.com/v1/search/movie.json?query=${text}&display=${limit}&start=${page}&genre=")
        Log.d("text","$text")
        Log.d("url","$url")
        FormBody.Builder()
            .add("query", "${text}")
            .add("display", "${limit}")
            .add("start", "${page}")
            .add("genre", "1")
            .build()
        val request = Request.Builder()
            .url(url)
            .addHeader("X-Naver-Client-Id", clientId)
            .addHeader("X-Naver-Client-Secret", clientSecret)
            .method("GET", null)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string()

                println("Success to execute request : ${body}")
                //Gson을 Kotlin에서 사용 가능한 object로 만든다.
                val gson = GsonBuilder().create()
                val homefeed = gson.fromJson(body, Homefeed::class.java)

                //어답터를 연결하자. 메인쓰레드 변경하기 위해 이 메소드 사용
                runOnUiThread {
                    adapter = RecyclerViewAdapter(homefeed)
                    recyclerView.adapter = adapter
                    progress_bar.visibility = View.INVISIBLE
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute request")
            }
        })
    }

    override fun onDestroy() {
        HistoryDatabase.destroyInstance()
        super.onDestroy()
    }

}
