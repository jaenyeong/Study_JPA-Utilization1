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
  * 터미널에서 `h2` 명령어로 사용 가능
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
  * 테스트 메서드의 태깅된 애너테이션은 메서드 종료 후 롤백
    * @Rollback(false) 애너테이션 태깅으로 롤백 무시 설정

### IDEA JPA 설정
**설정이 되어 있지 않은 경우**
* `,` + `;` 단축키로 프로젝트 설정 진입
* Project Settings > Modules
  * 패키지의 `main` 경로에 JPA 설정
  * `jpashop` > `main` > JPA 추가, `Provider`를 `Hibernate`로 설정

## [도메인 설계]

### 기능 목록
* 회원 기능
  * 회원 등록
  * 회원 조회
* 상품 기능
  * 상품 등록
  * 상품 수정
  * 상품 조회
* 주문 기능
  * 상품 주문
  * 주문 내역 조회
  * 주문 취소
* 기타
  * 상품은 재고 관리 필요
  * 상품의 종류는 도서, 음반, 영화가 존재
  * 상품을 카테고리로 구분할 수 있음
  * 상품 주문시 배송 정보를 입력할 수 있음

### 구현하지 않는 목록
* 로그인, 권한 관리 등
* 파라미터 검증, 예외 처리 단순화
* 도서 외에 상품은 사용하지 않음
* 카테고리, 배송 정보 등 사용하지 않음

### 도메인 모델과 테이블 설계
`@MappedSuperclass`
* 상속을 목적으로 작성한 구현하지 않는 클래스에 태깅

`@Inheritance`
* 상속 전략 : `strategy = InheritanceType.?` 형태로 적용
  * InheritanceType.JOINED
    * 각각 테이블로 변환하는 조인 전략
  * InheritanceType.SINGLE_TABLE
    * 통합 테이블로 변환하는 단일 테이블 전략
  * InheritanceType.TABLE_PER_CLASS
    * 구현 클래스별 테이블 전략

`@DiscriminatorColumn`
* 슈퍼(상위) 클래스에 태깅
* 하위 클래스 구분을 목적으로 한 컬럼

`@DiscriminatorValue`
* 서브(구현) 클래스에 태깅
* `@DiscriminatorColumn`으로 지정된 컬럼의 값을 지정

`@Enumerated(value = EnumType.STRING)`
* 필드로 enum 사용할 때 태깅
* `@Enumerated(value = EnumType.ORDINAL)` 사용 금지
  * 순서(값)을 저장하기 때문에 순서가 변경되면 데이터 불일치됨

### 엔티티 설계 시 주의점
**`setter`을 제거하여 값을 최대한 변경하지 못하게 할 것**
* 값 변경이 필요한 경우 비즈니스 성격의 변경 메서드 사용할 것

**`protected ClassName()` 형태로 기본 생성자를 생성할 것**
* JPA에서 리플렉션, 프록시 기술을 통해 사용하기 때문
* 접근제어자는 `public`이 아닌 `protected`로 선언하는 것이 **그나마 안전함**

**모든 연관 관계는 지연 로딩으로 설정할 것**
* 즉시 로딩(`EAGER`)은 예측이 어렵고, 어떤 SQL이 실행될지 추적하기 어려움
* 특히 JPQL을 실행할 때 `N+1` 문제가 자주 발생
* 따라서 실무에서 모든 연관 관계는 지연 로딩(`LAZY`)으로 설정할 것을 권함
* 연관된 엔티티를 함께 DB에서 조회하려면 `fetch join` 또는 `엔티티 그래프` 기능 사용
  * `@XToOne(OneToOne, ManyToOne)` 관계는 기본이 즉시 로딩이므로 직접 지연 로딩으로 설정 해야 함

