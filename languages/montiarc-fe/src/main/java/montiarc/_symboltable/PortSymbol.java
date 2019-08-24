/* (c) https://github.com/MontiCore/monticore */

package montiarc._symboltable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import montiarc.helper.SymbolPrinter;

/**
 * Symboltable entry for ports.
 *
 * @author Arne Haber, Robert Heim
 */
public class PortSymbol extends CommonSymbol {
  public static final PortKind KIND = new PortKind();
  
  private final Map<String, Optional<String>> stereotype = new HashMap<>();
  
  /**
   * Maps direction incoming to true.
   */
  public static final boolean INCOMING = true;
  
  /**
   * Indicates whether the port is incoming.
   */
  private boolean incoming;
  
  private JTypeReference<? extends JTypeSymbol> typeReference;

  /**
   * Constructor for a PortSymbol object.
   * @param name Name of the PortSymbol
   */
  public PortSymbol(String name) {
    super(name, KIND);
  }
  
  /**
   * Setter for the direction of the port.
   * @param isIncoming The direction of the port. If true, the port is incoming,
   *                   otherwise, it is outgoing.
   */
  public void setDirection(boolean isIncoming) {
    incoming = isIncoming;
  }
  
  /**
   * Indicates whether the port is incoming.
   * @return true, if this is an incoming port, else false.
   */
  public boolean isIncoming() {
    return incoming;
  }
  
  /**
   * Indicates whether the port is outgoing.
   * @return true, if this is an outgoing port, else false.
   */
  public boolean isOutgoing() {
    return !isIncoming();
  }
  
  /**
   * Getter for the type reference.
   * @return The typeReference reference to the type from this port
   */
  public JTypeReference<? extends JTypeSymbol> getTypeReference() {
    return this.typeReference;
  }
  
  /**
   * Setter for the type reference.
   * @param typeReference The reference to the type from this port
   */
  public void setTypeReference(JTypeReference<? extends JTypeSymbol> typeReference) {
    this.typeReference = typeReference;
  }
  
  /**
   * returns the component which defines the connector this is independent from the component to
   * which the source and target ports belong to
   *
   * @return is optional, b/c a connector can belong to a component symbol or to an expanded
   * component instance symbol
   */
  public Optional<ComponentSymbol> getComponent() {
    if (!this.getEnclosingScope().getSpanningSymbol().isPresent()) {
      return Optional.empty();
    }
    if (!(this.getEnclosingScope().getSpanningSymbol().get() instanceof ComponentSymbol)) {
      return Optional.empty();
    }
    return Optional.of((ComponentSymbol) this.getEnclosingScope().getSpanningSymbol().get());
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
    return SymbolPrinter.printPort(this);
  }
}
