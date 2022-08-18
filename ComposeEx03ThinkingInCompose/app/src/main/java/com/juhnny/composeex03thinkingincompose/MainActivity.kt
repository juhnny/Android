package com.juhnny.composeex03thinkingincompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juhnny.composeex03thinkingincompose.ui.theme.ComposeEx03ThinkingInComposeTheme

// Thinking in Compose
// https://developer.android.com/jetpack/compose/mental-model?continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fcompose%23article-https%3A%2F%2Fdeveloper.android.com%2Fjetpack%2Fcompose%2Fmental-model,

// The declarative programming paradigm
// Compose는 선언적 API를 제공하여 명령형으로 View들을 수정하지 않고 앱의 UI를 render할 수 있게 함
// 역사적으로 안드로이드의 뷰 계층은 UI widget들의 계층으로 표현될 수 있었다.
// 유저 상호작용으로 앱의 state가 변함에 따라 UI 계층은 현재의 데이터를 표현하기 위해 업데이트돼야 한다.
// 가장 일반적인 UI 업데이트 방법은 findViewById() 같은 방식으로 트리에 접근해서
// button.setText(String), container.addChild(View), img.setImageBitmap(Bitmap) 같은 메소드들을 호출해
// 노드들을 변경하는 것이다. 이런 메소드들은 위젯의 내부 state를 변경한다.

// 뷰를 수동으로 조작하는 것은 오류 가능성을 높인다. 하나의 데이터가 여러 곳에서 렌더되고 있다면 그 뷰들 중 하나의 업데이트를 잊어버리기 쉽다.
// 두 업데이트가 예상치 못한 방식으로 충돌해서 부적절한 state를 만드는 경우도 있다.
// 예를 들어, 한 업데이트에서 값을 바꾸려 하는 노드가 다른 업데이트로 인해 방금 막 UI에서 제거됐을 수도 있다.
// 일반적으로, 소프트웨어 유지보수의 복잡성은 업데이트가 요구되는 뷰들의 수와 함께 증가한다.

// 전체 화면을 재생성하는 데 있어서의 챌린지는 그게 시간, 성능, 배터리 소모에 있어 잠재적으로 비싸다는 것이다.
// 이런 비용을 완화시키기 위해, Compose는 UI의 어느 부분이 다시 그려져야 하는지 선택한다.
// 이는, 아래 Recomposition에서 논의된 것처럼, 당신이 어떻게 UI component를 디자인해야 하는지를 알려준다.


// A simple composable function
// Compose에서는 데이터를 받아서 UI 요소를 emit하는 Composable 함수를 정의해 당신만의 UI를 만들 수 있다.

@Composable
fun Greeting(name: String){
    Text("Hello $name")
}

// 이 함수에 대해 알아둘 점들
// 1. 모든 Composable function은 @Composable annotation이 필요하다.
// 이 어노테이션은 Compose 컴파일러에게 이 함수가 데이터를 UI로 변환하는 목적으로 만들어졌음을 알려준다.
// 2. 이 함수는 데이터 입력을 받는다. Composable function은 파라미터를 받아들일 수 있으므로 app의 logic이 UI를 변경할 수 있게 한다.
// 위 위젯의 경우 String을 받아들여 유저를 이름으로써 환영할 수 있게 해준다.
// 3. 이 함수는 Text() 컴포저블 함수를 호출해서 UI에 문자열을 표현한다. 컴포저블 함수는 다른 컴포저블 함수를 호출해 UI 계층을 emit한다.
// 4. 이 함수는 아무것도 return하지 않는다. Compose function은 UI를 emit하지만 아무것도 return할 필요가 없다.
// because they describe the desired screen state instead of constructing UI widgets. (이해가 되지 않네...)
// screen state라는 값을 바꿀 뿐 UI widget을 진짜로 만들진 않는다는 걸까?
// 5. 빠르고, 멱등하고(idempotent), side-effect-free 하다.
// - 같은 인자에 대해 여러번 호출되더라도 같은 방식으로 동작한다. 전역변수나 random() 값 같은 다른 값들을 사용하지 않는다.
// - 프로퍼티나 전역변수를 수정하는 등의 side-effect 없이 UI를 묘사한다.