**컬렉션은 필드에서 초기화 할 것**
* 예시 : `private final List<Order> orders = new ArrayList<>();`
* 컬렉션은 필드에서 바로 초기화 하는 것이 안전
* null 문제에서 안전
* 하이버네이트는 엔티티를 영속화할 때, 컬렉션을 감싸서 하이버네이트가 제공하는 내장 컬렉션으로 변경
  * 만약 `getOrders()`처럼 임의의 메서드에서 컬렉션을 잘못 생성하면 하이버네이트 내부 메커니즘에 문제가 발생할 수 있음
  * 따라서 필드레벨에서 생성하는 것이 가장 안전하며 코드도 간결함

**테이블, 컬럼명 생성 전략**
* 스프링 부트에서 하이버네이트 기본 매핑 전략을 변경해서 실제 테이블 필드명은 다름
* 하이버네이트 기존 구현
  * 엔티티의 필드명을 그대로 테이블의 컬럼명으로 사용 (`SpringPhysicalNamingStrategy`)
* 스프링 부트 신규 설정 (엔티티(필드) 테이블(컬럼))
  1. 카멜 케이스 => 언더스코어 (`memberPoint` => `member_point`)
  2. .(점) => _(언더스코어)
  3. 대문자 => 소문자
* **적용 2단계**
  1. 논리명 생성
     * 명시적으로 컬럼, 테이블명을 직접 적지 않으면 ImplicitNamingStrategy 사용
     * `spring.jpa.hibernate.naming.implicit-strategy`
       * 테이블이나, 컬럼명을 명시하지 않을 때 논리명 적용
  2. 물리명 적용
     * `spring.jpa.hibernate.naming.physical-strategy`
       * 모든 논리명에 적용됨, 실제 테이블에 적용 (`username` => `usernm` 등으로 회사 룰로 바꿀 수 있음)

**Cascade**
~~~
class Order {
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private final List<OrderItem> orderItems = new ArrayList<>();
}

// 위 처럼 cascade를 적용한다면 아래와 같이 orderItem 영속성 컨텍스트를 각각 지정할 필요 없음
em.persist(orderItemA);
em.persist(orderItemB);
em.persist(orderItemC);
em.persist(order);

// 영속성 컨텍스트를 order만 지정해도 orderItem 같이 지정됨
em.persist(order);
~~~

**연관 관계 (편의)메서드**
~~~
public void setMember(final Member member) {
    this.member = member;
    member.getOrders().add(this);
}

public void addOrderItem(final OrderItem orderItem) {
    this.orderItems.add(orderItem);
    orderItem.setOrder(this);
}

pfublic void setDelivery(final Delivery delivery) {
    this.delivery = delivery;
    delivery.setOrder(this);
}

public void addChildCategory(final Category childCategory) {
    this.child.add(childCategory);
    childCategory.setParent(this);
}
~~~

**cascade = CascadeType.ALL**
* 전파 옵션은 사용할 때 주의할 것
* 여러 곳에서 해당 엔티티를 참조하는 경우엔 위험할 수 있음
  * 데이터 변경, 또는 삭제가 자동으로 처리됨
* 현재 예제의 `OrderItem`, `Delivery`처럼 `Order`에서만 사용하는 경우는 괜찮음
* `OrderItem`, `Delivery`를 다른 엔티티에서 참조하는 경우 주의해서 사용할 것
  * 이런 경우 별도의 `repository`를 사용해 `persist`하여 사용하는 것을 추천

### 도메인 모델 패턴
* 엔티티의 비즈니스 로직이 위치하고 서비스 레이어에서는 단순히 요청을 위임하는 구조

### 트랜잭션 스크립트 패턴
* 엔티티의 비즈니스 로직이 거의 없고, 서비스 레이어에 대부분의 비즈니스 로직이 있는 구조

