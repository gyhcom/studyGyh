package com.study.gyh.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("local")
@Component
public class ConsoleEmailService implements EmailService {
    //필요 없다고 해서 지웠는데.. 알고보니 부트 실행시 properties가 local일때
    //emailservice 빈을 못 찾아서 에러가 났는데 local일땐 메일이 날라가지 않고 log로 찍어주고
    //dev일때는 실제 메일이 날라간다.
    @Override
    public void sendEmail(EmailMessage emailMessage) {
        log.info("sent email: {}", emailMessage.getMessage());
    }
}
