/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis.check.ArcBasisWithinScopeBasicSymbolsResolver;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.function.Predicate;

/**
 * Resolves variables like its super class, except those the {@link SymbolSelection}
 * currently excludes, such that names resolve to the symbols chosen for a
 * configuration.
 */
public class VariableArcWithinScopeBasicSymbolsResolver extends ArcBasisWithinScopeBasicSymbolsResolver {

  private static final String LOG_NAME = VariableArcWithinScopeBasicSymbolsResolver.class.getSimpleName();

  public static void init(@NotNull SymbolSelection selection) {
    Log.trace(() -> "Initialize VariableArcWithinScopeBasicSymbolsResolver as within scope resolver", LOG_NAME);
    setDelegate(new VariableArcWithinScopeBasicSymbolsResolver(selection));
  }

  protected final SymbolSelection selection;

  public VariableArcWithinScopeBasicSymbolsResolver(@NotNull SymbolSelection selection) {
    super();
    this.selection = Preconditions.checkNotNull(selection);
  }

  @Override
  protected Predicate<VariableSymbol> getVariablePredicate() {
    return super.getVariablePredicate().and(this::isSelected);
  }

  protected boolean isSelected(@NotNull VariableSymbol variable) {
    return !this.selection.isExcluded(variable);
  }
}
