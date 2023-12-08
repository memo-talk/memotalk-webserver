package com.memotalk.api.memouser.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.memotalk.api.memouser.entity.enumeration.Lock;
import com.memotalk.api.workspace.entity.WorkSpace;
import com.memotalk.oauth.ProviderType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "tb_memo_user")
public class MemoUser {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userSeq;

    @Column(length = 64, unique = true)
    @NotNull
    @Size(max = 64)
    private String id; // 사용자에게 부여되는 고유 아이디

    @Setter
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String password;

    @Enumerated(EnumType.STRING)
    @Column
    @JsonIgnore
    private Lock lock;

    @CreationTimestamp
    @JsonIgnore
    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "memoUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkSpace> workSpaceList;

    @Column
    private ProviderType providerType;

    public MemoUser(String email, String password) {
        this.email = email;
        this.password = password;
        this.lock = Lock.UNLOCK;
    }

    public MemoUser(String id, String name, String email, ProviderType providerType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.lock = Lock.UNLOCK;
        this.providerType = providerType;
    }

    public void lock() {
        this.lock = Lock.LOCK;
    }

    public void unlock() {
        this.lock = Lock.UNLOCK;
    }

    public void resetPassword(String password) {
        this.password = password;
    }
}
