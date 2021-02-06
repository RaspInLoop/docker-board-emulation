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

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.10.0)", date = "2017-03-01")

public class Instance implements org.apache.thrift.TBase<Instance, Instance._Fields>, java.io.Serializable, Cloneable, Comparable<Instance> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Instance");

  private static final org.apache.thrift.protocol.TField INSTANCE_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("instanceName", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField COMPONENT_REF_FIELD_DESC = new org.apache.thrift.protocol.TField("componentRef", org.apache.thrift.protocol.TType.I64, (short)2);
  private static final org.apache.thrift.protocol.TField GUID_FIELD_DESC = new org.apache.thrift.protocol.TField("GUID", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField STATE_FIELD_DESC = new org.apache.thrift.protocol.TField("state", org.apache.thrift.protocol.TType.I32, (short)4);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new InstanceStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new InstanceTupleSchemeFactory();

  public java.lang.String instanceName; // required
  public long componentRef; // required
  public java.lang.String GUID; // required
  /**
   * 
   * @see ModelState
   */
  public ModelState state; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    INSTANCE_NAME((short)1, "instanceName"),
    COMPONENT_REF((short)2, "componentRef"),
    GUID((short)3, "GUID"),
    /**
     * 
     * @see ModelState
     */
    STATE((short)4, "state");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // INSTANCE_NAME
          return INSTANCE_NAME;
        case 2: // COMPONENT_REF
          return COMPONENT_REF;
        case 3: // GUID
          return GUID;
        case 4: // STATE
          return STATE;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __COMPONENTREF_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.INSTANCE_NAME, new org.apache.thrift.meta_data.FieldMetaData("instanceName", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.COMPONENT_REF, new org.apache.thrift.meta_data.FieldMetaData("componentRef", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.GUID, new org.apache.thrift.meta_data.FieldMetaData("GUID", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.STATE, new org.apache.thrift.meta_data.FieldMetaData("state", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, ModelState.class)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Instance.class, metaDataMap);
  }

  public Instance() {
  }

  public Instance(
    java.lang.String instanceName,
    long componentRef,
    java.lang.String GUID,
    ModelState state)
  {
    this();
    this.instanceName = instanceName;
    this.componentRef = componentRef;
    setComponentRefIsSet(true);
    this.GUID = GUID;
    this.state = state;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Instance(Instance other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetInstanceName()) {
      this.instanceName = other.instanceName;
    }
    this.componentRef = other.componentRef;
    if (other.isSetGUID()) {
      this.GUID = other.GUID;
    }
    if (other.isSetState()) {
      this.state = other.state;
    }
  }

  public Instance deepCopy() {
    return new Instance(this);
  }

  @Override
  public void clear() {
    this.instanceName = null;
    setComponentRefIsSet(false);
    this.componentRef = 0;
    this.GUID = null;
    this.state = null;
  }

  public java.lang.String getInstanceName() {
    return this.instanceName;
  }

  public Instance setInstanceName(java.lang.String instanceName) {
    this.instanceName = instanceName;
    return this;
  }

  public void unsetInstanceName() {
    this.instanceName = null;
  }

  /** Returns true if field instanceName is set (has been assigned a value) and false otherwise */
  public boolean isSetInstanceName() {
    return this.instanceName != null;
  }

  public void setInstanceNameIsSet(boolean value) {
    if (!value) {
      this.instanceName = null;
    }
  }

  public long getComponentRef() {
    return this.componentRef;
  }

  public Instance setComponentRef(long componentRef) {
    this.componentRef = componentRef;
    setComponentRefIsSet(true);
    return this;
  }

  public void unsetComponentRef() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __COMPONENTREF_ISSET_ID);
  }

  /** Returns true if field componentRef is set (has been assigned a value) and false otherwise */
  public boolean isSetComponentRef() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __COMPONENTREF_ISSET_ID);
  }

  public void setComponentRefIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __COMPONENTREF_ISSET_ID, value);
  }

  public java.lang.String getGUID() {
    return this.GUID;
  }

  public Instance setGUID(java.lang.String GUID) {
    this.GUID = GUID;
    return this;
  }

  public void unsetGUID() {
    this.GUID = null;
  }

  /** Returns true if field GUID is set (has been assigned a value) and false otherwise */
  public boolean isSetGUID() {
    return this.GUID != null;
  }

  public void setGUIDIsSet(boolean value) {
    if (!value) {
      this.GUID = null;
    }
  }

  /**
   * 
   * @see ModelState
   */
  public ModelState getState() {
    return this.state;
  }

  /**
   * 
   * @see ModelState
   */
  public Instance setState(ModelState state) {
    this.state = state;
    return this;
  }

  public void unsetState() {
    this.state = null;
  }

  /** Returns true if field state is set (has been assigned a value) and false otherwise */
  public boolean isSetState() {
    return this.state != null;
  }

  public void setStateIsSet(boolean value) {
    if (!value) {
      this.state = null;
    }
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case INSTANCE_NAME:
      if (value == null) {
        unsetInstanceName();
      } else {
        setInstanceName((java.lang.String)value);
      }
      break;

    case COMPONENT_REF:
      if (value == null) {
        unsetComponentRef();
      } else {
        setComponentRef((java.lang.Long)value);
      }
      break;

    case GUID:
      if (value == null) {
        unsetGUID();
      } else {
        setGUID((java.lang.String)value);
      }
      break;

    case STATE:
      if (value == null) {
        unsetState();
      } else {
        setState((ModelState)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case INSTANCE_NAME:
      return getInstanceName();

    case COMPONENT_REF:
      return getComponentRef();

    case GUID:
      return getGUID();

    case STATE:
      return getState();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case INSTANCE_NAME:
      return isSetInstanceName();
    case COMPONENT_REF:
      return isSetComponentRef();
    case GUID:
      return isSetGUID();
    case STATE:
      return isSetState();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof Instance)
      return this.equals((Instance)that);
    return false;
  }

  public boolean equals(Instance that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_instanceName = true && this.isSetInstanceName();
    boolean that_present_instanceName = true && that.isSetInstanceName();
    if (this_present_instanceName || that_present_instanceName) {
      if (!(this_present_instanceName && that_present_instanceName))
        return false;
      if (!this.instanceName.equals(that.instanceName))
        return false;
    }

    boolean this_present_componentRef = true;
    boolean that_present_componentRef = true;
    if (this_present_componentRef || that_present_componentRef) {
      if (!(this_present_componentRef && that_present_componentRef))
        return false;
      if (this.componentRef != that.componentRef)
        return false;
    }

    boolean this_present_GUID = true && this.isSetGUID();
    boolean that_present_GUID = true && that.isSetGUID();
    if (this_present_GUID || that_present_GUID) {
      if (!(this_present_GUID && that_present_GUID))
        return false;
      if (!this.GUID.equals(that.GUID))
        return false;
    }

    boolean this_present_state = true && this.isSetState();
    boolean that_present_state = true && that.isSetState();
    if (this_present_state || that_present_state) {
      if (!(this_present_state && that_present_state))
        return false;
      if (!this.state.equals(that.state))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetInstanceName()) ? 131071 : 524287);
    if (isSetInstanceName())
      hashCode = hashCode * 8191 + instanceName.hashCode();

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(componentRef);

    hashCode = hashCode * 8191 + ((isSetGUID()) ? 131071 : 524287);
    if (isSetGUID())
      hashCode = hashCode * 8191 + GUID.hashCode();

    hashCode = hashCode * 8191 + ((isSetState()) ? 131071 : 524287);
    if (isSetState())
      hashCode = hashCode * 8191 + state.getValue();

    return hashCode;
  }

  @Override
  public int compareTo(Instance other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetInstanceName()).compareTo(other.isSetInstanceName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetInstanceName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.instanceName, other.instanceName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetComponentRef()).compareTo(other.isSetComponentRef());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetComponentRef()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.componentRef, other.componentRef);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetGUID()).compareTo(other.isSetGUID());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetGUID()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.GUID, other.GUID);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetState()).compareTo(other.isSetState());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetState()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.state, other.state);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("Instance(");
    boolean first = true;

    sb.append("instanceName:");
    if (this.instanceName == null) {
      sb.append("null");
    } else {
      sb.append(this.instanceName);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("componentRef:");
    sb.append(this.componentRef);
    first = false;
    if (!first) sb.append(", ");
    sb.append("GUID:");
    if (this.GUID == null) {
      sb.append("null");
    } else {
      sb.append(this.GUID);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("state:");
    if (this.state == null) {
      sb.append("null");
    } else {
      sb.append(this.state);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class InstanceStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public InstanceStandardScheme getScheme() {
      return new InstanceStandardScheme();
    }
  }

  private static class InstanceStandardScheme extends org.apache.thrift.scheme.StandardScheme<Instance> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Instance struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // INSTANCE_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.instanceName = iprot.readString();
              struct.setInstanceNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // COMPONENT_REF
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.componentRef = iprot.readI64();
              struct.setComponentRefIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // GUID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.GUID = iprot.readString();
              struct.setGUIDIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // STATE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.state = ModelState.findByValue(iprot.readI32());
              struct.setStateIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, Instance struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.instanceName != null) {
        oprot.writeFieldBegin(INSTANCE_NAME_FIELD_DESC);
        oprot.writeString(struct.instanceName);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(COMPONENT_REF_FIELD_DESC);
      oprot.writeI64(struct.componentRef);
      oprot.writeFieldEnd();
      if (struct.GUID != null) {
        oprot.writeFieldBegin(GUID_FIELD_DESC);
        oprot.writeString(struct.GUID);
        oprot.writeFieldEnd();
      }
      if (struct.state != null) {
        oprot.writeFieldBegin(STATE_FIELD_DESC);
        oprot.writeI32(struct.state.getValue());
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class InstanceTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public InstanceTupleScheme getScheme() {
      return new InstanceTupleScheme();
    }
  }

  private static class InstanceTupleScheme extends org.apache.thrift.scheme.TupleScheme<Instance> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Instance struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetInstanceName()) {
        optionals.set(0);
      }
      if (struct.isSetComponentRef()) {
        optionals.set(1);
      }
      if (struct.isSetGUID()) {
        optionals.set(2);
      }
      if (struct.isSetState()) {
        optionals.set(3);
      }
      oprot.writeBitSet(optionals, 4);
      if (struct.isSetInstanceName()) {
        oprot.writeString(struct.instanceName);
      }
      if (struct.isSetComponentRef()) {
        oprot.writeI64(struct.componentRef);
      }
      if (struct.isSetGUID()) {
        oprot.writeString(struct.GUID);
      }
      if (struct.isSetState()) {
        oprot.writeI32(struct.state.getValue());
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Instance struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(4);
      if (incoming.get(0)) {
        struct.instanceName = iprot.readString();
        struct.setInstanceNameIsSet(true);
      }
      if (incoming.get(1)) {
        struct.componentRef = iprot.readI64();
        struct.setComponentRefIsSet(true);
      }
      if (incoming.get(2)) {
        struct.GUID = iprot.readString();
        struct.setGUIDIsSet(true);
      }
      if (incoming.get(3)) {
        struct.state = ModelState.findByValue(iprot.readI32());
        struct.setStateIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}
