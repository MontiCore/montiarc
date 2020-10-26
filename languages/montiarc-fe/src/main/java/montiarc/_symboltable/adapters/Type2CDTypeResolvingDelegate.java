/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable.adapters;

import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.ITypeSymbolResolvingDelegate;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Type2CDTypeResolvingDelegate implements ITypeSymbolResolvingDelegate {

  protected CD4AnalysisGlobalScope globalScope;

  public Type2CDTypeResolvingDelegate(@NotNull CD4AnalysisGlobalScope globalScope) {
    this.globalScope = globalScope;
  }

  @Override
  public List<TypeSymbol> resolveAdaptedTypeSymbol(boolean foundSymbols, String name,
                                                   AccessModifier modifier, Predicate<TypeSymbol> predicate) {
    List<TypeSymbol> result = new ArrayList<>();
    Optional<CDTypeSymbol> symbol = globalScope.resolveCDType(name, modifier);
    if (symbol.isPresent() && symbol.get().isIsPublic()) {
      result.add(new CDType2TypeAdapter(symbol.get()));
    }
    return result;
  }
}