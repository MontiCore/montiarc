/* (c) https://github.com/MontiCore/monticore */
package genericarc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ArcPortSymbol;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents generic component types with filled type parameters. E.g., a {@code TypeExprOfGenericComponent} can
 * represent generic component type usages, such as, {@code MyComp<Person>} and {@code OtherComp<List<T>>}. Note,
 * however, that {@code Person}, {@code List<>}, and {@code T} must be accessible type symbols in the enclosing scope
 * of the generic component type usage.
 */
public class TypeExprOfGenericComponent extends CompTypeExpression {

  protected final ImmutableList<SymTypeExpression> typeArguments;

  private ImmutableMap<TypeVarSymbol, SymTypeExpression> typeVarBindingsAsMap;

  public TypeExprOfGenericComponent(@NotNull ComponentTypeSymbol compTypeSymbol,
                                    @NotNull List<SymTypeExpression> typeArguments) {
    super(compTypeSymbol);
    Preconditions.checkNotNull(typeArguments);

    this.typeArguments = ImmutableList.copyOf(typeArguments);
  }

  public ImmutableMap<TypeVarSymbol, SymTypeExpression> getTypeVarBindings() {
    if (typeVarBindingsAsMap == null) {
      this.typeVarBindingsAsMap = calcTypeVarBindingsAsMap();
    }

    return this.typeVarBindingsAsMap;
  }

  private ImmutableMap<TypeVarSymbol, SymTypeExpression> calcTypeVarBindingsAsMap() {
    ImmutableMap.Builder<TypeVarSymbol, SymTypeExpression> typeVarBindingBuilder = ImmutableMap.builder();

    for (int i = 0; i < typeArguments.size(); i++) {
      // We deal with the wrong number of parameters through cocos
      List<TypeVarSymbol> typeParams = this.getTypeInfo().getTypeParameters();
      if (i < typeParams.size() && typeArguments.get(i) != null) {
        TypeVarSymbol typeParam = this.getTypeInfo().getTypeParameters().get(i);
        typeVarBindingBuilder.put(typeParam, typeArguments.get(i));
      }
    }

    return typeVarBindingBuilder.build();
  }

  @Override
  public String printName() {
    StringBuilder builder = new StringBuilder(this.getTypeInfo().getName()).append('<');
    for (int i = 0; i < this.getTypeBindingsAsList().size(); i++) {
      builder.append(this.getTypeBindingsAsList().get(i).print());
      if (i < this.getTypeBindingsAsList().size() - 1) {
        builder.append(',');
      }
    }
    return builder.append('>').toString();
  }

  @Override
  public String printFullName() {
    StringBuilder builder = new StringBuilder(this.getTypeInfo().getFullName()).append('<');
    for (int i = 0; i < this.getTypeBindingsAsList().size(); i++) {
      builder.append(this.getTypeBindingsAsList().get(i).printFullName());
      if (i < this.getTypeBindingsAsList().size() - 1) {
        builder.append(',');
      }
    }
    return builder.append('>').toString();
  }

  @Override
  public List<CompTypeExpression> getParentTypeExpr() {

    ComponentTypeSymbol rawType = this.getTypeInfo();
    if (rawType.isEmptyParents()) {
      return rawType.getParentsList();
    }

    return rawType.getParentsList().stream().map(unboundParentExpr -> {
    if (unboundParentExpr instanceof TypeExprOfComponent) {
      return unboundParentExpr;
    } else if (unboundParentExpr instanceof TypeExprOfGenericComponent) {
      return ((TypeExprOfGenericComponent) unboundParentExpr).bindTypeParameter(this.getTypeVarBindings());
    } else {
      throw new UnsupportedOperationException("Encountered a type expression for components that is not known." +
        String.format(" (We only know '%s' and '%s')",
          TypeExprOfComponent.class.getName(), TypeExprOfGenericComponent.class.getName()
        )
      );
    }}).collect(Collectors.toList());
  }

