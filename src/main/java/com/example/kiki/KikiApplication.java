package com.example.kiki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class KikiApplication {
	public static void main(String[] args) {
		SpringApplication.run(KikiApplication.class, args);
	}
}
//https://kebab-rule-blandness.ngrok-free.dev/api/

// & "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U temi -d kiki_db -h localhost

//SELECT id, created_at, email, username, role, phone_number FROM users;
