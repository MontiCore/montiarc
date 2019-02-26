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
		LoaderManager loman = new LoaderManager((ILoader) new FileSystemLoader());
		loman.registerLoader("test", new FileSystemLoader());
		ILoader fLoader = loman.getRegisteredLoaders().get("test");
		assertTrue(fLoader != null);
		loman.unregisterLoader("test");
		fLoader = loman.getRegisteredLoaders().get("test");
		assertTrue(fLoader == null);
	}
	
	@Test
	public void getNewLoaderTest() {
		LoaderManager loman = new LoaderManager((ILoader) new FileSystemLoader());
		ILoader loader = loman.getNewLoader();
		assertTrue(loader != null);
	}
}
