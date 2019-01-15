# Protozoa

## Introduction
gRPC is a modern RPC framework developed by Google to call remote methods as easy as if they are local methods. There are a lots of article about gRPC comparison over REST or JSON. So I am not going to talk about it. I will show you how to configure basic gRPC framework to communicate with Backend Server. For server side implementation use this one: https://github.com/grpc/grpc/tree/master/examples

## Implementation

### The PROTO file
The file should be the same in the server.
```proto
syntax = "proto3";

option java_multiple_files = true;
option java_package = "io.grpc.examples.helloworld";
option java_outer_classname = "HelloWorldProto";
option objc_class_prefix = "HLW";

package helloworld;

// The greeting service definition.
service Greeter {
  // Sends a greeting
  rpc SayHello (HelloRequest) returns (HelloReply) {}
}

// The request message containing the user's name.
message HelloRequest {
  string name = 1;
}

// The response message containing the greetings
message HelloReply {
  string message = 1;
}
```
https://github.com/zhenleiji/Android-Protobuf-Playground/blob/master/Protozoa/app/src/main/proto/helloworld.proto

This file should be located on `app/src/main/proto` folder.

### Gradle environment
We will use [gRPC-Java](https://github.com/grpc/grpc-java) library.

To generate the Proto classes from the PROTO File. Add this task to your [build.gradle](https://github.com/zhenleiji/Android-Protobuf-Playground/blob/master/Protozoa/app/build.gradle).

```gradle
protobuf { // ADDED
    protoc {
        artifact = 'com.google.protobuf:protoc:3.1.0'
    }
    plugins {
        javalite {
            artifact = 'com.google.protobuf:protoc-gen-javalite:3.0.0'
        }
        grpc {
            artifact = 'io.grpc:protoc-gen-grpc-java:1.0.1'
        }
    }
    generateProtoTasks {
        all().each { task ->
            task.plugins {
                javalite {}
                grpc {
                    // Options added to --grpc_out
                    option 'lite'
                }
            }
        }
    }
}
```

## The App code
Don't forget to change the host to your ip address.

```kotlin
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
```
Your project is ready to send request and get response from server. Just run it as an android project.

![](https://media.giphy.com/media/3o6wNYLBSc13xPKbsI/giphy.gif)
