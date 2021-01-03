/* (c) https://github.com/MontiCore/monticore */
package arccompute._ast;

import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Holds tests for the handwritten methods of {@link ASTArcComputeBuilder}.
 */
public class ASTArcComputeBuilderTest {

  /**
   * Method under test {@link ASTArcComputeBuilder#build()}.
   */
  @Test
  public void shouldBuildCorrectly() {
    //Given
    final ASTArcComputeBuilder builder = new ASTArcComputeBuilder();
    final ASTMCBlockStatement blockStatement = Mockito.mock(ASTMCBlockStatement.class);

    //When
    final ASTArcCompute compute = builder.setMCBlockStatement(blockStatement).build();

    //Then
    Assertions.assertEquals(blockStatement, compute.getMCBlockStatement());
  }

  /**
   * Method under test {@link ASTArcComputeBuilder#build()}.
   */
  @Test
  public void buildShouldDetectIllegalState() {
    //Given
    final ASTArcComputeBuilder builder = new ASTArcComputeBuilder();

    //When && Then
    Assertions.assertThrows(IllegalStateException.class, builder::build);
  }

  /**
   * Method under test {@link ASTArcComputeBuilder#setMCBlockStatement(ASTMCBlockStatement)}.
   */
  @Test
  public void setterForBlockStatementShouldDetectIllegalArgument() {
    //Given
    final ASTArcComputeBuilder builder = new ASTArcComputeBuilder();

    //When && Then
    Assertions.assertThrows(IllegalArgumentException.class, () -> builder.setMCBlockStatement(null));
  }
}