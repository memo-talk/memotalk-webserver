package com.memotalk.api.workspace.respository;

import com.memotalk.api.workspace.dto.WorkSpaceResponseDTO;
import com.memotalk.api.workspace.entity.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkSpaceRepository extends JpaRepository<WorkSpace, Long> {
    Optional<WorkSpace> findByIdAndMemoUser_Email(Long id, String email);
    Optional<Object> deleteByIdAndMemoUser_Email(Long id, String email);
    List<WorkSpace> findAllByMemoUser_Email(String email);
    @Query(value = "SELECT MAX(w.id) FROM WorkSpace w WHERE w.memoUser.email = :email")
    Long findMaxIdByMemoUser_Email(@Param("email") String email);

}
