/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.StaticAccessModifier;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.check.VariableArcVariantOOWithinTypeBasicSymbolsResolver;

import java.util.List;
import java.util.function.Predicate;

public class MAOOWithinTypeBasicSymbolsResolver extends VariableArcVariantOOWithinTypeBasicSymbolsResolver {

  private static final String LOG_NAME = MAOOWithinTypeBasicSymbolsResolver.class.getSimpleName();

  public static void init() {
    Log.trace(() -> "Initialize MAOOWithinTypeBasicSymbolsResolver as within type resolver", LOG_NAME);
    setDelegate(new MAOOWithinTypeBasicSymbolsResolver());
  }

  @Override
  @SuppressWarnings("removal")
  /* Since the methods #resolveConstructorLocally and #resolveConstructors perform different tasks, it is not advisable to exchange them.
  Furthermore, the access modifier of #resolveConstructorLocally is likely to change to protected and not to private, which would not affect our current implementation.
  Therefore, the deprecation warning can be suppressed.
  */
  protected List<FunctionSymbol> resolveFunctionLocally(@NotNull IBasicSymbolsScope scope,
                                                        @NotNull String name,
                                                        @NotNull AccessModifier accessModifier,
                                                        @NotNull Predicate<FunctionSymbol> predicate) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(accessModifier);
    Preconditions.checkNotNull(predicate);

    List<FunctionSymbol> f = super.resolveFunctionLocally(scope, name, accessModifier, predicate);
    String s = StaticAccessModifier.STATIC.getDimensionToModifierMap().keySet().stream().findFirst().get();
    if (accessModifier.getDimensionToModifierMap().containsKey(s)
      && accessModifier.getDimensionToModifierMap().get(s) == StaticAccessModifier.STATIC) {
      f.addAll(super.resolveConstructorLocally(scope, name, accessModifier, predicate));
    }
    return f;
  }
}
