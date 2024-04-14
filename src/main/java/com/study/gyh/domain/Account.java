/* (C)2024 */
package com.study.gyh.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id @GeneratedValue private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime joinedAt;

    private String bio;

    private String url;

    private String occupation;

    private String location;

    // 기본적으로 String은 varchar(255)로 매팽된다.
    // 이미지는 그거보다 사이즈가 커지기 때문에 Lob로 매핑으로 해준다.
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb = true;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb = true;

    private boolean studyUpdatedByWeb = true;
    private boolean studyUpdatedByEmail;
    private LocalDateTime emailCheckTokenGeneratedAt;

    @ManyToMany private Set<Tag> tags = new HashSet<>();

    @ManyToMany private Set<Zone> zones = new HashSet<>();

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSEndConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusMinutes(5));
    }

    public boolean isManagerOf(Study study) {
        /*

           한참 검색해보았다.. 이것이 무엇인가 결국 호출한 값을 가지고 현재 Account가 Study의 관리자인지
           확인하는 부분이다.

           isManagerOf 메서드는 주어진 UserAccount 객체가 해당 Study 객체의 관리자인지 여부를 확인하는 역할을 합니다.
           구체적으로 말하자면, isManagerOf 메서드는 다음을 수행합니다:

           메서드에 전달된 UserAccount 객체를 사용하여 해당 사용자가 Study 객체의 관리자인지 확인합니다.
           managers 집합에 사용자의 계정이 포함되어 있는지를 확인합니다.
           만약 사용자의 계정이 managers 집합에 포함되어 있다면 true를 반환하고, 그렇지 않으면 false를 반환합니다.

           따라서 isManager 메서드를 호출하면 해당 UserAccount 객체가 관리자인지 여부를 확인할 수 있습니다.

        */
        return study.getManagers().contains(this);
    }
}
