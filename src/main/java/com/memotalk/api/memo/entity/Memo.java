package com.memotalk.api.memo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.memotalk.api.workspace.entity.WorkSpace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_memo")
public class Memo {

    @Id
    @Column
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition="TEXT")
    private String description;

    @CreationTimestamp
    @JsonIgnore
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonIgnore
    private LocalDateTime modifiedAt;

    private boolean isImportant;

    private String s3FileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    @JsonIgnore
    private WorkSpace workspace;

    public Memo(WorkSpace workSpace, String description, String s3FileUrl) {
        this.workspace = workSpace;
        this.description = description;
        this.isImportant = false;
        this.s3FileUrl = s3FileUrl;
    }

    public Memo markImportant() {
        this.isImportant = !this.isImportant;
        return this;
    }
}
