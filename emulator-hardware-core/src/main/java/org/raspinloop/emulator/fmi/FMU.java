/*******************************************************************************
 * Copyright 2018 RaspInLoop
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.raspinloop.emulator.fmi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.raspinloop.emulator.fmi.modeldescription.Constants;
import org.raspinloop.emulator.fmi.modeldescription.Fmi2ScalarVariable;
import org.raspinloop.emulator.fmi.modeldescription.Fmi2VariableDependency;
import org.raspinloop.emulator.fmi.modeldescription.FmiModelDescription;
import org.raspinloop.emulator.fmi.modeldescription.FmiModelDescription.LogCategories.Category;
import org.raspinloop.emulator.hardwareemulation.HardwareEmulation;
import org.raspinloop.emulator.hardwareproperties.HardwareProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FMU {

	private File fmuFileName;

	public interface Locator {
		URL resolve(URL url);
	}
	
	public FMU(File fmuFileName) {
		this.fmuFileName = fmuFileName;
	}

	public void generate(HardwareProperties hwProperties) throws FMUGenerateException {
		HardwareBuilderFactory hbf = new ClassLoaderBuilderFactory();
		HardwareEmulation emulationImplementation;
		FmiReferenceRegister referenceRegister = new FmiReferenceRegister();
		try {
			emulationImplementation = hbf.createBuilder(hwProperties, referenceRegister).build(null);
		} catch (Exception e) {
			throw new FMUGenerateException("Cannot generate fmu file: " + e.getMessage(), e);
		}
		if (emulationImplementation != null) {

			try (ZipOutputStream fmuzip = new ZipOutputStream(new FileOutputStream(fmuFileName))) {			

				byte[] modelDescription = null;

				modelDescription = generateDescription(emulationImplementation);

				fmuzip.putNextEntry(new ZipEntry("modelDescription.xml"));
				copy(modelDescription, fmuzip);
				fmuzip.closeEntry();

				fmuzip.putNextEntry(new ZipEntry("model.png"));
				copy(this.getClass().getResourceAsStream("model.png"), fmuzip);
				fmuzip.closeEntry();

				fmuzip.putNextEntry(new ZipEntry("sources/"));
				fmuzip.closeEntry();

				fmuzip.putNextEntry(new ZipEntry("documentation/"));
				fmuzip.closeEntry();

				// put coSimulation lib
				fmuzip.putNextEntry(new ZipEntry("binaries/linux64/ril_fmi.so"));
				copy(this.getClass().getResourceAsStream("binaries/linux64/ril_fmi.so"), fmuzip);
				fmuzip.closeEntry();

			}

			catch (IOException | JAXBException e) {
				throw new FMUGenerateException("Cannot generate fmu file: " + e.getMessage(), e);
			}
		}

	}

	private void copy(InputStream in, OutputStream out) throws IOException {
		if (in == null)
			return;
		byte[] buffer = new byte[1024];
		while (true) {
			int readCount = in.read(buffer);
			if (readCount < 0) {
				break;
			}
			out.write(buffer, 0, readCount);
		}
	}

	private void copy(byte[] bytes, OutputStream out) throws IOException {
		out.write(bytes, 0, bytes.length);
		out.flush();
	}

	byte[] generateDescription(HardwareEmulation ch) throws IOException, JAXBException {

		FmiModelDescription model = new FmiModelDescription();
		model.setAuthor(Constants.AUTHOR);
		model.setCopyright(Constants.COPYRIGHT);
		// TODO model.setDefaultExperiment(DefaultExperiment)
		// TODO model.setDescription(String)
		model.setFmiVersion(Constants.VERSION);
		try {
			model.setGenerationDateAndTime(getXMLGregorianCalendarNow());
		} catch (DatatypeConfigurationException e) {
			log.warn("Cannot determine generation date {}", e.getLocalizedMessage());
		}
		model.setGenerationTool(Constants.GENERATION_TOOL);
		model.setGuid(ch.getProperties().getGuid());
		model.setLicense(Constants.LICENSE);
		FmiModelDescription.LogCategories logs = new FmiModelDescription.LogCategories();
		addLogCategory(logs, "logAll");
		addLogCategory(logs, "logError");
		addLogCategory(logs, "logFmiCall");
		addLogCategory(logs, "logEvent");
		model.setLogCategories(logs);
		model.setModelName(ch.getProperties().getType());
		model.setNumberOfEventIndicators(1L);

		FmiModelDescription.CoSimulation cosimulation = new FmiModelDescription.CoSimulation();
		cosimulation.setCanHandleVariableCommunicationStepSize(true);
		cosimulation.setModelIdentifier(Constants.CO_SIMULATION_IDENTIFIER);
		model.getModelExchangeAndCoSimulation().add(cosimulation);

		FmiModelDescription.ModelStructure structure = new FmiModelDescription.ModelStructure();
		structure.setOutputs(new Fmi2VariableDependency());
		model.setModelStructure(structure);

		FmiModelDescription.ModelVariables variables = new FmiModelDescription.ModelVariables();

		for (Fmi2ScalarVariable scalarVar : ch.getModelVariables()) {
			variables.getScalarVariable().add(scalarVar);
		}
		model.setModelVariables(variables);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		JAXBContext jaxbContext = JAXBContext.newInstance(FmiModelDescription.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.marshal(model, os);

		// TODO FmiModelDescription.TypeDefinitions typeDefinition = new
		// FmiModelDescription.TypeDefinitions();
		// TODO model.setTypeDefinitions(typeDefinition);

		// TODO model.setUnitDefinitions(UnitDefinitions)
		// TODO model.setVariableNamingConvention(String)
		// TODO model.setVendorAnnotations(new Fmi2Annotation());
		// TODO model.setVersion("")

		return os.toByteArray();
	}

	private void addLogCategory(FmiModelDescription.LogCategories logs, String name) {
		Category logAll = new Category();
		logAll.setName(name);
		logs.getCategory().add(logAll);
	}

	public XMLGregorianCalendar getXMLGregorianCalendarNow() throws DatatypeConfigurationException {
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
		return datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
	}
}
