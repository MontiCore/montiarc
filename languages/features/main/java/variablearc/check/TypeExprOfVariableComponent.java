/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.IVariableArcScope;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents variable component types with filled parameters. E.g., a {@code TypeExprOfVariableComponent} can
 * represent component usages, such as, {@code MyComp myComp(p1, p2)} and {@code OtherComp otherComp(p1, f1 = True)}.
 */
public class TypeExprOfVariableComponent extends TypeExprOfComponent {

  protected final ImmutableMap<VariableSymbol, ASTExpression> parameterVarBindings;

  public TypeExprOfVariableComponent(ComponentTypeSymbol compTypeSymbol) {
    super(compTypeSymbol);
    Preconditions.checkArgument(this.getTypeInfo().getSpannedScope() instanceof IVariableArcScope);
    this.parameterVarBindings = ImmutableMap.<VariableSymbol, ASTExpression>builder().build();
  }

  public TypeExprOfVariableComponent(ComponentTypeSymbol compTypeSymbol,
                                     @NotNull List<ASTExpression> parameterArguments) {
    super(compTypeSymbol);
    Preconditions.checkNotNull(parameterArguments);
    Preconditions.checkArgument(this.getTypeInfo().getSpannedScope() instanceof IVariableArcScope);

    ImmutableMap.Builder<VariableSymbol, ASTExpression> parameterVarBindingBuilder = ImmutableMap.builder();
    // We know guava immutable maps are ordered by insertion time. As we rely on the fact that the ordering of the
    // arguments is consistent with the ordering in the map, the following iteration ensures it:
    for (int i = 0; i < this.getTypeInfo().getParameters().size(); i++) {
      if (i < parameterArguments.size()) // Deal with wrong number of parameters through cocos
        parameterVarBindingBuilder.put(this.getTypeInfo().getParameters().get(i), parameterArguments.get(i));
    }
    this.parameterVarBindings = parameterVarBindingBuilder.build();
  }

  public ImmutableMap<VariableSymbol, ASTExpression> getParameterBindings() {
    return this.parameterVarBindings;
  }

  public Optional<ASTExpression> getBindingFor(@NotNull VariableSymbol var) {
    Preconditions.checkNotNull(var);
    return Optional.ofNullable(this.getParameterBindings().get(var));
  }


  public ImmutableList<ASTExpression> getBindingsAsList() {
    // We know guava immutable maps are ordered and thus .values represents the order of the arguments
    ImmutableList.Builder<ASTExpression> builder = ImmutableList.builder();
    builder.addAll(this.getParameterBindings().values());
    return builder.build();
  }

  public TypeExprOfVariableComponent bindParameters(@NotNull List<ASTExpression> parameterArguments) {
    Preconditions.checkNotNull(parameterArguments);
    return new TypeExprOfVariableComponent(this.getTypeInfo(), parameterArguments);
  }

  @Override
  public TypeExprOfVariableComponent deepClone() {
    List<ASTExpression> clonedBindings =
      this.getBindingsAsList().stream().map(ASTExpression::deepClone).collect(Collectors.toList());
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
    equal &= this.getBindingsAsList().size() == otherCompExpr.getBindingsAsList().size();
    for (int i = 0; i < this.getBindingsAsList().size(); i++) {
      equal &= this.getBindingsAsList().get(i).deepEquals(otherCompExpr.getBindingsAsList().get(i));
    }

    return equal;
  }

}
