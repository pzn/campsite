package io.github.pzn.test.quarkus;

import io.quarkus.test.common.QuarkusTestResourceConfigurableLifecycleManager;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Map;

public class PostgresTestContainersQuarkusTestResource implements QuarkusTestResourceConfigurableLifecycleManager {

  private static final PostgreSQLContainer postgres =
      new PostgreSQLContainer("postgres:13-alpine")
          .withUsername("campsitetest")
          .withPassword("campsitetest")
          .withDatabaseName("campsitetest");

  @Override
  public Map<String, String> start() {
    postgres.start();
    return Map.of(
        "quarkus.datasource.username", postgres.getUsername(),
        "quarkus.datasource.password", postgres.getPassword(),
        "quarkus.datasource.jdbc.url", postgres.getJdbcUrl());
  }

  @Override
  public void stop() {
    postgres.stop();
  }
}
