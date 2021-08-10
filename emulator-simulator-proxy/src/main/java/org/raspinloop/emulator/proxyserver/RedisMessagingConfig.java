package org.raspinloop.emulator.proxyserver;

import org.raspinloop.emulator.proxyserver.messaging.RedisReceiver;
import org.raspinloop.orchestrator.api.EmulatorStartMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisMessagingConfig {

	@Bean
	RedisMessageListenerContainer container(@Autowired RedisConnectionFactory connectionFactory,
			@Autowired MessageListenerAdapter listenerAdapter) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(listenerAdapter, new PatternTopic("emulator.rapsberry"));
		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter(@Autowired RedisReceiver receiver) {
		MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(receiver, "receiveMessage");
		messageListenerAdapter.setSerializer(new Jackson2JsonRedisSerializer<>(EmulatorStartMessage.class));
		return messageListenerAdapter;
	}

}
