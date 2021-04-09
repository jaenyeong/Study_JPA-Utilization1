package com.jaenyeong.jpabook.jpashop.service;

import com.jaenyeong.jpabook.jpashop.domain.item.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;

@SpringBootTest
class ItemServiceTest {

    @Autowired
    private EntityManager em;

    @Test
    void updateTest() throws Exception {
        final Book book = em.find(Book.class, 1L);

        book.setName("책이름 변경");

        // 트랜잭션 내에서 커밋되면 DirtyChecking 기능을 통해 변경된 데이터가 반영됨
    }
}
