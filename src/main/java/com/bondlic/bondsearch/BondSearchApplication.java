package com.bondlic.bondsearch;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@SpringBootApplication
public class BondSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(BondSearchApplication.class, args);
	}

}
