/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcBehaviorElement;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import montiarc.util.ArcError;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

public class OnlyOneBehaviorTest extends AbstractTest {

  protected static Stream<Arguments> numberOfBehaviorsWithErrorProvider() {
    return Stream.of(
      Arguments.of(0, new ArcError[]{}),
      Arguments.of(1, new ArcError[]{}),
      Arguments.of(2, new ArcError[]{ ArcError.MULTIPLE_BEHAVIOR, ArcError.MULTIPLE_BEHAVIOR }),
      Arguments.of(3, new ArcError[]{ ArcError.MULTIPLE_BEHAVIOR, ArcError.MULTIPLE_BEHAVIOR, ArcError.MULTIPLE_BEHAVIOR })
    );
  }

  @ParameterizedTest
  @MethodSource("numberOfBehaviorsWithErrorProvider")
  public void testCocoWithNBehaviors(int numberOfBehaviors, @NotNull ArcError... expectedErrors) {
    Preconditions.checkArgument(numberOfBehaviors >= 0);
    Preconditions.checkNotNull(expectedErrors);

    // Given
    ASTComponentType compType = ArcBasisMill.componentTypeBuilder()
      .setName("Comp")
      .setBody(ArcBasisMill.componentBodyBuilder().build())
      .setHead(Mockito.mock(ASTComponentHead.class))
      .build();
    for(int i = 0; i < numberOfBehaviors; i++) {
      compType.getBody().addArcElement(Mockito.mock(ASTArcBehaviorElement.class));
    }

    // When
    OnlyOneBehavior coco = new OnlyOneBehavior();
    coco.check(compType);

    // Then
    checkOnlyExpectedErrorsPresent(expectedErrors);
  }
}
