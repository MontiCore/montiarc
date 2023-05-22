/* (c) https://github.com/MontiCore/monticore */
package montiarc.trafo;

import arcbasis._ast.ASTConnector;
import arcbasis._cocos.PortUniqueSender;
import com.google.common.base.Preconditions;
import de.monticore.types.check.TypeRelations;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public class AutoConnectTrafoTest extends MontiArcAbstractTest {

  static Stream<Arguments> validModels() {
    return Stream.of(
      Arguments.of(
        "component Comp1 { " +
          "  port in int i;" +
          "  port out int o;" +
          "}", 0),
      Arguments.of(
        "component Comp2 { " +
          "  autoconnect type;" +
          "  port in int i;" +
          "  component Inner inner {" +
          "    port in int i_inner;" +
          "  }" +
          "}", 1),
      Arguments.of(
        "component Comp3 { " +
          "  autoconnect type;" +
          "  port out int o;" +
          "  component Inner inner {" +
          "    port out int o_inner;" +
          "  }" +
          "}", 1),
      Arguments.of(
        "component Comp4 { " +
          "  autoconnect type;" +
          "  port in boolean i;" +
          "  component Inner inner {" +
          "    port in boolean i_inner;" +
          "  }" +
          "}", 1),
      Arguments.of(
        "component Comp5 { " +
          "  autoconnect type;" +
          "  port out boolean o;" +
          "  component Inner inner {" +
          "    port out boolean o_inner;" +
          "  }" +
          "}", 1),
      Arguments.of(
        "component Comp6 { " +
          "  autoconnect type;" +
          "  port in int i;" +
          "  component Inner inner {" +
          "    port in int i1;" +
          "    port in int i2;" +
          "  }" +
          "}", 2),
      Arguments.of(
        "component Comp7 { " +
          "  autoconnect type;" +
          "  port out int o1, o2;" +
          "  component Inner inner {" +
          "    port out int o;" +
          "  }" +
          "}", 2),
      Arguments.of(
        "component Comp8 { " +
          "  autoconnect type;" +
          "  port out int o; " +
          "  component Inner inner {" +
          "    port in boolean o;" +
          "  }" +
          "}", 0),
      Arguments.of(
        "component Comp9 { " +
          "  autoconnect type;" +
          "  port out boolean o; " +
          "  component Inner inner {" +
          "    port in int o;" +
          "  }" +
          "}", 0),
      Arguments.of(
        "component Comp10 { " +
          "  autoconnect type;" +
          "  port in int i; " +
          "  port out boolean o; " +
          "  component Inner inner {" +
          "    port in int i;" +
          "    port out boolean o; " +
          "  }" +
          "}", 2),
      Arguments.of(
        "component Comp11 { " +
          "  autoconnect type;" +
          "  port in boolean i;" +
          "  port out boolean o;" +
          "  component Inner inner {" +
          "    port in int i;" +
          "    port out int o;" +
          "  }" +
          "}", 2),
      Arguments.of(
        "component Comp12 { " +
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
      Arguments.of(
        "component Comp13 {" +
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
        "component Comp14 { " +
          "  autoconnect off;" +
          "  port in int i;" +
          "  port out int o;" +
          "  component Inner inner {" +
          "    port in int i;" +
          "    port out int o;" +
          "  }" +
          "}", 0),
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
          "  port in int i; " +
          "  port out int o;" +
          "  component Inner inner {" +
          "    port in int i; " +
          "    port out int o;" +
          "  }" +
          "}", 2)
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
    List<ASTConnector> before = ast.getComponentType().getConnectors();

    MAAutoConnectTrafo trafo = new MAAutoConnectTrafo(new TypeRelations());

    // When
    trafo.apply(ast);

    List<ASTConnector> after = ast.getComponentType().getConnectors();

    // Then
    SoftAssertions.assertSoftly(a -> {
      a.assertThat(after.size()).as("Checking number of new connectors").isEqualTo(expected);
      a.assertThat(after).as("Should retain connectors").containsAll(before);
      a.assertThat(this.collectErrorCodes(Log.getFindings())).as("Checking Error log").isEmpty();
    });

  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg("component Comp1 {" +
          "  autoconnect type;" +
          "  port in int i1, i2;" +
          "  port out int o;" +
          "}",
        ArcError.PORT_MULTIPLE_SENDER),
      arg("component Comp2 {" +
          "  autoconnect type;" +
          "  port in int i1, i2;" +
          "  port out int o1, o2;" +
          "}",
        ArcError.PORT_MULTIPLE_SENDER,
        ArcError.PORT_MULTIPLE_SENDER),
      arg("component Comp3 {" +
          "  autoconnect type;" +
          "  port in int a, b, c;" +
          "  port out int x;" +
          "}",
        ArcError.PORT_MULTIPLE_SENDER,
        ArcError.PORT_MULTIPLE_SENDER)
    );
  }

  @MethodSource("invalidModels")
  @ParameterizedTest
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcTrafos.afterParsing().applyAll(ast);
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);

    MAAutoConnectTrafo trafo = new MAAutoConnectTrafo(new TypeRelations());

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortUniqueSender());

    // When
    trafo.apply(ast);
    checker.checkAll(ast);

    Assertions.assertThat(this.collectErrorCodes(Log.getFindings()))
      .as("Checking Error log")
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
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

    MAAutoConnectTrafo trafo = new MAAutoConnectTrafo(new TypeRelations());

    // When
    trafo.apply(ast);

    // Then
    Assertions.assertThat(ast.getComponentType().getConnectors())
      .as("Checking connectors within component")
      .isEmpty();
  }
}
