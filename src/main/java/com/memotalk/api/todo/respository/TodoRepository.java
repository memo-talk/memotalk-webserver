package com.memotalk.api.todo.respository;

import com.memotalk.api.todo.entity.Todo;
import com.memotalk.api.todo.entity.enumeration.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByWorkspace_Id(Long workspaceId);
    void deleteByWorkspace_MemoUser_EmailAndId(String email, Long todoId);
    Todo findByWorkspace_MemoUser_EmailAndId(String email, Long todoId);
    List<Todo> findAllByWorkspace_IdAndStatus(Long workspaceId, Status done);

    void deleteAllByWorkspace_MemoUser_EmailAndWorkspace_Id(String email, Long workspaceId);
    Todo findByIdAndWorkspace_Id(Long todoId, Long workspaceId);

    @Modifying
    @Query("DELETE FROM Todo t WHERE t.status = 'Done' AND t.workspace.memoUser.id = :memoUserId AND t.workspace.id = :workspaceId")
    void deleteDoneTodosByUserId(@Param("memoUserId") String memoUserId, @Param("workspaceId") Long workspaceId);
}