// *idempotence 멱등성
// 명령형 프로그래밍에서는 서브루틴을 여러번 호출했을 때의 system state에 대한 side effect가 한번 호출했을 때와 동일함을 의미.
// 함수형 프로그래밍에서는 수학적으로 멱등한 것을 의미
// 멱등성은 여러 경우에 유용하다. 멱등하다는 것은 그 operation을 필요한 만큼 자주, 의도치 않은 effect 없이 반복할 수 있다는 것.
// 멱등하지 않은 operation을 사용하는 알고리즘에서는 그 operation이 이미 실행된 적이 있는지 없는지 계속 추적해야 한다.
// 예시로는 DB에서 값을 읽어오는 GET 작업이 있다. DB에 대해 어떤 수정도 가하지 않기 때문.
// 고객의 주소를 "ABC"로 바꾸는 요청도 마찬가지. 한번 실행하나 여러번 실행하나 결과가 같기 때문.
// 상품을 주문하는 요청은 멱등하지 않다. 계속 새 주문이 더해질 테니까.
// 특정 주문을 취소하는 요청은 멱등하나. 한번 취소하나 여러번 취소하나 그 주문은 똑같이 취소된 상태일 테니까.


// The declarative paradigm shift
// 명령형 객체지향 UI toolkit들에서는 widget들의 tree를 인스턴스화해서 UI를 초기화한다.
// 당신은 이를 위해 때때로 XML layout 파일을 inflate할 것이다.
// 각각의 위젯들은 각자의 internal state를 유지하고, app logic이 상호작용할 수 있도록 getter, setter 메소드를 노출한다.

// Compose의 선언형 접근법에서 위젯들은 상대적으로 stateless하고 getter, setter 메소드를 노출하지 않는다.
// UI를 업데이트할 때는 같은 composable function을 다른 인수와 함꼐 호출한다.
// 이는 ViewModel 같은 architectural pattern들에 state를 제공하는 것을 더 쉽게 한다. (Guide to app architecture 참고 - https://developer.android.com/jetpack/guide)
// 이렇게 하면 composable들은 observable data들이 업데이트될 때마다 현재의 application state를 UI로 변환하는 책임을 갖게 된다.

// Figure2
// app logic은 상위레벨 composable function에 data를 제공한다.
// 그 함수는 그 데이터를 이용해 다른 composable들을 호출해서 UI를 묘사하고,
// 그 composable들에 적절한 데이터를 전달하고, 계층을 따라 반복한다.

// Figure3
// 유저가 UI와 상호작용하면, UI는 onClick 같은 이벤트를 발생시킨다.
// 이벤트들은 app logic에 전달되어야 하고, 그러면 app logic은 app의 state를 바꿀 수 있다.
// state가 변경되면, composable function들이 자동으로 새 데이터와 함께 호출된다. 이것이 UI 요소들이 다시 그려지는 원인이다.
// 이런 과정을 Recomposition이라고 부른다.


// Dynamic content
// composable function들은 XML 대신 Kotlin으로 쓰여 있으므로 여느 코틀린 코드들처럼 dynamic하다.
// 예를 들어, Greeting을 통해 한 리스트의 유저를 보여주는 UI를 만든다고 하자.

@Composable
fun Greeting(names: List<String>) {
    for (name in names) {
        Text("Hello $name")
    }
}

// 당신은 if문을 써서 특정 UI 요소만 보여줄 수도 있다. 반복문을 쓸 수도 있다. 다른 helper 함수를 부를 수도 있다.
// 당신은 그 언어의 유연성을 충분히 활용할 수 있다. 이 파워와 유연성이 Compose의 장점이다.


// Recomposition
// 명령형 UI 모델에서는 위젯을 변경하려면 그 위젯의 internal state을 변경하기 위해 setter 함수를 호출한다.
// Compose에서는 composable function을 새로운 데이터와 함께 호출한다.
// 이는 그 함수가 recomposed 되게 한다. - 그 함수가 emit한 위젯들은 (필요에 따라 새 데이터를 활용해) redrawn 된다.
// Compose framework는 변화된 component들만 지능적으로 recompose 한다.

// 예를 들어, 여기 버튼을 보여주는 composable function이 있다.
@Composable
fun ClickCounter(clicks: Int, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("Clicked $clicks times")
    }
}
// 버튼이 클릭될 때마다 호출자는 clicks의 값을 업데이트한다.
// 그러면 Compose는 Text 함수와 함께 onClick 함수를 호출한다.
// 이 과정을 recomposition이라 부른다.
// 이 값에 의존하지 않는 다른 함수들은 recompose되지 않는다.

// 위에서 말했던 것처럼, UI tree 전체를 recompose하는 건 값 비싼 작업니다.
// Compose는 이 문제를 intelligent recomposition으로 해결한다.

// Recomposition은 input이 달라진 composable function들을 다시 호출하는 것이다.
// 새로운 input들에 기반하여, Compose는 변화됐을 수 있는 composable 함수들만 recompose 한다.
// 파라미터들이 변하지 않은 composable 함수들은 skip함으로써 효율적으로 recompose할 수 있다.

