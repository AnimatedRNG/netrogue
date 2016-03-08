package animated.spferical.netrogue.test;

import java.io.Serializable;

import animated.spferical.netrogue.networking.NetworkObject;

class ExampleNetworkObject extends NetworkObject implements Serializable {
		
	private static final long serialVersionUID = -269444597085367965L;

	public ExampleNetworkObject(boolean leaf) {
		if (!leaf) {
			for (int i = 0; i < Math.random() * 2; i++)
				this.put(new String("" + Math.random()), Math.random());
			for (int i = 0; i < 0; i++)
			{
				ExampleNetworkObject child;
				if (Math.random() < 0.1)
					child = new ExampleNetworkObject(false);
				else
					child = new ExampleNetworkObject(true);
				this.putChild(child);
			}
		}
	}
	
	public void update() {
		for (String name : this.getAllAttributes().keySet())
			this.put(name, Math.random());
	}
}
