/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.OptionalConfigurationParametersLast;
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
import java.util.stream.Stream;

/**
 * The class under test is {@link OptionalConfigurationParametersLast}.
 */
public class OptionalConfigurationParametersLastTest extends MontiArcAbstractTest {

  @ParameterizedTest
  @ValueSource(strings = {
    // no parameters
    "component Comp1 { }",
    // one mandatory parameter
    "component Comp2(int p) { }",
    // two mandatory parameters
    "component Comp3(int p1, int p2) { }",
    // one optional parameter
    "component Comp4(int p = 1) { }",
    // two optional parameters
    "component Comp5(int p1 = 1, int p2 = 2) { }",
    // one mandatory and one optional parameter
    "component Comp6(int p1, int p2 = 2) { }",
    // two mandatory and one optional parameter
    "component Comp7(int p1, int p2, int p3 = 3) { }",
    // one mandatory and two optional parameters
    "component Comp8(int p1, int p2 = 2, int p3 = 3) { }",
    // two mandatory and two optional parameters
    "component Comp9(int p1, int p2, int p3 = 3, int p4 = 4) { }"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new OptionalConfigurationParametersLast());

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
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new OptionalConfigurationParametersLast());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // one optional and one mandatory parameter
      arg("component Comp1(int p2 = 1, int p1) { }",
        ArcError.OPTIONAL_PARAMS_LAST),
      // two optional and one mandatory parameter
      arg("component Comp2(int p3 = 1, int p2 = 2, int p1) { }",
        ArcError.OPTIONAL_PARAMS_LAST),
      // one mandatory, one optional, and one mandatory parameter
      arg("component Comp3(int p1, int p3 = 2, int p2) { }",
        ArcError.OPTIONAL_PARAMS_LAST),
      // two mandatory and two optional parameter intertwined
      arg("component Comp4(int p1, int p3 = 2, int p2, int p4 = 4) { }",
        ArcError.OPTIONAL_PARAMS_LAST)
    );
  }
}
