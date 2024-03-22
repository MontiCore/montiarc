/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolTOP;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.util.TypeContextCalculator;
import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;
import de.monticore.types3.util.WithinTypeBasicSymbolsResolver;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

import static de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION;

public class ArcBasisWithinScopeBasicSymbolsResolver extends WithinScopeBasicSymbolsResolver {

  public ArcBasisWithinScopeBasicSymbolsResolver() {
    this(new ArcBasisTypeContextCalculator(), new ArcBasisWithinTypeBasicSymbolsResolver());
  }

  protected ArcBasisWithinScopeBasicSymbolsResolver(@NotNull TypeContextCalculator typeCtxCalc,
                                                    @NotNull WithinTypeBasicSymbolsResolver withinTypeResolver) {
    super(Preconditions.checkNotNull(typeCtxCalc), Preconditions.checkNotNull(withinTypeResolver));
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

  @Override
  public Optional<SymTypeExpression> resolveType(@NotNull IBasicSymbolsScope scope,
                                                 @NotNull String name) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(name);

    Optional<TypeVarSymbol> typeVar = scope.resolveTypeVarMany(
      name, ALL_INCLUSION, getTypeVarPredicate()
    ).stream().findFirst();

    if (typeVar.isPresent()) {
      return Optional.of(SymTypeExpressionFactory
        .createTypeVariable(typeVar.get()));
    }

    Optional<TypeSymbol> obj = scope.resolveTypeMany(
      name, ALL_INCLUSION, getTypePredicate()
    ).stream().findFirst();

    return obj.map(SymTypeExpressionFactory::createFromSymbol);
  }
}
