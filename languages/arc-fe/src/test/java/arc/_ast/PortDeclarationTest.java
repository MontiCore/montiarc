/* (c) https://github.com/MontiCore/monticore */
package arc._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import montiarc.AbstractTest;
import montiarc.util.ArcError;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.regex.Pattern;

/**
 * Holds test for the handwritten methods of {@link ASTPortDeclaration}.
 */
public class PortDeclarationTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @Test
  public void shouldReturnExpectedDirection() {
    ASTPortDeclaration astPortDeclaration1 = ArcMill.portDeclarationBuilder()
      .setType(Mockito.mock(ASTMCObjectType.class))
      .setDirection("in").setPortList("i1").build();
    Assertions.assertTrue(astPortDeclaration1.isIncoming());
    Assertions.assertFalse(astPortDeclaration1.isOutgoing());
    ASTPortDeclaration astPortDeclaration2 = ArcMill.portDeclarationBuilder()
      .setType(Mockito.mock(ASTMCObjectType.class))
      .setDirection("out").setPortList("o1").build();
    Assertions.assertFalse(astPortDeclaration2.isIncoming());
    Assertions.assertTrue(astPortDeclaration2.isOutgoing());
  }
}