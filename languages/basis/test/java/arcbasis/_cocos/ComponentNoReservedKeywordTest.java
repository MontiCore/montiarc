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

public class ComponentNoReservedKeywordTest extends AbstractTest {

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
    ArcError expectedError = ArcError.RESTRICTED_IDENTIFIER;

    // When
    coco.check(cType);

    // Then
    checkOnlyExpectedErrorsPresent(expectedError);
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
    checkOnlyExpectedErrorsPresent();
  }
}
