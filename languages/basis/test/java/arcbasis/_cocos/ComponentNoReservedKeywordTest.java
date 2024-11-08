/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentNoReservedKeywordTest extends ArcBasisTestBase {

  @Test
  public void checkPortNameMatchesKeyword() {
    // Given
    ASTComponentType cType = ArcBasisMill.componentTypeBuilder()
      .setName("keyword")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ComponentNoReservedKeyword coco =
      new ComponentNoReservedKeyword("testLang", Collections.singleton("keyword"));

    // When
    coco.check(cType);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(ArcError.RESTRICTED_IDENTIFIER));
  }

  @Test
  public void checkPortNameIsNoKeyword() {
    // Given
    ASTComponentType cType = ArcBasisMill.componentTypeBuilder()
      .setName("noKeyword")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ComponentNoReservedKeyword coco =
      new ComponentNoReservedKeyword("testLang", Collections.singleton("keyword"));

    // When
    coco.check(cType);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }
}
