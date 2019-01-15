# Protozoafit

## Introduction
This app doesn't use gRPC, only HTTP request with PROTO files. In order to convert, I am using Retrofit protobuf converter.

## Implementation

### The PROTO file
The file should be the same in the server.
```proto
syntax = "proto3";
package tutorial;

message Person {
    string name = 1;
    int32 id = 2;
    string email = 3;
    string phone = 4;
}
```
https://github.com/zhenleiji/Android-Protobuf-Playground/blob/master/Protozoafit/app/src/main/proto/elyeproject.proto

This file should be located on `app/src/main/proto` folder.

### Gradle environment
We will use [Retrofit protobuf converter](https://github.com/square/retrofit/tree/master/retrofit-converters/protobuf) library.

To generate the Proto classes from the PROTO File. Add this task to your [build.gradle](https://github.com/zhenleiji/Android-Protobuf-Playground/blob/master/Protozoafit/app/build.gradle).

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

```kotlin
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
```
Your project is ready to send request and get response from server. Just run it as an android project.

![](https://media.giphy.com/media/l3nFsf65ci7zZMT60/giphy.gif)
