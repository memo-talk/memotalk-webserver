package com.memotalk.api.memouser.respository;

import com.memotalk.api.memouser.entity.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {
    UserRefreshToken findByUserId(Long userId);
    UserRefreshToken findByUserIdAndRefreshToken(Long userId, String refreshToken);
}
