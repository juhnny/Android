Guide to app architecture

On this page
Mobile app user experiences

Common architectural principles
Separation of concerns
Drive UI from data models
Single source of truth
Unidirectional Data Flow

Recommended app architecture
UI layer


This guide encompasses best practices and recommended architecture for building robust, high-quality apps.Mobile app user experiences
일반적인 안드로이드 앱에는 Activities, fragments, services, content providers, broadcast receivers를 비롯한 여러 앱 컴포넌트가 포함된다. 당신은 이러한 앱 컴포넌트의 대부분을 app manifest 안에 선언할 것이다. Android OS는 이 파일을 이용해 그 디바이스의 전반적인 UX에 어떻게 앱을 통합시킬지를 결정한다. 일반적인 안드로이드 앱은 여러 컴포넌트를 포함하고, 사용자들은 짧은 시간 안에 여러 앱과 상호작용할 수 있다는 걸 고려했을 때, 앱들은 다양한 종류의 유저 중심 workflow와 task들에 적응할 수 있어야 한다. 
모바일 기기들은 자원이 제한돼있으므로 언제든 OS가 새 앱을 위한 공간을 만들기 위해 일부 앱 프로세스를 종료할 수 있음을 기억해라.
이러한 환경적인 조건을 고려했을 때, 당신의 앱 컴포넌트들은 개별적으로 그리고 비순차적으로 실행될 수 있고, OS나 유저에 의해 언제든 종료될 수 있다. 이런 이벤트들은 직접 제어할 수 없기에, 앱 컴포넌트에 application data나 state를 저장해서는 안되며, 앱 컴포넌트들은 서로 종속되면 안된다. 
Common architectural principles 일반적인 아키텍처 원칙
application data나 state를 앱 컴포넌트에 저장해선 안된다면 앱을 어떻게 디자인해야 할까?
앱은 크기는 점점 커지기 때문에 앱을 확장할 수 있게 하고, 앱의 견고함을 향상시키고, 앱의 테스트를 쉽게 만들 수 있는 아키텍처를 정의하는 게 중요하다. 
앱 아키텍처는 앱의 각 부분 사이의 경계와 각 부분의 책임을 정의한다. 위에서 언급한 요구사항을 만족시키기 위해 앱 아키텍처가 몇가지 구체적인 원칙을 따르도록 디자인해야 한다.관심사의 분리 Separation of concerns 

The most important principle to follow is separation of concerns. It's a common mistake to write all your code in an Activity or a Fragment. These UI-based classes should only contain logic that handles UI and operating system interactions. By keeping these classes as lean as possible, you can avoid many problems related to the component lifecycle, and improve the testability of these classes.
Keep in mind that you don't own implementations of Activity and Fragment; rather, these are just glue classes that represent the contract between the Android OS and your app. The OS can destroy them at any time based on user interactions or because of system conditions like low memory. To provide a satisfactory user experience and a more manageable app maintenance experience, it's best to minimize your dependency on them.Drive UI from data models
Another important principle is that you should drive your UI from data models, preferably persistent models. Data models represent the data of an app. They're independent from the UI elements and other components in your app. This means that they are not tied to the UI and app component lifecycle, but will still be destroyed when the OS decides to remove the app's process from memory.
Persistent models are ideal for the following reasons:
	* 
Your users don't lose data if the Android OS destroys your app to free up resources.
	* 
Your app continues to work in cases when a network connection is flaky or not available.


If you base your app architecture on data model classes, you make your app more testable and robust.Single source of truth
When a new data type is defined in your app, you should assign a Single Source of Truth (SSOT) to it. The SSOT is the owner of that data, and only the SSOT can modify or mutate it. To achieve this, the SSOT exposes the data using an immutable type, and to modify the data, the SSOT exposes functions or receive events that other types can call.
This pattern brings multiple benefits:
	* 
It centralizes all the changes to a particular type of data in one place.
	* 
It protects the data so that other types cannot tamper with it.
	* 
It makes changes to the data more traceable. Thus, bugs are easier to spot.


