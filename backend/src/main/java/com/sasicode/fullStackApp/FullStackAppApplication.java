package com.sasicode.fullStackApp;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.sasicode.fullStackApp.customer.Customer;
import com.sasicode.fullStackApp.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Random;

@SpringBootApplication
public class FullStackAppApplication {

	public static void main(String[] args) {

		ConfigurableApplicationContext configurableApplicationContext =
				SpringApplication.run(FullStackAppApplication.class, args);

		//printBeans(configurableApplicationContext);
	}

	@Bean
	CommandLineRunner commandLineRunner(CustomerRepository customerRepository) {
		return args -> {
			Faker faker = new Faker();
			Name name = faker.name();
			String firstName = name.firstName();
			String lastName = name.lastName();
			Random random = new Random();
			Customer customer = new Customer(
					firstName + " " + lastName,
					firstName.toLowerCase() + "." + lastName.toLowerCase() + "@sasicode.com",
					random.nextInt(16, 99)
			);
			customerRepository.save(customer);
		};
	}


	@Bean
	public Foo getFoo() {
		return new Foo("sasi");
	}

	record Foo(String name) {}

	private static void printBeans(ConfigurableApplicationContext ctx) {
		String[] beanDefinitionNames =
				ctx.getBeanDefinitionNames();
		for (String beanDefinitionName : beanDefinitionNames) {
			System.out.println(beanDefinitionName);
		}
	}

}
