package org.raspinloop.emulator.fmi;

import java.util.function.BiConsumer;
import java.util.function.Function;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@Data
public class Reference<T> {
	
	private int ref;
	private void setRef(int ref) {
		this.ref =ref;  
	}
	
	private final Class<T> type;
	private final FmiReferenceRegister register;
	private Object object;
	@EqualsAndHashCode.Exclude
	private Function<Object, T> getter;
	@EqualsAndHashCode.Exclude
	private BiConsumer<Object, T> setter;
	private String tags;
	
	/**
     * Uses custom builder class
     */
    public static <T> ReferenceBuilder<T> builder() {
        return new CustomReferenceBuilder<>();
    }
	
    /**
     * Custom  builder class
     */
    private static class CustomReferenceBuilder<T> extends ReferenceBuilder<T> {
        @Override
        public Reference<T> build() {
        	Reference<T> result = super.build();        	        	
        	result.setRef(result.getRegister().add(result));
            return result;
        }
    }
    
}
