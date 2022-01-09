/* (c) https://github.com/MontiCore/monticore */
package genericarc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolTOP;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeVariable;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents generic component types with filled type parameters. E.g., a {@code TypeExprOfGenericComponent} can
 * represent generic component type usages, such as, {@code MyComp<Person>} and {@code OtherComp<List<T>>}. Note,
 * however, that {@code Person}, {@code List<>}, and {@code T} must be accessible type symbols in the enclosing scope
 * of the generic component type usage.
 */
public class TypeExprOfGenericComponent extends CompTypeExpression {

  protected final ImmutableMap<TypeVarSymbol, SymTypeExpression> typeVarBindings;

  public ImmutableMap<TypeVarSymbol, SymTypeExpression> getTypeVarBindings() {
    return this.typeVarBindings;
  }

  public TypeExprOfGenericComponent(@NotNull ComponentTypeSymbol compTypeSymbol,
                                    @NotNull List<SymTypeExpression> typeArguments) {
    super(compTypeSymbol);
    Preconditions.checkNotNull(typeArguments);
    Preconditions.checkArgument(compTypeSymbol.getSpannedScope().getTypeVarSymbols().size() == typeArguments.size(),
      "Component type '%s' has %s type parameters, but you supplied '%s' type arguments.",
      compTypeSymbol.getName(), compTypeSymbol.getSpannedScope().getTypeVarSymbols().size(), typeArguments.size());

    ImmutableMap.Builder<TypeVarSymbol, SymTypeExpression> typeVarBindingBuilder = ImmutableMap.builder();
    // We know guava immutable maps are ordered by insertion time. As we rely on the fact that the ordering of the
    // type arguments is consistent with the ordering in the map, the following iteration ensures it:
    for (int i = 0; i < typeArguments.size(); i++) {
      typeVarBindingBuilder.put(this.getTypeInfo().getTypeParameters().get(i), typeArguments.get(i));
    }

    this.typeVarBindings = typeVarBindingBuilder.build();
  }

  @Override
  public String printName() {
    StringBuilder builder = new StringBuilder(this.getTypeInfo().getName()).append('<');
    for (int i = 0; i < this.getBindingsAsList().size(); i++) {
      builder.append(this.getBindingsAsList().get(i).print());
      if (i < this.getBindingsAsList().size() - 1) {
        builder.append(',');
      }
    }
    return builder.append('>').toString();
  }

  @Override
  public String printFullName() {
    StringBuilder builder = new StringBuilder(this.getTypeInfo().getFullName()).append('<');
    for (int i = 0; i < this.getBindingsAsList().size(); i++) {
      builder.append(this.getBindingsAsList().get(i).printFullName());
      if (i < this.getBindingsAsList().size() - 1) {
        builder.append(',');
      }
    }
    return builder.append('>').toString();
  }

  @Override
  public Optional<CompTypeExpression> getParentTypeExpr() {
    if (!this.getTypeInfo().isPresentParentComponent()) {
      return Optional.empty();
    }

    CompTypeExpression unboundParentExpr = this.getTypeInfo().getParent();
    if (unboundParentExpr instanceof TypeExprOfComponent) {
      return Optional.of(unboundParentExpr);
    } else if (unboundParentExpr instanceof TypeExprOfGenericComponent) {
      return Optional.of(((TypeExprOfGenericComponent) unboundParentExpr).bindTypeParameter(this.typeVarBindings));
    } else {
      throw new UnsupportedOperationException("Encountered a type expression for components that is not known." +
        String.format(" (We only know '%s' and '%s')",
          TypeExprOfComponent.class.getName(), TypeExprOfGenericComponent.class.getName()
        )
      );
    }
  }

