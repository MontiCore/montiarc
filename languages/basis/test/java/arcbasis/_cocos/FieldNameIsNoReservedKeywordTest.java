/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._ast.ASTComponentInstance;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class FieldNameIsNoReservedKeywordTest extends ArcBasisTestBase {

  @Test
  void checkPortNameMatchesKeyword() {
    // Given
    ASTComponentInstance instance = ArcBasisMill.componentInstanceBuilder()
      .setName("keyword")
      .setArcArgumentsAbsent()
      .build();
    SubcomponentNoReservedKeyword coco =
      new SubcomponentNoReservedKeyword("testLang", Collections.singleton("keyword"));

    // When
    coco.check(instance);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes( ArcError.RESTRICTED_IDENTIFIER));
  }

  @Test
  void checkPortNameIsNoKeyword() {
    // Given
    ASTComponentInstance instance = ArcBasisMill.componentInstanceBuilder()
      .setName("noKeyword")
      .setArcArgumentsAbsent()
      .build();
    SubcomponentNoReservedKeyword coco =
      new SubcomponentNoReservedKeyword("testLang", Collections.singleton("keyword"));

    // When
    coco.check(instance);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }
}
