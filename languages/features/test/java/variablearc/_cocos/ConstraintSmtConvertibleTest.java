/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import variablearc.VariableArcAbstractTest;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcConstraintDeclaration;

/**
 * Tests for {@link ConstraintSmtConvertible}
 */
public class ConstraintSmtConvertibleTest extends VariableArcAbstractTest {

  @Test
  public void shouldConvertToSMT() {
    // Given
    ASTArcConstraintDeclaration constraint = VariableArcMill.arcConstraintDeclarationBuilder()
      .setExpression(VariableArcMill.literalExpressionBuilder().setLiteral(VariableArcMill.booleanLiteralBuilder().setSource(ASTConstantsMCCommonLiterals.TRUE).build()).build())
      .build();

    // When
    new ConstraintSmtConvertible().check(constraint);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldNotConvertToSMT() {
    // Given
    ASTArcConstraintDeclaration constraint = VariableArcMill.arcConstraintDeclarationBuilder()
      .setExpression(VariableArcMill.literalExpressionBuilder().setLiteral(VariableArcMill.nullLiteralBuilder().build()).build())
      .build();

    // When
    new ConstraintSmtConvertible().check(constraint);

    // Then
    this.checkOnlyExpectedErrorsPresent(VariableArcError.EXPRESSION_NOT_SMT_CONVERTIBLE);
  }
}
