package com.musicradio.radio.auth.security;

import com.musicradio.radio.auth.entity.AdminStatus;
import com.musicradio.radio.auth.entity.AdminUser;
import com.musicradio.radio.auth.repository.AdminUserRepository;
import com.musicradio.radio.auth.util.CookieUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AdminUserRepository adminUserRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private AdminUser activeAdmin;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        activeAdmin = AdminUser.builder()
                .id(1)
                .email("admin@musicradio.local")
                .fullName("System Administrator")
                .status(AdminStatus.ACTIVE)
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("doFilterInternal with valid Bearer header authenticates user into SecurityContext")
    void doFilterInternal_WithValidBearerHeader_AuthenticatesUser() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer valid.jwt.token");
        when(jwtTokenProvider.validateToken("valid.jwt.token")).thenReturn(true);
        when(jwtTokenProvider.extractEmail("valid.jwt.token")).thenReturn("admin@musicradio.local");
        when(adminUserRepository.findByEmail("admin@musicradio.local")).thenReturn(Optional.of(activeAdmin));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo("admin@musicradio.local");
        assertThat(auth.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("doFilterInternal with valid access_token cookie authenticates user into SecurityContext")
    void doFilterInternal_WithValidCookie_AuthenticatesUser() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        Cookie[] cookies = new Cookie[]{new Cookie(CookieUtils.ACCESS_TOKEN_COOKIE_NAME, "valid.cookie.token")};
        when(request.getCookies()).thenReturn(cookies);
        when(jwtTokenProvider.validateToken("valid.cookie.token")).thenReturn(true);
        when(jwtTokenProvider.extractEmail("valid.cookie.token")).thenReturn("admin@musicradio.local");
        when(adminUserRepository.findByEmail("admin@musicradio.local")).thenReturn(Optional.of(activeAdmin));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo("admin@musicradio.local");

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("doFilterInternal without token leaves SecurityContext unauthenticated")
    void doFilterInternal_WithoutToken_ProceedsUnauthenticated() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("doFilterInternal with invalid token does not authenticate user")
    void doFilterInternal_WithInvalidToken_DoesNotAuthenticate() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer invalid.token");
        when(jwtTokenProvider.validateToken("invalid.token")).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("doFilterInternal with inactive administrator does not set authentication")
    void doFilterInternal_WithInactiveAdmin_DoesNotAuthenticate() throws ServletException, IOException {
        AdminUser inactiveAdmin = AdminUser.builder()
                .id(2)
                .email("inactive@musicradio.local")
                .status(AdminStatus.INACTIVE)
                .build();

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer inactive.admin.token");
        when(jwtTokenProvider.validateToken("inactive.admin.token")).thenReturn(true);
        when(jwtTokenProvider.extractEmail("inactive.admin.token")).thenReturn("inactive@musicradio.local");
        when(adminUserRepository.findByEmail("inactive@musicradio.local")).thenReturn(Optional.of(inactiveAdmin));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();

        verify(filterChain).doFilter(request, response);
    }
}
