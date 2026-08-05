package com.example.kiki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KikiApplication {
	public static void main(String[] args) {
		SpringApplication.run(KikiApplication.class, args);
	}
}

// & "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U temi -d kiki_db -h localhost

//INSERT INTO products (id, name, description, price, image_url, stock_quantity, created_at)
//VALUES (nextval('product_sequence'), 'Electric Kettle', 'Electric Glass and Steel Hot Tea Water Kettle - 1.7-Liter', 30.74, 'images/products/electric-glass-and-steel-hot-water-kettle.webp', 50, now());