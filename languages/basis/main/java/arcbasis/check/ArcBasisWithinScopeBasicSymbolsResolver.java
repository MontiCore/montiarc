/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolTOP;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.generics.TypeParameterRelations;
import de.monticore.types3.util.TypeContextCalculator;
import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;
import de.monticore.types3.util.WithinTypeBasicSymbolsResolver;
import de.se_rwth.commons.logging.Log;
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

    Optional<SymTypeExpression> type;
    Optional<TypeVarSymbol> optTypeVar;

    // Java-esque languages do not allow
    // to resolve type variables using qualified names, e.g.,
    // class C<T> {C.T t = null;} // invalid Java
    if (isNameWithQualifier(name)) {
      optTypeVar = Optional.empty();
    } else {
      // modified, here do not fail if two types are found
      optTypeVar = resolverHotfix(() ->
        scope.resolveTypeVarMany(
            name, ALL_INCLUSION, getTypeVarPredicate())
          .stream().findFirst()
      );
    }
    // object, modified, here do not fail if two types are found
    Optional<TypeSymbol> optObj = resolverHotfix(() ->
      scope.resolveTypeMany(name, ALL_INCLUSION,
          getTypePredicate().and((t -> optTypeVar.map(tv -> tv != t).orElse(true))))
        .stream().findFirst()
    );
    // in Java the type variable is preferred
    // e.g. class C<U>{class U{} U v;} //new C<Float>().v has type Float
    if (optTypeVar.isPresent() && optObj.isPresent()) {
      Log.trace("found type variable and object type for \""
          + name
          + "\", selecting type variable",
        "TypeVisitor");
    }
    if (optTypeVar.isPresent()) {
      type = Optional.of(SymTypeExpressionFactory
        .createTypeVariable(optTypeVar.get()));
    } else if (optObj.isPresent()) {
      type = Optional.of(SymTypeExpressionFactory
        .createFromSymbol(optObj.get())
      );
    } else {
      type = Optional.empty();
    }
    // replace free type variables
    Optional<SymTypeExpression> typeReplacedVars = type
      .map(t -> TypeParameterRelations.replaceFreeTypeVariables(t, scope));
    return typeReplacedVars;
  }
}
