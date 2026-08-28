/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ParameterDefaultValueTypeFits;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
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

import java.util.stream.Stream;

import static montiarc.util.ArcError.PARAM_DEFAULT_TYPE_MISMATCH;
import static montiarc.util.MCError.TARGET_TYPE_MISMATCH;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ParameterDefaultValueTypeFits}.
 */
class ParameterDefaultValueTypeFitsTest extends MontiArcTestBase {

  @BeforeEach
  protected void initSymbols() {
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // component without parameters
    "component ValidComp1 { }",
    // mandatory boolean parameter without a default value
    "component ValidComp2(boolean p) { }",
    // optional boolean parameter with a matching default value
    "component ValidComp3(boolean p = true) { }",
    // optional int parameter with a matching default value
    "component ValidComp4(int p = 1) { }",
    // two optional int parameters with matching default values
    "component ValidComp5(int p1 = 1, int p2 = 2) { }",
    // optional boolean and optional int parameter, both with matching default values
    "component ValidComp6(boolean p1 = true, int p2 = 2) { }",
    // mandatory boolean parameter and optional int parameter with a matching default value
    "component ValidComp7(boolean p1, int p2 = 2) { }",
    // optional Double parameter with an int literal default value
    "component ValidComp8(java.lang.Double p = 5) { }",
    // optional Number parameter with an int literal default value
    "component ValidComp9(java.lang.Number p = 5) { }",
    //"component ValidComp10(java.lang.Integer p = java.lang.Integer.Integer(1)) { }",
    //"component ValidComp11(java.lang.Comparable<java.lang.Integer> p = java.lang.Integer.Integer(1)) { }"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ParameterDefaultValueTypeFits());

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
    checker.addCoCo(new ParameterDefaultValueTypeFits());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // boolean parameter with a mismatching int literal default value
      arg("component InvalidComp1(boolean p = 1) { }",
        PARAM_DEFAULT_TYPE_MISMATCH),
      // int parameter with a mismatching boolean literal default value
      arg("component InvalidComp2(int p = true) { }",
        PARAM_DEFAULT_TYPE_MISMATCH),
      // one int parameter with a matching default value, one with a mismatching boolean default value
      arg("component InvalidComp3(int p1 = 1, int p2 = false) { }",
        PARAM_DEFAULT_TYPE_MISMATCH),
      // two int parameters, both with mismatching boolean default values
      arg("component InvalidComp4(int p1 = true, int p2 = false) { }",
        PARAM_DEFAULT_TYPE_MISMATCH,
        PARAM_DEFAULT_TYPE_MISMATCH),
      // Integer parameter with an incompatible String default value (general type mismatch)
      arg("component InvalidComp5(java.lang.Integer p = java.lang.String.String()) { }",
        TARGET_TYPE_MISMATCH),
      // Integer parameter with a mismatching double literal default value
      arg("component InvalidComp6(java.lang.Integer p = 5.0) { }",
        PARAM_DEFAULT_TYPE_MISMATCH),
      // Comparable<Double> parameter with a mismatching int literal default value
      arg("component InvalidComp7(java.lang.Comparable<java.lang.Double> p = 5) { }",
        PARAM_DEFAULT_TYPE_MISMATCH),
      // unbound generic type parameter with a mismatching int literal default value
      arg("component InvalidComp8<T>(T p = 1) { }",
        PARAM_DEFAULT_TYPE_MISMATCH),
      // unbound generic type parameter with an incompatible Integer default value (general type mismatch)
      arg("component InvalidComp9<T>(T p = java.lang.Integer.Integer(1)) { }",
        TARGET_TYPE_MISMATCH)
    );
  }
}
