/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import variablearc._symboltable.ArcFeatureSymbol;

import java.util.List;
import java.util.function.Predicate;

public class MontiArcArtifactScope extends MontiArcArtifactScopeTOP {

  @Override
  public List<PortSymbol> resolvePortManyEnclosing(boolean foundSymbols, String name, AccessModifier modifier,
                                                      Predicate<PortSymbol> predicate) {
    return resolvePortMany(foundSymbols, name, modifier, predicate);
  }

  @Override
  public List<VariableSymbol> resolveVariableManyEnclosing(boolean foundSymbols, String name, AccessModifier modifier,
                                                           Predicate<VariableSymbol> predicate) {
    return resolveVariableMany(foundSymbols, name, modifier, predicate);
  }

  @Override
  public List<SubcomponentSymbol> resolveSubcomponentManyEnclosing(boolean foundSymbols, String name,
                                                                             AccessModifier modifier,
                                                                             Predicate<SubcomponentSymbol> predicate) {
    return resolveSubcomponentMany(foundSymbols, name, modifier, predicate);
  }

  @Override
  public List<ArcFeatureSymbol> resolveArcFeatureManyEnclosing(boolean foundSymbols, String name,
                                                               AccessModifier modifier,
                                                               Predicate<ArcFeatureSymbol> predicate) {
    return resolveArcFeatureMany(foundSymbols, name, modifier, predicate);
  }
}