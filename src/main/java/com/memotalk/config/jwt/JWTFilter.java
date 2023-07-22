package com.memotalk.config.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final TokenProvider tokenProvider;

    public JWTFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            String token = resolveToken(request);

            // 토큰 유효성 검사
            if (StringUtils.hasText(token) && this.tokenProvider.validateToken(token)) {
                // 토큰으로부터 이메일 추출
                String email = tokenProvider.getEmailFromToken(token);
                // 인증 객체 초기화
                AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null, AuthorityUtils.NO_AUTHORITIES);
                // 인증 객체에 세부정보(IP 주소, 브라우저 정보 등..)를 생성하여 설정
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 현재 스레드에서 실행 중인 사용자를 나타내는 인증 객체를 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("security context 내에 user authentication 을 설정할 수 없습니다.");
        }
        filterChain.doFilter(request, response);
    }

    // 요청 헤더에서 토큰을 파싱하는 과정
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
