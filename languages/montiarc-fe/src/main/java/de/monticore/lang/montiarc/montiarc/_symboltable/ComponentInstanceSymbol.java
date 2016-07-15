/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.montiarc._symboltable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.monticore.lang.expression.symboltable.ValueSymbol;
import de.monticore.lang.montiarc.helper.SymbolPrinter;
import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.types.TypeSymbol;
import de.monticore.symboltable.types.references.TypeReference;

/**
 * Represents an instance of a component.
 *
 * @author Robert Heim
 */
public class ComponentInstanceSymbol extends CommonScopeSpanningSymbol {

  public static final ComponentInstanceKind KIND = ComponentInstanceKind.INSTANCE;

  private final ComponentSymbolReference componentType;

  /**
   * List of configuration arguments.
   */
  private List<ValueSymbol<TypeReference<TypeSymbol>>> configArgs = new ArrayList<>();

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
    return getSpannedScope().<ConnectorSymbol>resolveLocally(ConnectorSymbol.KIND);
  }

  /**
   * @return List of configuration arguments
   */
  public List<ValueSymbol<TypeReference<TypeSymbol>>> getConfigArguments() {
    return this.configArgs;
  }

  /**
   * @param cfg configuration argument to add
   */
  public void addConfigArgument(ValueSymbol<TypeReference<TypeSymbol>> cfg) {
    this.configArgs.add(cfg);
  }

  /**
   * @param cfgList configuration arguments to set
   */
  public void setConfigArgs(List<ValueSymbol<TypeReference<TypeSymbol>>> configArgs) {
    this.configArgs = configArgs;
  }

  @Override
  public String toString() {
    return SymbolPrinter.printComponentInstance(this);
  }
}
