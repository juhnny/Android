package com.juhnny.coroutinesex03advancedconcepts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.*

// Advanced coroutines concepts
// https://developer.android.com/kotlin/coroutines/coroutines-adv

// Improve app performance with Kotlin coroutines

// Manage long-running tasks
// 코루틴은 일반 함수 위에 장시간 작업들을 위한 두가지 작업을 추가하여 빌드된다.
// invoke(또는 call) 와 return에 더해 코루틴이 suspend와 resume을 추가한다.

// suspend(정지)는 현재 코루틴의 실행을 일시중지하고 모든 지역 변수들을 저장한다.
// resume은 정지(suspend)되었던 위치부터 코루틴의 실행을 재개한다.

// 새 코루틴을 시작하기 위해서는 또 다른 suspend 함수로부터 호출하거나
// 혹은 코루틴 builder(launch 같은)를 사용함으로써 suspend 함수를 호출할 수 있다.

// 다음 예는 가상의 장시간 작업을 위한 간단한 코루틴 구현 방법을 보여준다.

suspend fun fetchDocs() {                             // Dispatchers.Main
    val result = get("https://developer.android.com") // Dispatchers.IO for `get`
//    show(result)                                      // Dispatchers.Main
}

suspend fun get(url: String) = withContext(Dispatchers.IO) {
    /* ... */
}

// 이 예에서 get()은 여전히 메인 스레드에서 실행되지만 네트워크 요청을 시작하기 전에 코루틴을 정지suspend한다.
// 네트워크 요청이 완료되면 get()은 메인 스레드에 notify하는 콜백을 사용하는 대신 정지되었던 코루틴을 재개한다.

// 코틀린은 어느 함수가 어느 지역변수와 함께 실행되고 있는지 관리하기 위해 stack frame을 사용한다.
// 코루틴을 정지할 때, 현재 stack frame이 나중을 위해 복사 및 저장됩니다.
// 작업이 재개될 때엔, stack frame이 저장되었던 곳으로부터 다시 되복사되고, 함수가 다시 실행을 시작한다.
// 비록 코드는 일반적인 순차적이고 block을 일으키는 요청처럼 보일지라도,
// 코루틴은 네트워크 요청이 메인 스레드의 블락을 회피하는 걸 보장한다.


// Use coroutines for main-safety

// 코루틴은 코루틴 실행에 어떤 스레드가 쓰이는지 결정하기 위해 dispatcher를 사용한다.
// 코드를 메인 스레드 밖에서 실행하기 위해 코루틴에게 Default 또는 IO dispatcher에서 실행되도록 지시할 수 있다.
// 모든 코루틴은, 메인 스레드에서 실행중인 경우에도, 반드시 dispatcher 안에서 실행되어야 한다.
// 코루틴은 스스로를 suspend할 수 있고, dispatcher가 작업재개를 담당한다.

// 코틀린은 세 종류의 dispatcher를 제공한다.

// Dispatchers.Main
// 코루틴을 main Android thread에서 실행하려면 이 dispatcher를 사용하라.
// UI와의 상호작용이나 빠른 작업을 위해서만 사용돼야 한다.
// 예를 들어 suspend 함수의 호출, Android UI framework 작업의 실행, LiveData 객체들의 업데이트 같은 작업이 있다.

// Dispatchers.IO
// 디스크나 네트워크 I/O 작업을 메인 스레드 밖에서 수행하는 데 최적화되어 있다.
// 예를 들어 Room 컴포넌트의 사용, 파일 읽기 또는 쓰기, 네트워크 작업의 실행 같은 작업이 있다.

// Dispatchers.Default
// CPU-intensive한 작업을 메인 스레드 밖에서 수행하는 데 최적화되어 있다.
// 예를 들어 list sorting, JSON parsing 같은 작업이 있다.

// (이전 예제에 이어서) get() 함수의 재정의하는 데 dispatcher를 사용할 수 있다.
// get()의 본문 내에서 withContext(Dispatchers.IO)를 호출해서 IO 스레드 풀에서 실행되는 블록을 생성한다.
// 블록 안에 작성한 코드는 언제나 IO dispatcher를 통해 실행된다.
// withContext 자체가 suspend 함수이기 때문에 get() 함수 또한 suspend 함수다.

suspend fun fetchDocs() {                      // Dispatchers.Main
    val result = get("developer.android.com")  // Dispatchers.Main
//    show(result)                               // Dispatchers.Main
}

suspend fun get(url: String) =                 // Dispatchers.Main
    withContext(Dispatchers.IO) {              // Dispatchers.IO (main-safety block)
        /* perform network IO here */          // Dispatchers.IO (main-safety block)
    }                                          // Dispatchers.Main
}

// 코루틴을 이용하면 스레드 할당(dispatch)을 미세하게 제어할 수 있다.
// withContext() 어느 줄의 코드라도 callback을 사용하지 않고 그 스레드 풀을 제어할 수 있게 해주므로,
// 데이터베이스 읽기나 네트워크 요청처럼 아주 작은 함수에도 적용할 수 있다.
// withContext()를 사용해 모든 함수들을 main-safe하게 만들고, 그리하여 메인 스레드에서도 호출할 수 있도록 만드는 게 좋다.
// 그리하면 호출자는 그 함수가 어느 스레드에서 실행되어야 하는지 생각할 필요가 없다.

