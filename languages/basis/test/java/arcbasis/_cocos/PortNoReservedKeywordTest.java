/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._ast.ASTArcPort;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class PortNoReservedKeywordTest extends ArcBasisTestBase {

  @Test
  void checkPortNameMatchesKeyword() {
    // Given
    ASTArcPort port = ArcBasisMill.arcPortBuilder().setName("keyword").build();
    PortNoReservedKeyword coco = new PortNoReservedKeyword("testLang", Collections.singleton("keyword"));

    // When
    coco.check(port);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(ArcError.RESTRICTED_IDENTIFIER));
  }

  @Test
  void checkPortNameIsNoKeyword() {
    // Given
    ASTArcPort port = ArcBasisMill.arcPortBuilder().setName("noKeyword").build();
    PortNoReservedKeyword coco = new PortNoReservedKeyword("testLang", Collections.singleton("keyword"));

    // When
    coco.check(port);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }
}
