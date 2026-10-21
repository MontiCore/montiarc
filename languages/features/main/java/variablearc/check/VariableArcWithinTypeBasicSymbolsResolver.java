/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis.check.ArcBasisWithinTypeBasicSymbolsResolver;
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
public class VariableArcWithinTypeBasicSymbolsResolver extends ArcBasisWithinTypeBasicSymbolsResolver {

  private static final String LOG_NAME = VariableArcWithinTypeBasicSymbolsResolver.class.getSimpleName();

  public static void init(@NotNull SymbolSelection selection) {
    Log.trace(() -> "Initialize VariableArcWithinTypeBasicSymbolsResolver as within scope resolver", LOG_NAME);
    setDelegate(new VariableArcWithinTypeBasicSymbolsResolver(selection));
  }

  protected final SymbolSelection selection;

  public VariableArcWithinTypeBasicSymbolsResolver(@NotNull SymbolSelection selection) {
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
