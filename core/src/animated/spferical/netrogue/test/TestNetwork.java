package animated.spferical.netrogue.test;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import animated.spferical.netrogue.networking.Diff;
import animated.spferical.netrogue.networking.DiffGenerator;
import animated.spferical.netrogue.networking.NetworkObject;

public class TestNetwork {

	@Before
	public void setUp() throws Exception {
		this.obj = new ExampleNetworkObject(null, false);
		this.obj.lastUpdate++;
		this.obj1 = new ExampleNetworkObject(null, false);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		List<Diff> diffs = DiffGenerator.generateDiffs(this.obj1, this.obj);
		
		for (Diff diff : diffs)
			diff.apply(this.obj1);
		
		List<Diff> shouldBeEmpty = DiffGenerator.generateDiffs(this.obj1, this.obj);
		
		assert(!diffs.isEmpty());
		assert(shouldBeEmpty.isEmpty());
	}

	class ExampleNetworkObject extends NetworkObject {
		
		private static final long serialVersionUID = -269444597085367965L;

		public ExampleNetworkObject(NetworkObject parent, boolean leaf) {
			super(parent);
			if (!leaf) {
				for (int i = 0; i < Math.random() * 100; i++)
					this.put(new String("" + Math.random()), Math.random());
				for (int i = 0; i < 6; i++)
				{
					ExampleNetworkObject child;
					if (Math.random() < 0.2)
						child = new ExampleNetworkObject(this, false);
					else
						child = new ExampleNetworkObject(this, true);
					this.putChild(child.ID, child);
				}
			}
		}
	}
	
	private NetworkObject obj;
	private NetworkObject obj1;
}
