package giorgiaformicola.capstone.security;

import giorgiaformicola.capstone.entities.User;
import giorgiaformicola.capstone.exceptions.UnauthorizedException;
import giorgiaformicola.capstone.services.UsersService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;


@Component
public class TokenFilter extends OncePerRequestFilter {

    private final TokenTools tokenTools;
    private final UsersService usersService;

    public TokenFilter(TokenTools tokenTools, UsersService usersService) {
        this.tokenTools = tokenTools;
        this.usersService = usersService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
                throw new UnauthorizedException("Invalid token supplied in the authorization header");
            String accessToken = authorizationHeader.replace("Bearer ", "");
            tokenTools.verifyToken(accessToken);
            UUID userId = this.tokenTools.extractIdFromToken(accessToken);
            User authenticatedUser = this.usersService.findById(userId);
            if (!authenticatedUser.isEnabled()) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"ACCOUNT_DISABLED\",\"message\":\"Your account has been disabled\"}");
                return;
            }
            Authentication authentication = new UsernamePasswordAuthenticationToken(authenticatedUser, null, authenticatedUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            response.sendError(401, ex.getMessage());
        }

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return new AntPathMatcher().match("/auth/**", request.getServletPath());
    }
}
