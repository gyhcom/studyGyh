package com.study.gyh.account;

import com.study.gyh.domain.Account;
import com.study.gyh.settings.Profile;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional//Todo @Transactional 붙이지 않으면 에러가 남 persist 상태와 detached 상태에 대해 공부
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

//    @Transactional//Todo @Transactional 붙이지 않으면 에러가 남 persist 상태와 detached 상태에 대해 공부
    public Account processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
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

    public void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        newAccount.generateEmailCheckToken();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("Study Gyh, Join Success");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
            "&email=" + newAccount.getEmail());

        javaMailSender.send(mailMessage);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            new UserAccount(account),
            account.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if (account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }

        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }
        return new UserAccount(account);
    }

    public void completeSignUp(Account account) {
        account.completeSignUp();
        login(account);
    }

    public void updateProfile(Account account, Profile profile) {
        account.setUrl(profile.getUrl());
        account.setOccupation(profile.getOccupation());
        account.setLocation(profile.getLocation());
        account.setBio(profile.getBio());
        // Todo 프로필 이미지

        //@Transactional 과 별개로 Repository Save를 해주지 않으면
        //위의 업데이트는 저장되지 않는다 detach 상태이기 때문에
        accountRepository.save(account);

    }
}
