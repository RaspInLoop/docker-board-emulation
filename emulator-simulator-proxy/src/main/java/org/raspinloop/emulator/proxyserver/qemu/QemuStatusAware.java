package org.raspinloop.emulator.proxyserver.qemu;

public interface QemuStatusAware {
	default void onLaunched(QemuStartInfo info) {
	}

	default void onStopped(int returnCode) {
	}

	default void onStopping() {
	}

}
