/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis.timing.Timing;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

/**
 * Holds test for the handwritten methods of {@link ASTPortDeclaration}.
 */
public class PortDeclarationTest extends AbstractTest {

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

  @Test
  public void shouldReturnExpectedTiming() {
    // Given
    ASTPortDeclaration astPortDeclaration = ArcBasisMill.portDeclarationBuilder()
        .setMCType(Mockito.mock(ASTMCObjectType.class))
        .setStereotype(ArcBasisMill.stereotypeBuilder().setValuesList(List.of(ArcBasisMill.stereoValueBuilder().setName(Timing.SYNC.getName()).setContent("").build())).build())
        .setPortDirection(ArcBasisMill.portDirectionBuilder().setIn(true).build()).setPortList("i1").build();

    // Then
    Assertions.assertEquals(1, astPortDeclaration.getTimings().size());
    Assertions.assertEquals(Timing.SYNC, astPortDeclaration.getTimings().get(0));
  }
}