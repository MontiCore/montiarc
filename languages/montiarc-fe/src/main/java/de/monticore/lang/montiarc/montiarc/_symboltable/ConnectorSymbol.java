package de.monticore.lang.montiarc.montiarc._symboltable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import de.monticore.lang.montiarc.helper.SymbolPrinter;
import de.monticore.symboltable.CommonSymbol;

/**
 * Symboltable entry for connectors.
 *
 * @author Arne Haber, Michael von Wenckstern
 */
public class ConnectorSymbol extends CommonSymbol {

  /* generated by template symboltable.symbols.KindConstantDeclaration*/

  public static final ConnectorKind KIND = ConnectorKind.INSTANCE;

  private final Map<String, Optional<String>> stereotype = new HashMap<>();

  /**
   * Source of this connector.
   */
  protected String source;

  /**
   * Target of this connector.
   */
  protected String target;

  /**
   * use {@link #builder()}
   */
  protected ConnectorSymbol(String name) {
    super(name, KIND);
  }

  public static ConnectorBuilder builder() {
    return new ConnectorBuilder();
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
    return target;
  }

  /**
   * @param target the target to set
   */
  public void setTarget(String target) {
    this.target = target;
  }

  /**
   * Adds the stereotype key=value to this entry's map of stereotypes
   *
   * @param key      the stereotype's key
   * @param optional the stereotype's value
   */
  public void addStereotype(String key, Optional<String> optional) {
    stereotype.put(key, optional);
  }

  /**
   * Adds the stereotype key=value to this entry's map of stereotypes
   *
   * @param key   the stereotype's key
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

  @Override
  public String getName() {
    return getTarget();
  }

}
