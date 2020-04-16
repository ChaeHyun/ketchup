# 개인프로젝트 - Ketchup

Todo 리스트 애플리케이션을 구현해나가면서 AAC(Android Architecture Component)를 적용해보는 프로젝트입니다.

Ketchup은 할일을 미루지말고 Catch-up하자라는 의미에서 붙인 이름입니다. 

마지막에 케첩 아이콘을 테마로 디자인 해볼 생각입니다.😄



\***아직 개발진행중인 미완성 프로젝트입니다.**

[Github 바로가기](https://github.com/ChaeHyun/ketchup)

[개발하면서 공부한것을 기록한 블로그 링크](https://saucecode.tistory.com/category/Android)



## 개발목적

+ MVVM 아키텍쳐와 LiveData를 사용합니다.
+ Room 데이터베이스를 사용합니다.
+ Dagger를 사용해 DI 시스템을 도입합니다.

+ Notification의 동작을 이해합니다.
+ RecyclerView의 동작을 이해합니다.



## 아키텍쳐

![전체아키텍처](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2Fr8orA%2FbtqDk6Jx0v6%2FYIrMVADE3kIzH8j18ubiOK%2Fimg.png)



### - Model

![ MVVM 구조](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FqlGz2%2FbtqDnv88Zru%2Filk0u4EDhkBzdFujRVJ6oK%2Fimg.png)

+ Repository 모듈을 도입해 데이터에 접근하는 유일한 통로로 역할을 분담합니다.
+ 할일(Task) 자료는 Room 데이터베이스에 저장됩니다.



### - View

![ View의 구성 Navigation Component](https://k.kakaocdn.net/dn/3jfTg/btqDj3NiiTP/P9O73CLd5EhtCTzHiFLIRk/img.png)

+ AAC의 Navigation Component를 활용합니다.
+ Single Activity - Multi Fragments 구조를 사용합니다.

Navigation Component는  Activity와 Fragment의 화면 전환에 도움을 받을 수 있는 라이브러리이고 안드로이드 스튜디오에서 쉽게 화면 구성을 그래픽으로 확인 할 수 있어서 적극 도입했습니다.

위 그림의 NavHostFragment 영역에 자식 프래그먼트들이 전환되어 사용되어집니다.



### - ViewModel

![ViewModel의 구조](https://k.kakaocdn.net/dn/bcvTWj/btqDjHwT3qN/rK6i6tkFEP08xkzoEfs3t0/img.png)

+ View와 Model을 이어주는 중간자 역할입니다.
+ 하지만 Presenter와 다르게 View에 대한 참조를 전혀 갖고있지 않습니다.
+ LiveData 홀더에 요청받은 데이터를 담으면, View의 옵저버에서 LiveData의 갱신된 데이터를 사용하는 구조입니다.

기존 프로젝트에서 사용했던 Presenter구조는 사용하기전에 정의해야할 인터페이스의 양이 많았는데 ViewModel의 구조는 LiveData와 함께 사용시 적은 양의 코드로 같은 로직 분리를 실현할 수 있었습니다.



### - DI 구조

![DI 구조](C:\Users\iamrc\AppData\Roaming\Typora\typora-user-images\image-20200417015710007.png)





## 실행화면

![할 일 등록화면](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FVnqfr%2FbtqDky7gibn%2FqldikIZ90ZKBk9JZCqiWQk%2Fimg.png)

+ 라벨 색상으로 할 일을 분류할 수 있도록 만들었습니다.



![분류방법 & 노티피케이션 액션](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FcQbOPr%2FbtqDnwtsCv4%2FZ6X3PUxn6h5CfNnDtBcCkK%2Fimg.png)

+ 등록된 할일은 시간🕓에 맞춰 자동으로 분류 되는 시스템을 적용했습니다.
+ **오늘 할 일**과 **내일 할 일**에 조금 더 집중할 수 있도록 사용자에게 적극 알려줄 수 있도록 구현하기 위함입니다.



![리사이클러뷰 아이템 접기 기능](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FspXJz%2FbtqDjen3szn%2Fe44rR73j8FuBpnVAeu4ZE0%2Fimg.png)

+ 많은 수의 할 일이 저장되었을 때 쉽게 찾아 볼 수 있도록 리사이클러뷰의 아이템을 접을 수 있도록 구현하였습니다.