In an offline-first application, the source of truth for application data is typically a database. In some other cases, the source of truth can be a ViewModel or even the UI.Unidirectional Data Flow
The single source of truth principle is often used in our guides with the Unidirectional Data Flow (UDF) pattern. In UDF, state flows in only one direction. The events that modify the data flow in the opposite direction.
In Android, state or data usually flow from the higher-scoped types of the hierarchy to the lower-scoped ones. Events are usually triggered from the lower-scoped types until they reach the SSOT for the corresponding data type. For example, application data usually flows from data sources to the UI. User events such as button presses flow from the UI to the SSOT where the application data is modified and exposed in an immutable type.
This pattern better guarantees data consistency, is less prone to errors, is easier to debug and brings all the benefits of the SSOT pattern.Recommended app architecture
This section demonstrates how to structure your app following recommended best practices.
Note: The recommendations and best practices present in this page can be applied to a broad spectrum of apps to allow them to scale, improve quality and robustness, and make them easier to test. However, you should treat them as guidelines and adapt them to your requirements as needed.
Considering the common architectural principles mentioned in the previous section, each application should have at least two layers:
	* 
The UI layer that displays application data on the screen.
	* 
The data layer that contains the business logic of your app and exposes application data.


You can add an additional layer called the domain layer to simplify and reuse the interactions between the UI and data layers.

Figure 1. Diagram of a typical app architecture.

Note: The arrows in the diagrams in this guide represent dependencies between classes. For example, the domain layer depends on data layer classes.
UI layer
UI layer(or presentation layer)의 역할은 application data를 화면에 보여주는 것이다. data가 변하면 언제든, 사용자 상호작용(버튼을 누르는 등) 때문이든 외부적인 입력(네트워크 응답 등) 때문이든, 그 변화를 반영하기 위해 UI가 업데이트 되어야 한다.
UI layer는 두 가지로 이뤄진다.
- data를 화면에 render하는 UI 요소. View나 Jetpack Compose function을 이용해 만든다.
- data를 갖고 있고, 이를 UI에 노출하고, 로직을 다루는 state holders(ViewModel 클래스 같은)

Figure 2. The UI layer's role in app architecture.
To learn more about this layer, see the UI layer page.Data layer
앱의 Data layer는 비즈니스 로직을 포함한다. 비즈니스 로직은 당신의 앱에 value를 준다.(비즈니스 로직은 앱이 data를 만들고, 저장하고, 변경하는지를 결정하는 규칙들로 이뤄진다)
Data layer는 Repositoriy들로 이뤄지고 각각은 0개 이상의 Data source를 포함할 수 있다. 앱에서 다루는 각각의 데이터 타입을 위한 repository 클래스를 만들어야 한다. 예를 들어, movies 관련 데이터를 위한 MoviesRepository를 만들거나, payments 관련 데이터를 위한 PaymentsRepository 클래스를 만들 수 있다.

Figure 3. The data layer's role in app architecture.

Repository 클래스가 책임지는 태스크
- 앱의 나머지 부분에 data를 노출한다.
- data에 대한 변경의 중앙화
- data source들간의 충돌 해결
- 앱의 나머지 부분으로부터 data의 출처를 추상화
- 비즈니스 로직 포함

각 data source 클래스는 오직 하나의 데이터 출처와의 작업만 책임을 져야 한다. 이는 파일일 수도, 네트워크 소스일 수도, 로컬 DB일 수도 있다. Data source 클래스는 application과 데이터 작업을 위한 시스템간의 브릿지다.

이 레이어에 대해 더 알고 싶다면 - data layer page

Repository classes are responsible for the following tasks:
	* 
Exposing data to the rest of the app.
	* 
Centralizing changes to the data.
	* 
Resolving conflicts between multiple data sources.
	* 
Abstracting sources of data from the rest of the app.
	* 
Containing business logic.


Each data source class should have the responsibility of working with only one source of data, which can be a file, a network source, or a local database. Data source classes are the bridge between the application and the system for data operations.
To learn more about this layer, see the data layer page.Domain layer
Domain layer는 UI layer와 Data layer 사이에 위치하는 선택적인 레이어다.
Domain layer는 복잡한 비즈니스 로직의 캡슐화 또는 여러개의 ViewModel에 의해 재사용되는 간단한 비즈니스 로직을 책임진다. 모든 앱들이 이를 필요로 하진 않기 때문에 이 레이어는 선택적이다. You should use it only when needed—for example, to handle complexity or favor reusability.

