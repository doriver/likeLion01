package com.lion.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(auth -> auth.disable())  // csrf 방어기능 비활성
                .headers( x -> x.frameOptions(y -> y.disable())) // H2 디비 쓰려고
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/book/list", "/book/detail", "/mall/list", "/mall/detail"
                                ,"/user/register", "/h2-console", "/demo/**"
                                , "/img/**", "/js/**", "/css/**", "/error/**").permitAll()
                        .requestMatchers("/book/insert", "/book/yes24", "/order/listAll"
                                ,"/order/bookStat", "/user/delete", "/user/list").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(auth -> auth
                        .loginPage("/user/login") // login form
                        .loginProcessingUrl("/user/login") // 스프링이 낚아챔, UserDetailService 구현 객체에서 처리해줘야함
                        .usernameParameter("uid").passwordParameter("pwd")
                        .defaultSuccessUrl("/user/loginSuccess", true) // 로그인후 해야할 일
                        .permitAll()
                )
                .logout( auth -> auth
                        .logoutUrl("/usr/logout")
                        .invalidateHttpSession(true) // 로그아웃시 세션 삭제
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/user/login")
                )
        ;
        return http.build();
    }

}
