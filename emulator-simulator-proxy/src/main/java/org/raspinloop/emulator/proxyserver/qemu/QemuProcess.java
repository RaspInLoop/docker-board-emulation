package org.raspinloop.emulator.proxyserver.qemu;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class QemuProcess {

	@Autowired
	List<QemuStatusAware> statusListener;

	@Autowired
	QemuCommandFactory qemuCommandFactory;

	@Getter
	@Setter
	private QemuCommand command;

	private Process qemu;
	
	private CopyOnWriteArrayList<String> startLogBuilder =  new CopyOnWriteArrayList<>(); 

	@Getter
	private QemuStartInfo startStatus;

	public boolean isAlive() {
		return qemu.isAlive();
	}
	
	public void copyImageLocally() throws IOException {
		if (command == null) {
			throw new RuntimeException("command must be set before calling copyImageLocally()");
		}			
		Path to = Paths.get("/raspberry/", command.getImage());
		Path from = Paths.get("/data/raspberry/", command.getImage());
		try {
			log.debug("Copying image {} to {}", from, to);
			Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
			log.debug("image copied");
		} catch (IOException e) {
			log.error("Cannot copy file from {} to {}: {}", from, to , e.getMessage());
			throw e;
		}
	}

	public void startAndWaitInBackground(int startTimeout)
			throws InterruptedException, IOException, TimeoutException {
		if (command == null) {
			throw new RuntimeException("command must be set before calling copyImageLocally()");
		}	
		Runtime.getRuntime().addShutdownHook(getClosingThread());
		// prepare qemu setup
		ProcessBuilder builder = new ProcessBuilder(command.toCommandList());
		try {
			// launch qemu
			log.info("starting: {}", command.toCommandList().stream().collect(Collectors.joining(" ")));
			qemu = builder.start();
			inheritIO(qemu.getErrorStream(), l -> log.error("qemu: {}", l));
			inheritIO(qemu.getInputStream(), l -> { 
				log.info("qemu: {}", l);
				startLogBuilder.add(l);			
			});
			// monitor qemu starting
			QemuStartInfo bootStartStatus = waitForStart(qemu, startTimeout);
			if (!bootStartStatus.isStarted()) {
				qemu.destroyForcibly();
				qemu.waitFor();
				throw new TimeoutException("start timeout reached");
			}
			
			QemuStartInfo sshStartStatus = waitForSsh(qemu, command.getSshPort(), startTimeout);
			if (!sshStartStatus.isStarted()) {
				qemu.destroyForcibly();
				qemu.waitFor();
				throw new TimeoutException("start timeout reached");
			}
			startStatus = new QemuStartInfo(true,
					bootStartStatus.getStartDuration() + sshStartStatus.getStartDuration(),
					sshStartStatus.getStartTime());
			notifyLaunched(startStatus);
			// monitor qemu
			CompletableFuture.supplyAsync(() -> {
				try {
					return qemu.waitFor();
				} catch (InterruptedException e) {
					log.error("QEMU run error: " + e.getMessage());
					return -1;
				}
			}).thenAccept(this::notifyStopped);

		} catch (InterruptedException | IOException | TimeoutException e) {
			log.error("QEMU start error: " + e.getMessage());
			throw e;
		}
	}

	private void notifyStopped(int returnCode) {
		statusListener.forEach(QemuStatusAware::onStopping);
		statusListener.forEach(l -> l.onStopped(returnCode));
	}

	private void notifyLaunched(QemuStartInfo info) {
		log.info("QEMU started in {} seconds", startStatus.getStartDuration());
		statusListener.forEach(l -> l.onLaunched(info));
	}

	private static void inheritIO(final InputStream src, Consumer<String> consumer) {
		new Thread(() -> {
				try (Scanner sc = new Scanner(src)) {
					while (sc.hasNextLine()) {
						consumer.accept(sc.nextLine());
					}
				}
			}).start();
	}

	private QemuStartInfo waitForStart(Process qemu, int timeout) throws InterruptedException {
		int incrementInSec = 1;
		int lastread = 0;
		boolean exited = false;
		int remainingSec = timeout;
		while (!exited && remainingSec > 0) {
			exited = qemu.waitFor(incrementInSec, TimeUnit.SECONDS);
			if (!exited) {
				if (startLogBuilder.size() > 0 ) {
					int i = lastread;
					for (; i < startLogBuilder.size(); i++) {
						if (startLogBuilder.get(i).contains("Raspbian GNU/Linux 10 raspberrypi")) {
							return new QemuStartInfo(true, timeout - remainingSec, Instant.now());
						}					
					}
					lastread=i;
				}
			}
			remainingSec -= incrementInSec;
		}
		return new QemuStartInfo(false, timeout - remainingSec, Instant.now());
	}

	private QemuStartInfo waitForSsh(Process qemu, short port, int timeout) throws InterruptedException {
		int incrementInSec = 1;
		boolean exited = false;
		boolean sshReady = false;
		int remainingSec = timeout;
		while (!exited && remainingSec > 0) {
			exited = qemu.waitFor(incrementInSec, TimeUnit.SECONDS);
			if (!exited) {
				sshReady = testAvailablility(port, "SSH-2.0-OpenSSH");				
				if (sshReady) {
					break;
				}
				log.trace("Process not yet ready on port {}", port);
			}
			remainingSec -= incrementInSec;
		}
		if (exited) {
			log.trace("Process exited during start");
		}
		if (sshReady) {
			log.trace("SSH ready during start");
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
			toReturn = true;
		} catch (Exception ex) { // SocketException for connect, IOException for
									// the read.
			log.trace("TestAvailable - Could not connect to server.  Exception info: {}", ex.getMessage());
		}
		return toReturn;
	}

	public Thread getClosingThread() {
		return new Thread() {
			@Override
			public void run() {
				if (qemu != null) {
					qemu.destroy();
				}
			}
		};
	}


}
