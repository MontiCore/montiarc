/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;

/**
 * @author (last commit) Crispin Kirchner
 * @version $Revision$, $Date$
 */
public class ComponentWithTypeParametersHasInstance
    implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    ComponentSymbol componentSymbol = (ComponentSymbol) node.getSymbol().get();
    
    Collection<ComponentInstanceSymbol> subComponents = componentSymbol.getSubComponents();
    
    Set<ComponentSymbol> instantiatedInnerComponents = subComponents
        .stream()
        .map(instanceSymbol -> instanceSymbol.getComponentType().getReferencedSymbol())
        .filter(symbol -> symbol.hasFormalTypeParameters())
        .collect(Collectors.toSet());
    
    List<ComponentSymbol> notInstantiatedInnerComponents = componentSymbol
        .getInnerComponents()
        .stream()
        .filter(symbol -> symbol.hasFormalTypeParameters())
        .filter(innerComponent -> !instantiatedInnerComponents.contains(innerComponent))
        .collect(Collectors.toList());
    
    for (ComponentSymbol notInstantiatedInnerComponent : notInstantiatedInnerComponents) {
      Log.error(
          String.format(
              "0xMA009 Inner component \"%s\" must have an instance defining its formal type parameters.",
              notInstantiatedInnerComponent.getName()),
          notInstantiatedInnerComponent.getSourcePosition());
    }
  }
}
