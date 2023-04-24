/* (c) https://github.com/MontiCore/monticore */
package montiarc.trafo;

import arcbasis._ast.*;
import arcbasis._cocos.ConnectorPortsExist;
import com.google.common.base.Preconditions;
import comfortablearc._ast.ASTConnectedComponentInstance;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.util.ArcError;
import montiarc.util.ComfortableArcError;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import variablearc._ast.ASTArcVarIf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ConnectedToNormalCompInstanceTrafoTest extends MontiArcAbstractTest {

  static Stream<Arguments> validModels() {
    return Stream.of(
      Arguments.of(
        "component NoTransformation {" +
          "  port out int o;" +
          "  " +
          "  Inner inner;" +
          "  inner.o -> o;" +
          "  component Inner {" +
          "    port out int o;" +
          "  }" +
          "}", 1, 1),
      Arguments.of(
        "component SimpleTransformation {" +
          "  port out int o;" +
          "  " +
          "  Inner inner [o -> o;];" +
          "  " +
          "  component Inner {" +
          "    port out int o;" +
          "  }" +
          "}", 1, 1),
      Arguments.of(
        "component TwoNewConnectors {" +
          "  port out int a, b;" +
          "  " +
          "  Inner inner [x -> a; y -> b;];" +
          "  " +
          "  component Inner {" +
          "    port out int x;" +
          "  port out int y;" +
          "  }" +
          "}", 1, 2),
      Arguments.of(
        "component MoreInstances {" +
          "  port out int a, b;" +
          "  " +
          "  Inner inner [x -> a;], inner2 [x -> b;];" +
          "  " +
          "  component Inner {" +
          "    port out int x;" +
          "  }" +
          "}", 2, 2),
      Arguments.of(
        "component PairedWithDirectCompInstantiation {" +
          "  port out int a;" +
          "  " +
          "  component Inner inner [x -> a;] {" +
          "    port out int x;" +
          "  }" +
          "}", 1, 1),
      Arguments.of(
        "component ToOtherInstance {" +
          "  Other other;" +
          "  Inner inner [x -> other.y;];" +
          "  " +
          "  component Inner {" +
          "    port out int x;" +
          "  }" +

          "  component Other {" +
          "    port in int y;" +
          "  }" +
          "}", 2, 1),
      Arguments.of(
        "component CarriesOwnInstanceName {" +
          "  port out int a;" +
          "  " +
          "  Inner inner [inner.x -> a;];" +
          "  " +
          "  component Inner {" +
          "    port out int x;" +
          "  }" +
          "}", 1, 1)
    );
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldApplyTrafo(@NotNull String model,
                        int expectedInstances,
                        int expectedConnectors) throws IOException {
    Preconditions.checkNotNull(model);
    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    new MASeparateCompInstantiationFromTypeDeclTrafo().apply(ast);

    MAConnectedToNormalCompInstanceTrafo trafo = new MAConnectedToNormalCompInstanceTrafo();

    // When
    trafo.apply(ast);

    // Then
    List<ASTConnector> connectorsAfter = ast.getComponentType().getConnectors();
    List<ASTComponentInstance> instantiancesAfter = ast.getComponentType().getSubComponents();
    List<ASTConnectedComponentInstance> connectedComps = connectedCompsWithin(ast.getComponentType().getBody());

    SoftAssertions.assertSoftly(a -> {
      a.assertThat(connectorsAfter.size()).as("Number of connectors").isEqualTo(expectedConnectors);
      a.assertThat(instantiancesAfter.size()).as("Number of instances").isEqualTo(expectedInstances);
      a.assertThat(connectedComps).as("Untransformed asts").isEmpty();
      a.assertThat(this.collectErrorCodes(Log.getFindings())).as("Checking Error log").isEmpty();
    });
  }

  @Test
  void shouldConnectInstanceSrcPortDespiteNameConflictWithCompTypePort() throws IOException {
    // Given
    String model =
      "component Comp {" +
      "  port in int src;" +
      "  port out int o;" +
      "  " +
      "  Inner inner [src -> o;];" +
      "  " +
      "  component Inner {" +
      "    port out int src;" +
      "  }" +
      "}";
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MAConnectedToNormalCompInstanceTrafo trafo = new MAConnectedToNormalCompInstanceTrafo();

    // When
    trafo.apply(ast);

    // Then
    ASTConnector connector = ast.getComponentType().getConnectors().get(0);
    Assertions.assertThat(connector.getSource().isPresentComponent())
      .as("Checking whether port is of an instance")
      .isTrue();
    Assertions.assertThat(connector.getSource().getComponent())
      .as("Port's component's name")
      .isEqualTo("inner");
  }

  @Test
  void shouldNotConnectInstancesTargetPorts() throws IOException {
    // Given
    String model =
      "component Comp {" +
      "  port in int a;" +
      "  " +
      "  Inner inner [a -> x;];" +
      "  " +
      "  component Inner {" +
      "  port in int x;" +
      "  }" +
      "}";
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MAConnectedToNormalCompInstanceTrafo trafo = new MAConnectedToNormalCompInstanceTrafo();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConnectorPortsExist());

    // When
    trafo.apply(ast);
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings()))
      .as("Checking Error log")
      .containsExactlyElementsOf(this.collectErrorCodes(new ArcError[] {ArcError.MISSING_PORT, ArcError.MISSING_PORT}));
  }

  @Test
  void shouldLogErrorOnSourcePortsWithNames() throws IOException {
    // Given
    String model =
      "component Comp {" +
        "  port out int a;" +
        "  " +
        "  Inner inner [illegalName.x -> a;];" +
        "  " +
        "  component Inner {" +
        "    port out int x;" +
        "  }" +
        "}";
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MAConnectedToNormalCompInstanceTrafo trafo = new MAConnectedToNormalCompInstanceTrafo();

    // When
    trafo.apply(ast);
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    // Then
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings()))
      .as("Checking Error log")
      .containsExactlyElementsOf(this.collectErrorCodes(new ComfortableArcError[] {
        ComfortableArcError.CONNECTED_COMPONENT_CONNECTOR_SRC_HAS_COMP_NAME
      }));
  }
  @Test
  void shouldTransformWithinVarIfBlock() throws IOException {
    // Given
    String model =
      "component Comp {" +
      "  port out int a;" +
      "  " +
      "  varif (true) {" +
      "    varif (true) {" +
      "      Inner inner [x -> a;];" +
      "    }" +
      "  }" +
      "  " +
      "  component Inner {" +
      "    port out int x;" +
      "  }" +
      "}";
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MAConnectedToNormalCompInstanceTrafo trafo = new MAConnectedToNormalCompInstanceTrafo();

    // When
    trafo.apply(ast);

    // Then
    ASTArcVarIf firstArcIf = varIfsWithin(ast.getComponentType().getBody()).get(0);
    ASTArcVarIf nestedArcIf = varIfsWithin(((ASTComponentBody) firstArcIf.getThen())).get(0);
    ASTComponentBody arcIfContent = (ASTComponentBody) nestedArcIf.getThen();

    SoftAssertions.assertSoftly(a -> {
      a.assertThat(connectorsWithin(arcIfContent)).as("Number of connectors").hasSize(1);
      a.assertThat(instancesWithin(arcIfContent)).as("Number of instances").hasSize(1);
      a.assertThat(connectedCompsWithin(arcIfContent)).as("Untransformed asts").isEmpty();
      a.assertThat(this.collectErrorCodes(Log.getFindings())).as("Checking Error log").isEmpty();
    });
  }

  private List<ASTConnector> connectorsWithin(ASTComponentBody body) {
    return  body.getElementsOfType(ASTConnector.class);
  }

  private List<ASTArcVarIf> varIfsWithin(ASTComponentBody body) {
    return body.getElementsOfType(ASTArcVarIf.class);
  }

  private List<ASTComponentInstance> instancesWithin(ASTComponentBody body) {
    List<ASTComponentInstance> instances = new ArrayList<>();

    body.streamArcElementsOfType(ASTComponentInstantiation.class)
      .flatMap(ASTComponentInstantiation::streamComponentInstances)
      .forEach(instances::add);

    body.streamArcElementsOfType(ASTComponentType.class)
      .flatMap(ASTComponentType::streamComponentInstances)
      .forEach(instances::add);

    return instances;
  }

  private List<ASTConnectedComponentInstance> connectedCompsWithin(ASTComponentBody body) {
    return instancesWithin(body).stream()
      .filter(ASTConnectedComponentInstance.class::isInstance)
      .map(ASTConnectedComponentInstance.class::cast)
      .collect(Collectors.toList());
  }
}
