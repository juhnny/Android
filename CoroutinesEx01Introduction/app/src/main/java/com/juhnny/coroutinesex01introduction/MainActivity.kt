package com.juhnny.coroutinesex01introduction

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.CoroutineContext

// Introduction to coroutines (codelab)
// https://developer.android.com/courses/pathways/android-basics-kotlin-unit-4-pathway-1#codelab-https://developer.android.com/codelabs/basic-android-kotlin-training-introduction-coroutines

// 요점
//Why concurrency is needed
//What a thread is, and why threads are important for concurrency -
//How to write concurrent code in Kotlin using coroutines
//When and when not to mark a function as "suspend"
//The roles of a CoroutineScope, Job, and Dispatcher
//The difference between Deferred and Await


// Thread
// in a running app, there are more threads in addition to the main thread.
// Behind the scenes, the processor doesn't actually work with separate threads,
// but rather, switches back and forth between the different series of instructions
// to give the appearance of multitasking.
// * The scheduler gives out a slice of time to each thread

// A thread is an abstraction that you can use when writing code
// to determine which path of execution each instruction should go.
// Working with threads other than the main thread, allows your app to perform complex tasks,
// such as downloading images, in the background while the app's user interface remains responsive.
// This is called concurrent code, or simply, concurrency.

// Multithreading and concurrency
// Concurrency allows multiple units of code to execute out of order or seemingly in parallel
// permitting more efficient use of resources.
// The operating system can use characteristics of the system, programming language,
// and concurrency unit to manage multitasking.

// Why do you need to use concurrency? As your app gets more complex,
// it's important for your code to be non-blocking.
// This means that performing a long-running task, such as a network request,
// won't stop the execution of other things in your app.

fun fun1(){
    val thread = Thread(){
        run() { println("${Thread.currentThread()}") } // thread's name, priority, and thread group
    }
    thread.start() //스레드를 만들고 스레드에서 실행
    thread.run() //이건 메인 스레드가 그냥 이 객체의 멤버함수를 직접 실행하는 것
}

// 다중 스레드를 돌려보자
// 결과(프린트 순서)가 매번 달라지는 걸 확인할 수 있다.
fun fun2(){
    val states = arrayOf("START", "..ING", "END")
    repeat(3){
        Thread{
            println("${Thread.currentThread()} 시작!")
            for(state in states){
                println("${Thread.currentThread()} - $state")
                Thread.sleep(50)
            }
        }.start()
    }
}

// Challenges(problems) with threads

// # Threads require a lot of resources

// Creating, switching, and managing threads takes up system resources and time
// limiting the raw number of threads that can be managed at the same time.
// The costs of creation can really add up(증가하다).

// main thread는 UI를 담당하므로 긴 작업으로 인해 block되면 앱이 반응을 하지 않게 된다.

// 요즘 폰들은 초당 60~120 프레임. UI를 준비하고 그리는 데 아주 짧은 시간이 주어짐(60fps일 떄 한 프레임에 16ms 이하)
// Android OS는 유저 반응성을 확보하기 위해 노력한다.
// (UI 작업이 오래 걸리면) Android will drop frames,
// or abort trying to complete a single update cycle to attempt to catch up.
// Some frames drop and fluctuation is normal but too many will make your app unresponsive.


// Race conditions and unpredictable behavior

// a thread is an abstraction for how a processor appears to handle multiple tasks at once.
// As the processor switches between sets of instructions on different threads,
// the exact time a thread is executed and when a thread is paused is beyond your control.
// You can't always expect predictable output when working with threads directly.

fun countTo50(){
    var count = 0
    for (i in 1..50){
        Thread{
            count++
            println("Thread: $i Count: $count")
        }.start()
    }
}
// count가 제대로 이뤄질까? 아니다.
// count++이 실행되기도 전에 다음 Thread 실행문의 내용이 정해지고, 실행순서는 뒤죽박죽 제멋대로 섞여버린다.
// 출력결과를 보고서 count 값을 순서대로 추적한다는 게 불가능하다.

// 또한 다중 스레드 작업을 하다보면 race condition(경쟁 상태)이라는 걸 겪게 된다.
// This is when multiple threads try to access the same value in memory at the same time.
// Race conditions can result in hard to reproduce, random looking bugs,
// which may cause your app to crash, often unpredictably.

// Performance issues, race conditions, and hard to reproduce bugs are some of the reasons
// why we don't recommend working with threads directly.


// Coroutines in Kotlin

// thread를 직접 만들고 사용하는 경우도 있다.
// 그치만 코틀린은 코루틴이라는, 동시성을 다루기 위한, 더 유연하고 쉬운 방법을 제공한다.

// Coroutines enable multitasking, but provide another level of abstraction over simply working with threads.
// One key feature of coroutines is the ability to store state, so that they can be halted and resumed.
// A coroutine may or may not execute.

// The state, represented by continuations, allows portions of code to signal
// when they need to hand over control or wait for another coroutine to complete its work before resuming.
// This flow is called cooperative multitasking.

// Job
// lifecycle이 있고 취소 가능한 작업 유닛인 Job. launch() 함수로 생성

// CoroutineScope
// A CoroutineScope is a context that enforces cancellation and other rules to its children and their children recursively.
// launch()나 async()처럼 새 코루틴을 만들기 위해 사용되는 함수들은 CoroutineScope을 상속한다.(정말?)

