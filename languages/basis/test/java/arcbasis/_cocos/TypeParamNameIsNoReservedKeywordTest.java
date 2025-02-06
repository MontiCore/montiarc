/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import de.monticore.types.typeparameters._ast.ASTTypeParameter;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class TypeParamNameIsNoReservedKeywordTest extends ArcBasisTestBase {

  @Test
  void checkPortNameMatchesKeyword() {
    // Given
    ASTTypeParameter typeParam = ArcBasisMill.typeParameterBuilder().setName("keyword").build();
    TypeParamNameIsNoReservedKeyword coco =
      new TypeParamNameIsNoReservedKeyword("testLang", Collections.singleton("keyword"));

    // When
    coco.check(typeParam);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(ArcError.RESTRICTED_IDENTIFIER));
  }

  @Test
  void checkPortNameIsNoKeyword() {
    // Given
    ASTTypeParameter typeParam = ArcBasisMill.typeParameterBuilder().setName("noKeyword").build();
    TypeParamNameIsNoReservedKeyword coco =
      new TypeParamNameIsNoReservedKeyword("testLang", Collections.singleton("keyword"));

    // When
    coco.check(typeParam);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }
}
