package com.jaenyeong.jpabook.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("초기 프로젝트 설정을 위한 테스트 클래스")
@SpringBootTest
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    // EM을 통한 모든 데이터 변경은 트랜잭션 안에서 처리되어야 하기 때문에 @Transactional 애너테이션을 태깅하지 않으면 에러 발생
    @Transactional
    // 롤백 처리 무시
    @Rollback(value = false)
    void testMember() {
        Member member = new Member();
        member.setName("member A");

        final Long savedId = memberRepository.save(member);
        final Member findMember = memberRepository.find(savedId);

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getName()).isEqualTo(member.getName());
        assertThat(findMember).isEqualTo(member);
    }
}
