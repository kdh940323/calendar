package com.office.calendar.config;

import com.office.calendar.member.security.MemberAccessDeniedHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity      //spring security 객체로 동작시킴
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();

    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable());

        http // 인터셉터의 역할
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/css/*",
                                "/js/*",
                                "/img/*",
                                "/member/signup",
                                "/member/signup_confirm",
                                "/member/signin",
                                "/member/findpassword",
                                "/member/findpassword_confirm",
                                "/member/signin_result").permitAll()
                        .requestMatchers("/planner/**").hasAnyRole("USER")
                        .anyRequest().authenticated() // 위 요청들 외 다른 요청들은 반드시 요청받아라
                );

        http
                .formLogin(login -> login
                        .loginPage("/member/signin")
                        .loginProcessingUrl("/member/signin_confirm")
                        .usernameParameter("id")
                        .passwordParameter("pw")

                        // spring security가 인증에 성공했을 경우
                        .successHandler((request, response, authentication) -> {
                            log.info("signin successHandler()");

                            User user = (User) authentication.getPrincipal();
                            String targetURI = "/member/signin_result?loginedID=" + user.getUsername();

                            response.sendRedirect(targetURI);

                        })

                        // spring security가 인증에 실패했을 경우
                        .failureHandler((request, response, exception) -> {
                            log.info("signin failureHandler()");

                            String targetURI = "/member/signin_result";

                            response.sendRedirect(targetURI);

                        })
                );

        http
                .logout(logout -> logout
                        .logoutUrl("/member/signout_confirm")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            log.info("signout logoutSuccessHandler()");

                            String targetURI = "/";
                            response.sendRedirect(targetURI);

                        })
                );


        http
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(new MemberAccessDeniedHandler()));

        return http.build();
    }
}
