package animated.spferical.netrogue.networking;

import java.util.HashMap;

public abstract class LocalCache {
	
	public static synchronized void put(String name, Object object) {
		conditionalInit();
		data.put(name, object);
	}

	public static synchronized Object get(String name) {
		conditionalInit();
		return data.get(name);
	}
	
	public static synchronized Object remove(String name) {
		conditionalInit();
		return data.remove(name);
	}
	
	public static synchronized boolean has(String name) {
		conditionalInit();
		return data.containsKey(name);
	}
	
	private static void conditionalInit() {
		if (data == null)
			data = new HashMap<>();
	}
	
	private static HashMap<String, Object> data;
}
