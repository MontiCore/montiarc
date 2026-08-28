/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import montiarc.util.MCError;
import montiarc.util.MontiArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link MaUnitTestConfiguredCorrectly}.
 */
class MaUnitTestConfiguredCorrectlyTest extends MontiArcTestBase {

  @BeforeEach
  protected void initSymbols() {
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // no test
    "component ValidComp1 { }",
    // no test with other stereotypes
    "<<a, b=\"as\", c=[]>> component ValidComp2 { }",
    // test
    "<<test>> component ValidComp3 { }",
    // test, custom tick count
    "<<test, ticks=5>> component ValidComp4 { }",
    // test, custom tick count, default parameter value
    "<<test, ticks=5>> component ValidComp5(int i = 0) { }",
    // test, custom tick count, custom parameter value
    "<<test, ticks=5, i=5>> component ValidComp6(int i = 0) { }",
    // test, custom tick count, custom feature value
    "<<test, f=true>> component ValidComp7() { feature f; }",
    // 2 tests, custom ticks count
    "<<test, ticks=[1, 2]>> component ValidComp8() { }",
    // 2 tests, custom ticks count, custom parameter value
    "<<test, ticks=[1, 2], b=true>> component ValidComp9(boolean b) { }",
    // 2 tests, custom ticks count, custom parameter values
    "<<test, ticks=[1, 2], b=[false, true]>> component ValidComp10(boolean b) { }",
    // 2 tests, custom parameter values
    "<<test, b=[false, true]>> component ValidComp11(boolean b) { }",
    // 2 tests, custom tick count, custom parameter values
    "<<test, ticks=3, b=[false, true]>> component ValidComp12(boolean b) { }",
    // 2 tests, test source, custom tick count, custom parameter values
    "<<test={[false],[true]}, ticks=[1,3]>> component ValidComp13(boolean b) { }",
    // 2 tests, test source, custom tick count, custom parameter values
    "<<test={[false, 1],[true, 2]}>> component ValidComp14(boolean b, int i) { }",
    // 2 tests, test source, custom tick count, custom parameter values, default omitted
    "<<test={[false, 1],[true]}>> component ValidComp15(boolean b, int i = 0) { }",
    // test, custom tick length
    "<<test, simulatedTickLength=1000>> component ValidComp16() { }",
    // 2 tests, custom ticks length
    "<<test, simulatedTickLength=[1, 2]>> component ValidComp17() { }",
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new MaUnitTestConfiguredCorrectly());

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
    checker.addCoCo(new MaUnitTestConfiguredCorrectly());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactly(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // test, 1 missing parameter assignment
      arg("<<test>> component InvalidComp1(int i) { }",
        MontiArcError.UNIT_MISSING_ARGUMENT),
      // test, 1 missing parameter assignment
      arg("<<test>> component InvalidComp2(int i, int j = 2) { }",
        MontiArcError.UNIT_MISSING_ARGUMENT),
      // test, 2 missing parameter assignments
      arg("<<test>> component InvalidComp3(int i, int j) { }",
        MontiArcError.UNIT_MISSING_ARGUMENT,
        MontiArcError.UNIT_MISSING_ARGUMENT),
      // test, missing parameter assignment
      arg("<<test, i = 1>> component InvalidComp4(int i, int j) { }",
        MontiArcError.UNIT_MISSING_ARGUMENT),
      // test, multiple parameter assignment
      arg("<<test, i = 1, i = 5>> component InvalidComp5(int i) { }",
        MontiArcError.UNIT_DUPLICATE_ARGUMENTS),
      // test, multiple parameter assignment
      arg("<<test, i = 1, i=[1,2]>> component InvalidComp6(int i) { }",
        MontiArcError.UNIT_DUPLICATE_ARGUMENTS),
      // test, multiple tick assignment
      arg("<<test, ticks = 1, ticks=[1,2]>> component InvalidComp7() { }",
        MontiArcError.UNIT_DUPLICATE_ARGUMENTS),
      // 3 tests, missing values for tests
      arg("<<test, ticks=[1,2,3], i = [1,2]>> component InvalidComp8(int i = 0, int j = 0) { }",
        MontiArcError.UNIT_TEST_COUNT_MISMATCH),
      // 3 tests, missing values for tests (k is good since it's not using lists)
      arg("<<test, ticks=[1,2], i = [1,2,3], j=[1], k=0>> component InvalidComp9(int i = 0, int j = 0, int k) { }",
        MontiArcError.UNIT_TEST_COUNT_MISMATCH,
        MontiArcError.UNIT_TEST_COUNT_MISMATCH),
      // test, ticks type mismatch
      arg("<<test, ticks=true>> component InvalidComp10() { }",
        MontiArcError.UNIT_TYPE_MISMATCH),
      // 2 tests, parameter type mismatch in list
      arg("<<test, ticks=[1,2], i=true>> component InvalidComp11(int i) { }",
        MontiArcError.UNIT_TYPE_MISMATCH),
      // 2 tests, ticks type mismatch in list
      arg("<<test, ticks=[1,true]>> component InvalidComp12() { }",
        MCError.TARGET_TYPE_MISMATCH),
      // 2 tests, parameter type mismatch in list
      arg("<<test, ticks=[1,2], i=[1,true]>> component InvalidComp13(int i) { }",
        MCError.TARGET_TYPE_MISMATCH),
      // test, but not deployable
      arg("<<test>> component InvalidComp14 { port out int p; }",
        MontiArcError.UNIT_CANNOT_HAVE_PORTS),
      // 2 tests, test source, missing argument
      arg("<<test={[0], [1]}>> component InvalidComp15(int i, int j) { }",
        MontiArcError.UNIT_MISSING_ARGUMENTS,
        MontiArcError.UNIT_MISSING_ARGUMENTS),
      // 2 tests, test source, to many arguments
      arg("<<test={[0, 1, 2], [1]}>> component InvalidComp16(int i, int j = 0) { }",
        MontiArcError.UNIT_TOO_MANY_ARGUMENTS),
      // No test, test source misconfigured
      arg("<<test=5>> component InvalidComp17() { }",
        MontiArcError.UNIT_TEST_SOURCE_MISCONFIGURED),
      // 2 tests, test source, individual test misconfigured
      arg("<<test={[], 5}>> component InvalidComp18() { }",
        MontiArcError.UNIT_TEST_CASE_MISCONFIGURED),
      // 2 tests, test source, individual test misconfigured
      arg("<<test={[], {}}>> component InvalidComp19() { }",
        MontiArcError.UNIT_TEST_CASE_MISCONFIGURED),
      // 1 test, test source, unsupported list feature
      arg("<<test={[1..2]}>> component InvalidComp20(int i, int j) { }",
        MontiArcError.UNIT_TEST_CASE_PARAMETER_MISCONFIGURED,
        MontiArcError.UNIT_MISSING_ARGUMENTS),
      // 1 test, test source and value source combined
      arg("<<test={[1,2]}, j=2>> component InvalidComp21(int i, int j) { }",
        MontiArcError.UNIT_TEST_SOURCE_AND_VALUE_SOURCE),
      // 2 tests, test source, type mismatch
      arg("<<test={[true]}>> component InvalidComp22(int i) { }",
        MontiArcError.UNIT_TYPE_MISMATCH),
      // 3 tests, missing values for tests
      arg("<<test={[], [0]}, ticks=[1,2,3]>> component InvalidComp23(int i = 0, int j = 0) { }",
        MontiArcError.UNIT_TEST_COUNT_MISMATCH),
      // test, ticks type mismatch
      arg("<<test, simulatedTickLength=true>> component InvalidComp24() { }",
        MontiArcError.UNIT_TYPE_MISMATCH),
      // 2 tests, tick length type mismatch in list
      arg("<<test, simulatedTickLength=[1,true]>> component InvalidComp25() { }",
        MCError.TARGET_TYPE_MISMATCH)
    );
  }
}
