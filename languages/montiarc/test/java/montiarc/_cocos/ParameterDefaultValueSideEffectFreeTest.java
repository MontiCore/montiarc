/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ParameterDefaultValueSideEffectFree;
import com.google.common.base.Preconditions;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Paths;
import java.util.stream.Stream;

import static montiarc.util.ArcError.NON_PURE_EXPRESSION;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ParameterDefaultValueSideEffectFree}.
 */
class ParameterDefaultValueSideEffectFreeTest extends MontiArcTestBase {

  @BeforeEach
  @Override
  protected void init() {
    super.init();
    MontiArcMill.globalScope().setSymbolPath(new MCPath(Paths.get(TEST_RESOURCE, "symbols")));
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "component ValidComp1(int p) { }",
    "component ValidComp2(int p = 1) { }",
    "component ValidComp3(int p = 1 + 2) { }",
    "component ValidComp4(int p1, int p2 = p1) { }",
    "import montiarc.test.FunctionWithIntParameter; component ValidComp5(int p = FunctionWithIntParameter(1)) { }",
    "import montiarc.test.FunctionWithIntParameter; component ValidComp6(int p1 = 1, int p2 = FunctionWithIntParameter(p1)) { }"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ParameterDefaultValueSideEffectFree());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ParameterDefaultValueSideEffectFree());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg("component InvalidComp1(int p1, int p2 = p1 = 1) { }",
        NON_PURE_EXPRESSION),
      arg("component InvalidComp2(int p1, int p2 = p1 += 1) { }",
        NON_PURE_EXPRESSION),
      arg("component InvalidComp3(int p1, int p2 = ++p1) { }",
        NON_PURE_EXPRESSION),
      arg("component InvalidComp4(int p1, int p2 = --p1) { }",
        NON_PURE_EXPRESSION),
      arg("component InvalidComp5(int p1, int p2 = p1++) { }",
        NON_PURE_EXPRESSION),
      arg("component InvalidComp6(int p1, int p2 = p1--) { }",
        NON_PURE_EXPRESSION),
      arg("import montiarc.test.FunctionWithIntParameter; component InvalidComp7(int p1, int p2 = FunctionWithIntParameter(p1++)) { }",
        NON_PURE_EXPRESSION),
      arg("component InvalidComp8(int p1, int p2 = p1++, int p3 = p1--) { }",
        NON_PURE_EXPRESSION, NON_PURE_EXPRESSION)
    );
  }
}
