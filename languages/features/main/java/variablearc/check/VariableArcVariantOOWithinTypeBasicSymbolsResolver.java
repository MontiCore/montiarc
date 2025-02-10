/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.ArcBasisOOWithinTypeBasicSymbolsResolver;
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
public class VariableArcVariantOOWithinTypeBasicSymbolsResolver extends ArcBasisOOWithinTypeBasicSymbolsResolver {

  private static final String LOG_NAME = VariableArcVariantOOWithinTypeBasicSymbolsResolver.class.getSimpleName();

  public static void init() {
    Log.trace("Initialize VariableArcVariantOOWithinTypeBasicSymbolsResolver as within scope resolver", LOG_NAME);
    setDelegate(new VariableArcVariantOOWithinTypeBasicSymbolsResolver());
  }

  public VariableArcVariantOOWithinTypeBasicSymbolsResolver() {
    super();
  }

  @Override
  protected Optional<VariableSymbol> resolveVariableLocally(IBasicSymbolsScope scope, String name, AccessModifier modifier, Predicate<VariableSymbol> predicate) {
    return super.resolveVariableLocally(scope, name, modifier, predicate.and(getVariablePredicate()));
  }

  protected Predicate<VariableSymbol> getVariablePredicate() {
    Optional<ComponentTypeSymbol> variant = VariableArcTypeCheck.getCurrentVariant();
    if (variant.isEmpty() || !(variant.get() instanceof VariableArcVariantComponentTypeSymbol)) {
      return v -> true;
    } else {
      return ((VariableArcVariantComponentTypeSymbol) variant.get())::containsSymbol;
    }
  }
}