Figure 4. The domain layer's role in app architecture.
이 레이어의 클래스들은 use case 또는 interactor라고 불린다. 각 use case들은 단일 기능에 대한 책임만 가져야 한다. 예를 들어, 여러개의 화면에 적합한 메시지를 보여주기 위해 ViewModel들이 time zone들에 의존하고 있다면, GetTimeZoneUseCase 클래스를 둘 수 있다.
이 레이어에 대해 더 알고 싶다면 - domain layer pageManage dependencies between components
앱 안의 클래스들은 적절히 기능하기 위해 다른 클래스들에 의존하고 있다. 특정 클래스의 의존성을 모으기 위해 다음 두 종류의 디자인 패턴을 사용할 수 있다.
- Dependency injection(DI, 의존성 주입) : DI는 클래스들이 그들(아마도 dependencies)를 구성하지 않고도 그들의 dependencies를 정의할 수 있게 해준다. At runtime, another class is responsible for providing these dependencies.
- Service locator : The service locator pattern provides a registry where classes can obtain their dependencies instead of constructing them. 
이 패턴들은 같은 코드를 복제하거나 복잡성을 추가하지 않고도 dependencies를 관리하기 위한 명료한 패턴을 제공하기 때문에 당신이 코드를 scale할 수 있게 해준다. Furthermore, these patterns allow you to quickly switch between test and production implementations.
우리는 안드로이드 앱에서 DI 패턴을 따르고 Hilt 라이브러리를 사용하길 권한다. Hilt automatically constructs objects by walking the dependency tree, provides compile-time guarantees on dependencies, and creates dependency containers for Android framework classes. 
We recommend following dependency injection patterns and using the Hilt library in Android apps. Hilt automatically constructs objects by walking the dependency tree, provides compile-time guarantees on dependencies, and creates dependency containers for Android framework classes.General best practices
프로그래밍은 창의적인 영역이고 안드로이드 앱도 예외가 아니다. 앱이 겪게되는 문제 해결에는 여러 방법이 있다. 여러 Activity나 Fragment간에 데이터를 교환하거나, 원격 데이터를 가져와서 오프라인 모드에서 사용하도록 로컬에 보존하거나, 사소하지 않은 여러 일반적인 시나리오들을 다룬다. 
다음 권장사항은 필수는 아니지만, 대부분의 경우에 당신의 코드 베이스를 장기적으로 더 튼튼하고 테스트 및 유지보수가 쉽게 만들어준다.
Don't store data in app components.
앱의 진입점entry point(Activity, Service, Broadcast receiver 같은)을 데이터의 출처로 지정하는 걸 피해라. 대신 entry point들은 관련된, 데이터의 일부만 가져오도록 다른 컴포넌트들과 조직화해야 한다. 각 앱 컴포넌트들은 사용자와 기기의 상호작용, 그리고 전체 시스템의 현재 건강상태에 따라 단기간만 지속된다.

