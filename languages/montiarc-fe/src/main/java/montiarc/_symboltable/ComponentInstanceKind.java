/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc._symboltable;

import de.monticore.symboltable.SymbolKind;

/**
 * Symbol kind of component instances.
 *
 * @author Robert Heim
 */
public class ComponentInstanceKind implements SymbolKind {
  
  private static final String NAME = ComponentInstanceKind.class.getName();
  
  @Override
  public String getName() {
    return NAME;
  }
  
  @Override
  public boolean isKindOf(SymbolKind kind) {
    return NAME.equals(kind.getName()) || SymbolKind.super.isKindOf(kind);
  }
}
