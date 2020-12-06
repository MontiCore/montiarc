/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Holds the tests for the handwritten methods of {@link ASTPortAccessBuilder}.
 */
public class PortExpressionBuilderTest extends AbstractTest {

  protected ASTPortAccessBuilder builder = new ASTPortAccessBuilder();

  @ParameterizedTest
  @ValueSource(strings = {"i1", "comp1.i1", "o1", "_comp1.o1"})
  public void shouldBuildExpectedPortExpression(String qualifiedName) {
    ASTPortAccess ast = this.builder.setQualifiedName(qualifiedName).build();
    Assertions.assertEquals(ast.getQName(), qualifiedName);
  }
}