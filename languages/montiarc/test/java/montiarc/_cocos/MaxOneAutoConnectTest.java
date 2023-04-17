/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import comfortablearc._cocos.MaxOneAutoConnect;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ComfortableArcError;
import montiarc.util.Error;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * The class under test is {@link MaxOneAutoConnect}.
 */
public class MaxOneAutoConnectTest extends MontiArcAbstractTest {

  @BeforeEach
  @Override
  public void setUp() {
    super.setUp();
    compile("component A { }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // atomic no autoconnect
    "component Comp1 { }",
    // composed no autocnnect
    "component Comp2 { " +
      "A a; " +
      "}",
    // composed one autoconnect
    "component Comp3 { " +
      "A a; " +
      "autoconnect port; " +
      "}",
    "component Comp4 { " +
      "component Inner { " +
      "A a; " +
      "autoconnect port; " +
      "}" +
      "Inner sub; " +
      "autoconnect port; " +
      "}"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new MaxOneAutoConnect());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new MaxOneAutoConnect());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // composed two autoconnects
      arg("component Comp1 {" +
          "A a; " +
          "autoconnect port; " +
          "autoconnect port; " +
          "}",
        ComfortableArcError.MULTIPLE_AUTOCONNECTS),
      // composed two different autoconnects
      arg("component Comp1 {" +
          "A a; " +
          "autoconnect port; " +
          "autoconnect type; " +
          "}",
        ComfortableArcError.MULTIPLE_AUTOCONNECTS),
      // composed three autoconnects
      arg("component Comp1 {" +
          "A a; " +
          "autoconnect port; " +
          "autoconnect port; " +
          "autoconnect port; " +
          "}",
        ComfortableArcError.MULTIPLE_AUTOCONNECTS)
    );
  }
}
