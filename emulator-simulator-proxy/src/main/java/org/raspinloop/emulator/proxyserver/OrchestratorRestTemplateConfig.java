package org.raspinloop.emulator.proxyserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
public class OrchestratorRestTemplateConfig extends RestTemplate {
	
	
	@Bean
	public RestTemplate orchestratorRestTemplate(
			@Autowired RestTemplateBuilder builder,
			@Value("${raspinloop.orchestrator.endpoint.scheme:http}") String orchestratorScheme,
			@Value("${raspinloop.orchestrator.endpoint.host:ria.orchestrator.raspinloop.org}") String orchestratorHost,
			@Value("${raspinloop.orchestrator.endpoint.port:80}") short port) {
		UriComponents rootUri = UriComponentsBuilder.newInstance()
			      .scheme(orchestratorScheme).host(orchestratorHost).port(port).build();
		return builder.rootUri(rootUri.toUriString()).build();
	}
	
}
