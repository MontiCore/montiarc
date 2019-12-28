/* (c) https://github.com/MontiCore/monticore */
package arc._ast;

import montiarc.AbstractTest;
import montiarc.util.ArcError;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Holds the tests for the handwritten methods of {@link ASTPortExpressionBuilder}.
 */
public class PortExpressionBuilderTest extends AbstractTest {

  protected ASTPortExpressionBuilder builder = new ASTPortExpressionBuilder();

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @ParameterizedTest
  @ValueSource(strings = {"i1", "comp1.i1", "o1", "_comp1.o1"})
  public void shouldBuildExpectedPortExpression(String qualifiedName) {
    ASTPortExpression ast = this.builder.setQualifiedName(qualifiedName).build();
    Assertions.assertEquals(ast.getQualifiedName().getQName(), qualifiedName);
  }
}