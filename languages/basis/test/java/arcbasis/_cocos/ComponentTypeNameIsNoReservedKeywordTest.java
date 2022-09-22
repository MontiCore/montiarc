/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

class ComponentTypeNameIsNoReservedKeywordTest extends AbstractTest {

  @Test
  void checkPortNameMatchesKeyword() {
    // Given
    ASTComponentType cType = ArcBasisMill.componentTypeBuilder()
      .setName("keyword")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ComponentTypeNameIsNoReservedKeyword coco =
      new ComponentTypeNameIsNoReservedKeyword("testLang", Collections.singleton("keyword"));
    ArcError expectedError = ArcError.RESERVED_KEYWORD_USED;

    // When
    coco.check(cType);

    // Then
    checkOnlyExpectedErrorsPresent(expectedError);
  }

  @Test
  void checkPortNameIsNoKeyword() {
    // Given
    ASTComponentType cType = ArcBasisMill.componentTypeBuilder()
      .setName("noKeyword")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ComponentTypeNameIsNoReservedKeyword coco =
      new ComponentTypeNameIsNoReservedKeyword("testLang", Collections.singleton("keyword"));

    // When
    coco.check(cType);

    // Then
    checkOnlyExpectedErrorsPresent();
  }
}
