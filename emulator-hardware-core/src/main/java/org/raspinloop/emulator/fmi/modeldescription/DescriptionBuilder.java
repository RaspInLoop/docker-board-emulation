package org.raspinloop.emulator.fmi.modeldescription;

import org.raspinloop.emulator.fmi.modeldescription.Fmi2ScalarVariable.Real;

import lombok.Data;
import lombok.experimental.Accessors;

public class DescriptionBuilder {

	public BooleanBuilder getBooleanBuilder(CausalityType type) {
		return new BooleanBuilder(type);
	}
	
	public RealBuilder getRealBuilder(CausalityType type) {
		return new RealBuilder(type);
	}

	public enum CausalityType {
		OUTPUT, INPUT;
	}

	@Data
	@Accessors(chain = true)
	public abstract class Builder {
		private CausalityType type;

		Builder(CausalityType type) {
			this.type = type;
		}

		private String name;
		private long ref;
		private String descritpion;

		public abstract Fmi2ScalarVariable build();

		protected String getCausality() {
			if (CausalityType.OUTPUT.equals(type)) {
				return "output";
			} else {
				return "input";
			}
		}
	}

	public class BooleanBuilder extends Builder {
		BooleanBuilder(CausalityType type) {
			super(type);
		}

		public Fmi2ScalarVariable build() {
			Fmi2ScalarVariable sc = new Fmi2ScalarVariable();
			org.raspinloop.emulator.fmi.modeldescription.Fmi2ScalarVariable.Boolean scb = new Fmi2ScalarVariable.Boolean();
			sc.setBoolean(scb);
			sc.setName(getName());
			sc.setValueReference(getRef());
			sc.setDescription(getDescritpion());
			sc.setCausality(getCausality());
			sc.setVariability("continuous");
			return sc;
		}
	}

	public class RealBuilder extends Builder {

		RealBuilder(CausalityType type) {
			super(type);
		}

		public Fmi2ScalarVariable build() {
			Fmi2ScalarVariable sc = new Fmi2ScalarVariable();
			Real scr = new Fmi2ScalarVariable.Real();
			sc.setReal(scr);
			sc.setName(getName());
			sc.setValueReference(getRef());
			sc.setDescription(getDescritpion());
			sc.setCausality(getCausality());
			sc.setVariability("continuous");
			return sc;
		}
	}
}
