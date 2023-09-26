/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._symboltable;

import comfortablearc.ComfortableArcAbstractTest;
import comfortablearc.ComfortableArcMill;
import comfortablearc._ast.ASTConnectedComponentInstance;
import comfortablearc._ast.ASTFullyConnectedComponentInstantiation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Holds tests for the handwritten methods of {@link ComfortableArcScopesGenitor}.
 */
public class ComfortableArcScopesGenitorTest extends ComfortableArcAbstractTest {

  @Test
  public void shouldVisitAsComponentInstantiation() {
    // Given
    ComfortableArcScopesGenitorDelegator delegator = ComfortableArcMill.scopesGenitorDelegator();
    ASTFullyConnectedComponentInstantiation fullyConnectedInstantiation = ComfortableArcMill.fullyConnectedComponentInstantiationBuilder().build();

    // When
    fullyConnectedInstantiation.accept(delegator.symbolTable.getTraverser());

    // Then
    Assertions.assertNotNull(fullyConnectedInstantiation.getEnclosingScope());
  }

  @Test
  public void shouldVisitAsComponentInstance() {
    // Given
    ComfortableArcScopesGenitorDelegator delegator = ComfortableArcMill.scopesGenitorDelegator();
    ASTConnectedComponentInstance fullyConnectedInstantiation = ComfortableArcMill.connectedComponentInstanceBuilder().setName("c").build();

    // When
    fullyConnectedInstantiation.accept(delegator.symbolTable.getTraverser());

    // Then
    Assertions.assertNotNull(fullyConnectedInstantiation.getEnclosingScope());
    Assertions.assertTrue(fullyConnectedInstantiation.isPresentSymbol());
  }

}
