package com.study.gyh.settings;

import com.study.gyh.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Notifications {

    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;
    private boolean studyEnrollmentResultByEmail;
    private boolean studyEnrollmentResultByWeb;
    private boolean studyUpdatedByEmail;
    private boolean studyUpdatedByWeb;

    public Notifications(Account account) {
        this.studyCreatedByEmail = studyCreatedByEmail;
        this.studyCreatedByWeb = studyCreatedByWeb;
        this.studyEnrollmentResultByEmail = studyEnrollmentResultByEmail;
        this.studyEnrollmentResultByWeb = studyEnrollmentResultByWeb;
        this.studyUpdatedByEmail = studyUpdatedByEmail;
        this.studyUpdatedByWeb = studyUpdatedByWeb;
    }
}
