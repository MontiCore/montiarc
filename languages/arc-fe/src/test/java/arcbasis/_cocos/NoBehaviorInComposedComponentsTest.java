/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcBehaviorElement;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.TypeExprOfComponent;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class NoBehaviorInComposedComponentsTest extends AbstractTest {

  @Test
  public void shouldAlertComposedComponentWithBehavior() {
    // Given
    ASTComponentType compType = createComponentTypeWithSymbol("Comp");
    ASTComponentType otherCompType = createComponentTypeWithSymbol("Other");
    addInstanceToComponentType(compType.getSymbol(), otherCompType.getSymbol(), "inner");
    compType.getBody().addArcElement(Mockito.mock(ASTArcBehaviorElement.class));

    // When
    NoBehaviorInComposedComponents coco = new NoBehaviorInComposedComponents();
    coco.check(compType);

    // Then
    checkOnlyExpectedErrorsPresent(ArcError.BEHAVIOR_IN_COMPOSED_COMPONENT);
  }

  @Test
  public void shouldPassComposedComponentWithoutBehavior() {
    // Given
    ASTComponentType compType = createComponentTypeWithSymbol("Comp");
    ASTComponentType otherCompType = createComponentTypeWithSymbol("Other");
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
    ASTComponentType compType = createComponentTypeWithSymbol("Comp");
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
    ASTComponentType compType = createComponentTypeWithSymbol("Comp");

    // When
    NoBehaviorInComposedComponents coco = new NoBehaviorInComposedComponents();
    coco.check(compType);

    // Then
    checkOnlyExpectedErrorsPresent();
  }

  /**
   * Adds a component instance of type {@code instanceType} with name {@code instanceName} as a subcomponent to
   * {@code newOwner}.
   */
  protected static void addInstanceToComponentType(@NotNull ComponentTypeSymbol newOwner,
                                                   @NotNull ComponentTypeSymbol instanceType,
                                                   @NotNull String instanceName) {
    Preconditions.checkNotNull(newOwner);
    Preconditions.checkNotNull(instanceType);
    Preconditions.checkNotNull(instanceName);

    ASTComponentInstance astInst = ArcBasisMill.componentInstanceBuilder()
      .setName(instanceName)
      .build();
    ASTComponentInstantiation astInstDecl = ArcBasisMill.componentInstantiationBuilder()
      .addComponentInstance(astInst)
      .setMCType(Mockito.mock(ASTMCType.class))
      .build();
    ComponentInstanceSymbol symInst = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName(instanceName)
      .setType(new TypeExprOfComponent(instanceType))
      .build();

    astInst.setSymbol(symInst);
    symInst.setAstNode(astInst);

    // Add the instance to its new owner
    newOwner.getSpannedScope().add(symInst);
    astInst.setEnclosingScope(newOwner.getSpannedScope());
    astInstDecl.setEnclosingScope(newOwner.getSpannedScope());

    if(newOwner.isPresentAstNode()) {
      newOwner.getAstNode().getBody().addArcElement(astInstDecl);
    }
  }
}
