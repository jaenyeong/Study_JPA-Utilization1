package com.jaenyeong.jpabook.jpashop.service;

import com.jaenyeong.jpabook.jpashop.domain.Member;
import com.jaenyeong.jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager em;

    @Test
//    @Rollback(value = false) // 롤백 처리를 적용하지 않을 때
    void 회원가입() throws Exception {
        // Arrange
        final Member member = new Member();
        member.setName("Kim");

        // Act
        final Long savedId = memberService.join(member);

        // Assert
//        em.flush(); // 삽입 쿼리를 보고 싶을 때
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test
    void 회원가입할때_중복회원_존재하면_예외() throws Exception {
        // Arrange
        final Member member1 = new Member();
        member1.setName("Kim");

        final Member member2 = new Member();
        member2.setName("Kim");

        // Act
        final Long saved1Id = memberService.join(member1);
//        final Long saved2Id = memberService.join(member2); // 예외 발생

        // Assert
        assertThrows(IllegalStateException.class, () -> memberService.join(member2));
//        fail("예외 발생"); // 여기에 닿으면 예외 발생
    }
}
