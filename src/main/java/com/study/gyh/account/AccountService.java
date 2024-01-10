package com.study.gyh.account;

import com.study.gyh.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional//Todo @Transactional 붙이지 않으면 에러가 남 persist 상태와 detached 상태에 대해 공부
    public void processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(newAccount);
    }
    private Account saveNewAccount(SignUpForm signUpForm) {
        Account account = Account.builder()
            .email(signUpForm.getEmail())
            .nickname(signUpForm.getNickname())
            .password(passwordEncoder.encode(signUpForm.getPassword()))
            .studyCreateByWeb(true)
            .studyUpdatedByWeb(true)
            .studyEnrollmentResultByWeb(true)
            .build();
        Account newAccount = accountRepository.save(account);
        return newAccount;
    }

    private void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        newAccount.generateEmailCheckToken();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("Study Gyh, Join Success");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
            "&email=" + newAccount.getEmail());

        javaMailSender.send(mailMessage);
    }

}
