/* (C)2024 */
package com.study.gyh.domain;

import com.study.gyh.account.UserAccount;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NamedEntityGraph(name = "Study.withAllRelations", attributeNodes = {
    @NamedAttributeNode("tags"),
    @NamedAttributeNode("zones"),
    @NamedAttributeNode("managers"),
    @NamedAttributeNode("member")})
@NamedEntityGraph(name = "Study.withZonesAndManagers", attributeNodes = {
    @NamedAttributeNode("zones"),
    @NamedAttributeNode("managers")})
@NamedEntityGraph(name = "Study.withManagers", attributeNodes = {
    @NamedAttributeNode("zones"),
    @NamedAttributeNode("managers")})
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Study {

    @Id @GeneratedValue private Long id;

    @ManyToMany private Set<Account> managers = new HashSet<>();

    @ManyToMany private Set<Account> member = new HashSet<>();

    @Column(unique = true)
    private String path;

    private String title;

    private String shortDescription;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToMany private Set<Tag> tags = new HashSet<>();

    @ManyToMany private Set<Zone> zones = new HashSet<>();

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdateDateTime;

    private boolean recruiting;

    private boolean published;

    private boolean closed;

    private boolean useBanner;

    public void addManager(Account account) {
        this.managers.add(account);
    }

    public boolean isJoinable(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return this.isPublished()
                && this.isRecruiting()
                && !this.member.contains(account)
                && !this.managers.contains(account);
    }

    public boolean isMember(UserAccount userAccount) {
        return this.member.contains(userAccount.getAccount());
    }

    public boolean isManager(UserAccount userAccount) {
        return this.managers.contains(userAccount.getAccount());
    }

    public void publish() {
        if (!this.closed && !this.published) {
            this.published = true;
            this.publishedDateTime = LocalDateTime.now();
        }else{
            throw new RuntimeException(("스터디를 공개 할수 없는 상태입니다. 스터디를 이미 공개했거나 종료했습니다."));
        }
    }

    public void close() {
        if (this.published && !this.closed) {
            this.closed = true;
            this.closedDateTime = LocalDateTime.now();
        }else{
            throw new RuntimeException(("스터디를 종료 할수 없는 상태입니다. 스터디를 이미 종료했거나 종료했습니다."));
        }
    }

    public boolean canUpdateRecruiting() {
        return this.published && this.recruitingUpdateDateTime == null
            || this.recruitingUpdateDateTime.isBefore(LocalDateTime.now().minusHours(1));
    }

    public void startRecruit() {
        if (canUpdateRecruiting()) {
            this.recruiting = true;
            this.recruitingUpdateDateTime = LocalDateTime.now();
        }else{
            throw new RuntimeException("인원 모집을 시작할수 없습니다. 스터디를 공개하거나 한 시간 뒤 다시 시도하세요");
        }
    }

    public void stopRecruit() {
        if (canUpdateRecruiting()) {
            this.recruiting =false;
            this.recruitingUpdateDateTime = LocalDateTime.now();
        } else{
            throw new RuntimeException("인원 모집을 멈출수 없습니다. 스터디를 공개하거나 한 시간 뒤 다시 시도하세요");
        }
    }
}
