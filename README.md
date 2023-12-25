
## 소개

"스프링부트와 AWS로 혼자 구현하는 웹 서비스" 책 클론코딩 리포지토리  

## 책과의 차이

- 자바 17을 사용했다. 사용된 기술들도 최대한 최신 버전을 활용하려고 했다.
- 도커 컨테이너까지 구현하는 것이 목표.
- 책과 다르게 구현하거나 문제 해결 시 어떤 부분이 변경되었는지, 왜 바꾸었는지, 어떻게 사용되는지를 기록했다.
- 책에서 부트스트랩을 엉망으로 사용하고 있다. 최대한 부트스트랩 규격에 맞게 작성했다.
- 헤더와 푸터를 사용하는 방식이 위험해 보이지만(태그가 흩어져 있음. html/body 태그가 헤더에서 열고, 푸터에서 닫힘), 일단 그대로 사용했다.

## EnableJpaAuditing 이후 오류 발생
책을 따라 실습하면서 @EnableJpaAuditing을 Application에 붙인 뒤, HelloControllerTest에서 오류가 발생했다.

https://velog.io/@suujeen/Error-creating-bean-with-name-jpaAuditingHandler  
EnableJpaAuditng을 Configuration으로 분리하는 걸로 해결했다.

### 왜 해결됐는가?
https://docs.spring.io/spring-data/jpa/docs/1.7.0.DATAJPA-580-SNAPSHOT/reference/html/auditing.html  
Spring Data Jpa 1.5부터, @Configuration에 @EnableJpaAuditing을 추가하면, JpaAuditing이 필요한 빈들이 해당 Configuration에 자동 등록되어 실행된다고 한다.

https://velog.io/@max9106/Spring-Configuration%EC%9D%84-%ED%86%B5%ED%95%9C-%EB%B9%88-%EB%93%B1%EB%A1%9D-%EC%8B%9C-%EC%8B%B1%EA%B8%80%ED%86%A4-%EA%B4%80%EB%A6%AC  
@Configuration을 사용하더라도, 내부 메서드에 @Bean을 붙이지 않으면 싱글턴이 아니게 된다고 한다.

## 컨트롤러 테스트 시 왜 MockMvc를 쓰는가?(왜 컨트롤러를 직접 호출하지 않는가?)
~~통합 테스트를 위해서?~~  
외부 의존성이 존재한다면 이를 조립해주어야 하는데, 그걸 일일이 구현하는 것보다는 그냥 스프링을 실행시키고 MockMvc로 쿼리를 날리는 것이 훨씬 편하므로.

## 테스트 방식의 차이
HelloController에서는 webMvcTest를 이용해서 테스트를 했고, PostsApiController에서는 @SpringBootTest를 이용해서 테스트를 진행했다.  
다른 방식으로 테스트를 한 이유를 잘 모르겠다. 다양한 테스트 방법을 보여주기 위해서?

https://spring.io/guides/gs/testing-web/  
https://wiselog.tistory.com/171  
위 문서에 대략적인 설명이 있다.

webMvcTest를 쓰면 컨트롤러 하나만 테스팅이 되고, springboottest를 쓰면 전체 통합 테스트가 된다.  
~~springboottest에서도 autoconfiguremockmvc를 넣어두면 MockMvc를 사용할 수 있다.(기본은 restTemplate 사용)~~  
좀 더 알아보고 작성할 것

PostsApiController에서 repository까지 검사하는 코드가 있어서 SpringBootTest를 사용한 것 같다.  
컨트롤러나 서비스 자체에 큰 로직이 없어서, 테스팅을 위해 repository까지 검사한 것으로 보인다.

## build.gradle에서 runtimeOnly, compileOnly, implementation의 차이

https://bnzn2426.tistory.com/136  
여기에 정리가 잘 되어 있다.

## Mustache에서 한글 깨짐 오류

https://stackoverflow.com/questions/65486789/whats-mean-enabled-ture-force-true-properties-in-this-code  
위 내용을 추가하여 해결하였다.

## jquery에서 화살표 함수를 쓰면 오류가 나는 이유

> ChatGPT의 힘을 빌려 작성

JavaScript의 화살표 함수(`() =>`)와 일반적인 함수 선언(`function ()`)은 스코프와 `this`에 대한 동작이 다르기 때문에 때로는 호환성 문제가 발생할 수 있습니다. 특히, jQuery에서 이벤트 핸들러로 사용될 때 이 차이가 나타날 수 있습니다.

