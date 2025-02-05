/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.ArcBasisWithinScopeBasicSymbolsResolver;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * A variant aware scope resolver. Filters out variables not included in the variant.
 */
public class VariableArcVariantWithinScopeBasicSymbolsResolver extends ArcBasisWithinScopeBasicSymbolsResolver {

  private static final String LOG_NAME = VariableArcVariantWithinScopeBasicSymbolsResolver.class.getSimpleName();

  public static void init() {
    Log.trace("Initialize VariableArcVariantWithinScopeBasicSymbolsResolver as within scope resolver", LOG_NAME);
    setDelegate(new VariableArcVariantWithinScopeBasicSymbolsResolver());
  }

  public VariableArcVariantWithinScopeBasicSymbolsResolver() {
    super();
  }

  @Override
  protected Predicate<VariableSymbol> getVariablePredicate() {
    Optional<ComponentTypeSymbol> variant = VariableArcTypeCheck.getCurrentVariant();
    if (variant.isEmpty() || !(variant.get() instanceof VariableArcVariantComponentTypeSymbol)) {
      return super.getVariablePredicate();
    } else {
      return ((VariableArcVariantComponentTypeSymbol) variant.get())::containsSymbol;
    }
  }
}