  @Override
  public Optional<SymTypeExpression> getTypeExprOfPort(@NotNull String portName) {
    Preconditions.checkNotNull(portName);
    // We first look if the requested port is part of our definition.
    // If not, we ask our parent if they have such a port.
    boolean portDefinedByUs = this.getTypeInfo().getPort(portName, false).isPresent();

    if (portDefinedByUs) {
      SymTypeExpression unboundPortType = this.getTypeInfo()
        .getPort(portName, false)
        .map(PortSymbol::getType)
        .orElseThrow(IllegalStateException::new);
      return this.createBoundTypeExpression(unboundPortType);
    } else if (this.getTypeInfo().isPresentParentComponent()) {
      // We do not have this port. Now we look if our parent has such a port.
      return this.getParentTypeExpr().orElseThrow(IllegalStateException::new).getTypeExprOfPort(portName);
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Optional<SymTypeExpression> getTypeExprOfParameter(@NotNull String parameterName) {
    Preconditions.checkNotNull(parameterName);

    SymTypeExpression unboundParamType = this.getTypeInfo()
      .getParameter(parameterName)
      .map(VariableSymbolTOP::getType)
      .orElseThrow(NoSuchElementException::new);

    return this.createBoundTypeExpression(unboundParamType);
  }

  public Optional<SymTypeExpression> getBindingFor(@NotNull TypeVarSymbol typeVar) {
    Preconditions.checkNotNull(typeVar);
    return Optional.of(this.getTypeVarBindings().get(typeVar));
  }

  public Optional<SymTypeExpression> getBindingFor(@NotNull String typeVarName) {
    Preconditions.checkNotNull(typeVarName);
    TypeVarSymbol searchedTypeVar = this.getTypeVarBindings().keySet().stream()
      .filter(tvar -> tvar.getName().equals(typeVarName))
      .findFirst().orElseThrow(IllegalStateException::new);
    return Optional.of(this.getTypeVarBindings().get(searchedTypeVar));
  }

  public ImmutableList<SymTypeExpression> getBindingsAsList() {
    // We know guava immutable maps are ordered and thus .values represents the order of the type arguments
    return this.getTypeVarBindings().values().asList();
  }

  /**
   * If this {@code TypeExprOfGenericComponent} references type variables (e.g. the type {@code Comp<List<T>>}) and you
   * provide a {@link SymTypeExpression} mapping for that type variable, this method returns a {@code
   * TypeExprOfGenericComponent} where that type variable has been reset by the SymTypeExpression you provided. E.g., if
   * you provide the mapping {@code T -> Person} for the above given example component, then this method would return
   * {@code Comp<List<Person>>}. If you provide mappings for type variables that do not appear in the component type
   * expression, then these will be ignored.
   */
  public TypeExprOfGenericComponent bindTypeParameter(
    @NotNull Map<TypeVarSymbol, SymTypeExpression> newTypeVarBindings) {
    Preconditions.checkNotNull(newTypeVarBindings);

    List<SymTypeExpression> newBindings = new ArrayList<>();
    // We know guava immutable maps are ordered and thus .values represents the order of the type arguments
    for(SymTypeExpression typeArg : this.typeVarBindings.values()) {
      SymTypeExpression newTypeArg;
      if(typeArg.isTypeVariable() && newTypeVarBindings.containsKey((typeArg.getTypeInfo()))) {
        newTypeArg = newTypeVarBindings.get(typeArg.getTypeInfo());
      } else {
        newTypeArg = typeArg.deepClone();
        newTypeArg.replaceTypeVariables(newTypeVarBindings);
      }
      newBindings.add(newTypeArg);
    }
    return new TypeExprOfGenericComponent(this.getTypeInfo(), newBindings);
  }

  @Override
  public TypeExprOfGenericComponent deepClone() {
    List<SymTypeExpression> clonedBindings = this.getBindingsAsList().stream()
      .map(SymTypeExpression::deepClone)
      .collect(Collectors.toList());
    return new TypeExprOfGenericComponent(this.getTypeInfo(), clonedBindings);
  }

  @Override
  public boolean deepEquals(@NotNull CompTypeExpression compSymType) {
    Preconditions.checkNotNull(compSymType);

    if(!(compSymType instanceof TypeExprOfGenericComponent)) {
      return false;
    }
    TypeExprOfGenericComponent otherCompExpr = (TypeExprOfGenericComponent) compSymType;

    boolean equal = this.getTypeInfo().equals(compSymType.getTypeInfo());
    equal &= this.getBindingsAsList().size() == otherCompExpr.getBindingsAsList().size();
    for(int i = 0; i < this.getBindingsAsList().size(); i++) {
      equal &= this.getBindingsAsList().get(i).deepEquals(otherCompExpr.getBindingsAsList().get(i));
    }

    return equal;
  }

  protected Optional<SymTypeExpression> createBoundTypeExpression(@NotNull SymTypeExpression unboundTypeExpr) {
    if (unboundTypeExpr.isTypeConstant() || unboundTypeExpr.isObjectType() || unboundTypeExpr.isNullType()) {
      return Optional.of(unboundTypeExpr);
    } else if (unboundTypeExpr.isTypeVariable()) {
      return this.getBindingFor(unboundTypeExpr.getTypeInfo().getName());
    } else {
      SymTypeExpression boundSymType = unboundTypeExpr.deepClone();
      boundSymType.replaceTypeVariables(this.getTypeVarBindings());
      return Optional.of(boundSymType);
    }
  }
}
