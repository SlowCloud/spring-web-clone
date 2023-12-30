package com.web.clone.config.auth;

import com.web.clone.domain.user.Role;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
//    private final CustomOAuth2UserService customOAuth2UserService;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 암호화 도구(BCrypt 알고리즘 사용)
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails admin = User.builder() // 사용자
                .username("admin") // id/username
                .password(passwordEncoder().encode("adminpassword")) // password
                .roles(Role.ADMIN.name()) // Role.ADMIN.name()
                .build();
        return new InMemoryUserDetailsManager(admin); // 사용자 리포지토리(in-memory)
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // csrf 토큰 끄기. ~~JWT 쓸 것이므로 꺼도 됨.~~ 일단 끔.
                .csrf(AbstractHttpConfigurer::disable)

//                .cors(Customizer.withDefaults())
//                .httpBasic(Customizer.withDefaults())

                // 접근제어
                .authorizeHttpRequests(authorize -> authorize
                        // 모든 권한이 접근 가능
                        .requestMatchers("/", "/css/**", "/images/**", "/js/**", "h2-console/**").permitAll()
                        // USER(게스트) 권한이 있어야 접근 가능
                        .requestMatchers("/api/v1/**").hasAnyRole("ADMIN")
                        // 그 외 주소는 인증된 사용자들만 접근 가능
                        .anyRequest().authenticated()
                )

                // oauth2 이전에 기본적인 아이디/비밀번호 로그인부터 구현
                .formLogin(Customizer.withDefaults())


                // oauth2 로그인
                // 아래와 같이 작성하면 기본 설정으로 로그인된다.
//                .oauth2Login(Customizer.withDefaults())

                // oauth2 로그인 구현
//                .oauth2Login(oauth2 -> oauth2
//                        .userInfoEndpoint(endpoint -> endpoint
//                                .userService(customOAuth2UserService)
//                        )
//                )

                // 로그아웃 기본체.
                // 이 녀석으로 하면 /logout으로 이동하면 알아서 로그아웃이 된다.
                .logout(Customizer.withDefaults());

                // 로그아웃
//                .logout(logout -> logout
//                        // 쿠키 제거. JSESSIONID가 스프링 시큐리티에서 쓰는 세션 쿠키
//                        // 이게 기본적으로 이루어지는 줄 알았는데 그렇지 않음. 허참
//                        // 생략한 내용은 기본 설정으로 되는 줄 알았는데, 그렇지 않나 보다.
//                        // 기본 구현을 찾아봐야지
//                        .deleteCookies("JSESSIONID")
//                        .logoutSuccessUrl("/")
//                );

        return http.build();
    }
}
