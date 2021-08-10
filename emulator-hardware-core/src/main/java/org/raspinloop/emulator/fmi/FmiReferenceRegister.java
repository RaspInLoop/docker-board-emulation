package org.raspinloop.emulator.fmi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class FmiReferenceRegister {
	


	Map<Class<?>, Map<Integer, Object>> register = new HashMap<>();
	AtomicInteger refcount = new AtomicInteger(0);

	public <T> Reference.ReferenceBuilder<T> getBuilder(Class<T> type) {		
		return Reference.<T>builder().register(this).type(type);
	}
	
	public <T> int add(Reference<T> refObj) {
		Map<Integer, Object> typedRegister = register.computeIfAbsent(refObj.getType(),
				key -> new HashMap<Integer, Object>());
		int ref = refcount.incrementAndGet();
		typedRegister.put(ref, refObj);
		return ref;
	}

	public <T> Reference<T> get(Object obj, String tags, Class<T> type) {
		Map<Integer, Object> typedRegister = register.get(type);
		if (typedRegister != null) {
			return typedRegister.values().stream()
			.map(o -> (Reference<T>)o)
			.filter(o -> o.getObject().equals(obj))
			.filter(o -> matchTags(o.getTags(), tags))
			.findFirst()
			.orElse(null);
		} 
		return null;
	}

	private Boolean matchTags(String referencetag, String tags) {
		return Stream.of(tags.split(",")).allMatch(referencetag::contains);
	}

	public <T> Reference<T> get(int ref, Class<T> type) {
		Map<Integer, Object> typedRegister = register.get(type);
		if (typedRegister != null) {
			Object reference = typedRegister.get(ref);
			return (Reference<T>) reference;
		}
		return null;
	}
}
