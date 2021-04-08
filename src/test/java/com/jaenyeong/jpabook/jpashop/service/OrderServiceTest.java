package com.jaenyeong.jpabook.jpashop.service;

import com.jaenyeong.jpabook.jpashop.domain.Address;
import com.jaenyeong.jpabook.jpashop.domain.Member;
import com.jaenyeong.jpabook.jpashop.domain.Order;
import com.jaenyeong.jpabook.jpashop.domain.OrderStatus;
import com.jaenyeong.jpabook.jpashop.domain.item.Book;
import com.jaenyeong.jpabook.jpashop.domain.item.Item;
import com.jaenyeong.jpabook.jpashop.exception.NotEnoughStockException;
import com.jaenyeong.jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void 상품주문() throws Exception {
        // Arrange
        final Member member = createMember();

        final Book book = createBook("JPA", 10_000, 10);

        // Act
        final int orderCount = 2;
        final Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // Assert
        final Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 ORDER이다.");
        assertEquals(1, getOrder.getOrderItems().size(), "주문한 상품 종류 수가 정확해야 한다.");
        assertEquals(10_000 * orderCount, getOrder.getTotalPrice(), "주문 가격은 가격과 수량을 곱한 값이다.");
        assertEquals(8, book.getStockQuantity(), "주문 수량만큼 재고가 줄어야 한다.");
    }

    @Test
    void 상품주문할때_재고수량초과() throws Exception {
        // Arrange
        final Member member = createMember();
        final Item item = createBook("JPA", 10_000, 10);

        final int orderCount = 11;

        // Act Assert
        assertThrows(NotEnoughStockException.class, () -> orderService.order(member.getId(), item.getId(), orderCount));
    }

    @Test
    void 주문취소() throws Exception {
        // Arrange
        final Member member = createMember();
        final Item item = createBook("JPA", 10_000, 10);

        final int orderCount = 2;
        final Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // Act
        orderService.cancelOrder(orderId);

        // Assert
        final Order getOrder = orderRepository.findOne(orderId);
        assertEquals(OrderStatus.CANCEL, getOrder.getStatus(), "주문 취소시 상태는 CANCEL이다.");
        assertEquals(10, item.getStockQuantity(), "주문이 취소된 상품은 그만큼 재고가 증가해야 한다.");
    }

    private Member createMember() {
        final Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강서로", "123456"));
        em.persist(member);

        return member;
    }

    private Book createBook(final String name, final int price, final int stockQuantity) {
        final Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);

        return book;
    }
}
