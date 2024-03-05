package com.stock;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.stock.utils.reporte.Rol;

public class PasswordGenerator {

	public static void main(String[] args) {
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String rawPassword = "12345";
		String encodedPassword = encoder.encode(rawPassword);
		
		System.out.println(encodedPassword);
		
		
		
	}
	
}
