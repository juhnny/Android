package com.juhnny.coroutinesex02introduction2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

// Introduction to coroutines (document)
// https://developer.android.com/kotlin/coroutines

// A coroutine is a concurrency design pattern that you can use on Android to simplify code that executes asynchronously.

// 기능
// - Lightweight : 코루틴을 실행 중인 스레드를 차단(block)하지 않는 정지(suspension)를 지원하므로 싱글 스레드에서 여러 코루틴을 실행할 수 있습니다.
// - Fewer memory leaks : scope 안에서 작업을 실행하기 위해 'structured concurrency'를 사용합니다.
// - Built-in cancellation support : 실행 중인 코루틴 계층 구조를 따라 자동으로 Cancellation이 전달됩니다.
// - Jetpack integration : 많은 Jetpack 라이브러리에 코루틴을 완전히 지원하는 확장 프로그램extension이 포함되어 있습니다.
//                          일부 라이브러리는 structured concurrency를 위해 사용할 수 있는 자체적인 coroutine scope도 제공합니다.

// 예시 개요
// 이 주제의 예제에서는 앱 아키텍쳐 가이드(https://developer.android.com/jetpack/docs/guide)에 따라
// 네트워크 요청을 보내고 result를 main thread로 리턴합니다. 그러면 앱에서 결과를 유저에게 표시할 수 있습니다.

// 특히 ViewModel 아키텍처 컴포넌트는 메인 스레드의 repository 레이어를 호출하여 네트워크 요청을 트리거한다.
// 이 가이드에서는 코루틴을 사용하는 다양한 솔루션을 반복해 메인 스레드를 block 없이 유지한다.

// ViewModel에는 코루틴과 직접 연동되는 KTX(KotlinX인듯) 확장 프로그램 모음(lifecycle-viewmodel-ktx 라이브러리로 이 가이드에서 사용함)이 포함된다.

// Android 프로젝트에서 코루틴을 사용하려면 앱의 build.gradle 파일에 다음 종속 항목 추가
//dependencies {
//    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9'
//}

// 백그라운드 스레드에서 실행하기
// 네트워크 요청을 메인 스레드에서 보내면 응답을 받을 때까지 스레드가 대기하거나 블락된다.
// 스레드가 블락돼있으므로 OS는 onDraw()를 호출할 수 없고, 이로 인해 앱이 freeze되고 ANR(Application not responding) 대화상자가 표시될 수 있습니다.
// 더 나은 유저 경험을 위해 이 작업을 백그라운드 스레드에서 실행해보자.

// 첫째, 우리의 Repository class를 보고 네트워크 요청을 어떻게 보내고 있는지 확인해보자.

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

class LoginRepository(private val responseParser: LoginResponseParser) {
    private const val loginUrl = "https://example.com/login"

    // Function that makes the network request, blocking the current thread
    fun makeLoginRequest(jsonBody: String): Result<LoginResponse> {
        val url = URL(loginUrl)
        (url.openConnection() as? HttpURLConnection)?.run {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json; utf-8")
            setRequestProperty("Accept", "application/json")
            doOutput = true
            outputStream.write(jsonBody.toByteArray())
            return Result.Success(responseParser.parse(inputStream))
        }
        return Result.Error(Exception("Cannot open HttpURLConnection"))
    }
}

// makeLoginRequest()는 동기식이며 호출 스레드를 차단한다.
// 네트워크 요청의 응답을 model 하기 위해 자체적인 Result 클래스를 사용한다.

// ViewModel은 유저가, 예를 들어, 버튼을 클릭하면 네트워크 요청(login 함수)을 트리거한다.

class LoginViewModel(
    private val loginRepository: LoginRepository
): ViewModel() {
    fun login(username: String, token: String) {
        val jsonBody = "{ username: \"$username\", token: \"$token\"}"
        loginRepository.makeLoginRequest(jsonBody)
    }
}
// 이전 코드에서 LoginViewModel은 네트워크 요청을 보낼 때 UI 스레드를 차단한다.
// 이 실행을 메인 스레드 외부로 이동시키는 가장 간단한 방법은 새 코루틴을 만들고 I/O 스레드에서 네트워크 요청을 실행하는 것이다.

class LoginViewModel(
    private val loginRepository: LoginRepository
): ViewModel() {

    fun login(username: String, token: String) {
        // Create a new coroutine to move the execution off the UI thread
        viewModelScope.launch(Dispatchers.IO) {
            val jsonBody = "{ username: \"$username\", token: \"$token\"}"
            loginRepository.makeLoginRequest(jsonBody)
        }
    }
}

// login 함수의 코루틴 코드를 해부해보자
// - viewModelScope는 ViewModel KTX extensions에 포함된 사전 정의된 CoroutineScope이다.
//   모든 코루틴은 scope 안에서 실행돼야 한다. CoroutineScope은 하나 이상의 관련된 코루틴을 다룬다.
// - launch()는 코루틴을 생성하고 함수 본문의 실행을 해당하는 dispatcher에 배분하는 함수다.
// - Dispatchers.IO는 이 코루틴이 I/O 작업용으로 예약된 스레드에서 실행돼야 함을 나타낸다.

// login()함수는 다음과 같이 실행된다.
// - 앱이 메인 스레드의 View 레이어에서 login() 함수를 호출한다.
// - launch()는 새 코루틴을 만들고, I/O 작업을 위해 예약된 스레드에서 독립적으로 네트워크 요청이 보내진다.
// - 코루틴이 실행되는 동안, 네트워크 요청이 완료되기 전에 login 함수가 계속 실행되어 결과를 반환한다.
//   편의를 위해 지금은 네트워크 응답이 무시된다.

