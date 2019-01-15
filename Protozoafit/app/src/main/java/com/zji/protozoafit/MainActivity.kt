package com.zji.protozoafit

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.mClickHereButton
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.protobuf.ProtoConverterFactory
import retrofit2.http.GET
import tutorial.Elyeproject

class MainActivity : AppCompatActivity() {

    private val serviceApi: ElyeProjectService by lazy {
        Retrofit.Builder()
            .baseUrl("http://elyeproject.x10host.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(ProtoConverterFactory.create())
            .build()
            .create(ElyeProjectService::class.java)
    }

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mClickHereButton.setOnClickListener {
            onClickHereButton()
        }
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

    private fun onClickHereButton() {
        disposable?.dispose()
        disposable = serviceApi.getPerson()
            .map { "Name: ${it.name}\nEmail: ${it.email}\nPhone: ${it.phone}" }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show() },
                { Toast.makeText(this@MainActivity, "Error: ${it.message}", Toast.LENGTH_SHORT).show() }
            )
    }

    interface ElyeProjectService {
        @GET("experiment/protobuf/")
        fun getPerson(): Single<Elyeproject.Person>
    }
}
