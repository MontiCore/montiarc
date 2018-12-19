package de.montiarcautomaton.runtimes.componentinstantiation;

import java.util.HashMap;
import java.util.Map;


/**
 * Stores all ComponentLoaders, so they can be stopped whenever a component gets replaced.
 */
public class LoaderManager {

  Map<String, FileSystemLoader> registeredLoaders = new HashMap<>();

  public void registerLoader(String instanceName, FileSystemLoader fsLoader){
    registeredLoaders.put(instanceName, fsLoader);
  }

  public void unregisterLoader(String instanceName){
    FileSystemLoader loader = registeredLoaders.get(instanceName);
    if (loader != null){
      loader.stop();
      registeredLoaders.remove(instanceName);
    }
    else{
      System.out.println("Could not unregister loader since no loader for "
              + instanceName + " is registered");
    }
  }

  public Map<String, FileSystemLoader> getRegisteredLoaders() {
    return registeredLoaders;
  }
}
