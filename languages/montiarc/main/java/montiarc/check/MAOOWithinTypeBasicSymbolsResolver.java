/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.StaticAccessModifier;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.check.SymbolSelection;
import variablearc.check.VariableArcOOWithinTypeBasicSymbolsResolver;

import java.util.List;
import java.util.function.Predicate;

public class MAOOWithinTypeBasicSymbolsResolver extends VariableArcOOWithinTypeBasicSymbolsResolver {

  private static final String LOG_NAME = MAOOWithinTypeBasicSymbolsResolver.class.getSimpleName();

  public static void init(@NotNull SymbolSelection selection) {
    Log.trace(() -> "Initialize MAOOWithinTypeBasicSymbolsResolver as within type resolver", LOG_NAME);
    setDelegate(new MAOOWithinTypeBasicSymbolsResolver(selection));
  }

  public MAOOWithinTypeBasicSymbolsResolver(@NotNull SymbolSelection selection) {
    super(selection);
  }

  @Override
  @SuppressWarnings("removal")
  /* Since the methods #resolveConstructorLocally and #resolveConstructors perform different tasks, it is not advisable to exchange them.
  Furthermore, the access modifier of #resolveConstructorLocally is likely to change to protected and not to private, which would not affect our current implementation.
  Therefore, the deprecation warning can be suppressed.
  */
  protected List<FunctionSymbol> resolveFunctionLocallyMany(@NotNull IBasicSymbolsScope scope,
                                                            @NotNull String name,
                                                            @NotNull AccessModifier accessModifier,
                                                            @NotNull Predicate<FunctionSymbol> predicate) {

    List<FunctionSymbol> resolved = super.resolveFunctionLocallyMany(scope, name, accessModifier, predicate);

    if (accessModifier.getDimensionToModifierMap().containsKey(StaticAccessModifier.DIMENSION)) {
      resolved.addAll(super.resolveConstructorLocally(scope, name, accessModifier, predicate));
    }

    return resolved;
  }
}
