/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc._symboltable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.types.TypeSymbol;
import de.monticore.symboltable.types.references.TypeReference;
import montiarc.helper.SymbolPrinter;

/**
 * Represents an instance of a component.
 *
 * @author Robert Heim
 */
public class ComponentInstanceSymbol extends CommonScopeSpanningSymbol {

  public static final ComponentInstanceKind KIND = new ComponentInstanceKind();

  private final ComponentSymbolReference componentType;

  /**
   * List of configuration arguments.
   */
  private List<ASTExpression> configArgs = new ArrayList<>();

  /**
   * Constructor for de.monticore.lang.montiarc.montiarc._symboltable.ComponentInstanceSymbol
   *
   * @param name
   * @param componentType the referenced component definition
   */
  public ComponentInstanceSymbol(String name, ComponentSymbolReference componentType) {
    super(name, KIND);
    this.componentType = componentType;
  }

  /**
   * @return componentType
   */
  public ComponentSymbolReference getComponentType() {
    return this.componentType;
  }

  /**
   * @return connectors of this component
   */
  public Collection<ConnectorSymbol> getSimpleConnectors() {
    return getSpannedScope().<ConnectorSymbol> resolveLocally(ConnectorSymbol.KIND);
  }

  /**
   * @return List of configuration arguments
   */
  public List<ASTExpression> getConfigArguments() {
    return this.configArgs;
  }

  /**
   * @param cfg configuration argument to add
   */
  public void addConfigArgument(ASTExpression cfg) {
    this.configArgs.add(cfg);
  }

  /**
   * @param cfgList configuration arguments to set
   */
  public void setConfigArgs(List<ASTExpression> configArgs) {
    this.configArgs = configArgs;
  }

  @Override
  public String toString() {
    return SymbolPrinter.printComponentInstance(this);
  }
}
