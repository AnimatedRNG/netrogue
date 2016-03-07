package animated.spferical.netrogue.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import animated.spferical.netrogue.MapGenerator;
import animated.spferical.netrogue.networking.NetworkObject;
import animated.spferical.netrogue.world.Level;

public class TestMapGenerator {
	@Before
	public void setUp() throws Exception {
		mapgen = new MapGenerator();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		NetworkObject level = new Level(null, 1);
		mapgen.generateMap(level);
		assertFalse(level.getAllChildren().isEmpty());
	}

	private MapGenerator mapgen;
}
