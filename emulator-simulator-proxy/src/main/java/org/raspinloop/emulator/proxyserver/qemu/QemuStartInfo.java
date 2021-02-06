package org.raspinloop.emulator.proxyserver.qemu;

import java.time.Instant;

import lombok.Value;

@Value
public class QemuStartInfo {
	public boolean started;
	public int startDuration;
	public Instant startTime;
}