**화살표 함수의 특징:**

1. **Lexical Scope (렉시컬 스코프):** 화살표 함수는 자신의 스코프가 아닌 상위 스코프의 변수에 접근할 수 있습니다. 이는 함수를 정의할 때의 컨텍스트를 기준으로 합니다.

2. **this 바인딩:** 화살표 함수는 자체적인 `this`를 가지지 않고, 상위 스코프의 `this`를 유지합니다.

jQuery에서의 이벤트 핸들러에서는 이 두 가지 특징이 예상과 다르게 동작할 수 있습니다. 일반적으로 jQuery 이벤트 핸들러에서 `this`는 이벤트를 발생시킨 요소를 가리켜야 하는데, 화살표 함수를 사용하면 `this`가 예상과 다를 수 있습니다.

**예시:**

```javascript
// 이벤트 핸들러를 화살표 함수로 작성한 경우
$('button').on('click', () => {
    // 여기서의 this는 이벤트를 발생시킨 요소가 아닌 상위 스코프의 this를 가리킴
    console.log(this); // 상위 스코프의 this를 출력
});

// 이벤트 핸들러를 일반 함수로 작성한 경우
$('button').on('click', function() {
    // 여기서의 this는 이벤트를 발생시킨 요소를 가리킴
    console.log(this); // 이벤트를 발생시킨 요소를 출력
});
```
따라서 jQuery의 이벤트 핸들러에서는 보통 function () 문법을 사용하여 이벤트 핸들러를 작성하는 것이 권장되며, 가능하면 화살표 함수를 사용하지 않는 것이 좋습니다.

## querydsl 오류

이제 starter-data-jpa에서 포함하지 않는 것 같다.(이전엔 포함했던 것 같다. 아마도.) 그래서 따로 종속성을 등록해줘야 한다. 아래 링크를 참고하면 된다.  
https://ittrue.tistory.com/293

종속성 추가하는 게 마음에 안 들어서 그냥 JPA 기능으로 구현했다.  
키워드를 잘 맞춰서 메소드를 생성하면 그에 맞추어서 쿼리가 생성된다. 하단 링크 참고  
https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html  
Find All "By" Order By Id Desc  
메소드명 작성 시 By를 빼먹지 않도록 주의.

## restTemplate의 exchange 오류

PostsApiControllerTest에서 Posts_수정 테스트 작성 중 exchange에서 지속적으로 오류가 발생했다. 

@PathVariable 설정 시, 어노테이션 파라미터로 이름을 넘겨서 해결했다.  
@PathVariable Long id --> @PathVariable("id") Long id

아래 링크에서 힌트를 얻었다.  
https://shanepark.tistory.com/331

그 외에도 조금 찾아보니 restTemplate가 deprecated된다는 이야기도 있어서 mockMvc로 구현한 코드도 넣어뒀다.
https://effortguy.tistory.com/285  
https://adjh54.tistory.com/234

코드를 제공하는 리포지토리에서도 mvc를 사용하는 코드로 바뀌어 있었다.  
https://github.com/jojoldu/freelec-springboot2-webservice/blob/master/src/test/java/com/jojoldu/book/springboot/web/PostsApiControllerTest.java


요즘은 restTemplate 대신 webClient를 사용할 것을 권장하는 것 같다.    
https://blog.naver.com/hj_kim97/222295259904  

webClient 사용법  
https://happycloud-lee.tistory.com/220


좀 더 찾아보니, restTemplate는 테스트 코드를 위한 라이브러리가 아니라, 실사용 코드에서도 쓰이는 웹 통신 라이브러리였다.   
MockMvc는 컨트롤러 테스트 시, restTemplate는 클라이언트 사이드 테스트를 할 때 사용하는 것이 옳다고 한다.  
https://stackoverflow.com/questions/25901985/difference-between-mockmvc-and-resttemplate-in-integration-tests  
https://spring.io/blog/2012/11/12/spring-framework-3-2-rc1-spring-mvc-test-framework/

클라이언트 사이드 테스트? 통합 테스트를 말하는 건지? 잘 모르겠다. 아래 내용을 읽어봐야겠다.  
https://docs.spring.io/spring-framework/reference/testing/spring-mvc-test-framework.html  
https://docs.spring.io/spring-framework/reference/testing/spring-mvc-test-client.html

테스트 시에는 TestRestTemplate를 사용한다. ...