package com.github.estuaryoss.libs.zephyruploader.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.github.estuaryoss.libs.zephyruploader.service")
@ComponentScan("com.github.estuaryoss.libs.zephyruploader.component")
public class ApplicationConfig {

}
