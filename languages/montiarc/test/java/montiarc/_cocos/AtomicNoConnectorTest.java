/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.AtomicNoConnector;
import com.google.common.base.Preconditions;
import montiarc.MontiArcAbstractTest;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * The class under test is {@link AtomicNoConnector}.
 */
public class AtomicNoConnectorTest extends MontiArcAbstractTest {

  @ParameterizedTest
  @ValueSource(strings = {
    // atomic no connector
    "component Comp1 { }",
    // composed with connector
    "component Comp2 { " +
      "component Inner1 i1 { } " +
      "port in int i;" +
      "port out int o;" +
      "i -> o; " +
      "}"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new AtomicNoConnector());

    // When
    checker.checkAll(ast);

    // Then
    checkOnlyExpectedErrorsPresent();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new AtomicNoConnector());

    // When
    checker.checkAll(ast);

    // Then
    checkOnlyExpectedErrorsPresent(errors);
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // atomic one connector
      arg("component Comp1 { " +
          "port in int i;" +
          "port out int o;" +
          "i -> o; " +
          "}",
        ArcError.CONNECTORS_IN_ATOMIC),
      // atomic two connectors
      arg("component Comp2 { " +
          "port in int i;" +
          "port out int o;" +
          "i -> o; " +
          "i -> o; " +
          "}",
        ArcError.CONNECTORS_IN_ATOMIC,
        ArcError.CONNECTORS_IN_ATOMIC)
    );
  }
}
