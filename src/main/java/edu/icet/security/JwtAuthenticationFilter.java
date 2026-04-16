package edu.icet.security;

import edu.icet.model.entity.User;
import edu.icet.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7).trim();

        if (token.isEmpty() || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String subject = jwtService.extractSubject(token);

        if (subject != null) {
            try {
                Long userId = Long.parseLong(subject);

                userRepository.findById(userId)
                        .filter(this::isApprovedUser)
                        .ifPresent(user -> {
                            if (!jwtService.isTokenValid(token, user)) {
                                return;
                            }

                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            user,
                                            null,
                                            List.of(new SimpleGrantedAuthority("ROLE_" + normalizeRole(user.getRole())))
                                    );

                            authentication.setDetails(
                                    new WebAuthenticationDetailsSource().buildDetails(request)
                            );
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        });
            } catch (NumberFormatException ignored) {
                // Ignore malformed token subjects and continue the filter chain without auth.
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isApprovedUser(User user) {
        return "APPROVED".equalsIgnoreCase(user.getAccessStatus());
    }

    private String normalizeRole(String role) {
        return "ADMIN".equalsIgnoreCase(role) ? "ADMIN" : "CUSTOMER";
    }
}
