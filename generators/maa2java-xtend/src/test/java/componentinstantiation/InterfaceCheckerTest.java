package componentinstantiation;

import static org.junit.Assert.assertTrue;
import de.montiarcautomaton.runtimes.componentinstantiation.InterfaceChecker;


import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.montiarcautomaton.generator.codegen.componentinstantiation.FileSystemLoader;
import de.montiarcautomaton.runtimes.componentinstantiation.ILoader;
import de.montiarcautomaton.runtimes.componentinstantiation.LoaderManager;

public class InterfaceCheckerTest {
	@Test
	public void checkInterfaceTest() {
		List<String> interface1 = new ArrayList();
		List<String> interface2 = new ArrayList();
		InterfaceChecker checker = new InterfaceChecker();
		interface1.add("In-String-port");
		interface1.add("Out-Int-port2");
		interface2.add("Out-Int-port2");
		interface2.add("In-String-port");

		assertTrue(checker.checkInterface(interface1, interface2));
		
		interface2.add("In-String-port3");
		assertTrue(!checker.checkInterface(interface1, interface2));


	}
}
