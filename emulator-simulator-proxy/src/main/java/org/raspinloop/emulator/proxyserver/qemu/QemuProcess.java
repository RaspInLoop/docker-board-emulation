package org.raspinloop.emulator.proxyserver.qemu;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Instant;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.raspinloop.orchestrator.api.EmulatorParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class QemuProcess {

	@Autowired 
	List<QemuStatusAware> statusListener;


	@Autowired
	QemuCommandFactory qemuCommandFactory;
	
	@Getter
	private QemuCommand command;

	private Process qemu;

	@Getter
	private QemuStartInfo startStatus;

	public boolean isAlive() {
		return qemu.isAlive(); 
	}
	
	public void startAndWait(EmulatorParam param, int startTimeout) throws Exception {
		command = qemuCommandFactory.buildFrom(param);
		// prepare qemu setup
		ProcessBuilder builder = new ProcessBuilder(command.toCommandList());
		try {
			// launch qemu
			log.info("starting: {}", command.toCommandList().stream().collect(Collectors.joining(" ")));
			// builder.inheritIO();
			qemu = builder.start();
			inheritIO(qemu.getErrorStream(), l -> log.error("qemu: {}", l));
			inheritIO(qemu.getInputStream(), l -> log.info("qemu: {}", l));
			// monitor qemu starting
			startStatus = waitForStart(qemu, command.getSshPort(), startTimeout);
			if (startStatus.isStarted()) {				
				notifystarted(startStatus);				
				// monitor qemu
				qemu.waitFor();
			} else {				
				qemu.destroyForcibly();
				qemu.waitFor();
				throw new TimeoutException("start timeout reached");
			}

		} catch (InterruptedException | IOException | TimeoutException e) {
			log.error("QEMU run error: " + e.getMessage());
			throw e;
		} finally {
			// stop qemu
			notifyStopped();
		}
	}

	private void notifyStopped() {
		statusListener.forEach(l -> l.onStopped());
	}

	private void notifystarted(QemuStartInfo info) {
		log.info("QEMU started in {} seconds", startStatus.getStartDuration()); 
		statusListener.forEach(l -> l.onStarted(info));
	}

	private static void inheritIO(final InputStream src, Consumer<String> consumer) {
		new Thread(new Runnable() {
			public void run() {
				try (Scanner sc = new Scanner(src)) {
					while (sc.hasNextLine()) {
						consumer.accept(sc.nextLine());
					}
				}
			}
		}).start();
	}

	private QemuStartInfo waitForStart(Process qemu, short port, int timeout) throws InterruptedException {
		int incrementInSec = 1;
		boolean exited = false;
		boolean sshReady = false;
		int remainingSec = timeout;
		while (!exited && remainingSec > 0) {
			exited = qemu.waitFor(incrementInSec, TimeUnit.SECONDS);
			if (!exited) {
				sshReady = testAvailablility(port, "SSH-2.0-OpenSSH");
				log.trace("Process not yet ready on port {}", port);
				if (sshReady) {
					break;
				}
			}
			remainingSec -= incrementInSec;
		}
		boolean started = !exited && sshReady;
		return new QemuStartInfo(started, timeout - remainingSec, Instant.now());
	}

	private boolean testAvailablility(int port, String responseStartsWith) {
		boolean toReturn = false;

		try (Socket client1 = new Socket()) {
			client1.setSoTimeout(1000);
			client1.connect(new InetSocketAddress("localhost", port), 1000);
			InputStream stream = client1.getInputStream();
			byte[] response = new byte[4096];
			stream.read(response, 0, response.length);
			String serverReturnString = new String(response);
			log.debug("TestAvailablility: serverReturnString = {} ", serverReturnString);
			if (serverReturnString.toLowerCase().startsWith(responseStartsWith.toLowerCase())) 
				toReturn = true;
		} catch (Exception ex) { // SocketException for connect, IOException for
									// the read.
			log.trace("TestAvailable - Could not connect to server.  Exception info: {}", ex.getMessage());
		}
		return toReturn;
	}

	public Thread getClosingThread() {
		return new Thread() {
			public void run() {
				qemu.destroy();
			}
		};
	}

}
