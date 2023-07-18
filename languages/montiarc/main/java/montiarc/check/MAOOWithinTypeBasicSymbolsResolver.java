/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.StaticAccessModifier;
import de.monticore.types3.util.OOWithinTypeBasicSymbolsResolver;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class MAOOWithinTypeBasicSymbolsResolver extends OOWithinTypeBasicSymbolsResolver {

  @Override
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
