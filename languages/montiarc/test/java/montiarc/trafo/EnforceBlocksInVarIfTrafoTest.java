/* (c) https://github.com/MontiCore/monticore */
package montiarc.trafo;

import arcbasis._ast.ASTArcElement;
import com.google.common.base.Preconditions;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import variablearc._ast.ASTArcVarIf;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

public class EnforceBlocksInVarIfTrafoTest extends MontiArcAbstractTest {

  static Stream<Arguments> validModels() {
    return Stream.of(
      Arguments.of(
        "component JustTrue {" +
          "  varif (true) port in int a;" +
          "}", "{ port in int a; }", null),
      Arguments.of(
        "component JustTrueTransform {" +
          "  varif (true) port in int a;" +
          "  else { port in int b; }" +
          "}", "{ port in int a; }", "{ port in int b; }"),
      Arguments.of(
        "component JustOtherwiseTransform {" +
          "  varif (true) { port in int a; }" +
          "  else port in int b;" +
          "}", "{ port in int a; }", "{ port in int b; }"),
      Arguments.of(
        "component TransformBoth {" +
          "  varif (true) port in int a;" +
          "  else port in int b;" +
          "}", "{ port in int a; }", "{ port in int b; }")
    );
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldApplyTrafo(@NotNull String model,
                        @NotNull String expectedThen,
                        @Nullable String expectedOtherwise) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(expectedThen);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    ASTArcVarIf varIf = ast.getComponentType().getBody().getElementsOfType(ASTArcVarIf.class).get(0);
    ASTArcElement astExpectedThen = MontiArcMill.parser().parse_StringArcElement(expectedThen).orElseThrow();
    Optional<ASTArcElement> astExpectedOtherwise = expectedOtherwise == null ? Optional.empty() :
      Optional.of(MontiArcMill.parser().parse_StringArcElement(expectedOtherwise).orElseThrow());

    MAEnforceBlocksInVarIfTrafo trafo = new MAEnforceBlocksInVarIfTrafo();

    // When
    trafo.apply(ast);

    // Then
    Assertions.assertThat(varIf.getThen())
      .as("Whether VarIf's then statement is as expected")
      .matches(astExpectedThen::deepEquals);

    astExpectedOtherwise.ifPresent(astArcElement -> Assertions.assertThat(varIf.getOtherwise())
      .as("Whether VarIf's otherwise statement is as expected")
      .matches(astArcElement::deepEquals));
  }

  static Stream<Arguments> validModelsWithNoTrafo() {
    return Stream.of(
      Arguments.of(
        "component NoTransformation {" +
          "  varif (true) { port in int a; }" +
          "  else { port in int b; }" +
          "}"),
      Arguments.of(
        "component SimpleNoTransformation {" +
          "  varif (true) { port in int a; }" +
          "}")
    );
  }
  @ParameterizedTest
  @MethodSource("validModelsWithNoTrafo")
  void shouldNotApplyTrafo(@NotNull String model) throws IOException{
    Preconditions.checkNotNull(model);
    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    // Replace component body with spy so we can verify that the list of component instantiations is not changed
    ASTArcVarIf originalVarIf = ast.getComponentType().getBody().getElementsOfType(ASTArcVarIf.class).get(0);
    ASTArcVarIf spyVarIf = Mockito.spy(originalVarIf);
    ast.getComponentType().getBody().removeArcElement(originalVarIf);
    ast.getComponentType().getBody().addArcElement(spyVarIf);

    MAEnforceBlocksInVarIfTrafo trafo = new MAEnforceBlocksInVarIfTrafo();

    // When
    trafo.apply(ast);

    // Then
    org.junit.jupiter.api.Assertions.assertAll(
      () -> Mockito.verify(spyVarIf, Mockito.never()).setThen(Mockito.any()),
      () -> Mockito.verify(spyVarIf, Mockito.never()).setOtherwise(Mockito.any()),
      () -> Mockito.verify(spyVarIf, Mockito.never()).setOtherwiseAbsent()
    );
  }
}
