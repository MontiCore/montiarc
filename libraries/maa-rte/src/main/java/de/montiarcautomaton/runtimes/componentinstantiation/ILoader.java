package de.montiarcautomaton.runtimes.componentinstantiation;

import java.util.Optional;

public interface ILoader extends Runnable{

	Optional<Object> hasNewSubPrinter();

	void checkForUpdate();

	void deleteFile();

}