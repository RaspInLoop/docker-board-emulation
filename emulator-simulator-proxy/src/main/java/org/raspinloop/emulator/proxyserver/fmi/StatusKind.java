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
/**
 * Autogenerated by Thrift Compiler (0.10.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.raspinloop.emulator.proxyserver.fmi;

public enum StatusKind implements org.apache.thrift.TEnum {
  DoStepStatus(0),
  PendingStatus(1),
  LastSuccessfulTime(2),
  Terminated(3);

  private final int value;

  private StatusKind(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static StatusKind findByValue(int value) { 
    switch (value) {
      case 0:
        return DoStepStatus;
      case 1:
        return PendingStatus;
      case 2:
        return LastSuccessfulTime;
      case 3:
        return Terminated;
      default:
        return null;
    }
  }
}
