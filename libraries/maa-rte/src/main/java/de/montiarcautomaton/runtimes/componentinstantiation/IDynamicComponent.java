package de.montiarcautomaton.runtimes.componentinstantiation;

import de.montiarcautomaton.runtimes.timesync.delegation.IComponent;
import de.montiarcautomaton.runtimes.timesync.delegation.Port;

import java.util.List;

public interface IDynamicComponent extends IComponent {
  public <T> void setPort(String name, Port<T> port);
  public <T> Port<T> getPort(String name);
  public void checkForCmp();
  public List<Port> reconfigure();
  public void propagatePortChanges(List<Port> changedPorts);
  public String getInstanceName();
  public List<Port> getPorts();
  public List<String> getInterface();

  public void setLoaderConfiguration(String instanceName, String storeDir, String targetDir, LoaderManager loaderManager);

}