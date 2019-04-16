package de.montiarcautomaton.runtimes.componentinstantiation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ILoader extends Runnable{

	Optional<Object> hasNewSubComponent(String name);

	void checkForUpdate();

	void deleteFile(String name);
	
	void stop();

	void init(String instanceName, String storeDir, String targetDir, List<String> subcomps,
			Map<String, List<String>> interfaces, Map<String, String> subcompTypes);

}