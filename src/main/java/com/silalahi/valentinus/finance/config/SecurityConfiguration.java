package com.silalahi.valentinus.finance.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private DataSource dataSource;
	
	private static final String SQL_LOGIN = "select u.username as username,p.password as password, active "
			+ "from s_user u "
			+ "inner join s_user_password p on p.id_user = u.id "
			+ "where username = ? ";
	
	private static final String SQL_ROLE = "select u.username as username,p.permission_value as authority "
			+ "from s_user u "
			+ "inner join s_role r on u.id_role = r.id "
			+ "inner join s_role_permission rp on rp.id_role = r.id "
			+ "inner join s_permission p on rp.id_permission = p.id "
			+ "where u.username = ?";

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// TODO Auto-generated method stub
		super.configure(auth);
		auth
			.jdbcAuthentication()
			.dataSource(dataSource)
			.usersByUsernameQuery(SQL_LOGIN)
			.authoritiesByUsernameQuery(SQL_ROLE)
			.passwordEncoder(passwordEncoder());
	}

	@Bean
	private PasswordEncoder passwordEncoder() {
		// TODO Auto-generated method stub
		return new BCryptPasswordEncoder(13);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		super.configure(http);
		http
			.authorizeRequests()
			.anyRequest().authenticated()
			.and().logout().permitAll()
			.and().formLogin().defaultSuccessUrl("/home", true)
			.loginPage("/login")
			.permitAll();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		// TODO Auto-generated method stub
		super.configure(web);
		web.ignoring()
				.antMatchers("/api/callback/bni/payment")
				.antMatchers("/info")
				.antMatchers("/js/*")
				.antMatchers("/img/*")
				.antMatchers("/css/*");
	}
	
	public static class ApiWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// TODO Auto-generated method stub
			super.configure(http);
			http.antMatcher("/api/client/**")
							.authorizeRequests()
							.anyRequest().authenticated().and().httpBasic()
							.and().csrf().disable();
		}
		
	}
	
	@Bean
	public SpringSecurityDialect springSecurityDialect() {
		return new SpringSecurityDialect();
	}
	
}
