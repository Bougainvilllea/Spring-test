package org.example.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories(basePackages = ["infrastructure.persistence.jpa.repository"])
@EntityScan(basePackages = ["infrastructure.persistence.jpa.entity"])
@ComponentScan(basePackages = [
    "infrastructure",
    "application",
    "infrastructure",
    "shared",
    "infrastructure.exception"
])
class ApplicationLab3

fun main(args: Array<String>) {
    runApplication<ApplicationLab3>(*args)
}
