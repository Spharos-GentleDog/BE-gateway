package egenius.gateway.filter;

import global.base.BaseResponseStatus;
import global.exception.BaseException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends
        AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    Environment env;

    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }

    public static class Config {

    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return Mono.error(new BaseException(BaseResponseStatus.JWT_NOT_EXIST));
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer ", "");

            log.info("jwt 토큰 : " + jwt);

            if (!isJwtValid(jwt)) {
                return Mono.error(new BaseException(BaseResponseStatus.JWT_NOT_VALID));
            }

            return chain.filter(exchange);
        });
    }

    private boolean isJwtValid(String jwt) {
        boolean returnValue = true;

        try {
            String secret = env.getProperty("token.secret");
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).getBody();
            log.info("claims : " + claims.toString());

            String role = claims.get("role", String.class);
            log.info("role : " + role);
            Date exp = claims.getExpiration();
            Date now = new Date();

            if (role == null || role.isEmpty() || exp == null || exp.before(now) || !role.equals(
                    "USER")) {
                returnValue = false;
            }

        } catch (Exception e) {
            log.info("try-catch 에러 발생!");
            returnValue = false;
        }

        return returnValue;
    }

}
