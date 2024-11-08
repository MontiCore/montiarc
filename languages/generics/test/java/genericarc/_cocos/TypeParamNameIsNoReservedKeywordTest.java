/* (c) https://github.com/MontiCore/monticore */
package genericarc._cocos;

import de.se_rwth.commons.logging.Log;
import genericarc.GenericArcMill;
import genericarc.GenericArcTestBase;
import genericarc._ast.ASTArcTypeParameter;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class TypeParamNameIsNoReservedKeywordTest extends GenericArcTestBase {

  @Test
  void checkPortNameMatchesKeyword() {
    // Given
    ASTArcTypeParameter typeParam = GenericArcMill.arcTypeParameterBuilder().setName("keyword").build();
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
    ASTArcTypeParameter typeParam = GenericArcMill.arcTypeParameterBuilder().setName("noKeyword").build();
    TypeParamNameIsNoReservedKeyword coco =
      new TypeParamNameIsNoReservedKeyword("testLang", Collections.singleton("keyword"));

    // When
    coco.check(typeParam);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }
}
