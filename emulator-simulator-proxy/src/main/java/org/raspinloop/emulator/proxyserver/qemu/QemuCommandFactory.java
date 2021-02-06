package org.raspinloop.emulator.proxyserver.qemu;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.raspinloop.orchestrator.api.EmulatorParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
/**
 * 
 * @author Frédéric Mahiant
 * 
 *Build specialized instance of QemuCommand based on sub type of EmulatorParam
 *QemuCommand must ebe defined as component to be available in this factory
 */
public class QemuCommandFactory {
	
	private Map<Class<? extends EmulatorParam>, QemuCommand> supportedEmulator;

	public QemuCommandFactory(
			@Autowired List<QemuCommand> qemuCommands) {
		supportedEmulator = qemuCommands.stream()
				.collect(Collectors.toMap(QemuCommand::getParamType, Function.identity()));
	}

	public QemuCommand buildFrom(EmulatorParam param) {
		QemuCommand command = supportedEmulator.get(param.getClass());
		if (command == null) {			
			throw new IllegalArgumentException(param.getClass() + "cannot be found in supported Emulators");
		}
		command.setParameter(param);
		return command;
	}
}