// Dispatcher
// coroutine이 사용할 스레드를 결정한다. Main dispatcher는 코루틴을 항상 main 스레드에서 실행한다.
// 반면 Default, IO, Unconfined 같은 dispatcher는 다른 스레드를 사용한다.
// Dispatchers are one of the ways coroutines can be so performant.(효능적)
// One avoids the performance cost of initializing new threads.

fun coroutine1(){
    repeat(3){
        GlobalScope.launch {
            println("Hi from ${Thread.currentThread()}")
        }
    }
}
//Hi from Thread[DefaultDispatcher-worker-1,5,main]
//Hi from Thread[DefaultDispatcher-worker-2,5,main]
//Hi from Thread[DefaultDispatcher-worker-3,5,main]

// The GlobalScope allows any coroutines in it to run as long as the app is running.
// For the reasons we talked about concerning the main thread, this is not recommended outside example code.

// launch()
// The launch() function creates a coroutine from the enclosed code wrapped in a cancelable Job object.
// launch() is used when a return value is not needed outside the confines of the coroutine.

//fun coroutine2(){
//    CoroutineScope.launch() {
//        context: CoroutineContext = EmptyCoroutineContext,
//        start: CoroutineStart = CoroutineStart.DEFAULT,
//        block: suspend CoroutineScope.() -> Unit
//    }
//} // 왜 import 안내문은 뜨는데 import가 안되지?

// suspend
// Behind the scenes, the block of code you passed to launch is marked with the suspend keyword.
// Suspend signals that a block of code or function can be paused or resumed.


// A word about runBlocking

// runBlocking
// 새 코루틴을 시작하고 작업 완료시까지 현재 스레드를 블락한다.
// It is mainly used to bridge between blocking and non-blocking code in main functions and tests.
// You will not be using it often in typical Android code.

suspend fun getValue():Double{
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val formatter = DateTimeFormatter.ISO_LOCAL_TIME
        val time = { formatter.format(LocalDateTime.now()) }

        println("entering getValue() at ${time()}")
        delay(3000)
        println("leaving getValue() at ${time()}")
        return Math.random()
    } else return -1.0
}

// async

//fun CoroutineScope.async() {
//    context: CoroutineContext = EmptyCoroutineContext,
//    start: CoroutineStart = CoroutineStart.DEFAULT,
//    block: suspend CoroutineScope.() -> T
//}: Deferred<T>

// The two calls to getValue() are independent and don't necessarily need the coroutine to suspend.
// Kotlin has an async function that's similar to launch. The async() function is defined as follows.
// async() 함수는 Deferred 타입을 리턴. Deferred 타입은 다른 언어에서는 Promise나 Future로도 불림
// Deferred 객체는 나중에 주어질 value의 참조값을 지닌, 취소 가능한 Job 객체
// Deferred는 나중에 이 객체에 값이 주어질 것임을 보장함
// 비동기작업은 기본적으로 스레드를 블록하거나 실행을 기다리지 않을 것이다.
// Deferred의 결과를 기다려야 할 때는 await을 사용한다. It will return the raw value(원시값?)


// 언제 suspend 키워드를 써야 하는가?
// 위 getValue() 함수에서는 suspend 키워드를 썼다. 이유는 delay라는 또 다른 suspend 함수를 썼기 때문.
// suspend 함수를 호출하려면 그 함수도 suspend 함수가 되어야 한다.

// 그렇다면 왜 onCreate()은 suspend 키워드를 필요로 하지 않나?
// getValue 함수는 runBlocking()의 lambda 안에서 호출되어졌다.
// launch()나 async()에 전달되는 중괄호 영역들과 마찬가지로, lambda는 suspend 함수다.
// 하지만 runBlocking() 함수 자체는 suspend 함수가 아니다. launch()나 async()도 그자체는 suspend 함수가 아니다.
// getValue()를 onCreate()에서 직접 호출한 것도 아니고 runBlocking()이 suspend 함수인 것도 아니기 때문에 onCreate()에 suspend가 필요하지 않다.


// 위에서 다중 스레드로 만들었던 fun2()를 코루틴으로 바꿔보자
fun coroutine3(){
    val states = arrayOf("START", "..ING", "END")
    repeat(3){
        GlobalScope.launch {
            println("${Thread.currentThread()} 시작!")
            for(state in states){
                println("${Thread.currentThread()} - $state")
                delay(50)
            }
        }
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        fun1()
//        fun1()
//        fun1()
//        fun1() //thread 번호는 계속 달라지네. priority는 계속 5?

//        fun2()

//        countTo50()

//        coroutine1()

//        runBlocking {
//            val num1 = getValue()
//            val num2 = getValue()
//            println("result of num1 + num2 is ${num1 + num2} \n") // 합 자체가 중요하진 않음
//        }

//        runBlocking {
//            val num1 = async { getValue() }
//            val num2 = async { getValue() }
//            println("result of num1 + num2 is ${num1.await() + num2.await()} \n")
//            //
//        }

        coroutine3()

    } // onCreate()
}

/* sdfsdfsd [sdfsf] */
// asdfasdf [sdfsd]

// Which statement below is false about async() and runBlocking()?
// 1. Both functions take a CoroutineScope(a suspend function) as a parameter
// 2. Both functions return a Deferred
// 3. You'll typically not use runBlocking() in Android app code
// 4. When using async, you need to await() to access the returned value

// 정답 2번

// Which of the following are suspend functions? (복수 정답)
// 1. async()
// 2. The lambda passed into async()
// 3. runBlocking()
// 4. The lambda passed into runBlocking()

// 정답 2, 4
