package com.memotalk.api.memouser.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "TB_USER_REFRESH_TOKEN")
public class UserRefreshToken {

    @Id
    @NotNull
    private Long userId;

    @NotNull
    @Size(max = 256)
    private String refreshToken;


    public UserRefreshToken(
            Long userId,
            @NotNull @Size(max = 256) String refreshToken
    ) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }
}