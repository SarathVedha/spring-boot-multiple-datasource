package com.vedha.source;

import com.vedha.source.mysql.entity.UserEntity;
import com.vedha.source.mysql.repository.UserRepository;
import com.vedha.source.postgres.entity.RoleEntity;
import com.vedha.source.postgres.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class SpringBootMultipleDataSourceApplication implements CommandLineRunner {

	private final UserRepository userRepository;

	private final RoleRepository roleRepository;

	private final JdbcTemplate postgresJdbcTemplate; // filed name as bean name

	private final JdbcClient postgresJdbcClient; // filed name as bean name

	private final JdbcTemplate mysqlJdbcTemplate; // filed name as bean name

	private final JdbcClient mysqlJdbcClient; // filed name as bean name

	public static void main(String[] args) {
		SpringApplication.run(SpringBootMultipleDataSourceApplication.class, args);
	}

	@Override
	public void run(String... args) {

		log.info("Inserting user data in User");
		UserEntity vedha = userRepository.save(UserEntity.builder().name("Vedha").email("vedha@gmail.com").build());
		log.warn("UserEntity: {}", vedha);
		log.info("Inserting user data in User Done");

		log.info("Inserting role data in User");
		RoleEntity save = roleRepository.save(RoleEntity.builder().name("Admin").description("admin").build());
		log.warn("RoleEntity: {}", save);
		log.info("Inserting role data in User Done");


		List<UserEntity> user = mysqlJdbcClient.sql("SELECT * FROM users").query(UserEntity.class).list();
		log.warn("mysqlJdbcClient UserEntity's: {}", user);

		List<RoleEntity> query = postgresJdbcTemplate.query("SELECT * FROM roles", (rs, rowNum) -> RoleEntity.builder()
				.id(rs.getLong("id"))
				.name(rs.getString("name"))
				.description(rs.getString("description"))
				.build());
		log.warn("postgresJdbcTemplate RoleEntity's: {}", query);
	}
}
