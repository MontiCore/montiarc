/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Holds test for the handwritten methods of {@link ASTPortDeclaration}.
 */
public class PortDeclarationTest extends ArcBasisAbstractTest {

  @Test
  public void shouldReturnExpectedDirection() {
    ASTPortDeclaration astPortDeclaration1 = ArcBasisMill.portDeclarationBuilder()
      .setMCType(Mockito.mock(ASTMCObjectType.class))
      .setPortDirection(ArcBasisMill.portDirectionBuilder().setIn(true).build()).setPortList("i1").build();
    Assertions.assertTrue(astPortDeclaration1.isIncoming());
    Assertions.assertFalse(astPortDeclaration1.isOutgoing());
    ASTPortDeclaration astPortDeclaration2 = ArcBasisMill.portDeclarationBuilder()
      .setMCType(Mockito.mock(ASTMCObjectType.class))
      .setPortDirection(ArcBasisMill.portDirectionBuilder().setOut(true).build()).setPortList("o1").build();
    Assertions.assertFalse(astPortDeclaration2.isIncoming());
    Assertions.assertTrue(astPortDeclaration2.isOutgoing());
  }
}