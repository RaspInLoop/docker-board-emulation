package org.raspinloop.emulator.proxyserver;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

import org.raspinloop.emulator.hardwareemulation.IoChange;
import org.raspinloop.emulator.proxyserver.messaging.GpioAction;
import org.raspinloop.emulator.proxyserver.messaging.GpioActionMessageConverter;
import org.raspinloop.emulator.proxyserver.messaging.GpiostateMessageProducer;
import org.raspinloop.emulator.proxyserver.messaging.GpiostateOutboundAdapter;
import org.raspinloop.emulator.proxyserver.simulation.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.expression.FunctionExpression;
import org.springframework.integration.handler.ReactiveMessageHandlerAdapter;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.zeromq.dsl.ZeroMq;
import org.springframework.integration.zeromq.outbound.ZeroMqMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.zeromq.SocketType;
import org.zeromq.ZContext;

@Configuration
@EnableIntegration
public class GpioMessagingConfig {

	@Bean
	ZContext zmqContext() {
		return new ZContext();
	}
		
	@Bean
	public MessageProducerSupport gpiostateInboundAdapter(@Autowired Board board) {
		return new GpiostateMessageProducer(board);
	}

	@Bean
	IntegrationFlow outFlows(
			@Qualifier("gpiostateInboundAdapter")  @Autowired MessageProducerSupport gpiostateInboundAdapter,
			@Autowired GpioActionMessageConverter gpioMessageConverter,			
			@Autowired ZeroMqMessageHandler zeroMqMessageHandler) {
		return IntegrationFlows.from(gpiostateInboundAdapter)
				.<Message<IoChange>, Message<GpioAction>>transform(this::toMessageAction)
				.handle(new ReactiveMessageHandlerAdapter (zeroMqMessageHandler))
				.get();
	}

	@Bean
	IntegrationFlow zmqInFlows(
			@Autowired ZContext context,
			@Autowired GpioActionMessageConverter gpioMessageConverter,
			@Autowired GpiostateOutboundAdapter gpiostateOutboundAdapter,
			@Value("${raspinloop.qemu.inboundzmq.port:5556}") short port) {
		return IntegrationFlows.from(
            ZeroMq.inboundChannelAdapter(context, SocketType.SUB)
                        .bindPort(port)
                        .topics("gpio")
                        .receiveRaw(false)
                        .consumeDelay(Duration.ofMillis(100))
                        .messageConverter(gpioMessageConverter))	
				.transform(this::fromAction)
				.handle(gpiostateOutboundAdapter)
				.get();
	}

	@Bean
	ZeroMqMessageHandler zeroMqMessageHandler(
			@Autowired ZContext context, 
			@Autowired GpioActionMessageConverter gpioMessageConverter,
			@Value("${raspinloop.qemu.outboundzmq.port:5557}") short port) {
		ZeroMqMessageHandler messageHandler = new ZeroMqMessageHandler(context, "tcp://*:"+port, SocketType.PUB);
		messageHandler
				.setTopicExpression(new FunctionExpression<Message<?>>((message) -> message.getHeaders().get("topic")));
		messageHandler.setMessageConverter(gpioMessageConverter);
		return messageHandler;
	}
	
	private  Message<GpioAction> toMessageAction(Message<IoChange> change){
		GpioAction action = toAction(change.getPayload());		
		Map<String, Object> headers = change.getHeaders().entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		return MessageBuilder.withPayload(action).copyHeaders(headers).build();
	}
	
	private  GpioAction toAction(IoChange change){
		GpioAction action;
		switch (change.getChange()) {
		case CLEAR:
			action= GpioAction.clear(change.getBit());
			break;
		case SET:
			action= GpioAction.set(change.getBit());
			break;
		default:
			throw new MessagingException("Unknonw  change type recieved");
		}		
		return action;
	}
	
	private  Message<IoChange> fromMessageAction(Message<GpioAction> action){
		IoChange change = fromAction(action.getPayload());
		Map<String, Object> headers = action.getHeaders().entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		return MessageBuilder.withPayload(change).copyHeaders(headers).build();
	}
	
	private  IoChange fromAction(GpioAction action){
		IoChange change;
		switch (action.getAction()) {
		case CLEAR:
			change= IoChange.clear(action.getBit());
			break;
		case SET:
			change= IoChange.set(action.getBit());
			break;
		default:
			throw new MessagingException("Unknonw  change type recieved");
		}		
		return change;
	}

	
}
