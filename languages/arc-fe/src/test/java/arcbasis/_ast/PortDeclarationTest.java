/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis.ArcBasisMill;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import montiarc.AbstractTest;
import arcbasis.util.ArcError;

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
    ASTPortDeclaration astPortDeclaration1 = ArcBasisMill.portDeclarationBuilder()
      .setMCType(Mockito.mock(ASTMCObjectType.class))
      .setPortDirection(ArcBasisMill.portDirectionInBuilder().build()).setPortList("i1").build();
    Assertions.assertTrue(astPortDeclaration1.isIncoming());
    Assertions.assertFalse(astPortDeclaration1.isOutgoing());
    ASTPortDeclaration astPortDeclaration2 = ArcBasisMill.portDeclarationBuilder()
      .setMCType(Mockito.mock(ASTMCObjectType.class))
      .setPortDirection(ArcBasisMill.portDirectionOutBuilder().build()).setPortList("o1").build();
    Assertions.assertFalse(astPortDeclaration2.isIncoming());
    Assertions.assertTrue(astPortDeclaration2.isOutgoing());
  }
}