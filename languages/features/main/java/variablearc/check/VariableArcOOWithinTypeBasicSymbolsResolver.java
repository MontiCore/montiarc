/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis.check.ArcBasisOOWithinTypeBasicSymbolsResolver;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Resolves variables like its super class, except those the {@link SymbolSelection}
 * currently excludes, such that names resolve to the symbols chosen for a
 * configuration.
 */
public class VariableArcOOWithinTypeBasicSymbolsResolver extends ArcBasisOOWithinTypeBasicSymbolsResolver {

  private static final String LOG_NAME = VariableArcOOWithinTypeBasicSymbolsResolver.class.getSimpleName();

  public static void init(@NotNull SymbolSelection selection) {
    Log.trace(() -> "Initialize VariableArcOOWithinTypeBasicSymbolsResolver as within scope resolver", LOG_NAME);
    setDelegate(new VariableArcOOWithinTypeBasicSymbolsResolver(selection));
  }

  protected final SymbolSelection selection;

  public VariableArcOOWithinTypeBasicSymbolsResolver(@NotNull SymbolSelection selection) {
    super();
    this.selection = Preconditions.checkNotNull(selection);
  }

  @Override
  protected Optional<VariableSymbol> resolveVariableLocally(IBasicSymbolsScope scope, String name, AccessModifier modifier, Predicate<VariableSymbol> predicate) {
    return super.resolveVariableLocally(scope, name, modifier, predicate.and(getVariablePredicate()));
  }

  protected Predicate<VariableSymbol> getVariablePredicate() {
    return this::isSelected;
  }

  protected boolean isSelected(@NotNull VariableSymbol variable) {
    return !this.selection.isExcluded(variable);
  }
}
