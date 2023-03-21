/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTPort;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class PortNoReservedKeywordTest extends AbstractTest {

  @Test
  void checkPortNameMatchesKeyword() {
    // Given
    ASTPort port = ArcBasisMill.portBuilder().setName("keyword").build();
    PortNoReservedKeyword coco = new PortNoReservedKeyword("testLang", Collections.singleton("keyword"));
    ArcError expectedError = ArcError.RESTRICTED_IDENTIFIER;

    // When
    coco.check(port);

    // Then
    checkOnlyExpectedErrorsPresent(expectedError);
  }

  @Test
  void checkPortNameIsNoKeyword() {
    // Given
    ASTPort port = ArcBasisMill.portBuilder().setName("noKeyword").build();
    PortNoReservedKeyword coco = new PortNoReservedKeyword("testLang", Collections.singleton("keyword"));

    // When
    coco.check(port);

    // Then
    checkOnlyExpectedErrorsPresent();
  }
}
