/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable.adapters;

import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOTypeSymbolResolvingDelegate;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Type2CDTypeResolvingDelegate implements IOOTypeSymbolResolvingDelegate {

  protected ICD4CodeGlobalScope globalScope;

  public Type2CDTypeResolvingDelegate(@NotNull ICD4CodeGlobalScope globalScope) {
    this.globalScope = globalScope;
  }

  @Override
  public List<OOTypeSymbol> resolveAdaptedOOTypeSymbol(boolean foundSymbols, String name,
                                                     AccessModifier modifier, Predicate<OOTypeSymbol> predicate) {
    List<OOTypeSymbol> result = new ArrayList<>();
    Optional<CDTypeSymbol> symbol = globalScope.resolveCDType(name, modifier);
    if (symbol.isPresent()) {
      result.add(new CDType2TypeAdapter(symbol.get()));
    }
    return result;
  }
}