// composable 함수의 recomposition이 skip될 수 있으므로, 함수가 실행됐을 때의 side effect에 의존하지 말라.
// 그럴 경우 유저는 예상치 못한 이상한 동작을 경험할 수도 있다.
// A side-effect is any change that is visible to the rest of your app.
// 예를 들어, 이런 action들은 모두 위험한 side effect들이다.
// - 공유된 객체의 프로퍼티에 대한 write
// - ViewModel의 observable에 대한 update
// - shared preference의 update

// animation이 render될 때처럼, composable function은 매 프레임마다 재실행될 수도 있다.
// 그러므로 형편없는 애니메이션을 피하려면 이 함수들은 빨라야 한다.
// 비싼 operation을 필요로 할 때(shared preferences를 읽을 때처럼)는
// 백그라운드에서 coroutine으로 하고 그 결과값을 composable function에 파라미터로 전달해라.

// 예를 들어, 아래 코드는 SharedPreferences의 값을 업데이트하기 위한 composable을 만들어낸다.
// 이 composable은 자체적으로 shared preference를 읽거나 쓰면 안 된다.
// Instead, this code moves the read and write to a ViewModel in a background coroutine.
// The app logic passes the current value with a callback to trigger an update.

// - Composable 함수는 순서에 상관없이 실행될 수 있다.
// - Composable 함수들은 병렬적parallel으로 실행될 수 있다.
// - Recomposition은 가능한 한 많은 함수들을 skip한다.
// - Recomposition is optimistic and may be canceled.
// - Composable 함수는 애니메이션의 매 프레임만큼 자주 실행될 수 있다.

// 다음 섹션에서는 어떻게 recomposition을 고려하여 composable function을 작성하는지 다룬다.
// 어떤 경우에도, 최선의 방법은 composable function을 fast, idempotent, and side-effect free 하게 유지하는 것이다.

// 1. Composable functions can execute in any order
// composable function을 보면서 코드들이 순서대로 실행될 거라고 생각할 수도 있지만 언제나 참은 아니다.
// composable function 안에 다른 composable function 호출이 포함돼있을 때, 그 함수들은 어떤 순서대로든 실행될 수 있다.
// Compose에는 특정 UI 요소가 다른 요소들보다 우선순위에 있는지, 먼저 그려지는지를 인식할 수 있는 옵션이 있다.

// 예를 들어 다음처럼 tab layout에서 세 screen을 그리는 경우를 가정해보자.
//@Composable
//fun ButtonRow() {
//    MyFancyNavigation {
//        StartScreen()
//        MiddleScreen()
//        EndScreen()
//    }
//}
// StartScreen, MiddleScreen, EndScreen에 대한 호출은 어느 순서로든 일어날 수 있다.
// 다시 말해 StartScreen()에서 어떤 전역변수를 set하고 MiddleScreen()에서 그 바뀌어진 변수 값을 사용하는 방식으로 쓰면 안된다는 것이다.
// 각각의 composable function들은 독립적self-contained이어야 한다.

// 2. Composable functions can run in parallel
// Compose에서는 composable function들을 병렬적으로 실행해서 recomposition을 최적화한다.
// 이는 멀티 코어의 이점을 활용할 수 있도록 하고, 화면에 나오지 않는 composable function(이하 CF라고 하자. 너무 길다ㅜㅜ)을 낮은 우선순위로 실행할 수 있게 해준다.

// This optimization means a composable function might execute within a pool of background threads.
// 이는 한 CF가 백그라운드 스레드 풀에서 실행될 수 있음을 의미한다.
// 어느 CF에서 ViewModel의 어느 함수를 실행하게 돼있다면, Compose는 그 함수를 여러 스레드에서 동시에 호출할 수도 있다.

// 앱이 올바르게 동작하게 하려면, 모든 CF들은 side effect가 없어야 한다.
// side effect들은 callback(onClick 같이 언제나 UI thread에서 실행되는)에서 trigger해라.
// (하나의 스레드에서만 실행되면 순서대로 실행될테니까 그런 듯)

// CF가 호출될 때, 이 호출은 다른 스레드에서 일어난 것일 수도 있다. (이미 한번 호출된 상황을 가정한 건가?)
// 따라서 composable lambda(?) 안에 있는 변수를 수정하는 코드는 두가지 이유에서 피해야 한다.
// 첫째로 그런 코드는 thread-safe하지 않기 때문이고,
// 두번째로 그것은 composable lambda의 허용할 수 없는 side effect이기 때문이다.(?)

