package com.juhnny.composeex02tutorial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juhnny.composeex02tutorial.ui.theme.ComposeEx02TutorialTheme

// Jetpack Compose Tutorial 따라하기
// XML Layout이나 Layout Editor를 사용하는 일 없이
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

            MessageCard2(msg = Message("Anna", "Where are you?"))
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
            )
            // 수평 space 추가
            Spacer(modifier = Modifier.width(8.dp))
            // Column 함수를 쓰면 세로 방향으로 배열
            Column {
                Text(text = msg.author)
                //수직 space 추가
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = msg.body)
            }    
        }
        
    }

    @Preview @Composable fun PreviewMessageCard2(){
        MessageCard2(
            msg = Message("Anna", "Where are you Olaf?")
        )
    }

}
