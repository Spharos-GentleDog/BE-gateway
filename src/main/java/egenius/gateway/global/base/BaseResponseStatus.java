package egenius.gateway.global.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum BaseResponseStatus {

    /**
     * 200: 요청 성공
     **/
    SUCCESS(HttpStatus.OK, true, 200, "요청에 성공하였습니다."),

    /**
     * 900: 기타 에러
     */
    INTERNAL_SERVER_ERROR(HttpStatus.BAD_REQUEST, false, 900, "Internal server error"),

    /**
     * 요청 실패
     */
    // Token, Code
    TokenExpiredException(HttpStatus.BAD_REQUEST,false, 2001, "토큰이 만료되었습니다."),
    JWT_INVALID(HttpStatus.BAD_REQUEST, false, 2002, "토큰이 유효하지 않습니다."),
    JWT_NOT_EXIST(HttpStatus.BAD_REQUEST, false, 2003, "토큰이 존재하지 않습니다."),
    JWT_CREATE_FAILED(HttpStatus.BAD_REQUEST, false, 2004, "토큰 생성에 실패했습니다."),
    JWT_VALID_FAILED(HttpStatus.BAD_REQUEST, false, 2005, "토큰 검증에 실패했습니다."),
    JWT_NOT_VALID(HttpStatus.BAD_REQUEST, false, 2006, "토큰이 유효하지 않습니다."),
    JWT_NOT_MATCH(HttpStatus.BAD_REQUEST, false, 2007, "토큰이 일치하지 않습니다.");

    private final HttpStatusCode httpStatusCode;
    private final boolean isSuccess;
    private final int code;
    private String message;
}

