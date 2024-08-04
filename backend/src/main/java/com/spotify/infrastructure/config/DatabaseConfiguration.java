package com.spotify.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories({ "com.spotify.usercontext.repository",
        "com.spotify.catalogcontext.repository" })
@EnableJpaAuditing
public class DatabaseConfiguration {
}
