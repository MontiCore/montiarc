/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTComponentHead;
import arcbasis.ArcBasisMill;
import arcbasis.util.ArcError;
import de.se_rwth.commons.logging.Log;
import montiarc.AbstractTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Holds tests for the handwritten methods of {@link PortUniqueSender}.
 */
public class PortUniqueSenderTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @ParameterizedTest
  @MethodSource("componentAndErrorCodeProvider")
  public void shouldDetectMultipleConnectedTarget(ASTComponentType ast, ArcError[] errors) {
    PortUniqueSender coco = new PortUniqueSender();
    coco.check(ast);
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(), errors);
  }

  static Stream<Arguments> componentAndErrorCodeProvider() {
    ASTComponentType comp1 = ArcBasisMill. componentTypeBuilder().setName("Comp1")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i1").setTargetList("o1").build())
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i2").setTargetList("o1").build())
        .build())
      .build();
    ArcError[] errors1 = new ArcError[] { ArcError.PORT_MUlTIPPLE_SENDER };
    ASTComponentType comp2 = ArcBasisMill. componentTypeBuilder().setName("Comp2")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i1").setTargetList("o2").build())
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i1").setTargetList("o2").build())
        .build())
      .build();
    ArcError[] errors2 = new ArcError[] { ArcError.PORT_MUlTIPPLE_SENDER };
    ASTComponentType comp3 = ArcBasisMill. componentTypeBuilder().setName("Comp3")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i1").setTargetList("o1").build())
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i2").setTargetList("o2").build())
        .addArcElement(ArcBasisMill.connectorBuilder().setSource("i3").setTargetList("o3", "o2").build())
        .build())
      .build();
    ArcError[] errors3 = new ArcError[] { ArcError.PORT_MUlTIPPLE_SENDER };
    ASTComponentType comp4 = ArcBasisMill. componentTypeBuilder().setName("Comp4")
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