package componentinstantiation;

import static org.junit.Assert.assertTrue;
import de.montiarcautomaton.runtimes.componentinstantiation.InterfaceChecker;


import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.montiarcautomaton.generator.codegen.componentinstantiation.FileSystemLoader;
import de.montiarcautomaton.runtimes.componentinstantiation.ILoader;
import de.montiarcautomaton.runtimes.componentinstantiation.LoaderManager;
import portTest.DynamicOutComp;
import portTest.DynamicPortTest;

public class ComponentTest {

	static String STOREPATH = "src/test/resources/store/models/";
	static String CLASSPATH = "src/test/resources/store/classes/";
	
	@Test
	public void reconfigureTest() {
	    LoaderManager loman = new LoaderManager();
	    final DynamicOutComp cmp = new DynamicOutComp();
	    
	    cmp.setUp();
	    cmp.setLoaderConfiguration("portTest", STOREPATH, CLASSPATH, loman);
	    cmp.init();
	    
	    assertTrue(cmp.reconfigure().isEmpty());
	}
	
	
	@Test
	public void getPortsTest() {
	    LoaderManager loman = new LoaderManager();
	    final DynamicOutComp cmp = new DynamicOutComp();
	    
	    cmp.setUp();
	    cmp.setLoaderConfiguration("portTest", STOREPATH, CLASSPATH, loman);
	    cmp.init();
	    
	    assertTrue(cmp.getPorts().size() == 1);
	}
	
	@Test
	public void getInterfaceTest() {
	    LoaderManager loman = new LoaderManager();
	    final DynamicOutComp cmp = new DynamicOutComp();
	    
	    cmp.setUp();
	    cmp.setLoaderConfiguration("portTest", STOREPATH, CLASSPATH, loman);
	    cmp.init();
	    
	    assertTrue(cmp.getInterface().equals("Out-outPort-String"));
	}
	

}
