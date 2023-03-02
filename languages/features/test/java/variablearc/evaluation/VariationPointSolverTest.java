/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import variablearc.AbstractTest;
import variablearc.VariableArcMill;
import variablearc._symboltable.*;

import java.util.Collections;
import java.util.List;

public class VariationPointSolverTest extends AbstractTest {


  protected static ASTNameExpression createNameExpression(@NotNull String name,
                                                          @NotNull IVariableArcScope scope) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(scope);
    ASTNameExpression res = VariableArcMill.nameExpressionBuilder().setName(name).build();
    res.setEnclosingScope(scope);
    return res;
  }

  @Test
  public void shouldIgnoreNamedVariableFromOutsideScope() {
    // Given
    IVariableArcScope innerScope = VariableArcMill.scope();
    IVariableArcScope outerScope = VariableArcMill.scope();

    // variation point with condition f1
    VariableArcVariationPoint variationPoint =
      new VariableArcVariationPoint(createNameExpression("f1", innerScope));
    innerScope.add(variationPoint);

    ASTComponentType astComponentType = Mockito.mock(ASTComponentType.class);
    Mockito.when(astComponentType.getBody()).thenReturn(Mockito.mock(ASTComponentBody.class));

    // component with feature f1 and feature f2
    ComponentTypeSymbol compType = VariableArcMill.componentTypeSymbolBuilder()
      .setAstNode(astComponentType)
      .setName("comp1").setSpannedScope(innerScope).build();
    ArcFeatureSymbol f1 = VariableArcMill.arcFeatureSymbolBuilder().setName("f1").build();
    ArcFeatureSymbol f2 = VariableArcMill.arcFeatureSymbolBuilder().setName("f2").build();
    innerScope.add(f1);
    innerScope.add(new ArcFeature2VariableAdapter(f1));
    innerScope.add(f2);
    innerScope.add(new ArcFeature2VariableAdapter(f2));

    // as a subcomponent with f1=f2 (but f2 is a feature in the outer scope)
    TypeExprOfComponent typeExpr = new TypeExprOfComponent(compType);

    ComponentInstanceSymbol componentInstanceSymbol = VariableArcMill.componentInstanceSymbolBuilder()
      .setName("subcomp").setArcArguments(
        Collections.singletonList(
          ArcBasisMill.arcArgumentBuilder().setName("f1")
          .setExpression(createNameExpression("f2", outerScope)).build()
        )
      ).setType(typeExpr).build();
    VariableArcSymbolTableCompleter completer = VariableArcMill.symbolTableCompleter();
    completer.visit(componentInstanceSymbol);
    VariationPointSolver solver = new VariationPointSolver(componentInstanceSymbol);

    // When
    List<VariableArcVariationPoint> variationPoints = solver.getVariationPoints();

    // Then the variation point should be found
    Assertions.assertEquals(1, variationPoints.size());
    Assertions.assertEquals(variationPoint, variationPoints.get(0));
  }
}
