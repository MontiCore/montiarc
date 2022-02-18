/* (c) https://github.com/MontiCore/monticore */
package arcbehaviorbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbehaviorbasis.AbstractTest;
import arcbehaviorbasis.BehaviorError;
import arcbehaviorbasis._ast.ASTArcBehaviorElement;
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
      Arguments.of(0, new BehaviorError[]{}),
      Arguments.of(1, new BehaviorError[]{}),
      Arguments.of(2, new BehaviorError[]{ BehaviorError.MULTIPLE_BEHAVIOR }),
      Arguments.of(3, new BehaviorError[]{ BehaviorError.MULTIPLE_BEHAVIOR })
    );
  }

  @ParameterizedTest
  @MethodSource("numberOfBehaviorsWithErrorProvider")
  public void testCocoWithNBehaviors(int numberOfBehaviors, @NotNull BehaviorError... expectedErrors) {
    Preconditions.checkArgument(numberOfBehaviors >= 0);
    Preconditions.checkNotNull(expectedErrors);

    // Given
    ASTComponentType compType = provideComponentTypeWithSymbol("Comp");
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
