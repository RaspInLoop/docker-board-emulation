package org.raspinloop.emulator.proxyserver.qemu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class Raspberrypi3bOptions extends EmulatorParam {
	private String image;
}