### 주문 검색 기능 동적쿼리
**일반적인 파라미터를 사용한 쿼리**
~~~
public List<Order> findAll(final OrderSearch orderSearch) {
    final TypedQuery<Order> orderTypedQuery = em.createQuery("select o from Order o join o.member m"
            + " where o.status = :status"
            + " and m.name like :name",
        Order.class)
        .setParameter("status", orderSearch.getOrderStatus())
        .setParameter("name", orderSearch.getMemberName())
//        .setFirstResult(100)
        .setMaxResults(1_000);
    
    return orderTypedQuery.getResultList();
}
~~~

**JPQL을 사용한 쿼리**
~~~
public List<Order> findAllByString(OrderSearch orderSearch) {
    final String jpql = "select o From Order o join o.member m";
    boolean isFirstCondition = true;
    
    //주문 상태 검색
    if (orderSearch.getOrderStatus() != null) {
        if (isFirstCondition) {
            jpql += " where";
            isFirstCondition = false;
        } else {
            jpql += " and";
        }
        jpql += " o.status = :status";
    }
    
    //회원 이름 검색
    if (StringUtils.hasText(orderSearch.getMemberName())) {
        if (isFirstCondition) {
            jpql += " where";
            isFirstCondition = false;
        } else {
            jpql += " and";
        }
        jpql += " m.name like :name";
    }
    
    final TypedQuery<Order> query = em.createQuery(jpql, Order.class).setMaxResults(1000); //최대 1000건
    if (orderSearch.getOrderStatus() != null) {
        query = query.setParameter("status", orderSearch.getOrderStatus());
    }
    if (StringUtils.hasText(orderSearch.getMemberName())) {
        query = query.setParameter("name", orderSearch.getMemberName());
    }
    
    return query.getResultList();
}
~~~

**JPA criteria를 사용한 쿼리**
~~~
public List<Order> findAllByCriteria(final OrderSearch orderSearch) {
    final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
    final CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
    final Root<Order> o = criteriaQuery.from(Order.class);
    final Join<Object, Object> m = o.join("member", JoinType.INNER);
    
    final List<Predicate> criteria = new ArrayList<>();
    
    // 주문 상태 검색
    if (orderSearch.getOrderStatus() != null) {
        final Predicate status = criteriaBuilder.equal(o.get("status"), orderSearch.getOrderStatus());
        criteria.add(status);
    }
    
    // 회원 이름 검색
    if (StringUtils.hasText(orderSearch.getMemberName())) {
        final Predicate name = criteriaBuilder.like(m.get("name"), "%" + orderSearch.getMemberName() + "%");
        criteria.add(name);
    }
    
    criteriaQuery.where(criteriaBuilder.and(criteria.toArray(new Predicate[criteria.size()])));
    final TypedQuery<Order> resultQuery = em.createQuery(criteriaQuery).setMaxResults(1_000);
    
    return resultQuery.getResultList();
}
~~~

**QueryDSL을 사용한 쿼리**
~~~
public List<Order> findAll(final OrderSearch orderSearch) {
    final QOrder order = QOrder.order;
    final QMember member = QMember.member;
    
    return query
            .select(order)
            .from(order)
            .join(order.member, member)
            .where(statusEq(orderSearch.getOrderStatus()), nameLike(orderSearch.getmemberName()))
            .limit(1_000)
            .fetch();
}
~~~

## 웹 계층 개발

### header, footer 등 중복 제거
[타임리프 문서](https://www.thymeleaf.org/doc/articles/layouts.html)

### Model validation, BindingResult
BindingResult 객체를 통해 파라미터(`Model`)의 유효성 검사와 바인딩 결과를 확인, 처리 가능
* 검증된 모델 객체 뒤에 와야 함
* 에러가 발생해도 데이터를 그대로 가져감 (예제에서 `MemberForm` 객체)

도메인 엔티티가 아닌 파라미터 (`Model`, DTO 등) 객체를 사용하는 이유
* 데이터 송수신의 유효성 검사 기준과 비즈니스 로직(도메인)에서의 유효성 검사 기준이 다를 수 있음
  * 따라서 정말 간단한 수준이 아니라면 각각 다른 클래스로 사용하는 것을 권장
