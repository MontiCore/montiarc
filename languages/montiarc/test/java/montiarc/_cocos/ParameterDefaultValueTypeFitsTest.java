/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ParameterDefaultValueTypeFits;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import montiarc.util.MCError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ParameterDefaultValueTypeFits}.
 */
public class ParameterDefaultValueTypeFitsTest extends MontiArcTestBase {

  @BeforeEach
  protected void initSymbols() {
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "component Comp1 { }",
    "component Comp2(boolean p) { }",
    "component Comp2(boolean p = true) { }",
    "component Comp3(int p = 1) { }",
    "component Comp4(int p1 = 1, int p2 = 2) { }",
    "component Comp5(boolean p1 = true, int p2 = 2) { }",
    "component Comp6(boolean p1, int p2 = 2) { }",
    "component Comp7(java.lang.Double p = 5) { }",
    "component Comp8(java.lang.Number p = 5) { }",
    //"component Comp7(java.lang.Integer p = java.lang.Integer.Integer(1)) { }",
    //"component Comp8(java.lang.Comparable<java.lang.Integer> p = java.lang.Integer.Integer(1)) { }"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ParameterDefaultValueTypeFits());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ParameterDefaultValueTypeFits());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg("component Comp1(boolean p = 1) { }",
        ArcError.PARAM_DEFAULT_TYPE_MISMATCH),
      arg("component Comp2(int p = true) { }",
        ArcError.PARAM_DEFAULT_TYPE_MISMATCH),
      arg("component Comp3(int p1 = 1, int p2 = false) { }",
        ArcError.PARAM_DEFAULT_TYPE_MISMATCH),
      arg("component Comp4(int p1 = true, int p2 = false) { }",
        ArcError.PARAM_DEFAULT_TYPE_MISMATCH,
        ArcError.PARAM_DEFAULT_TYPE_MISMATCH),
      arg("component Comp5(int p1 = true, int p2 = false) { }",
        ArcError.PARAM_DEFAULT_TYPE_MISMATCH,
        ArcError.PARAM_DEFAULT_TYPE_MISMATCH),
      arg("component Comp6(java.lang.Integer p = java.lang.String.String()) { }",
        MCError.TARGET_TYPE_MISMATCH),
      arg("component Comp6a(java.lang.Integer p = 5.0) { }",
        ArcError.PARAM_DEFAULT_TYPE_MISMATCH),
      arg("component Comp6b(java.lang.Comparable<java.lang.Double> p = 5) { }",
        ArcError.PARAM_DEFAULT_TYPE_MISMATCH),
      arg("component Comp7<T>(T p = 1) { }",
        ArcError.PARAM_DEFAULT_TYPE_MISMATCH),
      arg("component Comp8<T>(T p = java.lang.Integer.Integer(1)) { }",
        MCError.TARGET_TYPE_MISMATCH)
    );
  }
}
