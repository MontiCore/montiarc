/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcElement;
import de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import variablearc.VariableArcAbstractTest;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcVarIf;

/**
 * Tests for {@link VarIfSmtConvertible}
 */
public class VarIfSmtConvertibleTest extends VariableArcAbstractTest {

  @Test
  public void shouldConvertToSMT() {
    // Given
    ASTArcVarIf varIf = VariableArcMill.arcVarIfBuilder()
      .setCondition(VariableArcMill.literalExpressionBuilder().setLiteral(VariableArcMill.booleanLiteralBuilder().setSource(ASTConstantsMCCommonLiterals.TRUE).build()).build())
      .setThen(Mockito.mock(ASTArcElement.class))
      .build();

    // When
    new VarIfSmtConvertible().check(varIf);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldNotConvertToSMT() {
    // Given
    ASTArcVarIf varIf = VariableArcMill.arcVarIfBuilder()
      .setCondition(VariableArcMill.literalExpressionBuilder().setLiteral(VariableArcMill.nullLiteralBuilder().build()).build())
      .setThen(Mockito.mock(ASTArcElement.class))
      .build();

    // When
    new VarIfSmtConvertible().check(varIf);

    // Then
    this.checkOnlyExpectedErrorsPresent(VariableArcError.EXPRESSION_NOT_SMT_CONVERTIBLE);
  }
}
