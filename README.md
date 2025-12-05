## Daglo Assignment

Rick and Morty 오픈 API를 활용해 캐릭터 목록 / 검색 / 상세 보기 등을 제공하는 안드로이드 앱입니다.  
**Clean Architecture + MVVM + Dagger2 + Coroutines/Flow** 구조로 구현되어 있습니다.

---

### 목차

- 소개
- 주요 기능
- 기술 스택
- 앱 사용 방법
- 빌드 

---

### 소개

- **프로젝트 이름**: Daglo  
- **설명**: Rick and Morty API 기반 캐릭터 리스트 앱  
- **목표**:
  - Clean Architecture와 MVVM 패턴 적용
  - 네트워크 + 페이징 + 검색 + 에러 처리 등 실전 패턴 정리
  - Dagger2 기반 DI 환경 구성

---

### 주요 기능

- **캐릭터 목록**
  - Rick and Morty 캐릭터 목록을 RecyclerView로 표시
  - Glide로 캐릭터 프로필 이미지 로딩
  - 리스트 끝에 가까워지면 **자동 페이지네이션(무한 스크롤)**

- **검색**
  - 상단 검색창에서 캐릭터 이름 검색
  - `TextWatcher + Channel + Flow(debounce 400ms)`로 **디바운스 검색** 구현
  - 검색어가 바뀌면 1페이지부터 재조회, 같은 검색어라면 다음 페이지 이어서 로드

- **스와이프 새로고침**
  - `SwipeRefreshLayout` 으로 위로 당겨 새로고침
  - 현재 검색어 기준으로 1페이지부터 다시 조회

- **캐릭터 상세 화면**
  - 리스트 아이템 클릭 시 상세 Fragment로 이동
  - `EndPoint` + `Navigable` 패턴으로 Fragment 간 내비게이션

- **에러/상태 처리**
  - 404(검색 결과 없음), 서버 에러, 알 수 없는 에러를 Toast로 안내
  - 마지막 페이지 도달 시 “마지막 페이지입니다” 메시지 표시
  - `viewState.isLoading`, `viewEvent` 로 로딩/스크롤/에러 상태 관리

---

### 기술 스택

- **언어**
  - Kotlin

- **Android**
  - AndroidX AppCompat, Fragment KTX, RecyclerView
  - ViewBinding
  - SwipeRefreshLayout

- **아키텍처**
  - Clean Architecture (data / domain / presenter 레이어 분리)
  - MVVM (View + ViewModel + StateFlow/SharedFlow)

- **DI**
  - Dagger2  


- **네트워크 & 비동기**
  - Retrofit2
  - Kotlin Coroutines
  - StateFlow / SharedFlow / Channel / debounce

- **이미지**
  - Glide 4.16.0

- **테스트**
  - JUnit
  - MockK
  - kotlinx-coroutines-test

---


### 앱 사용 방법

- **앱 실행**
  - 실행하면 바로 Rick and Morty 캐릭터 리스트가 표시됩니다.

- **캐릭터 리스트 스크롤**
  - 리스트를 아래로 스크롤하면 자동으로 다음 페이지를 로드합니다.
  - 더 이상 불러올 데이터가 없으면 “마지막 페이지입니다” Toast가 표시됩니다.

- **검색**
  - 상단 검색창에 캐릭터 이름(일부 포함)을 입력합니다.
  - 입력이 멈춘 뒤 약 0.4초 후 자동으로 검색 요청이 나갑니다.
  - 검색 결과가 없으면 “검색 결과가 없습니다” 메시지가 Toast로 출력됩니다.

- **새로고침**
  - 리스트를 위로 살짝 당기면 스와이프 새로고침이 동작합니다.
  - 현재 검색어 기준으로 1페이지부터 다시 로드합니다.

- **상세 보기**
  - 캐릭터 아이템을 탭하면 상세 화면으로 이동해 선택한 캐릭터의 상세 정보를 볼 수 있습니다.

---

### 빌드

- **개발 환경**
  - Android Studio
  - JDK 11
  - `minSdk = 24`, `targetSdk = 36`
