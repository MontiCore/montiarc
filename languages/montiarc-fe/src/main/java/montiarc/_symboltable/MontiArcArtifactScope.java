/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class MontiArcArtifactScope extends MontiArcArtifactScopeTOP {

  @Override
  public List<ComponentTypeSymbol> continueComponentTypeWithEnclosingScope(boolean foundSymbols, String name,
                                                                           AccessModifier modifier,
                                                                           Predicate<ComponentTypeSymbol> predicate) {
    List<ComponentTypeSymbol> result = new ArrayList<>();
    if (checkIfContinueWithEnclosingScope(foundSymbols) && getEnclosingScope() != null) {
      final Set<String> potentialQualifiedNames = calculateQualifiedNames(name, getPackageName(), getImportsList());

      for (final String potentialQualifiedName : potentialQualifiedNames) {
        final List<ComponentTypeSymbol> resolvedFromEnclosing = this.getEnclosingScope()
          .resolveComponentTypeMany(foundSymbols, potentialQualifiedName, modifier, predicate);
        foundSymbols = foundSymbols || resolvedFromEnclosing.size() > 0;
        result.addAll(resolvedFromEnclosing);
      }
    }
    return result;
  }
}