// 아래 예시는 한 리스트와 그 길이를 보여주는 CF다.
@Composable
fun ListComposable(myList: List<String>) {
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        Column() {
            for (item in myList) {
                Text("Item: $item")
            }
        }
        Text("Count: ${myList.size}")
    }
}
// 이 코드는 side-effect free 하고 입력된 list를 UI로 변환한다. 소량의 리스트를 보여주기 위한 좋은 코드다.
// 그러나 지역변수를 사용하게 되면 thread-safe하지 않거나 옳지 않은 코드가 될 것이다.
@Composable
fun ListWithBug(myList: List<String>) {
    var items = 0
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        Column() {
            for (item in myList) {
                Text("Item: $item")
                items++ // Avoid! Side-effect of the column recomposing.
            }
        }
        Text("Count: $items")
    }
}
// In this example, items is modified with every recomposition.
// That could be every frame of an animation, or when the list updates.
// Either way, the UI will display the wrong count.
// Because of this, writes like this are not supported in Compose;
// by prohibiting those writes, we allow the framework to change threads to execute composable lambdas.

// 돌려보면 에러는 안 난다. 아마 items가 state가 아니기 때문에 실제로는 recompose하지 않는데 그냥 예시로 그렇게 한 듯
// 이게 튜토리얼 바로 다음에 나오는 초급 문서이니까.

// 3. Recomposition skips as much as possible
// When portions of your UI are invalid(값이 바뀌었을 때를 말하는 듯),
// Compose does its best to recompose just the portions that need to be updated.
// This means it may skip to re-run a single Button's composable without executing any of the composables above or below it in the UI tree.

// Every composable function and lambda might recompose by itself. 
// Here's an example that demonstrates how recomposition can skip some elements when rendering a list:

// Display a list of names the user can click with a header
@Composable
fun NamePicker(
    header: String,
    names: List<String>,
    onNameClicked: (String) -> Unit
) {
    //아, lambda가 이 Column()의 중괄호 영역 말하는 거구나!
    Column {
        // this will recompose when [header] changes, but not when [names] changes
        Text(text = header, style = MaterialTheme.typography.h5)
        Divider()

        // LazyColumn is the Compose version of a RecyclerView.
        // The lambda passed to items() is similar to a RecyclerView.ViewHolder.
        LazyColumn {
            items(names){ name ->
                // When an item's [name] updates, the adapter for that item will recompose.
                // This will not recompose when [header] changes
                NamePickerItem(name, onNameClicked)
            }
        }
    }
}

//Display a single name the user can click
@Composable
private fun NamePickerItem(name: String, onClicked: (String) -> Unit) {
    Text(name, Modifier.clickable( onClick = {onClicked(name)} ))
//    Text(name, Modifier.clickable( onClick = onClicked(name) )) //이건 안되고 위에는 되네. 이건 실행문이고 위는 익명 함수인가?
}

// recomposition 시 각각의 scope만이 실행된다.
// header 값이 바뀌면 Compose는 Column의 lambda(중괄호)만 실행한다. Column의 어떤 부모도 실행하지 않고 skip한다.
// Column이 실행될 때, names가 바뀌지 않았다면 LazyColumn 영역도 skip된다.

// 다시 한번 말하지만 모든 CF와 lambda는 side-effect free여야 한다.
// side-effect를 행해야 할 때는 callback에서 트리거되도록 해라.

// 4. Recomposition is optimistic
// Recomposition starts whenever Compose thinks that the parameters of a composable might have changed.
// Recomposition is 'optimistic', which means Compose expects to finish recomposition before the parameters change again.
// If a parameter does change before recomposition finishes, Compose might cancel the recomposition
// and restart it with the new parameter.
// (값이 바뀌었어? 아직 작업중이었는데.. 그럼 새 값으로 다시 하지 뭐.. 이런 낙관인가)

// When recomposition is canceled, Compose discards the UI tree from the recomposition.
// If you have any side-effects that depend on the UI being displayed,
// the side-effect will be applied even if composition is canceled. This can lead to inconsistent app state.

// Ensure that all composable functions and lambdas are idempotent and side-effect free to handle optimistic recomposition.

// 5. Composable functions might run quite frequently
// In some cases, a composable function might run for every frame of a UI animation.
// If the function performs expensive operations, like reading from device storage,
// the function can cause UI jank.
//
// For example, if your widget tried to read device settings,
// it could potentially read those settings hundreds of times a second,
// with disastrous effects on your app's performance.
//
// If your composable function needs data, it should define parameters for the data.
// CF에 데이터가 필요하다면 그 데이터를 위한 파라미터를 정의해야 한다 - 잊지 말 것!
// 무거운 작업은 UI 구성 밖으로 빼서, 별도 스레드로 옮기고
// mutableStateOf나 LiveData를 사용해서 Compose로 데이터를 전달해라


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
            ) {
//                ListComposable(myList = listOf("Apple", "Banana", "Carrot"))
//                ListWithBug(myList = listOf("Apple", "Banana", "Carrot"))
                NamePicker(
                    header = "Header is..",
                    names = listOf("Hong", "Kim", "Choi"),
                    onNameClicked = {str -> Log.e("LOG", "$str")}
                )
            }
        }
    }
}