package egenius.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationVendorHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationVendorHeaderFilter.Config> {

    Environment env;

    public AuthorizationVendorHeaderFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }

    public static class Config {

    }

    @Override
    public GatewayFilter apply(Config config) {

        // GatewayFilter를 구현한 익명 클래스를 반환한다.
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 만약 요청 헤더에 'Authorization' 헤더가 없다면 JWT_NOT_EXIST 에러를 반환
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "no authorization header", HttpStatus.UNAUTHORIZED);
            }

            // 만약 요청 헤더에 'Authorization' 헤더가 있다면, 헤더에서 JWT 토큰을 가져온다.
            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            // JWT 토큰에서 'Bearer ' 부분을 제거한다.
            String jwt = authorizationHeader.replace("Bearer ", "");

            // JWT 토큰이 유효하지 않다면 JWT_NOT_VALID 에러를 반환
            if (!isJwtValid(jwt)) {
                // JWT 토큰이 유효하지 않다면, 요청을 통과시키지 않는다.
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            // JWT 토큰이 유효하다면, 요청을 통과시킨다.
            return chain.filter(exchange);
        });
    }

    // JWT 토큰이 유효한지 검사하는 메소드
    private boolean isJwtValid(String jwt) {
        boolean returnValue = true;

        try {
            // JWT 토큰에서 'role' 정보를 가져온다.
            log.info("토큰 검증");
            Claims claims = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                    .parseClaimsJws(jwt).getBody();
            log.info("claims={}",claims);

            // 'role' 정보가 없거나, 만료시간이 지났거나, 'ADMIN'이 아니라면 JWT 토큰이 유효하지 않다.
            String role = claims.get("role", String.class);
            Date exp = claims.getExpiration();
            Date now = new Date();
            log.info("role={}",role);

            // 'role' 정보가 없거나, 만료시간이 지났거나, 'ADMIN'이 아니라면 JWT 토큰이 유효하지 않다.
            if (role == null || role.isEmpty() || exp == null || exp.before(now) || !role.equals("VENDOR")) {
                returnValue = false;
            }

        } catch (Exception e) {
            returnValue = false;
        }

        return  returnValue;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);
        return response.setComplete();

    }

}
