package br.com.finsavior;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class FinsaviorApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinsaviorApplication.class, args);
	}

}
