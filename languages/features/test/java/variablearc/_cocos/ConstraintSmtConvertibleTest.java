/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import variablearc.VariableArcMill;
import variablearc.VariableArcTestBase;
import variablearc._ast.ASTArcConstraintDeclaration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ConstraintSmtConvertible}
 */
public class ConstraintSmtConvertibleTest extends VariableArcTestBase {

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
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(
        getErrorCodes(VariableArcError.EXPRESSION_NOT_SMT_CONVERTIBLE)
      );
  }
}
