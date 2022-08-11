package com.juhnny.composeex01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juhnny.composeex01.ui.theme.ComposeEx01Theme

// Compose 첫인상
// 음.. 우선 폴더 구조를 보자
// themes.xml의 내용이 확 줄어들고 대신 java 패키지 안에 ui.theme 패키지가 생겨났다.
// Color, Shape, Type(폰트) 세가지와 content를 더해 MaterialTheme이라는 걸 만들어내는 곳이 Theme.kt
// ComposeEx01Theme() 함수가 그 역할을 한다.
// ComposeEx01Theme() 함수는 setContent()에서도 쓰이고, DefaultPreview()에서도 쓰이고 있다.
// 안드로이드의 View나 RN의 컴포넌트 같은 걸 만들어 뱉는 거 같다.

// @Composable annotation이 눈에 띈다.
// 함수를 컴포넌트로 사용할 수 있게 해주는 기능 같다.
// 맨 아래 설명 추가
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeEx01Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

// DefaultPreview()는 어디서 쓰이지?
// Ctrl + B를 눌러봐도 쓰이는 곳이 없다.
// @Preview는 뭘까?
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeEx01Theme {
        Greeting("iOS")
    }
}

//xml 폴더로 가보자.
// backup_rules.xml랑 data_extraction_rules.xml은 백업에 관한 파일이네
// 그럼 DefaultPreview()는 어디서 쓰이고 있는거야?


//Composable.kt 중 인용
//Composable functions are the fundamental building blocks of an application built with Compose.

//Composable can be applied to a function or lambda to indicate that the function/lambda can be used
// as part of a composition to describe a transformation from application data into a tree or hierarchy.

//Annotating a function or expression with Composable changes the type of that function or expression.
// For example, Composable functions can only ever be called from within another Composable function.
// A useful mental model for Composable functions is that an implicit "composable context" is passed into
// a Composable function, and is done so implicitly when it is called from within another Composable function.
// This "context" can be used to store information from previous executions of the function that happened
// at the same logical point of the tree.

// Composable은 또 다른 Composable 안에서만 쓸 수 있다고 하는 게 특이
