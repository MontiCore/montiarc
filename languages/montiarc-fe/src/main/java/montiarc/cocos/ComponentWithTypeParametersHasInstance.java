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
 * Inner components that have formal type parameters have to be explicitly
 * instantiated and with that have actual type parameters assigned.
 *
 * @implements No literature reference
 *
 * @author (last commit) Crispin Kirchner
 */
public class ComponentWithTypeParametersHasInstance
    implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    if (!node.getSymbolOpt().isPresent()) {
      Log.error(String.format(
          "0xMA071 Symbol of component \"%s\" is missing. " +
              "The context condition \"%s\" can't be checked that way.",
          node.getName(), TopLevelComponentHasNoInstanceName.class.getName()));
      return;
    }

    ComponentSymbol componentSymbol = (ComponentSymbol) node.getSymbolOpt().get();
    Collection<ComponentInstanceSymbol> subComponents = componentSymbol.getSubComponents();
    
    Set<ComponentSymbol> instantiatedInnerComponents = subComponents
        .stream()
        .map(instanceSymbol -> instanceSymbol.getComponentType().getReferencedSymbol())
        .filter(ComponentSymbol::hasFormalTypeParameters)
        .collect(Collectors.toSet());
    
    List<ComponentSymbol> notInstantiatedInnerComponents = componentSymbol
        .getInnerComponents()
        .stream()
        .filter(ComponentSymbol::hasFormalTypeParameters)
        .filter(innerComponent -> !instantiatedInnerComponents.contains(innerComponent))
        .collect(Collectors.toList());
    
    for (ComponentSymbol notInstantiatedInnerComponent : notInstantiatedInnerComponents) {
      Log.error(
          String.format(
              "0xMA009 Inner component \"%s\" must have an instance " +
                  "defining its formal type parameters.",
              notInstantiatedInnerComponent.getName()),
          notInstantiatedInnerComponent.getSourcePosition());
    }
  }
}
