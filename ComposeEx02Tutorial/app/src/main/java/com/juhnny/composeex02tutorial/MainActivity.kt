package com.juhnny.composeex02tutorial

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juhnny.composeex02tutorial.ui.theme.ComposeEx02TutorialTheme

// Jetpack Compose Tutorial 따라하기
// https://developer.android.com/jetpack/compose/tutorial

// Compose에서는 XML Layout이나 Layout Editor를 사용하는 일 없이
// composable function을 호출하면 Compose compiler가 알아서 한다.

// 1. Composable function
// 함수명 위에 @Composable annotation만 붙이면 Composable function
// Jetpack Compose는 Composable function을 중심으로 설계됨
// UI가 어떻게 보여져야 하고 data와의 의존관계는 어떠한지를 더 programmatically 정의할 수 있게 해줌

// Composable function은 다른 composable function 안에서만 호출될 수 있다.
// 그리고 composable fucntion은 setContent 블록 안에서 호출될 수 있다.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            Text("Hello World!")
            //사용자 정의 Composable function
//            MessageCard(name = "Olaf")
            //그냥 호출하니 위 Text 컴포넌트(?)랑 겹쳐서 보여진다.

//            MessageCard2(msg = Message("Anna", "Where are you?"))

            // Theme을 이용해 composable들이 일관된 스타일을 갖게 할 수 있음
            // ui.theme 패키지에 있는 Theme.kt에 있는 Theme 함수를 이용
            // Surface는 무슨 차이가 있지?
            // Surface는 컨테이너로써의 역할. 마치 div 태그
//            ComposeEx02TutorialTheme{
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
////                    color = Color.LightGray, // 마지막 콤마 찍어도 문제 없네
//                ){
//                    MessageCard2(msg = Message("Anna", "Where are you?"))
//                }
//            }

            ComposeEx02TutorialTheme() {
                Conversation(messages = SampleData.conversationSample)
            }
        }
    }

    @Composable
    fun MessageCard(name: String){
        Text(text = name)
    }

    // @Preview annotation
    // @Preview annatation은 composable function을 디바이스나 에뮬레이터에 빌드/설치할 필요 없이 Android Studio에서 볼 수 있게 해줌
    // 파라미터가 없는 Composable function에만 사용 가능.
    // 위에서 만든 MessageCard 함수는 파라미터가 있으므로 별도 함수를 만들어 적용해야 한다.

    @Preview
    @Composable
    fun PreviewMessageCard(){
        MessageCard(name = "Olaf!") //오 내용을 변경하면 즉각 보여지네! Hot reload처럼
        //literal은 Live Edit이 가능하지만 구성이 바뀌면 build & refresh 필요
    }
    
    // 2. Layouts
    // composable function 안에서 다른 composable function을 호출해 계층을 구성한다.

    // Composable을 꾸미거나 설정하기 위해서는 인수로 Modifier를 전달한다.
    // Modifier는 스타일, 스크롤 동작 등 요소에 추가적인 속성을 부여

    data class Message(val author : String, val body : String)

    @Composable
    fun MessageCard2(msg : Message){
        // Composable 요소들을 그냥 여러 개 호출하면 레이아웃에 대한 정보가 없으므로 겹쳐서 표현됨
        // Row 함수를 쓰면 가로 방향으로 배열
        Row(modifier = Modifier.padding(all = 8.dp)) {
            Image(
                painter = painterResource(id = R.drawable.profile_img),
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colors.secondary, CircleShape)
            )
            // 수평 space 추가
            Spacer(modifier = Modifier.width(8.dp))

            // Composable의 state (설명 하단에 추가)
            // by를 사용하기 위해 getValue, setValue import 필요
            var isExpanded by remember() {
                mutableStateOf(false)
            }

            // Column 함수를 쓰면 세로 방향으로 배열
            // click 때마다 isExpanded 값을 반대로 바꿈
            Column(modifier = Modifier.clickable { isExpanded = ! isExpanded }) {
                Text(
                    text = msg.author,
                    color = MaterialTheme.colors.secondaryVariant,
                    style = MaterialTheme.typography.subtitle2
                )
                //수직 space 추가
                Spacer(modifier = Modifier.height(4.dp))
                Surface(shape = MaterialTheme.shapes.medium, elevation = 2.dp){ //모서리가 둥근 사각형, 그림자 생김
                    Text(
                        text = msg.body,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(all = 4.dp),
                        // 메시지가 펼쳐져 있으면 내용을 전부 보여주고 아니면 한 줄만 보여주도록
                        maxLines = if(isExpanded) Int.MAX_VALUE else 1,
                    )
                }
            }
        }
    }

    // @Preview annotation을 동시에 여러 개 사용할 수도 있다.
    // 이름을 추가해 구분할 수 있고 Night mode를 켠 상태를 함께 볼 수도 있다.
    // 그느데 왜 Night mode 색상이 제대로 보여지지가 않지?
    @Preview(
        name = "Light Mode",
        showBackground = true //배경색 함께 보기
    )
    @Preview(
        name = "Dark Mode",
        uiMode = Configuration.UI_MODE_NIGHT_YES,
        showBackground = true
    )
    @Composable fun PreviewMessageCard2(){
        MessageCard2(
            msg = Message("Anna", "Where are you Olaf?")
        )
    }

    // 3. Material Design
    // Compose는 Material Design 원칙을 지원하기 위해(?) 만들어졌다.
    // 많은 Compose UI들이 별도 설정 없이도 Material Design을 구현하고 있다.
    // Material Design widget들로 앱을 꾸며보자
    // Material Design을 이용해서 디자인하는 게 일관성을 유지하며 유지보수하기 좋은 것 같다.

    // Material Design은 Color, Typography, Shape 세가지를 축으로 한다.

    // Color
    // 프로필 사진 주위로 border를 추가하고, Text에 color를 추가해보자.
    // MaterialTheme.colors를 쓸 수도 있고, Color.Blue처럼 쓸 수도 있다.

    // Typography
    // Text Composable에 MaterialTheme.typography를 사용해보자

    // Shape
    // body의 Text에 shape과 elevation을 추가하기 위해 Suface로 감싸추고 값을 준다.
    // 보기 좋게 padding도 추가해보자.


    // 4. Lists and animations
    // 위에서 만든 MessageCard2를 채팅처럼, ListView처럼 보여줘보자
    @Composable
    fun Conversation(messages : List<Message>){
        // LazyColumn, LazyRow
        // 화면에 보여지는 컴포저블만 render 한다. 대량의 데이터를 보여줄 때 효율적이다.
        LazyColumn(){
            items(messages){ message ->
                MessageCard2(msg = message)
            }
        }
    }

    // Tutorial에서 샘플로 제공된 데이터를 SampleData.kt로 추가하고 적절히 손봐주었다.
    // https://gist.github.com/yrezgui/26a1060d67bf0ec2a73fa12695166436

    @Preview
    @Composable
    fun PreviewConversation(){
        val data = SampleData.conversationSample
        ComposeEx02TutorialTheme {
            Conversation(messages = data)
        }
    }

    // 클릭하면 메시지가 펼쳐졌다가 닫혔다가 하도록 컴포저블의 모양을 바꿔보자
    // MessageCard2 내에 이 카드의 펼침/닫힘 상태를 저장하는 state 변수인 isExpanded 추가
    // Text composable에서 isExpanded를 조건으로 maxLines를 다르게 보여주도록 함

}