// 이전 예제에서, fetchDocs() 함수는 메인 스레드에서 실행된다.
// 하지만 get()을 안전하게 호출할 수 있고, get()의 네트워크 요청은 백그라운드에서 수행된다.
// 코루틴은 suspend와 resume을 제공하므로, 메인 스레드에서의 코루틴은 withContext() 블록이 끝나는대로 get()의 결과와 함께 재개된다.

// 중요: suspend를 쓰더라도 코틀린이 함수를 백그라운드 스레드에서 실행하는 것은 아니다.
// suspend 함수는 메인 스레드에서 실행되는 것이 일반적이다.
// 또한 코루틴은 메인 스레드에서 launch()하는 것이 일반적이다.
// 디스크 읽기/쓰기 작업, 네트워크 작업, CPU 집약적 작업을 실행할 때처럼 main-safety가 필요할 때에는
// 언제나 suspend 함수 내에서 withContext()를 사용해야 한다.

// Performance of withContext()
// withContext()는 같은 작업의 callback 기반 구현에 비해 extra overhead를 추가하지 않는다.
// 나아가 일부 케이스에서는 withContext() 호출을 최적화해 동등한 callback 기반 구현을 능가할 수 있다.
// 예를 들어, 함수에서 열 번의 네트워크 호출을 보낸다면,
// 당신은 외부 withContext()를 이용해 코틀린에게 한번만 스레드를 변경하도록 지시할 수 있다.
// 그러면 비록 네트워크 라이브러리는 withContext()를 여러번 사용하더라도,
// (함수의 실행은) 같은 dispatcher에 머무르고 스레드 전환을 피한다.
// 코틀린은 가능한 한 Dispatchers.Default와 Dispatchers.IO간의 전환을 최적화해서 스레드 전환을 피한다.

// 중요: 스레드 풀을 사용하는 dispatcher(예: Dispatchers.IO 또는 Dispatchers.Default)를 사용하더라도
// 그 블록이 처음부터 끝까지 같은 스레드에서 실행된다는 보장은 없다.
// 경우에 따라 코루틴은 suspend-and-resume 이후 다른 스레드로 실행을 옮길 수도 있다.
// 그러므로 thread-local 변수들이 withContext() 블록 내내 같은 값을 가리키지 않을 수 있다.


// Start a coroutine
// 다음 두가지 방법 중 하나로 코루틴을 시작할 수 있다.

// - launch()는 새 코루틴을 시작하고, 결과를 return하지 않는다.
// "fire and forget" 방식의 작업은 launch()를 사용해 시작할 수 있다.

// - async는 새 코루틴을 시작하고 await이라는 suspend 함수와 함께 결과를 return한다.

// 일반적으로, 일반 함수는 await을 call할 수 없기 때문에, 일반 함수에서는 launch()를 사용해 새 코루틴을 시작해야 한다.
// async는 다른 코루틴 내부에서만 사용하거나 suspend 함수 내에서 병렬 분해(?)parallel decomposition를 실행할 때 사용합니다.

// 주의 : launch()와 async에서는 예외를 다르게 처리한다.
// async는 최종적으로 await의 호출을 기대하기 때문에 예외를 hold했다가 await 호출의 일부로 다시 발생rethrow시킨다.
// 일반적인 함수에서 새 코루틴을 시작하려고 anync를 사용하면 exception은 조용히 drop된다.
// drop된 exception은 crash 분석도구나 logcat에도 나타나지 않는다.
// 자세한 내용은 'Cancellation and Exceptions in Coroutines' 문서를 참고

// 병렬 분해 Parallel decomposition
// suspend 함수 내에서 시작한 모든 코루틴은 함수가 return하면 반드시 stop되어야 한다.
// 그러니 return 전에 코루틴이 끝나도록 확실히 할 필요가 있다.
// Kotlin의 structured concurrency를 사용하면 하나 이상의 코루틴을 시작할 수 있는 coroutineScope를 정의할 수 있다.
// 그러면 await()(코루틴이 하나일 때) 또는 awaitAll()(코루틴이 여럿일 때)을 사용해
// 함수의 return 전에 코루틴이 모두 끝나도록 보장할 수 있다.

// 예를 들어 두 개의 문서를 비동기로 가져오는 coroutineScope를 정의해보자.
// 각 Deferred 참조변수에 대해 await()을 호출해 두 async 작업이 값을 return하기 전에 끝나도록 보장할 수 있다.

suspend fun fetchTwoDocs() =
    coroutineScope {
        val deferredOne = async { fetchDoc(1) }
        val deferredTwo = async { fetchDoc(2) }
        deferredOne.await()
        deferredTwo.await()
    }

// 아래 예와 같이 컬렉션에 awaitAll()을 사용할 수도 있다.

