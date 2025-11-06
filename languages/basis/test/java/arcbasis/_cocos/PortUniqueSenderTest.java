/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentHead;
import montiarc.util.ArcError;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Holds tests for the handwritten methods of {@link PortUniqueSender}.
 */
public class PortUniqueSenderTest extends ArcBasisTestBase {

  @ParameterizedTest
  @MethodSource("componentAndErrorCodeProvider")
  public void shouldDetectMultipleConnectedTarget(ASTArcComponentType ast, ArcError[] errors) {
    PortUniqueSender coco = new PortUniqueSender();
    coco.check(ast);
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> componentAndErrorCodeProvider() {
    ASTArcComponentType comp1 = ArcBasisMill.arcComponentTypeBuilder().setName("Comp1")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i1").setTargetList("o1").build())
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i2").setTargetList("o1").build())
        .build())
      .build();
    ArcError[] errors1 = new ArcError[] { ArcError.PORT_MULTIPLE_SENDER};
    ASTArcComponentType comp2 = ArcBasisMill.arcComponentTypeBuilder().setName("Comp2")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i1").setTargetList("o2").build())
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i1").setTargetList("o2").build())
        .build())
      .build();
    ArcError[] errors2 = new ArcError[] { ArcError.PORT_MULTIPLE_SENDER};
    ASTArcComponentType comp3 = ArcBasisMill.arcComponentTypeBuilder().setName("Comp3")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i1").setTargetList("o1").build())
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i2").setTargetList("o2").build())
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i3").setTargetList("o3", "o2").build())
        .build())
      .build();
    ArcError[] errors3 = new ArcError[] { ArcError.PORT_MULTIPLE_SENDER};
    ASTArcComponentType comp4 = ArcBasisMill.arcComponentTypeBuilder().setName("Comp4")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i1").setTargetList("o1").build())
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i1").setTargetList("o2").build())
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i2").setTargetList("o3", "o4").build())
        .build())
      .build();
    ArcError[] errors4 = new ArcError[] { };
    return Stream.of(Arguments.of(comp1, errors1), Arguments.of(comp2, errors2),
      Arguments.of(comp3, errors3), Arguments.of(comp4, errors4));
  }
}
