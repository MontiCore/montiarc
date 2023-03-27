/* (c) https://github.com/MontiCore/monticore */
package genericarc._cocos;

import arcbasis.ArcBasisAbstractTest;
import genericarc.GenericArcMill;
import genericarc._ast.ASTArcTypeParameter;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class TypeParamNameIsNoReservedKeywordTest extends ArcBasisAbstractTest {

  @Test
  void checkPortNameMatchesKeyword() {
    // Given
    ASTArcTypeParameter typeParam = GenericArcMill.arcTypeParameterBuilder().setName("keyword").build();
    TypeParamNameIsNoReservedKeyword coco =
      new TypeParamNameIsNoReservedKeyword("testLang", Collections.singleton("keyword"));
    ArcError expectedError = ArcError.RESTRICTED_IDENTIFIER;

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
