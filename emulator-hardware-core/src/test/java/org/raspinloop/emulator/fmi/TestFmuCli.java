package org.raspinloop.emulator.fmi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.raspinloop.emulator.fmi.FmuCLI;

import uk.org.webcompere.systemstubs.SystemStubs;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.security.SystemExit;
import uk.org.webcompere.systemstubs.stream.SystemErr;

@ExtendWith(SystemStubsExtension.class)
class TestFmuCli {

	@SystemStub
	private SystemErr systemErr;
	
	@SystemStub
	private SystemExit systemExit;

	@Test
	void testUsage() throws Exception {

		int exitCode = SystemStubs.catchSystemExit(() -> {
		
		FmuCLI.main(new String[] {}); // no args to test Usage message
		});
		assertEquals(1,  exitCode);
		Optional<String> found = systemErr.getLines()
		.filter(s -> "java -cp Core org.raspinloop.emulator.emulator.fmi.FmuCLI 'fmuFilename.fmu' 'jsonConfigFilename' ".equals(s))
		.findAny();
		assertTrue(found.isPresent());
		
	}
	
	@Test
	void test() throws Exception {
		Path tmp = Files.createTempDirectory("Raspinloop_test");
		Path filename = tmp.resolve("test.fmu");
		File jsonConfigFileName = new File(getClass().getClassLoader().getResource("defaultDummyBoard.json").getFile());
		
		
		FmuCLI.main(new String[] {filename.toString(), jsonConfigFileName.getAbsolutePath()}); // no args to test Usage message

		systemErr.getLines()
		.filter(StringUtils::isNotBlank)
		.forEach(l -> System.out.println("ERR :"+l));
		assertEquals(0, systemErr.getLines().filter(StringUtils::isNotBlank).count());
		
	}
}
