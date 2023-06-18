package com.memotalk.api.workspace.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.memotalk.api.memo.entity.Memo;
import com.memotalk.api.todo.entity.Todo;
import com.memotalk.api.memouser.entity.MemoUser;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Getter
@Entity
@Builder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "tb_work_space")
public class WorkSpace {

    @Id
    @Column
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private Integer imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memo_user_id", nullable = false)
    @JsonIgnore
    private MemoUser memoUser;

    @CreationTimestamp
    @JsonIgnore
    private LocalDateTime createdAt;

    @Column
    @UpdateTimestamp
    private LocalDateTime modifiedAt;

    @Column
    private Long priority;

    @JsonIgnore
    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Memo> memoList;

    @JsonIgnore
    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Todo> todoList;

    @Transient
    private Random random = new Random();

    public WorkSpace(MemoUser memoUser, Long priority){
        this.title = "워크 스페이스";
        this.memoUser = memoUser;
        this.imageId = generateRandomNumber();
        this.priority = priority;
    }

    public WorkSpace(MemoUser memoUser, Long priority, String title){
        this.title = title;
        this.memoUser = memoUser;
        this.imageId = generateRandomNumber();
        this.priority = priority;
    }

    public void modify(String title){
        this.title = title;
    }

    public void moveTop(Long priority){
        this.priority = priority - 1L;
    }

    private int generateRandomNumber() {
        return random.nextInt(8);
    }
}
