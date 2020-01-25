/* (c) https://github.com/MontiCore/monticore */
package arc._cocos;

import arc._ast.ASTComponent;
import arc._symboltable.ComponentInstanceSymbol;
import arc._symboltable.ComponentSymbol;
import arc.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Inner components that have formal type parameters have to be explicitly
 * instantiated as they require actual type arguments.
 */
public class ComponentWithTypeParametersHasInstance implements ArcASTComponentCoCo {

  @Override
  public void check(@NotNull ASTComponent node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    ComponentSymbol component = node.getSymbol();
    Collection<ComponentInstanceSymbol> instances = component.getSubComponents();
    Set<ComponentSymbol> instantiatedComponents = instances.stream()
      .filter(instance -> instance.getType().loadSymbol().isPresent())
      .map(instance -> instance.getType().getLoadedSymbol())
      .filter(ComponentSymbol::hasTypeParameter)
      .collect(Collectors.toSet());
    List<ComponentSymbol> notInstantiatedComponents = component.getInnerComponents();
    notInstantiatedComponents =
      notInstantiatedComponents.stream().filter(ComponentSymbol::hasTypeParameter)
        .collect(Collectors.toList());
    notInstantiatedComponents =
      notInstantiatedComponents.stream()
        .filter(innerComponent -> !instantiatedComponents.contains(innerComponent))
        .collect(Collectors.toList());
    for (ComponentSymbol innerComponent : notInstantiatedComponents) {
      Log.error(
        String.format(ArcError.INNER_WITH_TYPE_PARAMETER_REQUIRES_INSTANCE.toString(),
          innerComponent.getFullName()), innerComponent.getSourcePosition());
    }
  }
}