/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ComponentNameCapitalization;
import arcbasis._cocos.FieldNameCapitalization;
import arcbasis._cocos.ParameterNameCapitalization;
import arcbasis._cocos.PortNameCapitalization;
import arcbasis._cocos.SubcomponentNameCapitalization;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import genericarc._cocos.GenericTypeParameterNameCapitalization;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import montiarc.util.GenericArcError;
import montiarc.util.VariableArcError;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import variablearc._cocos.FeatureNameCapitalization;

import java.io.IOException;
import java.util.stream.Stream;

public class NamesCapitalizationTest extends MontiArcAbstractTest {

  @ParameterizedTest
  @ValueSource(strings = {
    // uppercase component
    "component Comp1 { }",
    // lowercase parameter
    "component Comp2(int p) { }",
    // lowercase port
    "component Comp3 { " +
      "port in int i; " +
      "}",
    // lowercase variable
    "component Comp4 { " +
      "int v = 0; " +
      "}",
    // uppercase inner component
    "component Comp5 { " +
      "component Inner { } " +
      "}",
    // lowercase subcomponent
    "component Comp6 {" +
      "component Inner { } " +
      "Inner sub; " +
      "}",
    // uppercase type-parameter
    "component Comp7<T> { }",
    // lowercase feature
    "component Comp8 { " +
      "feature f; " +
      "}",
    // one of each correctly capitalized
    "component Comp9<T> { " +
      "port in int i; " +
      "int v = 0; " +
      "component Inner { } " +
      "Inner sub; " +
      "feature f; " +
      "}",
    // lowercase parameter (inner component)
    "component Comp10 { " +
      "component Inner(int p) { } " +
      "}",
    // lowercase port (inner component)
    "component Comp11 { " +
      "component Inner { " +
      "port in int i; " +
      "} " +
      "}",
    // lowercase variable (inner component)
    "component Comp12 { " +
      "component Inner { " +
      "int v = 0; " +
      "} " +
      "}",
    // uppercase inner component (inner component)
    "component Comp13 { " +
      "component Inner { " +
      "component Inner2 { } " +
      "} " +
      "}",
    // lowercase subcomponent (inner component)
    "component Comp14 { " +
      "component Inner { " +
      "component Inner2 { } " +
      "Inner2 sub; " +
      "} " +
      "}",
    // uppercase type-parameter (inner component)
    "component Comp15 { " +
      "component Inner<T> { } " +
      "}",
    // lowercase feature (inner component)
    "component Comp16 { " +
      "component Inner { " +
      "feature f; " +
      "} " +
      "}"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ComponentNameCapitalization());
    checker.addCoCo(new SubcomponentNameCapitalization());
    checker.addCoCo(new ParameterNameCapitalization());
    checker.addCoCo(new FieldNameCapitalization());
    checker.addCoCo(new PortNameCapitalization());
    checker.addCoCo(new GenericTypeParameterNameCapitalization());
    checker.addCoCo(new FeatureNameCapitalization());

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
    checker.addCoCo(new ComponentNameCapitalization());
    checker.addCoCo(new SubcomponentNameCapitalization());
    checker.addCoCo(new ParameterNameCapitalization());
    checker.addCoCo(new FieldNameCapitalization());
    checker.addCoCo(new PortNameCapitalization());
    checker.addCoCo(new GenericTypeParameterNameCapitalization());
    checker.addCoCo(new FeatureNameCapitalization());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // lowercase component
      arg("component comp1 { }",
        ArcError.COMPONENT_LOWER_CASE),
      // uppercase parameter
      arg("component Comp2(int P) { }",
        ArcError.PARAMETER_UPPER_CASE),
      // uppercase port
      arg("component Comp3 { " +
          "port in int I; " +
          "}",
        ArcError.PORT_UPPER_CASE),
      // uppercase variable
      arg("component Comp4 { " +
          "int V = 0; " +
          "}",
        ArcError.FIELD_UPPER_CASE),
      // lowercase inner component
      arg("component Comp5 { " +
          "component inner { } " +
          "}",
        ArcError.COMPONENT_LOWER_CASE),
      // uppercase subcomponent
      arg("component Comp6 {" +
          "component Inner { } " +
          "Inner Sub; " +
          "}",
        ArcError.SUBCOMPONENT_UPPER_CASE),
      // lowercase type-parameter
      arg("component Comp7<t> { }",
        GenericArcError.TYPE_PARAMETER_UPPER_CASE),
      // uppercase feature
      arg("component Comp8 { " +
          "feature F; " +
          "}",
        VariableArcError.FEATURE_UPPER_CASE),
      // one of each wrongly capitalized
      arg("component Comp9<t> (int P) { " +
          "port in int I; " +
          "int V = 0; " +
          "component inner { } " +
          "inner Sub; " +
          "feature F; " +
          "} ",
        GenericArcError.TYPE_PARAMETER_UPPER_CASE,
        ArcError.PARAMETER_UPPER_CASE,
        ArcError.PORT_UPPER_CASE,
        ArcError.FIELD_UPPER_CASE,
        ArcError.COMPONENT_LOWER_CASE,
        ArcError.SUBCOMPONENT_UPPER_CASE,
        VariableArcError.FEATURE_UPPER_CASE),
      // uppercase parameter (inner component)
      arg("component Comp10 { " +
          "component Inner(int P) { } " +
          "}",
        ArcError.PARAMETER_UPPER_CASE),
      // uppercase port (inner component)
      arg("component Comp11 { " +
          "component Inner { " +
          "port in int I; " +
          "} " +
          "}",
        ArcError.PORT_UPPER_CASE),
      // uppercase variable (inner component)
      arg("component Comp12 { " +
          "component Inner { " +
          "int V = 0; " +
          "} " +
          "}",
        ArcError.FIELD_UPPER_CASE),
      // lowercase inner component (inner component)
      arg("component Comp13 { " +
          "component Inner { " +
          "component inner2 { } " +
          "} " +
          "}",
        ArcError.COMPONENT_LOWER_CASE),
      // uppercase subcomponent (inner component)
      arg("component Comp14 { " +
          "component Inner { " +
          "component Inner2 { } " +
          "Inner2 Sub; " +
          "} " +
          "}",
        ArcError.SUBCOMPONENT_UPPER_CASE),
      // lowercase type-parameter (inner component)
      arg("component Comp15 { " +
          "component Inner<t> { } " +
          "}",
        GenericArcError.TYPE_PARAMETER_UPPER_CASE),
      // uppercase feature (inner component)
      arg("component Comp16 { " +
          "component Inner { " +
          "feature F; " +
          "} " +
          "}",
        VariableArcError.FEATURE_UPPER_CASE)
    );
  }
}
