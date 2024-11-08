/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._ast.ASTArcParameter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class ParameterNoReservedKeywordTest extends ArcBasisTestBase {

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

    // When
    coco.check(param);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(ArcError.RESTRICTED_IDENTIFIER));
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
    assertThat(Log.getFindings()).isEmpty();
  }
}
