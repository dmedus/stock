package com.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.stock.entidades.servicio.UsuarioService;
import com.stock.utils.reporte.Rol;

@Configuration

@EnableWebSecurity

public class WebSecurityConfig extends WebSecurityConfigurerAdapter{



	@Autowired

	private UsuarioService usuarioServicio;



	@Autowired

	private BCryptPasswordEncoder passwordEncoder;

	

	@Bean

	public DaoAuthenticationProvider authenticationProvider() {

		DaoAuthenticationProvider auth = new DaoAuthenticationProvider();

		auth.setUserDetailsService(usuarioServicio);

		auth.setPasswordEncoder(passwordEncoder);

		return auth;

	}


	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}
		
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers(
				"/js/**",
				"/css/**",
				"/img/**").permitAll()
		.antMatchers("/admin/**").hasRole(Rol.ADMIN.getNombre())
        .antMatchers("/user/**").hasRole(Rol.USER.getNombre())
		.antMatchers("/listarPedidos", "/listarPedidoDetalles", "/verPedidoDetalles/**").hasAnyRole(Rol.USER.getNombre(), Rol.ADMIN.getNombre())
		.antMatchers("/pedidoForm/**", "/eliminarPedido/**", "/confirmarPedido/**", "/pedidoDetalleForm/**", "/eliminarPedidoDetalle/**").hasRole(Rol.ADMIN.getNombre())
		.antMatchers("/cargar-vinos/**").permitAll()
		.anyRequest().authenticated()
		.and()
		.formLogin()
		.loginPage("/login")
		.permitAll()
		.defaultSuccessUrl("/listarClientes", true)
		.and()
		.logout()
		.invalidateHttpSession(true)
		.clearAuthentication(true)
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		.logoutSuccessUrl("/login?logout")
		.permitAll();
	}
}
