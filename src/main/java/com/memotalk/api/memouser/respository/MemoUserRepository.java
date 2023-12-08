package com.memotalk.api.memouser.respository;

import com.memotalk.api.memouser.entity.MemoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemoUserRepository extends JpaRepository<MemoUser, Long> {
    boolean existsByEmail(String email);
    Optional<MemoUser> findByEmail(String email);
    MemoUser findById(String id);
}
