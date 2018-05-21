package montiarc._symboltable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import de.monticore.symboltable.CommonSymbol;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTConnector;
import montiarc.helper.SymbolPrinter;

/**
 * Symbol for {@link ASTConnector}s. The name of a connector symbol equals its
 * target and vice versa. This is valid since data for a port may only result
 * from a single source. <br/>
 * <br/>
 * The port names (source and target of the connector) must be set <b>relative
 * to the component scope</b> that the connector is defined in. This means that
 * the sourceName may be any in port of the component itself (e.g., "myInPort")
 * or an out port of any subcomponent (e.g., "subComponent.someOutPort"). The
 * targetName is either a out port of the component itself( e.g., "myOutPort")
 * or any of the sub components in ports (e.g., "subComponent.someInPort").
 * 
 * @author Arne Haber, Michael von Wenckstern, Robert Heim
 */
public class ConnectorSymbol extends CommonSymbol {
  
  public static final ConnectorKind KIND = new ConnectorKind();
  
  private final Map<String, Optional<String>> stereotype = new HashMap<>();
  
  /**
   * Source of this connector.
   */
  protected String source;
  
  /**
   * Creates a ConnectorSymbol.
   * 
   * @param sourceName the relative name of the source port (e.g.,
   * "subComponent.someOutPort" or "myInPort").
   * @param targetName relative name of the target port (e.g.,
   * "subComponent.someInPort" or "myOutPort").
   * @return
   */
  public ConnectorSymbol(String sourceName, String targetName) {
    super(targetName, KIND);
    setSource(sourceName);
  }
  
  /**
   * @return the source
   */
  public String getSource() {
    return source;
  }
  
  /**
   * @param source the source to set
   */
  public void setSource(String source) {
    this.source = source;
  }
  
  /**
   * @return the target
   */
  public String getTarget() {
    return getName();
  }
  
  public Optional<PortSymbol> getTargetPort() {
    return this.getPort(this.getTarget());
  }
  
  public Optional<PortSymbol> getSourcePort() {
    return this.getPort(this.getSource());
  }
  
  protected Optional<PortSymbol> getPort(String name) {
    ComponentSymbol cmp = (ComponentSymbol) this.getEnclosingScope().getSpanningSymbol().get();
    Optional<PortSymbol> foundPort = Optional.empty();
    
    // Case 1: componentinstance.port
    if (name.contains(".")) {
      Iterator<String> parts = Splitters.DOT.split(name).iterator();
      
      String instance = parts.next();
      String instancePort = parts.next();
      
      Optional<ComponentInstanceSymbol> inst = cmp.getSpannedScope()
          .<ComponentInstanceSymbol> resolveLocally(instance, ComponentInstanceSymbol.KIND);
      if(!inst.isPresent()) {
        Log.error("0xMA091 Instance " + instance+ " is not defined in the component type " +cmp.getName());
        return Optional.empty();
      }
      foundPort = inst.get().getComponentType().getReferencedSymbol().getAllPorts().stream().filter(p -> p.getName().equals(instancePort)).findFirst();
    }
    // Case 2: port
    else {
      // Is port defined in component type?
      foundPort = this.getEnclosingScope().<PortSymbol> resolveLocally(name,
          PortSymbol.KIND);
      
      // Is it an inherited port from a super component?
      if (!foundPort.isPresent()) {
        if (cmp.getSuperComponent().isPresent()) {
          ComponentSymbolReference superCmp = cmp.getSuperComponent().get();
          foundPort = superCmp.getReferencedComponent().get().getSpannedScope()
              .<PortSymbol> resolve(name,
                  PortSymbol.KIND);
        }
      }
      
    }
    return foundPort;
    
  }
  
  /**
   * Adds the stereotype key=value to this entry's map of stereotypes
   *
   * @param key the stereotype's key
   * @param optional the stereotype's value
   */
  public void addStereotype(String key, Optional<String> optional) {
    stereotype.put(key, optional);
  }
  
  /**
   * Adds the stereotype key=value to this entry's map of stereotypes
   *
   * @param key the stereotype's key
   * @param value the stereotype's value
   */
  public void addStereotype(String key, @Nullable String value) {
    if (value != null && value.isEmpty()) {
      value = null;
    }
    stereotype.put(key, Optional.ofNullable(value));
  }
  
  /**
   * @return map representing the stereotype of this component
   */
  public Map<String, Optional<String>> getStereotype() {
    return stereotype;
  }
  
  @Override
  public String toString() {
    return SymbolPrinter.printConnector(this);
  }
  
}
