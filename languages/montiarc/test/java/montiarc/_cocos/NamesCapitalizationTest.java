/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ComponentNameCapitalization;
import arcbasis._cocos.FieldNameCapitalization;
import arcbasis._cocos.ParameterNameCapitalization;
import arcbasis._cocos.PortNameCapitalization;
import arcbasis._cocos.SubcomponentNameCapitalization;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import arcbasis._cocos.TypeParameterCapitalization;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import variablearc._cocos.FeatureNameCapitalization;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the family of name-capitalization CoCos together, since they are conventionally checked
 * as a group: {@link ComponentNameCapitalization}, {@link SubcomponentNameCapitalization},
 * {@link ParameterNameCapitalization}, {@link FieldNameCapitalization},
 * {@link PortNameCapitalization}, {@link TypeParameterCapitalization},
 * {@link FeatureNameCapitalization}.
 */
class NamesCapitalizationTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // uppercase component
    "component ValidComp1 { }",
    // lowercase parameter
    "component ValidComp2(int p) { }",
    // lowercase port
    "component ValidComp3 { " +
      "port in int i; " +
      "}",
    // lowercase variable
    "component ValidComp4 { " +
      "int v = 0; " +
      "}",
    // uppercase inner component
    "component ValidComp5 { " +
      "component Inner { } " +
      "}",
    // lowercase subcomponent
    "component ValidComp6 {" +
      "component Inner { } " +
      "Inner sub; " +
      "}",
    // uppercase type-parameter
    "component ValidComp7<T> { }",
    // lowercase feature
    "component ValidComp8 { " +
      "feature f; " +
      "}",
    // one of each correctly capitalized
    "component ValidComp9<T> { " +
      "port in int i; " +
      "int v = 0; " +
      "component Inner { } " +
      "Inner sub; " +
      "feature f; " +
      "}",
    // lowercase parameter (inner component)
    "component ValidComp10 { " +
      "component Inner(int p) { } " +
      "}",
    // lowercase port (inner component)
    "component ValidComp11 { " +
      "component Inner { " +
      "port in int i; " +
      "} " +
      "}",
    // lowercase variable (inner component)
    "component ValidComp12 { " +
      "component Inner { " +
      "int v = 0; " +
      "} " +
      "}",
    // uppercase inner component (inner component)
    "component ValidComp13 { " +
      "component Inner { " +
      "component Inner2 { } " +
      "} " +
      "}",
    // lowercase subcomponent (inner component)
    "component ValidComp14 { " +
      "component Inner { " +
      "component Inner2 { } " +
      "Inner2 sub; " +
      "} " +
      "}",
    // uppercase type-parameter (inner component)
    "component ValidComp15 { " +
      "component Inner<T> { } " +
      "}",
    // lowercase feature (inner component)
    "component ValidComp16 { " +
      "component Inner { " +
      "feature f; " +
      "} " +
      "}"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ComponentNameCapitalization());
    checker.addCoCo(new SubcomponentNameCapitalization());
    checker.addCoCo(new ParameterNameCapitalization());
    checker.addCoCo(new FieldNameCapitalization());
    checker.addCoCo(new PortNameCapitalization());
    checker.addCoCo(new TypeParameterCapitalization());
    checker.addCoCo(new FeatureNameCapitalization());

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
    checker.addCoCo(new ComponentNameCapitalization());
    checker.addCoCo(new SubcomponentNameCapitalization());
    checker.addCoCo(new ParameterNameCapitalization());
    checker.addCoCo(new FieldNameCapitalization());
    checker.addCoCo(new PortNameCapitalization());
    checker.addCoCo(new TypeParameterCapitalization());
    checker.addCoCo(new FeatureNameCapitalization());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // lowercase component
      arg("component comp1 { }",
        ArcError.COMPONENT_LOWER_CASE),
      // uppercase parameter
      arg("component InvalidComp2(int P) { }",
        ArcError.PARAMETER_UPPER_CASE),
      // uppercase port
      arg("component InvalidComp3 { " +
          "port in int I; " +
          "}",
        ArcError.PORT_UPPER_CASE),
      // uppercase variable
      arg("component InvalidComp4 { " +
          "int V = 0; " +
          "}",
        ArcError.FIELD_UPPER_CASE),
      // lowercase inner component
      arg("component InvalidComp5 { " +
          "component inner { } " +
          "}",
        ArcError.COMPONENT_LOWER_CASE),
      // uppercase subcomponent
      arg("component InvalidComp6 {" +
          "component Inner { } " +
          "Inner Sub; " +
          "}",
        ArcError.SUBCOMPONENT_UPPER_CASE),
      // lowercase type-parameter
      arg("component InvalidComp7<t> { }",
        ArcError.TYPE_PARAMETER_UPPER_CASE),
      // uppercase feature
      arg("component InvalidComp8 { " +
          "feature F; " +
          "}",
        VariableArcError.FEATURE_UPPER_CASE),
      // one of each wrongly capitalized
      arg("component InvalidComp9<t> (int P) { " +
          "port in int I; " +
          "int V = 0; " +
          "component inner { } " +
          "inner Sub; " +
          "feature F; " +
          "} ",
        ArcError.TYPE_PARAMETER_UPPER_CASE,
        ArcError.PARAMETER_UPPER_CASE,
        ArcError.PORT_UPPER_CASE,
        ArcError.FIELD_UPPER_CASE,
        ArcError.COMPONENT_LOWER_CASE,
        ArcError.SUBCOMPONENT_UPPER_CASE,
        VariableArcError.FEATURE_UPPER_CASE),
      // uppercase parameter (inner component)
      arg("component InvalidComp10 { " +
          "component Inner(int P) { } " +
          "}",
        ArcError.PARAMETER_UPPER_CASE),
      // uppercase port (inner component)
      arg("component InvalidComp11 { " +
          "component Inner { " +
          "port in int I; " +
          "} " +
          "}",
        ArcError.PORT_UPPER_CASE),
      // uppercase variable (inner component)
      arg("component InvalidComp12 { " +
          "component Inner { " +
          "int V = 0; " +
          "} " +
          "}",
        ArcError.FIELD_UPPER_CASE),
      // lowercase inner component (inner component)
      arg("component InvalidComp13 { " +
          "component Inner { " +
          "component inner2 { } " +
          "} " +
          "}",
        ArcError.COMPONENT_LOWER_CASE),
      // uppercase subcomponent (inner component)
      arg("component InvalidComp14 { " +
          "component Inner { " +
          "component Inner2 { } " +
          "Inner2 Sub; " +
          "} " +
          "}",
        ArcError.SUBCOMPONENT_UPPER_CASE),
      // lowercase type-parameter (inner component)
      arg("component InvalidComp15 { " +
          "component Inner<t> { } " +
          "}",
        ArcError.TYPE_PARAMETER_UPPER_CASE),
      // uppercase feature (inner component)
      arg("component InvalidComp16 { " +
          "component Inner { " +
          "feature F; " +
          "} " +
          "}",
        VariableArcError.FEATURE_UPPER_CASE)
    );
  }
}
