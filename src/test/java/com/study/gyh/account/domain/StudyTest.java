package com.study.gyh.account.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.study.gyh.account.UserAccount;
import com.study.gyh.domain.Account;
import com.study.gyh.domain.Study;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StudyTest {

    Study study;
    Account account;
    UserAccount userAccount;


    @BeforeEach
    void beforeEach(){
        study = new Study();
        account = new Account();
        account.setNickname("gyh");
        account.setPassword("12345678");
        userAccount = new UserAccount(account);
    }

    @DisplayName("스터디 공개 인원 모집중, 이미 멤버나 스터디 관리자가 아니면 스터디 가입 가능")
    @Test
    void isJoinable(){
        study.setPublished(true);
        study.setRecruiting(true);

        assertTrue(study.isJoinable(userAccount));
    }

    @DisplayName("스터디를 공개했고 인원 모집중, 스터디 관리자는 스터디 가입이 불 필요하다.")
    @Test
    void isJoinable_false_manager() {
        study.setPublished(true);
        study.setRecruiting(true);
        study.addManager(account);
        assertFalse(study.isJoinable(userAccount));

    }
}
