package animated.spferical.netrogue.networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

/**
 * Every model in the game must extend {@link NetworkObject}!
 * 
 * @author srinivas
 */
public abstract class NetworkObject implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4277851132396689715L;
	public long lastUpdate;
	public long ID;
	public long parent;
	
	public NetworkObject() {
		this.lastUpdate = 0;
		this.ID = new Random().nextLong();
		this.children = new TreeMap<>();
		this.attributes= new TreeMap<>();
	}
	
	public NetworkObject(int ID) {
		this();
		this.ID = ID;
	}
	
	public NetworkObject(NetworkObject copy, boolean mustCopy) {
		if (!mustCopy)
			throw new RuntimeException("INVALID COPY");
		else
		{
			this.ID = copy.ID;
			this.parent = copy.parent;
			this.lastUpdate = copy.lastUpdate;
			this.children = copy.getAllChildren();
			this.attributes = copy.getAllAttributes();
		}
	}
	
	@Deprecated
	public NetworkObject(NetworkObject parent) {
		this();
		this.parent = parent.ID;
	}
	
	@Deprecated
	public NetworkObject(NetworkObject parent, long ID) {
		this(parent);
		this.ID = ID;
	}
	
	public synchronized void put(String name, Object obj) {
		this.attributes.put(name, obj);
	}
	
	public synchronized Object remove(String name) {
		return this.attributes.remove(name);
	}
	
	public synchronized Object get(String name) {
		return this.attributes.get(name);
	}
	
	public synchronized boolean has(String name) {
		return this.attributes.containsKey(name);
	}
	
	public synchronized boolean check(String name) {
		Object val = this.attributes.get(name);
		if (val instanceof Boolean)
			return (Boolean) val;
		else
			return false;
	}
	
	// Please don't abuse.
	public synchronized TreeMap<String, Object> getAllAttributes() {
		return new TreeMap<String, Object>(this.attributes);
	}
	
	public synchronized void putChild(NetworkObject child) {
		child.parent = this.ID;
		this.children.put(child.ID, child);
	}
	
	public synchronized NetworkObject removeChild(Long ID) {
		this.parent = 0;
		return this.children.remove(ID);
	}
	
	public synchronized NetworkObject getChild(Long ID) {
		return this.children.get(ID);
	}
	
	public synchronized boolean hasChild(Long ID) {
		return this.children.containsKey(ID);
	}
	
	// Please don't abuse
	public synchronized TreeMap<Long, NetworkObject> getAllChildren() {
		return new TreeMap<Long, NetworkObject>(this.children);
	}
	
	public synchronized NetworkObject searchChildren(Long ID) {
		for (Entry<Long, NetworkObject> child : this.getAllChildren().entrySet())
		{
			if (child.getKey().equals(ID))
				return child.getValue();
			else
				return searchChildren(ID);
		}
		return null;
	}
	
	/* Warning this is evil.
	 * 
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public synchronized NetworkObject clone() {
		try {
			ByteArrayOutputStream outByte = new ByteArrayOutputStream();
			ObjectOutputStream outObj = new ObjectOutputStream(outByte);
			ByteArrayInputStream inByte;
			ObjectInputStream inObject;
			outObj.writeObject(this);
			outObj.close();
			byte[] buffer = outByte.toByteArray();
			inByte = new ByteArrayInputStream(buffer);
			inObject = new ObjectInputStream(inByte);
			Object deepcopy =  inObject.readObject();
			inObject.close();
			return (NetworkObject) deepcopy;
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return null;
	}
	
	// ID -> Child
	private TreeMap<Long, NetworkObject> children;
	
	// Name -> Value
	private TreeMap<String, Object> attributes;
}
