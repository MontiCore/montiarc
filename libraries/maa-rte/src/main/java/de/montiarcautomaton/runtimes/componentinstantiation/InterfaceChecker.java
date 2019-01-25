package de.montiarcautomaton.runtimes.componentinstantiation;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class InterfaceChecker {
	public Boolean checkInterface(List<String> interface1, List<String> interface2) {
		return new HashSet<>(interface1).equals(new HashSet<>(interface2));
	}

}
