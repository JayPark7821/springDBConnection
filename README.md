## 예외와 트랜잭션 커밋,롤백

* 스프링은 기본적으로 체크 예외는 비즈니스 의미가 있을때 사용,  
  언체크 예외는 시스템 적으로 복구가 불가능한 예외로 가정한다.
* checked exception : Exception 을 상속받는 예외 커밋
* unchecked exception : RuntimeException 을 상속받는 예외 롤백

* ex) 비즈니스 요구사항 -> 주문을 하는데 상황에 따라 다음과 같이 조치
    * 정상 : 주문시 결제를 성공하면 주문 데이터를 저자하고 결제 상태를 완료로 처리
    * 시스템 예외 : 주문시 내부에 복구 불가능한 예외가 발생하면 전체 데이터를 롤백
    * 비즈니스 예외 : 주문시 결제 잔고가 부족하면 주문데이터를 저장하고, 결제 상태를 대기로 처리한다. (커밋)