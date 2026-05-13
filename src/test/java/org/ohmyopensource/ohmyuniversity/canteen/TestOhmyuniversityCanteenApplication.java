package org.ohmyopensource.ohmyuniversity.canteen;

import org.springframework.boot.SpringApplication;

public class TestOhmyuniversityCanteenApplication {

	public static void main(String[] args) {
		SpringApplication
				.from(OhmyuniversityCanteenApplication::main)
				.with(TestcontainersConfiguration.class)
				.run(args);
	}
}