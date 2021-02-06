package org.raspinloop.emulator.proxyserver.qemu;

public interface QemuStatusAware {
	void onStarted(QemuStartInfo info);
	void onStopped();
	

}
