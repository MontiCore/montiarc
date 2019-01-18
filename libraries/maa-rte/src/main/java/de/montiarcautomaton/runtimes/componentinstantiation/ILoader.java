package de.montiarcautomaton.runtimes.componentinstantiation;

import java.util.Optional;

public interface ILoader extends Runnable{

	Optional<Object> hasNewSubComponent(String name);

	void checkForUpdate();

	void deleteFile(String name);
	
	void stop();

}