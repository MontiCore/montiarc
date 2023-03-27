/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentInstance;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class FieldNameIsNoReservedKeywordTest extends ArcBasisAbstractTest {

  @Test
  void checkPortNameMatchesKeyword() {
    // Given
    ASTComponentInstance instance = ArcBasisMill.componentInstanceBuilder()
      .setName("keyword")
      .setArcArgumentsAbsent()
      .build();
    SubcomponentNoReservedKeyword coco =
      new SubcomponentNoReservedKeyword("testLang", Collections.singleton("keyword"));
    ArcError expectedError = ArcError.RESTRICTED_IDENTIFIER;

    // When
    coco.check(instance);

    // Then
    checkOnlyExpectedErrorsPresent(expectedError);
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
    checkOnlyExpectedErrorsPresent();
  }
}
