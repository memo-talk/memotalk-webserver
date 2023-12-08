package com.memotalk.exception.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    EMAIL_SEND_FAILURE("이메일 전송에 실패하였습니다."),
    AUTHENTICATION_NUMBER_MISMATCH("인증번호가 일치하지 않습니다."),
    NOT_FOUND_MEMO("존재하지 않는 메세지입니다."),
    WORKSPACE_NOT_FOUND("존재하지 않는 워크 스페이스입니다."),
    USER_NOT_FOUND("존재하지 않는 유저입니다."),
    PASSWORD_MISMATCH("비밀번호를 잘못 입력했습니다"),
    EMAIL_ALREADY_EXISTS("이미 가입된 이메일입니다."),
    USER_NOT_FOUND_OR_PASSWORD_MISMATCH("사용자를 찾을 수 없거나 비밀번호가 일치하지 않습니다."),
    TODO_NOT_FOUND("존재하지 않는 TODO 입니다."),
    FILE_UPLOAD_FAIL("파일 업로드에 실패했습니다."),
    FILE_DELETE_FAIL("파일 삭제에 실패했습니다."),
    MULTIPART_FILE_CONVERT_FAIL("MultiPart File을 Convert 하는 중 오류가 발생하였습니다. "),
    INVALID_ACCESS_TOKEN("유효하지 않은 액세스 토큰입니다."),
    NOT_EXPIRED_TOKEN_YET("토큰의 만료기간이 남아있습니다."),
    INVALID_REFRESH_TOKEN("유효하지 않은 리프레시 토큰입니다."),
    NOT_FOUND_EMAIL("유효하지 않은 이메일입니다.");
    private final String message;
}
