package com.memotalk.api.memo.respository;

import com.memotalk.api.memo.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {
    List<Memo> findAllByWorkspace_Id(Long workspaceId);
    List<Memo> findAllByWorkspace_IdAndDescriptionContainingOrderByCreatedAtDesc(Long workspaceId, String keyword);

    Memo findByWorkspace_Id(Long workspaceId);
}
