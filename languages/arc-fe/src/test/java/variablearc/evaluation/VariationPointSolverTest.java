/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.monticore.expressions.assignmentexpressions.AssignmentExpressionsMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import variablearc.AbstractTest;
import variablearc.VariableArcMill;
import variablearc._symboltable.IVariableArcScope;
import variablearc._symboltable.VariableArcVariationPoint;
import variablearc.check.TypeExprOfVariableComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VariationPointSolverTest extends AbstractTest {
  protected static ComponentTypeSymbol createComponentWithParams(@NotNull String compName,
                                                                 @NotNull String... varNames) {
    Preconditions.checkNotNull(compName);
    Preconditions.checkNotNull(varNames);

    List<VariableSymbol> vars = new ArrayList<>(varNames.length);
    for (String varName : varNames) {
      VariableSymbol var = VariableArcMill.variableSymbolBuilder().setName(varName).build();
      vars.add(var);
    }

    return VariableArcMill.componentTypeSymbolBuilder().setName(compName).setSpannedScope(VariableArcMill.scope()).setParameters(vars).build();
  }

  protected static ComponentTypeSymbol createComponentWithVariationPointAndParams(@NotNull String compName,
                                                                                  @NotNull VariableArcVariationPoint variationPoint, @NotNull String... varNames) {
    Preconditions.checkNotNull(compName);
    Preconditions.checkNotNull(varNames);

    List<VariableSymbol> vars = new ArrayList<>(varNames.length);
    for (String varName : varNames) {
      VariableSymbol var = VariableArcMill.variableSymbolBuilder().setName(varName).build();
      vars.add(var);
    }

    IVariableArcScope scope = VariableArcMill.scope();

    scope.add(variationPoint);

    return VariableArcMill.componentTypeSymbolBuilder().setName(compName).setSpannedScope(scope).setParameters(vars).build();
  }

  @Test
  public void shouldIgnoreNamedVariableFromOtherScope() {
    // --- Given ---
    // Parent with parameter a
    ComponentTypeSymbol parentComp = createComponentWithParams("Comp", "a");
    TypeExprOfVariableComponent parentCompTypeExpr = new TypeExprOfVariableComponent(parentComp,
      Collections.singletonList(VariableArcMill.nameExpressionBuilder().setName("false").build()));

    // Child with variation point
    VariableArcVariationPoint variationPoint =
      new VariableArcVariationPoint(VariableArcMill.nameExpressionBuilder().setName("feat").build());

    ComponentTypeSymbol comp = createComponentWithVariationPointAndParams("Comp", variationPoint, "a");
    comp.setParent(parentCompTypeExpr);
    ((IVariableArcScope) comp.getSpannedScope()).add(VariableArcMill.arcFeatureSymbolBuilder().setName("feat").build());

    ASTExpression aExpr = VariableArcMill.nameExpressionBuilder().setName("false").build(); // Workaround for not
    // having literals in VariableArc (a=false)
    ASTExpression featureAssignmentExpr = AssignmentExpressionsMill.assignmentExpressionBuilder()
      .setLeft(VariableArcMill.nameExpressionBuilder().setName("feat").build())
      .setOperator(0)
      .setRight(VariableArcMill.nameExpressionBuilder().setName("a").build())
      .build(); // (feat = a) where a references parentComp.a not comp.a
    List<ASTExpression> exprList = Lists.newArrayList(aExpr, featureAssignmentExpr);

    TypeExprOfVariableComponent compTypeExpr = new TypeExprOfVariableComponent(comp, exprList);

    // When
    List<VariableArcVariationPoint> returnedVariationPoints = compTypeExpr.getVariationPoints();

    // Then the variation point should be found
    Assertions.assertEquals(1, returnedVariationPoints.size());
    Assertions.assertEquals(variationPoint, returnedVariationPoints.get(0));
  }
}
