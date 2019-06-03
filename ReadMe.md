# Spring 기반 REST API 개발

- Spring Boot
- Spring Framework
- JPA
- HATEOAS
- REST Docs

### 목표
- CURD Event REST API 개발
- Self-Describtive Message, HATEOAS 만족하는 REST API 개발
- TDD 개발
- Spring Security OAuth2으로 인증

### 개발환경
- JDK 8
- Spring Framework 2.1.3.RELEASE
- Web
- JPA
- Lombok
- H2
- PostgreSQL
- Gradle 5.2.1

## TODO 
- [ ] docker-compose



## 
authentication manager 주요 interface
1. userdetailservice
2. password encoder
- basic authentication : header에 authentication + baisc + username + password encoding한 값을 가지고 
입력받은 username의 password를 읽어온 password와 사용자가 입력한 값이 매칭하는지 password encoder로 검사한다.
- 확인 후 scurity context holder에 저장을 한다.

##권한을 확인한다.
1. accessdecisionManager : user의 role로 확인한다. 

## spring security Oauth2.0
- AuthorizationServer : OAuth2 token 발행(/oauth/token) 및 토큰 인증(/oauth/authorize)
    - Oder 0 ( Resource server보다 우선순위가 높다. )
- ResourceServer : resource 요청 인증 처리 (Oauth 2 토큰 검사) 
    - Oder 3 
- 공통설정 ( spring security config )
  
 

