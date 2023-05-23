package com.stock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	@Bean
	protected UserDetailsService userDetailsService() {
		UserDetails usuario1 = User
				.withUsername("toto")
				.password("$2a$10$DaN7sQ/KKb6tZjv6sNvfQunmFi0PsTdicHcOWOZ3He1hLFzqlzd4y")
				.roles("USER")	
				.build();
		
		UserDetails usuario2 = User
				.withUsername("daian")
				.password("$2a$10$DaN7sQ/KKb6tZjv6sNvfQunmFi0PsTdicHcOWOZ3He1hLFzqlzd4y")
				.roles("ADMIN")	
				.build();
		
		UserDetails usuario3 = User
				.withUsername("dmedus")
				.password("$2a$10$DaN7sQ/KKb6tZjv6sNvfQunmFi0PsTdicHcOWOZ3He1hLFzqlzd4y")
				.roles("SUPER")	
				.build();
		
		return new InMemoryUserDetailsManager(usuario1,usuario2,usuario3);
	}
	
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		String[] staticResources  =  {
//		        "/css/**",
//		        "/img/**",
//		        "/fonts/**",
//		        "/scripts/**",
//		    };
//		
//		http.authorizeRequests()
//		 	.antMatchers(staticResources).permitAll()
//		    .antMatchers("/form/*","/eliminar/*").hasAnyRole("ADMIN","SUPER")
//		    .anyRequest().authenticated()
//		    .and()
//		    .formLogin()
//		        .loginPage("/login")
//		        .permitAll()
//		    .and()
//		    .logout().permitAll();
//	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		String[] staticResources  =  {
		        "/css/**",
		        "/img/**",
		        "/fonts/**",
		        "/scripts/**",
		    };

		    http
		        .authorizeRequests()
		            .antMatchers(staticResources).permitAll()
		            .anyRequest().authenticated()
		            .and()
		        .formLogin()
		            .loginPage("/login").permitAll()
		            .and()
		        .logout().permitAll();
	}
}
