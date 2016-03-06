package animated.spferical.netrogue.test;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import animated.spferical.netrogue.networking.Diff;
import animated.spferical.netrogue.networking.DiffGenerator;

public class TestNetwork {

	@Before
	public void setUp() throws Exception {
		this.obj = new ExampleNetworkObject(null, false);
		this.obj2 = new ExampleNetworkObject(null, false);
		this.obj3 = (ExampleNetworkObject) this.obj2.clone();
		this.obj.putChild(this.obj3.ID, this.obj3);
		this.obj1 = new ExampleNetworkObject(null, false);
		this.obj1.putChild(this.obj3.ID, this.obj3);
		this.obj.putChild(this.obj2.ID, this.obj2);
		this.obj1.update();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		for (int i = 0; i < 100; i++)
		{
			List<Diff> diffs = DiffGenerator.generateDiffs(this.obj1, this.obj);
			
			for (Diff diff : diffs)
				diff.apply(this.obj1);
			
			List<Diff> shouldBeEmpty = DiffGenerator.generateDiffs(this.obj1, this.obj);
			
			assert(!diffs.isEmpty());
			assert(shouldBeEmpty.isEmpty());
			
			this.obj1.update();
			this.obj.update();
		}
	}
	
	private ExampleNetworkObject obj;
	private ExampleNetworkObject obj1;
	private ExampleNetworkObject obj2;
	private ExampleNetworkObject obj3;
}
