/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolTOP;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

import static de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION;

public class ArcBasisWithinScopeBasicSymbolsResolver extends WithinScopeBasicSymbolsResolver {

  public ArcBasisWithinScopeBasicSymbolsResolver() {
    super(new ArcBasisTypeContextCalculator(), new ArcBasisWithinTypeBasicSymbolsResolver());
  }

  @Override
  protected Optional<SymTypeExpression> resolveVariableWithoutSuperTypes(@NotNull IBasicSymbolsScope scope,
                                                                         @NotNull String name) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(name);

    return scope.resolveVariableMany(name, ALL_INCLUSION, getVariablePredicate())
      .stream().findFirst()
      .map(VariableSymbolTOP::getType);
  }
}
