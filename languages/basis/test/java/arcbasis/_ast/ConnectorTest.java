/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Holds test for the handwritten methods of {@link ASTConnector}.
 */
public class ConnectorTest extends AbstractTest {

  @ParameterizedTest
  @MethodSource("connectorAndSourceProvider")
  public void shouldReturnExpectedSource(ASTConnector astConnector, String source) {
    Assertions.assertEquals(source, astConnector.getSourceName());
  }

  @ParameterizedTest
  @MethodSource("connectorAndTargetsProvider")
  public void shouldReturnExpectedTargets(ASTConnector astConnector, String[] targets) {
    List<String> expectedTargets = Arrays.asList(targets);
    List<String> actualTargets = astConnector.getTargetsNames();
    Assertions.assertTrue(expectedTargets.containsAll(actualTargets));
    Assertions.assertTrue(actualTargets.containsAll(expectedTargets));
  }

  static Stream<Arguments> connectorAndSourceProvider() {
    return Stream.of(Arguments.of(getFirstTestConnector(), "i1"),
      Arguments.of(getSecondTestConnector(), "comp1.o1"));
  }

  static Stream<Arguments> connectorAndTargetsProvider() {
    return Stream.of(
      Arguments.of(getFirstTestConnector(), new String[] { "o1", "comp1.i1" }),
      Arguments.of(getSecondTestConnector(), new String[] { "o1", "comp2.i1" }));
  }

  static ASTConnector getFirstTestConnector() {
    return ArcBasisMill.connectorBuilder().setSource("i1").setTargetList("o1", "comp1.i1").build();
  }

  static ASTConnector getSecondTestConnector() {
    return ArcBasisMill.connectorBuilder().setSource("comp1.o1").setTargetList("o1", "comp2.i1").build();
  }
}