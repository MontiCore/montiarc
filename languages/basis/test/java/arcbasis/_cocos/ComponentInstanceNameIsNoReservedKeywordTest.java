/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentInstance;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class ComponentInstanceNameIsNoReservedKeywordTest extends AbstractTest {

  @Test
  public void checkPortNameMatchesKeyword() {
    // Given
    ASTComponentInstance instance = ArcBasisMill.componentInstanceBuilder()
      .setName("keyword")
      .setArcArgumentsAbsent()
      .build();
    ComponentInstanceNameIsNoReservedKeyword coco =
      new ComponentInstanceNameIsNoReservedKeyword("testLang", Collections.singleton("keyword"));
    ArcError expectedError = ArcError.RESERVED_KEYWORD_USED;

    // When
    coco.check(instance);

    // Then
    checkOnlyExpectedErrorsPresent(expectedError);
  }

  @Test
  public void checkPortNameIsNoKeyword() {
    // Given
    ASTComponentInstance instance = ArcBasisMill.componentInstanceBuilder()
      .setName("noKeyword")
      .setArcArgumentsAbsent()
      .build();
    ComponentInstanceNameIsNoReservedKeyword coco =
      new ComponentInstanceNameIsNoReservedKeyword("testLang", Collections.singleton("keyword"));

    // When
    coco.check(instance);

    // Then
    checkOnlyExpectedErrorsPresent();
  }
}
