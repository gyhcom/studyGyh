package com.study.gyh.account;

import com.study.gyh.domain.Account;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AccountController {
    private final SignUpFormValidator signUpFormValidator;
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }


    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors) {
        if (errors.hasErrors()) {
            return "account/sign-up";
        }

        Account account = Account.builder()
            .email(signUpForm.getEmail())
            .nickname(signUpForm.getNickname())
            .password(signUpForm.getPassword()) //Todo encoding 해야함
            .studyCreateByWeb(true)
            .studyUpdatedByWeb(true)
            .studyEnrollmentResultByWeb(true)
            .build();
        Account newAccount = accountRepository.save(account);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        newAccount.generateEmailCheckToken();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("Study Gyh, Join Success");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
            "&email=" + newAccount.getEmail());

        javaMailSender.send(mailMessage);
        return "redirect:/";
    }
}