// 이 코루틴은 viewModelScope와 함께 시작되므로, ViewModel의 스코프 안에서 실행된다.
// 만약 유저가 해당 화면 밖으로 이동하여 ViewModel이 destroyed되는 경우,
// viewModelScope는 자동으로 cancel되고, 실행 중인 모든 코루틴도 cancel된다.

// 위 예에서 한가지 문제는, makeLoginRequest()를 호출하는 모든 항목이 이 작업을 메인 스레드 밖으로 명시적으로 옮겨야 한다는 걸 기억해야 한다는 것이다.
// (별도 스레드를 쓰도록 매번 기억하고 작업해줘야 한다는 뜻인 듯. 번거롭고 까먹을 수도 있으니 문제)
// 이 문제를 해결하기 위해 Repository를 수정하는 방법을 알아보자


// main-safety를 위해 코루틴 사용하기
// 함수가 메인 스레드의 UI 업데이트를 블락하지 않으면 main-safe하다고 한다.
// 위 makeLoginRequest() 함수는 main-safe하지 않다. 이 함수를 메인 스레드에서 호출하면 UI를 블락하기 때문에.

class LoginRepository(...) {
    ...
    suspend fun makeLoginRequest(
        jsonBody: String
    ): Result<LoginResponse> {
        // Move the execution of the coroutine to the I/O dispatcher
        return withContext(Dispatchers.IO) {
            // Blocking network request code
        }
    }
}
// 코루틴 라이브러리의 withContext() 함수를 사용해 코루틴의 실행을 다른 스레드로 옮긴다.
// 이를 통해 이 함수의 호출을 main-safe하게 만든다.

// makeLoginRequest() 함수에는 또한 suspend 키워드가 표시돼있다.
// suspend 키워드는 코틀린에서 그 함수를 코루틴 안으로부터 호출되도록 강제하는 방법이다.

// Note: 테스트를 쉽게 하기 위해 Dispatcher를 Repository 레이어에 삽입하길 추천한다.

// 다음 예에서는 LoginViewModel 안에 코루틴이 만들어진다.
// makeLoginRequest에서 실행을 메인 스레드 외부로 옮기므로 login() 함수 안의 코루틴을 이제 메인스레드에서 실행할 수 있다.

class LoginViewModel(
    private val loginRepository: LoginRepository
): ViewModel() {
    // 예를 들어 버튼을 누르면 login()을 실행할 거라고 가정
    fun login(username: String, token: String) {

        // Create a new coroutine on the UI thread
        viewModelScope.launch {
            val jsonBody = "{ username: \"$username\", token: \"$token\"}"

            // Make the network call and suspend execution until it finishes
            val result = loginRepository.makeLoginRequest(jsonBody)

            // Display result of the network request to the user
            when (result) {
                is Result.Success<LoginResponse> -> // Happy path
                else -> // Show error in UI
            }
        }
    }
}

// makeLoginRequest()가 suspend 함수이고, 모든 suspend 함수는 코루틴 안에서 실행되어야 하므로, 여기에서도 여전히 코루틴이 필요하다.

// 앞선 login 예제와 두가지 점에서 차이가 있다.
// - launch()에서 Dispatchers.IO를 파라미터로 받지 않는다.
//   launch()에 Dispatcher를 넘기지 않으면 viewModelScope에서 launch된 모든 코루틴은 메인 스레드에서 실행된다.
// - 네트워크 요청의 결과는 이제 성공/실패 UI를 보여주도록 다뤄진다.

// login() 함수는 이제 다음과 같이 실행된다.
// - 앱은 메인 스레드의 View 레이어에서 login()을 호출한다.
// - launch()가 메인 스레드에서 네트워크 요청을 보내기 위해 새 코루틴을 만들고 코루틴이 실행을 시작한다.
// - 코루틴 안의 loginRepository.makeLoginRequest() 호출은 이제
//   makeLoginRequest() 안의 withContext() 블록이 실행을 마칠 때까지 코루틴의 더 이상의 실행을 suspend한다.
// - withContext() 블록이 끝나면, login() 내부의 코루틴이 메인 스레드에서 네트워크 요청의 결과와 함께 실행을 재개한다.

// Note: ViewModel 레이어에서 View와 통신하기 위해 앱 아키텍처 가이드에서 권하는 대로 LiveData를 사용한다.
// 이 패턴을 따를 땐, ViewModel의 코드가 메인 스레드에서 실행되므로 MutableLiveData의 setValue() 함수를 직접 호출할 수 있다.


// 예외 처리

// Repository 레이어에서 발생할 수 있는 예외를 처리하기 위해 코틀린의 예외처리 내장 api를 사용한다.
// 다음 예에서는 try-catch 블록을 사용한다.

class LoginViewModel(
    private val loginRepository: LoginRepository
): ViewModel() {

    fun makeLoginRequest(username: String, token: String) {
        viewModelScope.launch {
            val jsonBody = "{ username: \"$username\", token: \"$token\"}"
            val result = try {
                loginRepository.makeLoginRequest(jsonBody)
            } catch(e: Exception) {
                Result.Error(Exception("Network request failed"))
            }
            when (result) {
                is Result.Success<LoginResponse> -> // Happy path
                else -> // Show error in UI
                // 이 예에서는 makeLoginRequest() 호출에서 발생한 모든 예기치 못한 예외는 UI에서 에러로 다루는 것으로 가정
            }
        }
    }
}


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}