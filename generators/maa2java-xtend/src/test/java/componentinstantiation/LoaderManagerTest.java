package componentinstantiation;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import de.montiarcautomaton.generator.codegen.componentinstantiation.FileSystemLoader;
import de.montiarcautomaton.runtimes.componentinstantiation.ILoader;
import de.montiarcautomaton.runtimes.componentinstantiation.LoaderManager;

public class LoaderManagerTest {
	@Test
	public void registerUnregisterTest() {
		LoaderManager loman = new LoaderManager();
		loman.registerLoader("test", new FileSystemLoader("","","", new ArrayList<String>()));
		ILoader fLoader = loman.getRegisteredLoaders().get("test");
		assertTrue(fLoader != null);
		loman.unregisterLoader("test");
		fLoader = loman.getRegisteredLoaders().get("test");
		assertTrue(fLoader == null);
	}
}
