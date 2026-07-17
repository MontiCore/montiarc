/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import com.google.common.collect.ImmutableList;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import variablearc._symboltable.ArcFeatureSymbol;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class MontiArcArtifactScope extends MontiArcArtifactScopeTOP {

  protected List<String> packageParts;

  public MontiArcArtifactScope() {
    this("", new java.util.ArrayList<>());
  }

  public MontiArcArtifactScope(String packageName, List<de.monticore.symboltable.ImportStatement> imports) {
    this(Optional.empty(), packageName, imports);
  }

  public MontiArcArtifactScope(Optional<IMontiArcScope> enclosingScope, String packageName, List<de.monticore.symboltable.ImportStatement> imports) {
    super(enclosingScope, packageName, imports);
    packageParts = ImmutableList.copyOf(de.se_rwth.commons.Splitters.DOT.splitToList(this.getPackageName()));
  }

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

  @Override
  public void setPackageName(String packageName) {
    super.setPackageName(packageName);
    packageParts = ImmutableList.copyOf(de.se_rwth.commons.Splitters.DOT.splitToList(this.getPackageName()));
  }

  @Override
  public List<String> getPackageParts() {
    return packageParts;
  }
}
