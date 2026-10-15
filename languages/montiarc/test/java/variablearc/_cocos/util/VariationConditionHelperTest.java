/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos.util;

import com.google.common.base.Preconditions;
import de.monticore.expressions.commonexpressions._ast.ASTBooleanAndOpExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc._symboltable.VariableArcVariationPoint;
import variablearc.evaluation.ExpressionSet;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link VariationConditionHelper}.
 */
class VariationConditionHelperTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    "component Comp1 { feature f; feature g; varif (f && (g || !f)) { } }",
    "component Comp2 { feature f; varif (f == (1 < 2)) { } }",
    "component Comp3 { feature f; varif (f && true) { varif (!f) { } } }"
  })
  void copyShouldKeepEnclosingScopes(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);
    IVariableArcComponentTypeSymbol symbol =
      (IVariableArcComponentTypeSymbol) ast.getArcComponentType().getSymbol();

    for (VariableArcVariationPoint variationPoint : symbol.getAllVariationPoints()) {
      ExpressionSet original = new ExpressionSet(variationPoint.getAllConditions());

      // When
      ExpressionSet copy = VariationConditionHelper.getExpressionSetCopyWithContext(original);

      // Then
      assertThat(copy.getExpressions()).hasSameSizeAs(original.getExpressions());
      for (int i = 0; i < original.getExpressions().size(); i++) {
        ASTExpression originalExpr = original.getExpressions().get(i).getAstExpression();
        ASTExpression copiedExpr = copy.getExpressions().get(i).getAstExpression();
        assertThat(copiedExpr).isNotSameAs(originalExpr);
        assertThat(copiedExpr.getEnclosingScope()).isNotNull().isSameAs(originalExpr.getEnclosingScope());
        if (copiedExpr instanceof ASTBooleanAndOpExpression and) {
          assertThat(and.getRight().getEnclosingScope()).isNotNull()
            .isSameAs(((ASTBooleanAndOpExpression) originalExpr).getRight().getEnclosingScope());
        }

        List<ASTExpression> copiedExpressions = new ArrayList<>();
        List<ASTLiteral> copiedLiterals = new ArrayList<>();
        VariationConditionHelper.collectNodes(copiedExpr, copiedExpressions, copiedLiterals);
        assertThat(copiedExpressions).allMatch(e -> e.getEnclosingScope() != null);
        assertThat(copiedLiterals).allMatch(l -> l.getEnclosingScope() != null);
      }
    }
  }
}
