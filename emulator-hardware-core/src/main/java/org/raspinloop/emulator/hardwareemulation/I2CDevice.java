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
package org.raspinloop.emulator.hardwareemulation;

public interface I2CDevice {

	int getAddress();

	void write(byte data) throws HardwareEmulationException;

	void write(byte[] data, int offset, int size) throws HardwareEmulationException;

	void write(byte[] buffer) throws HardwareEmulationException;

	void write(int address, byte data) throws HardwareEmulationException;

	void write(int address, byte[] data, int offset, int size) throws HardwareEmulationException;

	int read() throws HardwareEmulationException;

	int read(byte[] data, int offset, int size) throws HardwareEmulationException;

	int read(int address) throws HardwareEmulationException;

	int read(int address, byte[] data, int offset, int size) throws HardwareEmulationException;

	int read(byte[] writeData, int writeOffset, int writeSize, byte[] readData, int readOffset, int readSize) throws HardwareEmulationException;

}
