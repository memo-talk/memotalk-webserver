package com.memotalk.api.workspace.respository;

import com.memotalk.api.workspace.entity.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkSpaceRepository extends JpaRepository<WorkSpace, Long> {
    Optional<WorkSpace> findByIdAndMemoUser_Email(Long id, String email);

    Optional<Object> deleteByIdAndMemoUser_Email(Long id, String email);

    List<WorkSpace> findAllByMemoUser_EmailOrderByPriorityAsc(String email);

    Optional<WorkSpace> findDistinctTopByMemoUser_EmailOrderByPriorityDesc(String email);

    Optional<WorkSpace> findDistinctTopByMemoUser_EmailOrderByPriorityAsc(String email);
}
