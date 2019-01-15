package com.zji.protozoa

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import io.grpc.ManagedChannel
import io.grpc.examples.helloworld.GreeterGrpc
import io.grpc.examples.helloworld.HelloRequest
import io.grpc.okhttp.OkHttpChannelBuilder
import kotlinx.android.synthetic.main.activity_main.mClickHereButton

class MainActivity : AppCompatActivity() {
    private var rpcAsyncTask: RpcAsyncTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mClickHereButton.setOnClickListener {
            onClickHereButton()
        }
    }

    override fun onDestroy() {
        rpcAsyncTask?.cancel(true)
        super.onDestroy()
    }

    private fun onClickHereButton() {
        rpcAsyncTask?.cancel(true)
        rpcAsyncTask = RpcAsyncTask()
        rpcAsyncTask?.execute("Zhenlei")
    }

    private inner class RpcAsyncTask : AsyncTask<String, Void, String>() {
        // CHANGE TO YOUR IP ADDRESS
        private val host = "XXX.XXX.XXX.XXX"

        private val channel: ManagedChannel by lazy {
            OkHttpChannelBuilder.forAddress(host, 50051)
                .usePlaintext()
                .build()
        }

        override fun doInBackground(vararg params: String?): String {
            val client = GreeterGrpc.newBlockingStub(channel)
            val reply = client.sayHello(HelloRequest.newBuilder().setName(params[0]).build())
            return reply.message
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Toast.makeText(this@MainActivity, result, Toast.LENGTH_SHORT).show()
        }
    }
}
