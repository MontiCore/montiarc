/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ParameterNoReservedKeyword;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
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
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * The class under test is {@link ParameterNoReservedKeyword}.
 */
public class ParameterNoReservedKeywordTest extends MontiArcAbstractTest {

  @ParameterizedTest
  @ValueSource(strings = {
    "package key; component Comp1 { }",
    "component Comp2 { component I { } I key; }",
    "component Comp3(int p) { }",
    "component Comp4<key> { }",
    "component Comp5 { int key = 1; }",
    "component Comp6 { port in int key; }",
    "component Comp7 { automaton { state key; } }",
    "component Comp8 { component key { } }",
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ParameterNoReservedKeyword("lang", Arrays.asList("key", "word")));

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
    checker.addCoCo(new ParameterNoReservedKeyword("lang", Arrays.asList("key", "word")));

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(errors.length);
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg("component Comp1(int key) { }",
        ArcError.RESTRICTED_IDENTIFIER),
      arg("component Comp2(int word) { }",
        ArcError.RESTRICTED_IDENTIFIER),
      arg("component Comp3(int p, int key) { }",
        ArcError.RESTRICTED_IDENTIFIER),
      arg("component Comp4(int key, int key) { }",
        ArcError.RESTRICTED_IDENTIFIER,
        ArcError.RESTRICTED_IDENTIFIER)
    );
  }
}
