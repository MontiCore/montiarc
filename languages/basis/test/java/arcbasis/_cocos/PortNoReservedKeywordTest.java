/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcPort;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class PortNoReservedKeywordTest extends ArcBasisAbstractTest {

  @Test
  void checkPortNameMatchesKeyword() {
    // Given
    ASTArcPort port = ArcBasisMill.arcPortBuilder().setName("keyword").build();
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
    ASTArcPort port = ArcBasisMill.arcPortBuilder().setName("noKeyword").build();
    PortNoReservedKeyword coco = new PortNoReservedKeyword("testLang", Collections.singleton("keyword"));

    // When
    coco.check(port);

    // Then
    checkOnlyExpectedErrorsPresent();
  }
}
