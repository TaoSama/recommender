package com.wmw.recommender.config;

import com.wmw.recommender.mapper.UserMapper;
import com.wmw.recommender.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
          .antMatchers("/").permitAll()
          .anyRequest().authenticated()
        .and()
        .formLogin()
          .loginPage("/signin")
          .defaultSuccessUrl("/")
          .permitAll()
          .and()
        .rememberMe()
          .and()
        .logout()
          .logoutRequestMatcher(new AntPathRequestMatcher("/signout"))
          .logoutSuccessUrl("/")
          .permitAll();
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    // 解决静态资源被拦截的问题
    web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userService())
        .passwordEncoder(NoOpPasswordEncoder.getInstance());
  }

  @Bean
  public UserDetailsService userService() {
    return new UserService();
  }

  @Autowired
  @Bean
  public SpringTemplateEngine templateEngine(ITemplateResolver templateResolver){

    SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    templateEngine.setTemplateResolver(templateResolver);
    templateEngine.setEnableSpringELCompiler(true);
    // add dialect spring security
    templateEngine.addDialect(new SpringSecurityDialect());
    return templateEngine;
  }
  //  @Bean
//  @Override
//  public UserDetailsService userDetailsService() {
//    UserDetails user =
//        User.withDefaultPasswordEncoder()
//            .username("username")
//            .password("password")
//            .roles("USER")
//            .build();
//
//    return new InMemoryUserDetailsManager(user);
//  }
}

