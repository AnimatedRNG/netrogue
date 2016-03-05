package animated.spferical.netrogue.networking;

import java.util.Random;
import java.util.TreeMap;

/**
 * Every model in the game must extend {@link NetworkObject}!
 * 
 * @author srinivas
 */
public abstract class NetworkObject {

	public int lastUpdate;
	public long ID;
	public NetworkObject parent;
	
	public NetworkObject(NetworkObject parent) {
		this.lastUpdate = 0;
		this.ID = new Random().nextLong();
		this.parent = parent;
		this.children = new TreeMap<>();
		this.attributes= new TreeMap<>();
	}
	
	public NetworkObject(NetworkObject parent, long ID) {
		this(parent);
		this.ID = ID;
	}
	
	public void put(String name, Object obj) {
		this.attributes.put(name, obj);
	}
	
	public Object remove(String name) {
		return this.attributes.remove(name);
	}
	
	public Object get(String name) {
		return this.attributes.get(name);
	}
	
	public boolean has(String name) {
		return this.attributes.containsKey(name);
	}
	
	public boolean check(String name) {
		Object val = this.attributes.get(name);
		if (val instanceof Boolean)
			return (Boolean) val;
		else
			return false;
	}
	
	public void putChild(Long ID, NetworkObject child) {
		this.children.put(ID, child);
	}
	
	public NetworkObject removeChild(Long ID) {
		return this.children.remove(ID);
	}
	
	public NetworkObject getChild(Long ID) {
		return this.children.get(ID);
	}
	
	public boolean hasChild(Long ID) {
		return this.children.containsKey(ID);
	}
	
	// ID -> Child
	private TreeMap<Long, NetworkObject> children;
	
	// Name -> Value
	private TreeMap<String, Object> attributes;
}
