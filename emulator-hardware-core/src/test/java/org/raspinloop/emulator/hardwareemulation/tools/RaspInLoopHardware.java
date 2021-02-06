package org.raspinloop.emulator.hardwareemulation.tools;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.raspinloop.emulator.fmi.ClassLoaderBuilderFactory;
import org.raspinloop.emulator.fmi.FmiReferenceRegister;
import org.raspinloop.emulator.fmi.GsonProperties;
import org.raspinloop.emulator.fmi.HardwareBuilderFactory;
import org.raspinloop.emulator.fmi.HardwareClassFactory;
import org.raspinloop.emulator.hardwareemulation.HardwareEmulation;
import org.raspinloop.emulator.hardwareemulation.HardwareEmulationException;
import org.raspinloop.emulator.hardwareproperties.BoardHardwareProperties;

public class RaspInLoopHardware implements ParameterResolver {

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		Optional<HarwareSimulatedTest> annotation = parameterContext.findAnnotation(HarwareSimulatedTest.class);
		 if ( annotation.isPresent()) {
			 try {
				return createHardware(parameterContext.getDeclaringExecutable(), annotation.get().hwPropertiesFile());
			} catch (URISyntaxException | IOException | HardwareEmulationException e) {
				throw new ParameterResolutionException("Unable to generate HardwareEmulation", e);
			}
		 }
		 return null;
	}
		 
	private HardwareEmulation createHardware(Executable executable, String file) throws URISyntaxException, IOException, HardwareEmulationException {
			 Path path;
			 URL url = executable.getDeclaringClass().getResource(file);
			 if (url != null)
				 path = Paths.get(url.toURI());
			 else
				 path = Paths.get(file);
			 return build(path);
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext arg1) throws ParameterResolutionException {
		boolean annotated = parameterContext.isAnnotated(HarwareSimulatedTest.class);
		if (annotated && parameterContext.getDeclaringExecutable() instanceof Constructor) {
			throw new ParameterResolutionException(
				"@HarwareSimulatedTest is not supported on constructor parameters.");
		}
		return annotated;
	}

	private HardwareEmulation build(Path hwPropertiesFile) throws IOException, HardwareEmulationException {
		byte[] encoded = Files.readAllBytes(hwPropertiesFile);
		String jsonProps = new String(encoded, "UTF-8");
		GsonProperties conf = new GsonProperties(HardwareClassFactory.instance());		
		BoardHardwareProperties hardwareProperties = conf.read(jsonProps);
		
		HardwareBuilderFactory hbf = new ClassLoaderBuilderFactory();
		HardwareEmulation emulationImplementation;
		FmiReferenceRegister referenceRegister = new FmiReferenceRegister();
		emulationImplementation = hbf.createBuilder(hardwareProperties, referenceRegister).build(null);
		return emulationImplementation;
	}

}
