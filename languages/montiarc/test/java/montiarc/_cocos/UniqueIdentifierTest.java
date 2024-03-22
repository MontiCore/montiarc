/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.UniqueIdentifier;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

public class UniqueIdentifierTest extends MontiArcAbstractTest {

  @ParameterizedTest
  @ValueSource(strings = {
    // single unique component
    "component Comp1 { }",
    // single unique parameter
    "component Comp2(int p) { } ",
    // single unique port
    "component Comp3 { " +
      "port in int i; " +
      "}",
    // single unique variable
    "component Comp4 { " +
      "int v = 0; " +
      "}",
    // single unique inner component
    "component Comp5 { " +
      "component Inner { } " +
      "}",
    // single unique subcomponent
    "component Comp6 { " +
      "component Inner { } " +
      "Inner sub; " +
      "}",
    // single unique type-parameter
    "component Comp7<T> { }",
    // single unique feature
    "component Comp8 { " +
      "feature f; " +
      "}",
    // one of each unique identifier
    "component Comp10<T>(int p) { " +
      "port in int i; " +
      "int v = 0; " +
      "component Inner { } " +
      "Inner sub; " +
      "}",
    // unique parameter (inner component)
    "component Comp12(int p) { " +
      "component Inner(int p) { } " +
      "}",
    // unique port (inner component)
    "component Comp13 { " +
      "port in int i; " +
      "component Inner { " +
      "port in int i; " +
      "} " +
      "}",
    // unique variable (inner component)
    "component Comp14 { " +
      "int v = 0; " +
      "component Inner { " +
      "int v = 0; " +
      "} " +
      "}",
    // unique inner component (inner component)
    "component Comp15 { " +
      "component Inner { " +
      "component Inner { } " +
      "} " +
      "}",
    // unique subcomponent (inner component)
    "component Comp16 { " +
      "component Inner { " +
      "component Inner { } " +
      "Inner sub; " +
      "} " +
      "Inner sub; " +
      "}",
    // unique type-parameter (inner component)
    "component Comp17<T> { " +
      "component Inner<T> { } " +
      "}",
    // unique feature (inner component)
    "component Comp18 { " +
      "feature f; " +
      "component Inner { " +
      "feature f; " +
      "} " +
      "}"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new UniqueIdentifier());

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
    checker.addCoCo(new UniqueIdentifier());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // duplicate component
      //arg("component Comp1 { " +
      //    "component Comp1 { } " +
      //    "}",
      //  ArcError.UNIQUE_IDENTIFIER_NAMES),
      // duplicate parameter
      arg("component Comp2(int p, int p) { }",
        ArcError.UNIQUE_IDENTIFIER_NAMES),
      // duplicate port
      arg("component Comp3 { " +
          "port in int i; " +
          "port in int i; " +
          "}",
        ArcError.UNIQUE_IDENTIFIER_NAMES),
      // duplicate variable
      arg("component Comp4 { " +
          "int v = 0; " +
          "int v = 0; " +
          "}",
        ArcError.UNIQUE_IDENTIFIER_NAMES),
      // duplicate inner component
      arg("component Comp5 { " +
          "component Inner { } " +
          "component Inner { } " +
          "}",
        ArcError.UNIQUE_IDENTIFIER_NAMES),
      // duplicate subcomponent
      arg("component Comp6 { " +
          "component Inner { } " +
          "Inner sub; " +
          "Inner sub; " +
          "}",
        ArcError.UNIQUE_IDENTIFIER_NAMES),
      // duplicate type-parameter
      arg("component Comp7<T, T> { }",
        ArcError.UNIQUE_IDENTIFIER_NAMES),
      // duplicate feature
      //arg("component Comp8 { " +
      //   "feature f; " +
      //    "feature f; " +
      //    "}",
      //  ArcError.UNIQUE_IDENTIFIER_NAMES),
      // duplicate parameter in inner component
      arg("component Comp8() { component Inner(int i, double i, boolean i){ } }",
        ArcError.UNIQUE_IDENTIFIER_NAMES)
    );
  }
}