suspend fun fetchTwoDocs() =        // called on any Dispatcher (any thread, possibly Main)
    coroutineScope {
        val deferreds = listOf(     // fetch two docs at the same time
            async { fetchDoc(1) },  // async returns a result for the first doc
            async { fetchDoc(2) }   // async returns a result for the second doc
        )
        deferreds.awaitAll()        // use awaitAll to wait for both network requests
    }

// fetchTwoDocs()가 anync를 사용해 새 코루틴을 시작하더라도,
// 함수는 awaitAll()을 사용해 코루틴들이 끝나는 것을 기다렸다가 return한다.
// 하지만 awaitAll()을 사용하지 않더라도 coroutineScope builder는 모든 새 코루틴들이 완료될 때까지
// fetchTwoDocs()를 호출한 코루틴을 재개resume하지 않는다.
// (awaitAll()이나 await()을 생략해도 무방하다는 것인가???)

// 또한 coroutineScope는 코루틴들에서 발생한 모든 exception들을 잡아두었다가 호출자에게 돌려준다.

// parallel decomposition에 대한 자세한 정보는 'Composing suspending functions' 문서를 참고


// Coroutine concepts

// CoroutineScope
// CoroutineScope는 launch나 async를 이용해 만든 모든 코루틴을 추적한다.
// 실행중인 작업, 즉 실행중인 코루틴은 언제든 scope.cancel()을 호출해 취소시킬 수 있다.
// 안드로이드의 일부 KTX 라이브러리는 특정 lifecycle 클래스들을 위한 자체적인 CoroutineScope를 제공한다.
// 예를 들어, ViewModel의 viewModelScope, Lifecycle의 lifecycleScope가 있다.
// 하지만 Dispatcher와 달리 CoroutineScope은 코루틴을 실행하지 않는다.

// Note : viewModelScope에 대한 자세한 정보는 'Easy Coroutines in Android: viewModelScope' 참고
// viewModelScope는 'Background threading on Android with Coroutines'의 예제에서도 사용된다.

// 앱의 특정 레이어에 있는 코루틴의 lifecycle을 제어하기 위하여 자체적인 CoroutineScope을 만들어야 한다면 다음과 같이 만들 수 있다.

class ExampleClass {

    // Job and Dispatcher are combined into a CoroutineContext which will be discussed shortly
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    fun exampleMethod() {
        // Starts a new coroutine within the scope
        scope.launch {
            // New coroutine that can call suspend functions
            fetchDocs()
        }
    }

    fun cleanUp() {
        // Cancel the scope to cancel ongoing coroutines work
        scope.cancel()
    }
}

// 취소된 scope은 코루틴을 더 만들 수 없다. 그러므로 lifecycle을 제어할 수 있는 클래스가 destroy될 때만 scope.cancel()을 호출해야 한다.
// viewModelScope을 사용할 땐, ViewModel 클래스가 onCleared() 메소드 안에서 자동으로 scope을 cancel해준다.


// Job

// Job은 코루틴의 handle(?)이다.
// launch나 async로 생성한 각 코루틴은 그 코루틴을 고유하게 식별하고 그 lifecycle을 다룰 수 있는 Job 인스턴스를 return한다.
// 다음 예처럼, Job을 CoroutineScope로 전달하여 코루틴의 lifecycle을 추가로 다룰 수도 있다.

class ExampleClass {
    ...
    fun exampleMethod() {
        // Handle to the coroutine, you can control its lifecycle
        val job = scope.launch {
            // New coroutine
        }

        if (...) {
            // Cancel the coroutine started above, this doesn't affect the scope
            // this coroutine was launched in   //설명이 이해되지 않네..
            job.cancel()
        }
    }
}

//CoroutineContext

// CoroutineContext는 다음 요소들을 사용해 코루틴의 동작을 정의한다.
// - Job : 코루틴의 lifecycle을 제어한다.
// - CoroutineDispatcher : 적절한 스레드에 작업을 보낸다
// - CoroutineName : 코루틴의 이름. 디버깅에 유용하다.
// - CoroutineExceptionHandler : uncaught exception을 다룬다.

// scope 안에서 만들어진 새 코루틴에 대해 새 Job 인스턴스가 할당되고,
// 포함된 scope로부터 다른 CoroutineContext 요소들이 상속된다.
// 새 CoroutineContext를 launch 또는 async 함수에 전달하여 상속된 요소들을 오버라이드 할 수 있다.
// Job은 launch 또는 async에 전달해도 아무 효과가 없다. Job의 새 인스턴스는 항상 새 코루틴에 할당되기 때문이다.

class ExampleClass {
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    fun exampleMethod() {
        // Starts a new coroutine on Dispatchers.Main as it's the scope's default
        val job1 = scope.launch {
            // New coroutine with CoroutineName = "coroutine" (default)
        }

        // Starts a new coroutine on Dispatchers.Default
        val job2 = scope.launch(Dispatchers.Default + "BackgroundCoroutine") {
            // New coroutine with CoroutineName = "BackgroundCoroutine" (overridden)
        }
    }
}

Note: For more information about CoroutineExceptionHandler, see exceptions in coroutines blog post.


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}