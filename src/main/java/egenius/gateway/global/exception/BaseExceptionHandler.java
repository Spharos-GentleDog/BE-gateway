package egenius.gateway.global.exception;

import egenius.gateway.global.base.BaseResponse;
import egenius.gateway.global.base.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class BaseExceptionHandler {
    /**
     * 발생한 예외 처리
     */

    // 등록된 에러
    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<?> BaseError(BaseException e) {
        // BaseException의 BaseResponseStatus를 가져와서 BaseResponse를 만들어서 return해줌
        BaseResponse response = new BaseResponse(e.getStatus());
        log.info("BaseException: " + e.getStatus().getMessage());
        return new ResponseEntity<>(response, response.httpStatus());
    }

    // 런타임 에러
    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<?> RuntimeError(RuntimeException e) {
        // BaseException으로 잡히지 않는 RuntimeError는, INTERNAL_SEBVER_ERROR로 처리해줌
        BaseResponse response = new BaseResponse(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        log.info("RuntimeException: " + e.getMessage());
        return new ResponseEntity<>(response, response.httpStatus());
    }

}