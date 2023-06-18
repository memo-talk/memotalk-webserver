package com.memotalk.api.todo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.memotalk.api.todo.entity.enumeration.Status;
import com.memotalk.api.workspace.entity.WorkSpace;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@Table(name = "tb_todo")
public class Todo {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private Status status;

    @Lob
    private String description;

    @CreationTimestamp
    @JsonIgnore
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonIgnore
    private LocalDateTime modifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    @JsonIgnore
    private WorkSpace workspace;

    public Todo(WorkSpace workspace, String description){
        this.workspace = workspace;
        this.description = description;
        this.status = Status.TODO;
    }

    public void changeStatus(){
        if(this.status.equals(Status.TODO)) {
            this.status = Status.DONE;
        }
        else if (this.status.equals(Status.DONE)) {
            this.status = Status.TODO;
        }
    }

    public void update(String description){
        this.description = description;
    }
}
