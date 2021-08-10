package org.raspinloop.emulator.proxyserver;

import static org.springframework.integration.dsl.MessageChannels.queue;
import static org.springframework.integration.dsl.Pollers.fixedDelay;
import static org.springframework.integration.dsl.Transformers.objectToString;

import org.raspinloop.emulator.proxyserver.messaging.SimulatedTimeMessageProducer;
import org.raspinloop.emulator.proxyserver.messaging.SimulatedTimeOutboundAdapter;
import org.raspinloop.emulator.proxyserver.messaging.SmartLifeCycleTcpSendingMessageHandler;
import org.raspinloop.emulator.proxyserver.simulation.time.SimulatedClock;
import org.raspinloop.emulator.proxyserver.simulation.time.SimulatedTimeMessage;
import org.raspinloop.emulator.proxyserver.simulation.time.SleepingMessage;
import org.raspinloop.emulator.proxyserver.simulation.time.StartMessage;
import org.raspinloop.emulator.proxyserver.simulation.time.StopMessage;
import org.raspinloop.emulator.proxyserver.simulation.time.WaitingMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArraySingleTerminatorSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableScheduling
public class SimulTimeMessagingConfig {

	private String host = "127.0.0.1";
	
	@Value("${raspinloop.qemu.simultime.port:5554}")
	private int port;

	@Bean
	public MessageProducerSupport simulatedTimeMessageProducer(@Autowired SimulatedClock simulatedClock) {
		return new SimulatedTimeMessageProducer(simulatedClock);
	}
	@Bean
	IntegrationFlow outcomingSimulationTimeFlow(
			@Qualifier("simulatedTimeMessageProducer") @Autowired MessageProducerSupport simulatedTimeMessageProducer,	
			@Autowired  TcpSendingMessageHandler tcpSendingMessageHandler) {
		simulatedTimeMessageProducer.setAutoStartup(false);
	
		return IntegrationFlows.from(simulatedTimeMessageProducer)
				.transform(objectToString())
				.handle(tcpSendingMessageHandler)
				.get();
	}

    @Bean
    public IntegrationFlow controlBusFlow() {
        return IntegrationFlows.from("controlBus")
                  .controlBus()
                  .get();
    }
    
	@Bean
	public IntegrationFlow incomingSimulationTimeFlow( TcpReceivingChannelAdapter tcpReceivingChannelAdapter,
			SimulatedTimeOutboundAdapter simulatedTimeOutboundAdapter) {

		return IntegrationFlows.from(tcpReceivingChannelAdapter).channel(channels -> queue(1000))
				.bridge(bridge -> bridge.poller(fixedDelay(500))).transform(source -> {
					log.info("Transforming: {}", new String((byte[]) source));
					return source;
				}) 
				.transform(objectToString()) // converts message's payload from byte[] to String
				.transform(this::toSimulationTimeMessage)
				.handle(simulatedTimeOutboundAdapter)
				.get();
	}

	@Bean
	public TcpSendingMessageHandler tcpSendingMessageHandler(
			@Autowired AbstractClientConnectionFactory connectionFactory) {
		SmartLifeCycleTcpSendingMessageHandler tcpSendingMessageHandler = new SmartLifeCycleTcpSendingMessageHandler();		
		tcpSendingMessageHandler.setAutoStartup(false);
		tcpSendingMessageHandler.setClientMode(true);
		tcpSendingMessageHandler.setLoggingEnabled(false);
		tcpSendingMessageHandler.setConnectionFactory(connectionFactory);
		return tcpSendingMessageHandler;
	}
	
	@Bean
	public TcpReceivingChannelAdapter tcpReceivingChannelAdapter(
			@Autowired AbstractClientConnectionFactory connectionFactory) {

		// Each connection factory can have only one listener (channel adapter)
		// registered, so it's okay to
		// create it here as an 'anonymous bean' rather than 'fully blown bean'.

		TcpReceivingChannelAdapter tcpReceivingChannelAdapter = new TcpReceivingChannelAdapter();

		tcpReceivingChannelAdapter.setConnectionFactory(connectionFactory);

		tcpReceivingChannelAdapter.setAutoStartup(false); 

		tcpReceivingChannelAdapter.setClientMode(true); // was required to establish the connection for the first time

		// Default interval is one minute - retries are attempted both to re-establish
		// previously available connection
		// and to establish it for the very first time, e.g. when client was started
		// before the server.
		tcpReceivingChannelAdapter.setRetryInterval(10000);

		return tcpReceivingChannelAdapter;
	}

	@Bean
	public AbstractClientConnectionFactory clientCF() {
		TcpNetClientConnectionFactory cf = new TcpNetClientConnectionFactory(host, port);
		cf.setDeserializer(new ByteArraySingleTerminatorSerializer((byte)'\n'));
		return cf;
	}
	
	private SimulatedTimeMessage toSimulationTimeMessage(String message) {
		
		SimulatedTimeMessage result = SleepingMessage.parse(message);
		if (result == null) {
			 result = WaitingMessage.parse(message);
		}
		if (result == null) {
			 result = StartMessage.parse(message);
		}
		if (result == null) {
			 result = StopMessage.parse(message);
		}
		return result;		
	}
}