// Composable의 state
// https://origogi.github.io/android/compose-state
// Composable 은 상태에 따라 UI를 구성하고 Composable 은 함수이기 때문에
// 상태값을 지역 변수로 저장하면 재구성이 될 때마다 함수가 새로 호출이 되기 때문에
// 지역 변수에 저장된 상태 값은 소실이 됩니다. 따라서 재구성 즉 Composable 함수가 호출이 되어도
// 상태 값을 계속 유지를 해야합니다. 이를 지원하는 것이 바로 remember 라는 delegate 입니다.

// 그리고 Composable에서 상태를 저장하고 상태가 변경이 되었을 때 재구성을 하기 위해서는 관찰가능한 객체를 사용하게 됩니다.

// 이를 위해 MutableState<T> 를 사용하며 만약 State를 업데이트를 하게 된다면
// 해당 State를 관찰하고 있는 해당 Composable 를 재구성하게 됩니다.
// MutableState<T> 를 생성하기 위해서 mutableStateOf() api를 사용하게 됩니다.
//interface MutableState<T> : State<T> {
//   override var value: T
//}

// Composable에서 관찰가능하고 재구성이 여러번 호출되어도 상태 정보를 계속 유지하기 위해
// 위에서 언급한 remember 와 MutableState<T> 를 이용하여 구현합니다.

//Composable 에서 MutableState 객체를 선언하는 데는 세 가지 방법이 있습니다.
//val mutableState = remember { mutableStateOf(default) }
//var value by remember { mutableStateOf(default) }
//val (value, setValue) = remember { mutableStateOf(default) }

// Composable functions can store local state in memory by using remember,
// and track changes to the value passed to mutableStateOf.
// Composables (and their children) using this state will get redrawn automatically
// when the value is updated. This is called recomposition.

// TODO by, remember, MutableState에 대해 더 공부해봐야겠다.