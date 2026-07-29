/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentBody;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests {@link OracleStereotypeValueExists}.
 */
class OracleStereotypeValueExistsTest extends ArcBasisTestBase {

  protected static ASTExpression getStringLiteral(String value) {
    ASTExpression expression = ArcBasisMill.literalExpressionBuilder().setLiteral(ArcBasisMill.stringLiteralBuilder().setSource(value).build()).build();
    expression.setEnclosingScope(ArcBasisMill.globalScope());
    return expression;
  }

  @Test
  void ignoreOtherStereotypes() {
    // Given
    ASTArcComponentType comp = ArcBasisMill.arcComponentTypeBuilder().setName("A")
      .setStereotype(ArcBasisMill.stereotypeBuilder().setValuesList(List.of(
        ArcBasisMill.stereoValueBuilder().setName("test").build(),
        ArcBasisMill.stereoValueBuilder().setName("p").setExpression(getStringLiteral("a")).build(),
        ArcBasisMill.stereoValueBuilder().setName("s").build())).build())
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .setBody(Mockito.mock(ASTComponentBody.class)).build();

    // When
    new OracleStereotypeValueExists().check(comp);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  void correctOracleStereotypeComponent() {
    // Given
    ASTArcComponentType comp = ArcBasisMill.arcComponentTypeBuilder().setName("A")
      .setStereotype(ArcBasisMill.stereotypeBuilder().setValuesList(List.of(
        ArcBasisMill.stereoValueBuilder().setName("oracle").setExpression(getStringLiteral("first")).build())).build())
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .setBody(Mockito.mock(ASTComponentBody.class)).build();

    // When
    new OracleStereotypeValueExists().check(comp);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  void missingOracleStereotypeValue() {
    // Given
    ASTArcComponentType comp = ArcBasisMill.arcComponentTypeBuilder().setName("A")
      .setStereotype(ArcBasisMill.stereotypeBuilder().setValuesList(List.of(
        ArcBasisMill.stereoValueBuilder().setName("oracle").build())).build())
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .setBody(Mockito.mock(ASTComponentBody.class)).build();

    // When
    new OracleStereotypeValueExists().check(comp);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactly(getErrorCodes(ArcError.UNEXPECTED_ORACLE_TYPE));
  }

  @Test
  void wrongOracleType() {
    // Given
    ASTArcComponentType comp = ArcBasisMill.arcComponentTypeBuilder().setName("A")
      .setStereotype(ArcBasisMill.stereotypeBuilder().setValuesList(List.of(
        ArcBasisMill.stereoValueBuilder().setName("oracle").setExpression(getStringLiteral("a")).build())).build())
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .setBody(Mockito.mock(ASTComponentBody.class)).build();

    // When
    new OracleStereotypeValueExists().check(comp);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactly(getErrorCodes(ArcError.UNEXPECTED_ORACLE_TYPE));
  }
}
