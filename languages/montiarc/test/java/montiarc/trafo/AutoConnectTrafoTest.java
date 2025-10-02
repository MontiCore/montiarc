/* (c) https://github.com/MontiCore/monticore */
package montiarc.trafo;

import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import org.assertj.core.api.SoftAssertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class AutoConnectTrafoTest extends MontiArcTestBase {

  static Stream<Arguments> validModels() {
    return Stream.of(
      Arguments.of(
        "component Comp1 { " +
          "  port in int i;" +
          "  port out int o;" +
          "}", 0),
      Arguments.of(
        "component Comp2 { " +
          "  autoconnect off;" +
          "  port in int i;" +
          "  component Inner inner {" +
          "    port in int i_inner;" +
          "  }" +
          "}", 0),
      Arguments.of(
        "component Comp3 { " +
          "  autoconnect off;" +
          "  port out int o;" +
          "  component Inner inner {" +
          "    port out int o_inner;" +
          "  }" +
          "}", 0),
      Arguments.of(
        "component Comp4 { " +
          "  autoconnect off;" +
          "  component Inner inner {" +
          "    port in int i;" +
          "    port out int o;" +
          "  }" +
          "}", 0),
      Arguments.of(
        "component Comp5 { " +
          "  autoconnect off;" +
          "  port in int i;" +
          "  port out boolean o;" +
          "  component Inner inner {" +
          "    port in int i;" +
          "    port out int o;" +
          "  }" +
          "}", 0),
      Arguments.of(
        "component Comp6 { " +
          "  autoconnect off;" +
          "  port in boolean i;" +
          "  port out int o;" +
          "  component Inner inner {" +
          "    port in int i;" +
          "    port out int o;" +
          "  }" +
          "}", 0),
      Arguments.of(
        "component Comp7 { " +
          "  autoconnect type;" +
          "  port in int i;" +
          "  component Inner inner {" +
          "    port in int i_inner;" +
          "  }" +
          "}", 1),
      Arguments.of(
        "component Comp8 { " +
          "  autoconnect type;" +
          "  port out int o;" +
          "  component Inner inner {" +
          "    port out int o_inner;" +
          "  }" +
          "}", 1),
      Arguments.of(
        "component Comp9 { " +
          "  autoconnect type;" +
          "  component Inner inner {" +
          "    port in int i;" +
          "    port out int o;" +
          "  }" +
          "}", 1),
      Arguments.of(
        "component Comp10 { " +
          "  autoconnect type;" +
          "  port in int i;" +
          "  port out boolean o;" +
          "  component Inner inner {" +
          "    port in int i;" +
          "    port out int o;" +
          "  }" +
          "}", 0),
      Arguments.of(
        "component Comp11 { " +
          "  autoconnect type;" +
          "  port in boolean i;" +
          "  port out int o;" +
          "  component Inner inner {" +
          "    port in int i;" +
          "    port out int o;" +
          "  }" +
          "}", 2),
      Arguments.of(
        "component Comp12 { " +
          "  autoconnect type;" +
          "  port in int i;" +
          "  component Inner inner {" +
          "    port in int i1;" +
          "    port in int i2;" +
          "  }" +
          "}", 2),
      Arguments.of(
        "component Comp13 { " +
          "  autoconnect type;" +
          "  port out int o1, o2;" +
          "  component Inner inner {" +
          "    port out int o;" +
          "  }" +
          "}", 2),
      Arguments.of(
        "component Comp14 { " +
          "  autoconnect type;" +
          "  port in int i; " +
          "  port out boolean o; " +
          "  component Inner inner {" +
          "    port in int i;" +
          "    port out boolean o; " +
          "  }" +
          "}", 2),
      Arguments.of(
        "component Comp15 { " +
          "  autoconnect port;" +
          "  port in int i;" +
          "  component Inner inner {" +
          "    port in int i;" +
          "  }" +
          "}", 1),
      Arguments.of(
        "component Comp16 { " +
          "  autoconnect port;" +
          "  port out int o;" +
          "  component Inner inner {" +
          "    port out int o;" +
          "  }" +
          "}", 1),
      Arguments.of(
        "component Comp17 { " +
          "  autoconnect port;" +
          "  component Inner inner {" +
          "    port in int ip;" +
          "    port out int ip;" +
          "  }" +
          "}", 1),
      Arguments.of(
        "component Comp18 { " +
          "  autoconnect port;" +
          "  port in int ip;" +
          "  port out boolean o;" +
          "  component Inner inner {" +
          "    port in int ip;" +
          "    port out int ip;" +
          "  }" +
          "}", 0),
      Arguments.of(
        "component Comp19 { " +
          "  autoconnect port;" +
          "  port in boolean i;" +
          "  port out int op;" +
          "  component Inner inner {" +
          "    port in int op;" +
          "    port out int op;" +
          "  }" +
          "}", 2),
      Arguments.of(
        "component Comp20 {" +
          "  autoconnect type;" +
          "  fullyConnected InnerSource source;" +
          "  fullyConnected InnerTarget target;" +
          "  " +
          "  component InnerSource {" +
          "    port out int o;" +
          "  }" +
          "  component InnerTarget {" +
          "    port in int i;" +
          "  }" +
          "}", 0),
      Arguments.of(
        "component Comp21 {" +
          "  autoconnect port;" +
          "  fullyConnected InnerSource source;" +
          "  fullyConnected InnerTarget target;" +
          "  " +
          "  component InnerSource {" +
          "    port out int o;" +
          "  }" +
          "  component InnerTarget {" +
          "    port in int i;" +
          "  }" +
          "}", 0),
      Arguments.of(
        "component Comp22 {" +
          "  autoconnect off;" +
          "  fullyConnected InnerSource source;" +
          "  fullyConnected InnerTarget target;" +
          "  " +
          "  component InnerSource {" +
          "    port out int o;" +
          "  }" +
          "  component InnerTarget {" +
          "    port in int i;" +
          "  }" +
          "}", 0),
      Arguments.of(
        "component Comp23 { " +
          "  autoconnect port;" +
          "  port in boolean i;" +
          "  port out int o;" +
          "  component Inner inner {" +
          "    port in int i;" +
          "    port out int op;" +
          "  }" +
          "}", 0),
      Arguments.of(
        "component Comp24 { " +
          "  autoconnect type;" +
          "  port in boolean i;" +
          "  port out int op;" +
          "  component Inner inner {" +
          "    port in String i;" +
          "  }" +
          "}", 0),
      Arguments.of(
        "component Comp25 { " +
          "  autoconnect type;" +
          "  port in boolean i;" +
          "  port out int o;" +
          "  component Inner inner {" +
          "    port in int o;" +
          "    port out boolean i;" +
          "  }" +
          "}", 0),
      Arguments.of(
        "component Comp26 { " +
          "  autoconnect port;" +
          "  port in boolean i;" +
          "  port out int o;" +
          "  component Inner inner {" +
          "    port in boolean o;" +
          "    port out int i;" +
          "  }" +
          "}", 0),
      Arguments.of(
        "component Comp27 { " +
          "  autoconnect port;" +
          "  port in boolean i;" +
          "  port out int o;" +
          "  component Inner inner {" +
          "    port in int o;" +
          "    port out boolean i;" +
          "  }" +
          "}", 0),
      Arguments.of(
        "component Comp28 { " +
          "  autoconnect type;" +
          "  port in boolean i1, i2;" +
          "  port out boolean o1, o2;" +
          "  " +
          "  i1 -> inner.i;" +
          "  inner.o -> o1;" +
          "  i2 -> o2;" +
          "  " +
          "  component Inner inner {" +
          "    port in int i;" +
          "    port out int o;" +
          "  }" +
          "}", 3),
      // some transitions already exists
      Arguments.of(
        "component Comp29 { " +
          "  autoconnect port;" +
          "  port in boolean i;" +
          "  port out int o1, o2;" +
          "  " +
          "  inner.o -> o1;" +
          "  i -> o2;" +
          "  " +
          "  component Inner inner {" +
          "    port in boolean i;" +
          "    port out int o;" +
          "  }" +
          "}", 2),
      // one source port matches multiple target ports
      Arguments.of(
        "component Comp30 { " +
          "  autoconnect type;" +
          "  port in boolean i;" +
          "  port out boolean o;" +
          "  component Inner inner {" +
          "    port in int i, x;" +
          "    port out int o;" +
          "  }" +
          "}", 3),
      // usage of autoconnect port and autconnect type
      Arguments.of(
        "component Comp30 { " +
          "  autoconnect type;" +
          "  autoconnect port; " +
          "  port in int a;" +
          "  port in int b;" +
          "  port in int c;" +
          "  port out String y;" +
          "  component Inner inner {" +
          "    port in int a, b, c;" +
          "    port out String x;" +
          "  }" +
          "}", 4)
    );
  }

  @ParameterizedTest
  @MethodSource("validModels")
  public void shouldApplyTrafo(@NotNull String model, int expected) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcTrafos.afterParsing().applyAll(ast);
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);
    List<ASTConnector> before = ast.getArcComponentType().getConnectors();

    MAAutoConnectTrafo trafo = new MAAutoConnectTrafo();

    // When
    trafo.apply(ast);

    List<ASTConnector> after = ast.getArcComponentType().getConnectors();

    // Then
    SoftAssertions.assertSoftly(a -> {
      a.assertThat(after.size()).as("Checking number of new connectors").isEqualTo(expected);
      a.assertThat(after).as("Should retain connectors").containsAll(before);
      a.assertThat(after).map(ASTConnector::getSource).as("Source ports have transferred sub comp symbol")
        .allMatch(p -> p.isPresentComponent() == p.isPresentComponentSymbol());
      a.assertThat(after).map(ASTConnector::getSource).as("Source ports have transferred port symbol")
        .allMatch(ASTPortAccess::isPresentPortSymbol);
      a.assertThat(after).flatMap(ASTConnector::getTargetList)
        .as("Target ports have transferred sub comp symbol")
        .allMatch(p -> p.isPresentComponent() == p.isPresentComponentSymbol());
      a.assertThat(after).flatMap(ASTConnector::getTargetList).as("Target ports have transferred port symbol")
        .allMatch(ASTPortAccess::isPresentPortSymbol);
    });
    assertThat(Log.getFindings()).isEmpty();

  }

  @ValueSource(strings = {"type", "port"})
  @ParameterizedTest
  public void shouldNotConnectObscure(@NotNull String acMode) throws IOException {
    Preconditions.checkNotNull(acMode);

    // Given
    String model =
      "component Comp {" +
        "  autoconnect " + acMode + ";" +
        "  port in Obscure1 fooIn;" +
        "  port out Obscure2 fooOut;" +
        "  port in ObscureButSameTypeName barIn;" +
        "  port out ObscureButSameTypeName barOut;" +
        "}";

    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcTrafos.afterParsing().applyAll(ast);
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);

    MAAutoConnectTrafo trafo = new MAAutoConnectTrafo();

    // When
    trafo.apply(ast);

    // Then
    assertThat(ast.getArcComponentType().getConnectors())
      .as("Checking connectors within component")
      .isEmpty();
  }
}
