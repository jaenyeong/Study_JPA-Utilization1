# Study_JPA-Utilization1
### 인프런 실전! 스프링 부트와 JPA 활용1 - 웹 애플리케이션 개발 (김영한)
https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-JPA-%ED%99%9C%EC%9A%A9-1/dashboard
-----

## [Settings]
#### Project Name
* Study_JPA-Utilization1
#### java
* zulu jdk 11
#### gradle
* IDEA gradle wrapper
#### Spring boot
* 2.4.2
#### H2
* brew install h2
* JDBC URL : jdbc:h2:~/jpashop
  * 접속 URL : jdbc:h2:tcp://localhost/~/jpashop
* root 경로에 jpashop.mv.db 파일 생성 여부 확인
* 1.4.200 버전에서 MVCC(Multi-Version Concurrency Control, 다중 버전 동시성 제어) 옵션 사용시 에러 발생
  * 1.4.198 버전부터 삭제됨
* 기존에 테이블 존재시 모두 삭제
  * ```drop all objects;```
#### p6spy
* JPA 쿼리 파라미터 로깅 확인 라이브러리
* 1.6.3
* 운영에 사용할 때는 사전 성능 테스트 필수
-----

## [환경 설정]

### JPA, DB 설정
#### CQS (Command and Query Separation)
* 명령과 조회를 분리
  * MemberRepository의 save 메서드에서 CQS를 위해 반환을 Member 객체가 아닌 ID를 반환

#### 테스트 메서드 작성
* 테스트 메서드의 @Transactional 태깅
  * EM을 통한 모든 데이터 변경은 트랜잭션 안에서 처리되어야 함
  * 테스트 메서드의 태깅된 애너테이션은 메서드 종료후 롤백
    * @Rollback(false) 애너테이션 태깅으로 롤백 무시 설정
