/* (c) https://github.com/MontiCore/monticore */
package arcbehaviorbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbehaviorbasis.AbstractTest;
import arcbehaviorbasis.BehaviorError;
import arcbehaviorbasis._ast.ASTArcBehaviorElement;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class NoBehaviorInComposedComponentsTest extends AbstractTest {

  @Test
  public void shouldAlertComposedComponentWithBehavior() {
    // Given
    ASTComponentType compType = provideComponentTypeWithSymbol("Comp");
    ASTComponentType otherCompType = provideComponentTypeWithSymbol("Other");
    addInstanceToComponentType(compType.getSymbol(), otherCompType.getSymbol(), "inner");
    compType.getBody().addArcElement(Mockito.mock(ASTArcBehaviorElement.class));

    // When
    NoBehaviorInComposedComponents coco = new NoBehaviorInComposedComponents();
    coco.check(compType);

    // Then
    checkOnlyExpectedErrorsPresent(BehaviorError.BEHAVIOR_IN_COMPOSED_COMPONENT);
  }

  @Test
  public void shouldPassComposedComponentWithoutBehavior() {
    // Given
    ASTComponentType compType = provideComponentTypeWithSymbol("Comp");
    ASTComponentType otherCompType = provideComponentTypeWithSymbol("Other");
    addInstanceToComponentType(compType.getSymbol(), otherCompType.getSymbol(), "inner");

    // When
    NoBehaviorInComposedComponents coco = new NoBehaviorInComposedComponents();
    coco.check(compType);

    // Then
    checkOnlyExpectedErrorsPresent();
  }

  @Test
  public void shouldPassAtomicComponentWithBehavior() {
    // Given
    ASTComponentType compType = provideComponentTypeWithSymbol("Comp");
    compType.getBody().addArcElement(Mockito.mock(ASTArcBehaviorElement.class));

    // When
    NoBehaviorInComposedComponents coco = new NoBehaviorInComposedComponents();
    coco.check(compType);

    // Then
    checkOnlyExpectedErrorsPresent();
  }

  @Test
  public void shouldPassAtomicComponentWithoutBehavior() {
    // Given
    ASTComponentType compType = provideComponentTypeWithSymbol("Comp");

    // When
    NoBehaviorInComposedComponents coco = new NoBehaviorInComposedComponents();
    coco.check(compType);

    // Then
    checkOnlyExpectedErrorsPresent();
  }
}
