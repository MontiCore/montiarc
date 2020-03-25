/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Inner components that have formal type parameters have to be explicitly
 * instantiated and with that have actual type parameters assigned.
 *
 * @implements No literature reference
 *
 */
public class ComponentWithTypeParametersHasInstance
    implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    if (!node.getSymbolOpt().isPresent()) {
      Log.error(
          String.format("0xMA010 ASTComponent node \"%s\" has no " +
                            "symbol. Did you forget to run the " +
                            "SymbolTableCreator before checking cocos?",
              node.getName()));
      return;
    }

    ComponentSymbol componentSymbol = (ComponentSymbol) node.getSymbolOpt().get();
    Collection<ComponentInstanceSymbol> subComponents = componentSymbol.getSubComponents();
    
    Set<ComponentSymbol> instantiatedInnerComponents =
        subComponents.stream()
            .filter(i -> i.getComponentType().existsReferencedSymbol())
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
