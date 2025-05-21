/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis._symboltable.ArcComponentTypeSymbol;
import arcbasis.check.ArcBasisWithinTypeBasicSymbolsResolver;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * A variant aware scope resolver. Filters out variables not included in the variant.
 */
public class VariableArcVariantWithinTypeBasicSymbolsResolver extends ArcBasisWithinTypeBasicSymbolsResolver {

  private static final String LOG_NAME = VariableArcVariantWithinTypeBasicSymbolsResolver.class.getSimpleName();

  public static void init() {
    Log.trace(() -> "Initialize VariableArcVariantWithinTypeBasicSymbolsResolver as within scope resolver", LOG_NAME);
    setDelegate(new VariableArcVariantWithinTypeBasicSymbolsResolver());
  }

  public VariableArcVariantWithinTypeBasicSymbolsResolver() {
    super();
  }

  @Override
  protected Optional<VariableSymbol> resolveVariableLocally(IBasicSymbolsScope scope, String name, AccessModifier modifier, Predicate<VariableSymbol> predicate) {
    return super.resolveVariableLocally(scope, name, modifier, predicate.and(getVariablePredicate()));
  }

  protected Predicate<VariableSymbol> getVariablePredicate() {
    Optional<ArcComponentTypeSymbol> variant = VariableArcTypeCheck.getCurrentVariant();
    if (variant.isEmpty() || !(variant.get() instanceof VariableArcVariantComponentTypeSymbol)) {
      return v -> true;
    } else {
      return ((VariableArcVariantComponentTypeSymbol) variant.get())::containsSymbol;
    }
  }
}
