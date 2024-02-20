/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.ArcBasisWithinScopeBasicSymbolsResolver;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;

import java.util.function.Predicate;

/**
 * A variant aware scope resolver. Filters out variables not included in the variant.
 */
public class VariableArcVariantWithinScopeBasicSymbolsResolver extends ArcBasisWithinScopeBasicSymbolsResolver {

  protected ComponentTypeSymbol currentVariant;

  public VariableArcVariantWithinScopeBasicSymbolsResolver(@NotNull ComponentTypeSymbol currentVariant) {
    super();
    Preconditions.checkNotNull(currentVariant);
    this.currentVariant = currentVariant;
  }

  @Override
  protected Predicate<VariableSymbol> getVariablePredicate() {
    if (!(currentVariant instanceof VariableArcVariantComponentTypeSymbol)) return super.getVariablePredicate();
    return ((VariableArcVariantComponentTypeSymbol) currentVariant)::containsSymbol;
  }
}
