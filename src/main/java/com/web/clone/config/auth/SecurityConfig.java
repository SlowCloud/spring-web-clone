package com.web.clone.config.auth;

import com.web.clone.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // csrf 토큰 끄기. JWT 쓸 것이므로 꺼도 됨.
                .csrf(AbstractHttpConfigurer::disable)

                // 접근제어
                .authorizeHttpRequests(authorize -> authorize
                        // 모든 권한이 접근 가능
                        .requestMatchers("/", "/css/**", "/images/**", "/js/**", "h2-console/**").permitAll()
                        // "/", 를 빼둠. 확인을 위해서 ㅇㅇ
                        // USER(게스트) 권한이 있어야 접근 가능
                        .requestMatchers("/api/v1/**").hasRole(Role.USER.name())
                        // 그 외 주소는 인증된 사용자들만 접근 가능
                        .anyRequest().authenticated()
                )


                // oauth2 로그인
                // 아래와 같이 작성하면 기본 설정으로 로그인된다.
                 .oauth2Login(Customizer.withDefaults())

                // oauth2 로그인 구현
//                .oauth2Login(oauth2 -> oauth2
//                        .userInfoEndpoint(...)
//                )

                // 로그아웃
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                );

        return http.build();
    }
}