Reduce dependencies on Android classes. Android 클래스들에 대한 의존도를 낮춰라
app components만이 Context나 Toast 같은 Android framework SDK API들에 의존하는 유일한 클래스들이 되어야 한다. 앱의 다른 클래스들은 그들로부터 추상화해서 떨어뜨리는 것이 테스트 가능성을 높이고 앱 내의 결합도를 낮추는 데 도움이 된다. 
Create well-defined boundaries of responsibility between various modules in your app. 앱의 모듈들 사이의 책임의 경계를 잘 정의해라.
예를 들어, 네트워크로부터 데이터를 가져오는 코드를 코드 베이스의 여러 클래스와 패키지에 걸쳐 분산해놓으면 안된다. 마찬가지로 data caching과 data binding 같은 여러 개의 서로 관련없는 책임들을 한 클래스 안에 정의하면 안된다. 위에서 소개한 recommended app architecture 를 참고하면 도움이 된다.
Expose as little as possible from each module.  각 모듈 간의 노출을 최소화하라
예를 들어, 모듈로부터의 지름길(세부적인 내부 구현을 노출하는)을 만들고자 하는 욕심을 버려야 한다. 단기적으로는 시간을 벌 수 있겠지만 코드 베이스가 진화함에 따라 계속해서 기술 부채를 발생시키게 될 것이다. 
Focus on the unique core of your app so it stands out from other apps. 다른 앱들과 차별화되도록 내 앱만 고유한 핵심에 집중해라.
같은 보일러 코드를 계속 반복하면서 바퀴를 재발명하지 말라. 당신의 앱을 유니크하게 만드는 것에 시간과 에너지를 집중하고, Jetpack 라이브러리들과 다른 추천 라이브러리들을 이용해 반복적인 보일러 플레이트들을 처리하자. 
Don't reinvent the wheel by writing the same boilerplate code again and again. Instead, focus your time and energy on what makes your app unique, and let the Jetpack libraries and other recommended libraries handle the repetitive boilerplate.
Consider how to make each part of your app testable in isolation. 앱의 각 부분을 독립적(고립하여)으로 테스트하는 방법을 고려한다.
예를 들어, 네트워크로부터 데이터를 가져오는 API를 잘 정의하면, 그 데이터를 로컬 DB에 보존하는 모듈을 더 쉽게 테스트할 수 있다. 그렇지 않고 이 두 모듈로부터의 로직을 한 곳에 섞어 놓거나, 네트워크 코드를 코드 베이스 전체에 분산시켜 놓으면, 효과적인 테스트(다행히 불가능하지 않다면)를 훨씬 어렵게 한다.
For example, having a well-defined API for fetching data from the network makes it easier to test the module that persists that data in a local database. If instead, you mix the logic from these two modules in one place, or distribute your networking code across your entire code base, it becomes much more difficult—if not impossible—to test effectively.
Types are responsible for their concurrency policy. 타입들은 그들의 동시성 정책에 대한 책임이 있다.(?)
어느 타입이 장시간의 blocking 작업을 수행한다면 그 타입은 그 작업을 알맞은 스레드로 이동시킬 책임을 져야 한다. 그 타입은 그가 하고 있는 컴퓨팅 타입이 무엇이고 어느 스레드에서 실행돼야만 하는지 알고 있다. 타입들은 main-safe 해야 한다. 즉, 메인 스레드에서도 블락 없이 호출할 수 있어야 한다.
If a type is performing long-running blocking work, it should be responsible for moving that computation to the right thread. That particular type knows the type of computation that it is doing and in which thread it should be executed. Types should be main-safe, meaning they're safe to call from the main thread without blocking it.
Persist as much relevant and fresh data as possible. 가능한 한 관련성 높은 최신 데이터를 보존해라
그럼으로써 사용자들은 디바이스가 오프라인 모드일 때에도 앱의 기능을 즐길 수 있다. 모든 사용자들이 끊김없고 속도가 빠른 통신망을 사용하지 않는다는 걸, 그리고 그렇다 하더라도 혼잡한 곳에서는 수신상태가 좋지 않을 수 있다는 걸 기억하라.
Benefits of Architecture
앱에 좋은 아키텍쳐를 구현하면 프로젝트 팀과 엔지니어링 팀에 많은 이점을 가져다 준다.
- 앱 전반에 대한 유지보수성, 품질, 견고함이 개선된다.
- 앱을 확장할 수 있다. 코드 충돌이 최소화되어 더 많은 사람, 더 많은 팀들이 같은 코드 베이스에 기여할 수 있다.
- 온보딩을 돕는다. 아키텍처는 프로젝트에 일관성을 가져오므로 새로운 팀 멤버가 빠르게 업무를 시작하고 더 적은 시간 안에 효율을 높일 수 있다.
- 테스트가 더 쉽다. 좋은 아키텍처는 테스트하기 더 쉬운 간단한 타입을 사용하도록 지원한다.
- 잘 정의된 프로세스를 사용해 버그를 체계적으로 조사할 수 있다.
아키텍처에 대한 투자는 사용자들에게도 직접적인 영향을 준다. 엔지니어링팀의 생산성이 향상됨에 따라 더 안정적인 앱과 더 많은 기능을 이용할 수 있게 된다. 하지만 아키텍처는 초기에 시간 투자를 필요로 한다. 이  case studies 를 통해 다른 회사들에서 앱에 좋은 아키텍처를 적용한 성공사례를 확인할 수 있다.
Samples
The following Google samples demonstrate good app architecture. Go explore them to see this guidance in practice:
	* 
Now in Android
	* 
Architecture samples
	* 
Jetnews
	* 
iosched, the Google I/O app
	* 
Sunflower
	* 
Trackr