  @Override
  public Optional<SymTypeExpression> getTypeExprOfPort(@NotNull String portName) {
    Preconditions.checkNotNull(portName);
    // We first look if the requested port is part of our definition.
    // If not, we ask our parent if they have such a port.
    boolean portDefinedByUs = this.getTypeInfo().getPort(portName, false).isPresent();

    if (portDefinedByUs) {
      Optional<SymTypeExpression> unboundPortType = this.getTypeInfo()
        .getPort(portName, false)
        .filter(ArcPortSymbol::isTypePresent)
        .map(ArcPortSymbol::getType);
      return unboundPortType.map(this::createBoundTypeExpression);
    } else if (!this.getTypeInfo().isEmptyParents()) {
      // We do not have this port. Now we look if our parent has such a port.
      return this.getParentTypeExpr().stream().map(parent -> parent.getTypeExprOfPort(portName)).filter(Optional::isPresent).map(Optional::get).findAny();
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Optional<SymTypeExpression> getParameterType(@NotNull String name) {
    Preconditions.checkNotNull(name);

    return this.getTypeInfo().getParameter(name).map(p -> this.createBoundTypeExpression(p.getType()));
  }

  @Override
  public List<SymTypeExpression> getParameterTypes() {
    List<SymTypeExpression> unbound = this.getTypeInfo().getParameters()
      .stream().map(VariableSymbol::getType)
      .collect(Collectors.toList());

    return this.createBoundTypeExpression(unbound);
  }

  public Optional<SymTypeExpression> getTypeBindingFor(@NotNull TypeVarSymbol typeVar) {
    Preconditions.checkNotNull(typeVar);
    return Optional.ofNullable(this.getTypeVarBindings().get(typeVar));
  }

  public Optional<SymTypeExpression> getTypeBindingFor(@NotNull String typeVarName) {
    Preconditions.checkNotNull(typeVarName);
    Optional<TypeVarSymbol> searchedTypeVar = this.getTypeVarBindings().keySet().stream()
      .filter(tvar -> tvar.getName().equals(typeVarName))
      .findFirst();
    return searchedTypeVar.map(typeVarSymbol -> this.getTypeVarBindings().get(typeVarSymbol));
  }

  @Override
  public ImmutableList<SymTypeExpression> getTypeBindingsAsList() {
    return this.typeArguments;
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
    for(SymTypeExpression typeArg : this.typeArguments) {
      SymTypeExpression newTypeArg;
      if(typeArg.isTypeVariable() && newTypeVarBindings.containsKey(((TypeVarSymbol) typeArg.getTypeInfo()))) {
        newTypeArg = newTypeVarBindings.get((TypeVarSymbol) typeArg.getTypeInfo());
      } else {
        newTypeArg = typeArg.deepClone();
        newTypeArg.replaceTypeVariables(newTypeVarBindings);
      }
      newBindings.add(newTypeArg);
    }
    return new TypeExprOfGenericComponent(this.getTypeInfo(), newBindings);
  }

  @Override
  public TypeExprOfGenericComponent deepClone(@NotNull ComponentTypeSymbol compTypeSymbol) {
    List<SymTypeExpression> clonedBindings = this.getTypeBindingsAsList().stream()
      .map(SymTypeExpression::deepClone)
      .collect(Collectors.toList());
    return new TypeExprOfGenericComponent(compTypeSymbol, clonedBindings);
  }

  @Override
  public boolean deepEquals(@NotNull CompTypeExpression compSymType) {
    Preconditions.checkNotNull(compSymType);

    if(!(compSymType instanceof TypeExprOfGenericComponent)) {
      return false;
    }
    TypeExprOfGenericComponent otherCompExpr = (TypeExprOfGenericComponent) compSymType;

    boolean equal = this.getTypeInfo().equals(compSymType.getTypeInfo());
    equal &= this.getTypeBindingsAsList().size() == otherCompExpr.getTypeBindingsAsList().size();
    for(int i = 0; i < this.getTypeBindingsAsList().size(); i++) {
      equal &= this.getTypeBindingsAsList().get(i).deepEquals(otherCompExpr.getTypeBindingsAsList().get(i));
    }

    return equal;
  }

  protected SymTypeExpression createBoundTypeExpression(@NotNull SymTypeExpression typeExpr) {
    if (typeExpr.isPrimitive() || typeExpr.isObjectType() || typeExpr.isNullType()) {
      return typeExpr;
    } else if (typeExpr.isTypeVariable()) {
      return this.getTypeBindingFor(typeExpr.getTypeInfo().getName()).orElse(SymTypeExpressionFactory.createObscureType());
    } else {
      SymTypeExpression boundTypeExpr = typeExpr.deepClone();
      boundTypeExpr.replaceTypeVariables(this.getTypeVarBindings());
      return boundTypeExpr;
    }
  }

  protected List<SymTypeExpression> createBoundTypeExpression(@NotNull List<SymTypeExpression> typeExprS) {
    Preconditions.checkNotNull(typeExprS);
    return typeExprS.stream().map(this::createBoundTypeExpression).collect(Collectors.toList());
  }
}
