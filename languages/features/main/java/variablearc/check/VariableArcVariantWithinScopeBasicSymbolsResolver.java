/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.ArcBasisWithinScopeBasicSymbolsResolver;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * A variant aware scope resolver. Filters out variables not included in the variant.
 */
public class VariableArcVariantWithinScopeBasicSymbolsResolver extends ArcBasisWithinScopeBasicSymbolsResolver {

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
