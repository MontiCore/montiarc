/* (c) https://github.com/MontiCore/monticore */
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
