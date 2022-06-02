/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.ArcFeatureSymbol;
import variablearc._symboltable.IVariableArcScope;
import variablearc._symboltable.VariableArcVariationPoint;
import variablearc.evaluation.VariationPointSolver;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents variable component types with filled parameters. E.g., a {@code TypeExprOfVariableComponent} can
 * represent component usages, such as, {@code MyComp myComp(p1, p2)} and {@code OtherComp otherComp(p1, f1 = True)}.
 */
public class TypeExprOfVariableComponent extends TypeExprOfComponent {
  protected final ImmutableMap<VariableSymbol, ASTExpression> parameterVarBindings;
  protected final ImmutableMap<ArcFeatureSymbol, ASTExpression> featureVarBindings;
  private final VariationPointSolver variationPointSolver;

  public TypeExprOfVariableComponent(ComponentTypeSymbol compTypeSymbol) {
    super(compTypeSymbol);
    Preconditions.checkArgument(this.getTypeInfo().getSpannedScope() instanceof IVariableArcScope);
    this.featureVarBindings = ImmutableMap.<ArcFeatureSymbol, ASTExpression>builder().build();
    this.parameterVarBindings = ImmutableMap.<VariableSymbol, ASTExpression>builder().build();
    this.variationPointSolver = new VariationPointSolver(this);
  }

  public TypeExprOfVariableComponent(ComponentTypeSymbol compTypeSymbol, @NotNull List<ASTExpression> parameterArguments) {
    super(compTypeSymbol);
    Preconditions.checkNotNull(parameterArguments);
    Preconditions.checkArgument(this.getTypeInfo().getSpannedScope() instanceof IVariableArcScope);

    // Split parameterArguments into ordered and named parameters
    List<ASTAssignmentExpression> namedParameterArguments = parameterArguments.stream().filter(astExpression -> astExpression instanceof ASTAssignmentExpression).map(astExpression -> (ASTAssignmentExpression) astExpression).collect(Collectors.toList());
    parameterArguments = parameterArguments.stream().filter(astExpression -> !(astExpression instanceof ASTAssignmentExpression)).collect(Collectors.toList());

    Preconditions.checkArgument(((IVariableArcScope) compTypeSymbol.getSpannedScope()).getArcFeatureSymbols().size() >= namedParameterArguments.size(),
        "Component type '%s' has %s features, but you supplied '%s' feature assignments.",
        compTypeSymbol.getName(), ((IVariableArcScope) compTypeSymbol.getSpannedScope()).getArcFeatureSymbols().size(), namedParameterArguments.size());

    ImmutableMap.Builder<VariableSymbol, ASTExpression> parameterVarBindingBuilder = ImmutableMap.builder();
    // We know guava immutable maps are ordered by insertion time. As we rely on the fact that the ordering of the
    // arguments is consistent with the ordering in the map, the following iteration ensures it:
    for (int i = 0; i < this.getTypeInfo().getParameters().size(); i++) {
      if (i < parameterArguments.size()) // Deal with wrong number of parameters through cocos
        parameterVarBindingBuilder.put(this.getTypeInfo().getParameters().get(i), parameterArguments.get(i));
    }
    this.parameterVarBindings = parameterVarBindingBuilder.build();

    // Add feature bindings expressions
    ImmutableMap.Builder<ArcFeatureSymbol, ASTExpression> featureVarBindingBuilder = ImmutableMap.builder();
    for (ASTAssignmentExpression namedParameterArgument : namedParameterArguments) {
      if (namedParameterArgument.getLeft() instanceof ASTNameExpression) {
        String name = ((ASTNameExpression) namedParameterArgument.getLeft()).getName();
        Optional<ArcFeatureSymbol> symbol = ((IVariableArcScope) this.getTypeInfo().getSpannedScope()).resolveArcFeature(name);
        symbol.ifPresent(arcFeatureSymbol -> featureVarBindingBuilder.put(arcFeatureSymbol, namedParameterArgument.getRight()));
      }
    }
    this.featureVarBindings = featureVarBindingBuilder.build();
    this.variationPointSolver = new VariationPointSolver(this);
  }

  public ImmutableMap<VariableSymbol, ASTExpression> getParameterBindings() {
    return this.parameterVarBindings;
  }

  public ImmutableMap<ArcFeatureSymbol, ASTExpression> getFeatureBindings() {
    return this.featureVarBindings;
  }

  @Override
  public Optional<SymTypeExpression> getTypeExprOfPort(String portName) {
    return getTypeExprsOfPort(portName).stream().findFirst();
  }

