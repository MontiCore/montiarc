/* (c) https://github.com/MontiCore/monticore */
package genericarc._cocos;

import arcbasis.AbstractTest;
import genericarc.GenericArcMill;
import genericarc._ast.ASTArcTypeParameter;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class TypeParamNameIsNoReservedKeywordTest extends AbstractTest {

  @Test
  void checkPortNameMatchesKeyword() {
    // Given
    ASTArcTypeParameter typeParam = GenericArcMill.arcTypeParameterBuilder().setName("keyword").build();
    TypeParamNameIsNoReservedKeyword coco =
      new TypeParamNameIsNoReservedKeyword("testLang", Collections.singleton("keyword"));
    ArcError expectedError = ArcError.RESERVED_KEYWORD_USED;

    // When
    coco.check(typeParam);

    // Then
    checkOnlyExpectedErrorsPresent(expectedError);
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
    checkOnlyExpectedErrorsPresent();
  }
}