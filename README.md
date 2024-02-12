# LifeMash-NewsApp

- Jetpack Compose
- MaterialDesign
- Retrofit, OkHttp
- Jsoup
- Modularization
- Hilt
- WebView

<img src="gif/use_ex.gif" height="600"> <img src="gif/use_lottie.gif" height="600">"

## Jsoup

Jsoup는 자바 라이브러리로, 웹 페이지를 크롤링하고 HTML 문서를 파싱하는 데 사용된다.

- HTML 데이터를 직접 크롤링하기보다는 서버 통신을 통한 JSON 형식의 데이터를 받는게 더 일반적이므로 자주 사용할 라이브러리는 아니다.(일반적인 앱 서비스에서 자주 사용되는
  것은 아니다)

### 주요 기능

1. HTML 파싱: Jsoup는 웹 페이지의 HTML 소스를 읽어들여, 간편하게 **DOM** 구조로 파싱할 수 있다. 이를 통해 개발자들이 웹 페이지의 구조를 쉽게 이해하고
   조작할 수 있다.
2. CSS 셀렉터 지원: Jsoup는 CSS 셀렉터를 사용하여 웹 페이지에서 특정 요소를 선택할 수 있다. 이를 통해 개발자들이 원하는 데이터를 효율적으로 추출할 수 있다.
3. 데이터 추출: Jsoup를 사용하면 웹 페이지의 텍스트, 속성, 링크 등 다양한 데이터를 추출할 수 있다. 이를 통해 웹 페이지의 콘텐츠를 분석하고 활용할 수 있다.
4. 데이터 조작 및 정제: Jsoup는 웹 페에지의 HTML 요소를 추가, 수정, 삭제하는 등의 조작이 가능하다. 또한, 웹 페이지의 콘텐츠를 정제하여 원하는 형식으로 출력할 수
   있다.
5. 웹 페이지 크롤링: Jsoup는 웹 페이지를 크롤링하는 기능을 제공한다. 이를 통해 개발자들이 여러 웹 페이지의 데이터를 수집하고 분석할 수 있다.

Jsoup는 강력하고 사용하기 쉬운 기능을 제공하여 웹 페이지의 HTML 데이터를 추출, 파싱, 조작하는 작업을 쉽게 수행할 수 있도록 한다. 이를 활용하여 웹 스크래핑, 데이터
마이닝 등 다양한 분야에서 사용이 가능하다.

## Tikxml

이번 프로젝트는 구글 뉴스를 가져오는데, RSS 형식의 데이터를 가져올 것이다.
RSS 형식의 데이터 방식은 XML 형식으로 데이터를 내려주기 때문에 JSON이 아니라 XML을 파싱해야 한다.

마지막 릴리즈가 2018년 11월, 마지막 커밋이 2020년 7월로 오래되었기에 사용을 권장하지는 않는다.

그러나 Retrofit에서 support하는 XML Parser는

- Simple XML
- JAXB
  두개가 있지만 Simple XML은 Depecated되었고 JAXB는 Android를 지원하지 않는다...

그렇기 때문에 차선책으로 Tikxml을 사용하고 가급적이면 XML 데이터 포맷 대신 Json 형식을 사용하는 것이 좋을 것이다.

### 사용법

https://github.com/Tickaroo/tikxml/blob/master/docs/AnnotatingModelClasses.md

## RSS 데이터

Google News에서 (ex: news.google.com/rss/...) rss 키워드를 붙여주면 다음과 같은 XML 데이터를 볼 수 있다.
(다른 웹 사이트도 가능하다!)

![](.README_images/googlenews_rss.png)

![](.README_images/googlenews_1.png)

- 그러나 rss를 확인해보면 사진 데이터가 보이지 않기 때문에 해당 뉴스 게시물의 링크로 들어가 *메타 데이터*를 확인하기로 한다.

![](.README_images/googlenews_meta.png)

- meta `og:image`와  `og:title`이 보이는 것을 확인할 수 있다. 이것을 활용하도록 한다.

** 메타데이터는 데이터에 대한 데이터로, 다른 데이터의 구조, 의미, 특성, 위치 등을 설명하는 정보이다.

# 2023.04.26 Update

- Google News에서 이미지를 불러오는 것을 막아놓았기 때문에 SBS 뉴스로 변경함

![](.README_images/project.dot.png)