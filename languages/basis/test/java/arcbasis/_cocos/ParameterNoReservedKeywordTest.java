/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcParameter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

public class ParameterNoReservedKeywordTest extends ArcBasisAbstractTest {

  @Test
  void checkPortNameMatchesKeyword() {
    // Given
    ASTArcParameter param = ArcBasisMill.arcParameterBuilder()
      .setName("keyword")
      .setMCType(Mockito.mock(ASTMCType.class))
      .setDefaultAbsent()
      .build();
    ParameterNoReservedKeyword coco =
      new ParameterNoReservedKeyword("testLang", Collections.singleton("keyword"));
    ArcError expectedError = ArcError.RESTRICTED_IDENTIFIER;

    // When
    coco.check(param);

    // Then
    checkOnlyExpectedErrorsPresent(expectedError);
  }

  @Test
  void checkPortNameIsNoKeyword() {
    // Given
    ASTArcParameter port = ArcBasisMill.arcParameterBuilder()
      .setName("noKeyword")
      .setMCType(Mockito.mock(ASTMCType.class))
      .setDefaultAbsent()
      .build();
    ParameterNoReservedKeyword coco =
      new ParameterNoReservedKeyword("testLang", Collections.singleton("keyword"));

    // When
    coco.check(port);

    // Then
    checkOnlyExpectedErrorsPresent();
  }
}
