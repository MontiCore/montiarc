/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTPort;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class PortNameIsNoReservedKeywordTest extends AbstractTest {

  @Test
  void checkPortNameMatchesKeyword() {
    // Given
    ASTPort port = ArcBasisMill.portBuilder().setName("keyword").build();
    PortNameIsNoReservedKeyword coco = new PortNameIsNoReservedKeyword("testLang", Collections.singleton("keyword"));
    ArcError expectedError = ArcError.RESERVED_KEYWORD_USED;

    // When
    coco.check(port);

    // Then
    checkOnlyExpectedErrorsPresent(expectedError);
  }

  @Test
  void checkPortNameIsNoKeyword() {
    // Given
    ASTPort port = ArcBasisMill.portBuilder().setName("noKeyword").build();
    PortNameIsNoReservedKeyword coco = new PortNameIsNoReservedKeyword("testLang", Collections.singleton("keyword"));

    // When
    coco.check(port);

    // Then
    checkOnlyExpectedErrorsPresent();
  }
}
