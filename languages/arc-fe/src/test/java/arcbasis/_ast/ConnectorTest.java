/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis.ArcBasisMill;
import montiarc.AbstractTest;
import arcbasis.util.ArcError;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Holds test for the handwritten methods of {@link ASTConnector}.
 */
public class ConnectorTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

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