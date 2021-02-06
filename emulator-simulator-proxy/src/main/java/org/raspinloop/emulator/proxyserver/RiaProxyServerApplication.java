package org.raspinloop.emulator.proxyserver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableIntegration
public class RiaProxyServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RiaProxyServerApplication.class, args);
	}
	
    @Bean("singleThreaded")
    public ExecutorService singleThreadedExecutor() {
        return Executors.newSingleThreadExecutor();
    }
}