/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;
import java.util.function.Predicate;

public class ArcBasisWithinScopeBasicSymbolsResolver extends WithinScopeBasicSymbolsResolver {

  private static final String LOG_NAME = ArcBasisWithinScopeBasicSymbolsResolver.class.getSimpleName();

  public static void init() {
    Log.trace(() -> "Initialize ArcBasisWithinScopeBasicSymbolsResolver as within scope resolver", LOG_NAME);
    setDelegate(new ArcBasisWithinScopeBasicSymbolsResolver());
  }

  @Override
  protected Optional<VariableSymbol> resolveVariable(@NotNull IBasicSymbolsScope enclosingScope,
                                                     @NotNull String name, AccessModifier accessModifier,
                                                     @NotNull Predicate<VariableSymbol> predicate) {
    // we do not log an error if multiple symbols are found (avoid redundant error logging)
    return enclosingScope.resolveVariableMany(name, accessModifier, predicate).stream().findFirst();
  }

  @Override
  protected Optional<TypeSymbol> resolveType(IBasicSymbolsScope enclosingScope, String name, AccessModifier accessModifier, Predicate<TypeSymbol> typeSymbolPredicate) {
    // we do not log an error if multiple symbols are found (avoid redundant error logging)
    // also we do not resolve type var symbols as type symbols
    return enclosingScope.resolveTypeMany(name, accessModifier, typeSymbolPredicate.and(v -> !(v instanceof TypeVarSymbol))).stream().findFirst();
  }

  @Override
  protected Optional<TypeVarSymbol> resolveTypeVar(IBasicSymbolsScope enclosingScope, String name, AccessModifier accessModifier, Predicate<TypeVarSymbol> predicate) {
    // we do not log an error if multiple symbols are found (avoid redundant error logging)
    return enclosingScope.resolveTypeVarMany(name, accessModifier, predicate).stream().findFirst();
  }
}
