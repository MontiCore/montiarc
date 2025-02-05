/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types3.util.WithinTypeBasicSymbolsResolver;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;
import java.util.function.Predicate;

public class ArcBasisWithinTypeBasicSymbolsResolver extends WithinTypeBasicSymbolsResolver {

  private static final String LOG_NAME = ArcBasisWithinTypeBasicSymbolsResolver.class.getSimpleName();

  public static void init() {
    Log.trace("Initialize ArcBasisWithinTypeBasicSymbolsResolver as within type resolver", LOG_NAME);
    setDelegate(new ArcBasisWithinTypeBasicSymbolsResolver());
  }

  @Override
  protected Optional<VariableSymbol> resolveVariableLocally(@NotNull IBasicSymbolsScope scope,
                                                            @NotNull String name,
                                                            @NotNull AccessModifier modifier,
                                                            @NotNull Predicate<VariableSymbol> predicate) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(modifier);
    Preconditions.checkNotNull(predicate);

    Predicate<VariableSymbol> localPredicate = predicate.and(getIsLocalSymbolPredicate(scope));

    return scope.resolveVariableMany(name, modifier, localPredicate).stream().findFirst();
  }
}
