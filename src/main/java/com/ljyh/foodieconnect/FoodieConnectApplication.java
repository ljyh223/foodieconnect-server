package com.ljyh.foodieconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FoodieConnectApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodieConnectApplication.class, args);
	}

}
