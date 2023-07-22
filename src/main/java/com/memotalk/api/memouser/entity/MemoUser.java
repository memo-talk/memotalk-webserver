package com.memotalk.api.memouser.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.memotalk.api.memouser.entity.enumeration.Lock;
import com.memotalk.api.workspace.entity.WorkSpace;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
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

    @Id
    @Column
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
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

    public MemoUser(String email, String password) {
        this.email = email;
        this.password = password;
        this.lock = Lock.UNLOCK;
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