  public List<SymTypeExpression> getTypeExprsOfPort(String portName) {
    Preconditions.checkNotNull(portName);
    // We first look if the requested port is part of our definition.
    // If not, we ask our parent if they have such a port.
    boolean portDefinedByUs = this.getTypeInfo().getPort(portName, false).isPresent();

    if (portDefinedByUs) {
      return CompTypeExpressionTypeInfoBinding.getPorts(this, portName, false).stream().filter(PortSymbol::isTypePresent).map(PortSymbol::getType).collect(Collectors.toList());
    }
    else if (this.getParentTypeExpr().isPresent()) {
      // We do not have this port. Now we look if our parent has such a port.
      if (this.getParentTypeExpr().orElseThrow(IllegalStateException::new) instanceof TypeExprOfVariableComponent) {
        return ((TypeExprOfVariableComponent) this.getParentTypeExpr().orElseThrow(IllegalStateException::new)).getTypeExprsOfPort(portName);
      }
      else {
        Optional<SymTypeExpression> parentTypeExpr = this.getParentTypeExpr().orElseThrow(IllegalStateException::new).getTypeExprOfPort(portName);
        return parentTypeExpr.map(Collections::singletonList).orElse(Collections.emptyList());
      }
    }
    else {
      return Collections.emptyList();
    }
  }

  public List<VariableArcVariationPoint> getVariationPoints() {
    return this.variationPointSolver.getVariationPoints();
  }

  public Optional<ASTExpression> getBindingFor(@NotNull VariableSymbol var) {
    Preconditions.checkNotNull(var);
    return Optional.ofNullable(this.getParameterBindings().get(var));
  }

  public Optional<ASTExpression> getBindingFor(@NotNull ArcFeatureSymbol var) {
    Preconditions.checkNotNull(var);
    return Optional.ofNullable(this.getFeatureBindings().get(var));
  }

  public Optional<ASTExpression> getBindingFor(@NotNull String varName) {
    Preconditions.checkNotNull(varName);
    Optional<VariableSymbol> searchedVar = this.getParameterBindings().keySet().stream().filter(v -> v.getName().equals(varName)).findFirst();
    if (searchedVar.isPresent()) {
      return Optional.ofNullable(this.getParameterBindings().get(searchedVar.get()));
    }
    Optional<ArcFeatureSymbol> searchedFeat = this.getFeatureBindings().keySet().stream().filter(v -> v.getName().equals(varName)).findFirst();
    if (searchedFeat.isPresent()) {
      return Optional.ofNullable(this.getFeatureBindings().get(searchedFeat.get()));
    }
    throw new IllegalStateException();
  }

  public ImmutableList<ASTExpression> getAllBindingsAsList() {
    // We know guava immutable maps are ordered and thus .values represents the order of the arguments
    ImmutableList.Builder<ASTExpression> builder = ImmutableList.builder();
    builder.addAll(this.getParameterBindings().values());
    builder.addAll(this.getFeatureBindings().values());
    return builder.build();
  }

  public ImmutableList<ASTExpression> getParameterBindingsAsList() {
    // We know guava immutable maps are ordered and thus .values represents the order of the arguments
    return this.getParameterBindings().values().asList();
  }

  public TypeExprOfVariableComponent bindParameters(@NotNull List<ASTExpression> parameterArguments) {
    Preconditions.checkNotNull(parameterArguments);
    return new TypeExprOfVariableComponent(this.getTypeInfo(), parameterArguments);
  }

  @Override
  public TypeExprOfVariableComponent deepClone() {
    List<ASTExpression> clonedBindings = this.getAllBindingsAsList().stream().map(ASTExpression::deepClone).collect(Collectors.toList());
    return new TypeExprOfVariableComponent(this.getTypeInfo(), clonedBindings);
  }

  @Override
  public boolean deepEquals(@NotNull CompTypeExpression compSymType) {
    Preconditions.checkNotNull(compSymType);

    if (!(compSymType instanceof TypeExprOfVariableComponent)) {
      return false;
    }
    TypeExprOfVariableComponent otherCompExpr = (TypeExprOfVariableComponent) compSymType;

    boolean equal = this.getTypeInfo().equals(compSymType.getTypeInfo());
    equal &= this.getAllBindingsAsList().size() == otherCompExpr.getAllBindingsAsList().size();
    for (int i = 0; i < this.getAllBindingsAsList().size(); i++) {
      equal &= this.getAllBindingsAsList().get(i).deepEquals(otherCompExpr.getAllBindingsAsList().get(i));
    }

    return equal;
  }

}
