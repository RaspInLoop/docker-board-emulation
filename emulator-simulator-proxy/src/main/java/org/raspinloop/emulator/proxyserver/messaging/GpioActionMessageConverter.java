package org.raspinloop.emulator.proxyserver.messaging;

import java.nio.charset.StandardCharsets;

import org.springframework.integration.support.MutableMessageHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GpioActionMessageConverter implements MessageConverter {

	@Override
	public Object fromMessage(Message<?> message, Class<?> targetClass) {
		Object payload = message.getPayload();
		GpioAction gpioAction = GpioAction.class.cast(payload);
		String msg = String.format("%s %d3", gpioAction.getAction(),gpioAction.getBit() );
		return msg.getBytes(StandardCharsets.US_ASCII);			
	}

	@Override
	public Message<GpioAction> toMessage(Object payload, MessageHeaders headers) {
		Assert.isInstanceOf(byte[].class, payload);
		String msg = new String((byte[]) payload, StandardCharsets.US_ASCII);
		String[] token = msg.split("\\s");
		if (token.length != 2) {
			log.error("Invalid message received: {}", msg);
			throw new MessagingException("Invalid message received");
		}
		byte bit;
		try {
			 bit = Byte.parseByte(token[1]);
		} catch (NumberFormatException e) {
			log.error("Cannot convert 'bit' in message received: {}", msg);
			throw new MessagingException("Invalid message received");
		}		
		GpioAction gpio;
		switch (token[0].toLowerCase()) {
		case "set":
			gpio = GpioAction.set(bit);
			break;
		case "clear":
			gpio = GpioAction.clear(bit);
			break;
		default:
			return null;
		}			
		return new GenericMessage<>(gpio, new MutableMessageHeaders(headers));
	}

}
