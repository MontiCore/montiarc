/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._symboltable.ArcPortSymbol;
import arcbasis._symboltable.ComponentInstanceSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import variablearc._symboltable.ArcFeatureSymbol;

import java.util.List;
import java.util.function.Predicate;

public class MontiArcArtifactScope extends MontiArcArtifactScopeTOP {

  @Override
  public List<ArcPortSymbol> resolvePortManyEnclosing(boolean foundSymbols, String name, AccessModifier modifier,
                                                      Predicate<ArcPortSymbol> predicate) {
    return resolveArcPortMany(foundSymbols, name, modifier, predicate);
  }

  @Override
  public List<VariableSymbol> resolveVariableManyEnclosing(boolean foundSymbols, String name, AccessModifier modifier,
                                                           Predicate<VariableSymbol> predicate) {
    return resolveVariableMany(foundSymbols, name, modifier, predicate);
  }

  @Override
  public List<ComponentInstanceSymbol> resolveComponentInstanceManyEnclosing(boolean foundSymbols, String name,
                                                                             AccessModifier modifier,
                                                                             Predicate<ComponentInstanceSymbol> predicate) {
    return resolveComponentInstanceMany(foundSymbols, name, modifier, predicate);
  }

  @Override
  public List<ArcFeatureSymbol> resolveArcFeatureManyEnclosing(boolean foundSymbols, String name,
                                                               AccessModifier modifier,
                                                               Predicate<ArcFeatureSymbol> predicate) {
    return resolveArcFeatureMany(foundSymbols, name, modifier, predicate);
  }
}