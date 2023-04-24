/* (c) https://github.com/MontiCore/monticore */
package montiarc.trafo;

import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.stream.Stream;

class SeparateCompInstantiationFromTypeDeclTrafoTest extends MontiArcAbstractTest {

  static Stream<Arguments> validModels() {
    return Stream.of(
      Arguments.of(
        "component SimpleTrafo {" +
          "  component Inner inner { }" +
          "}", "Inner inner;"),
      Arguments.of(
        "component TrafoWithArg {" +
          "  component Inner(int a) inner(1) { }" +
          "}", "Inner inner(1);"),
      Arguments.of(
        "component MultipleInstances {" +
          "  component Inner inner1, inner2 { }" +
          "}", "Inner inner1, inner2;"),
      Arguments.of(
        "component MultipleInstancesAndArgs {" +
          "  component Inner(int a) inner1(1), inner2(2) { }" +
          "}", "Inner inner1(1), inner2(2);"),
      Arguments.of(
        "component GenericInstantiation {" +
          "  component Inner<T> inner { }" +
          "}", "Inner inner;"),
      Arguments.of(
        "component ConnectedInstantiation {" +
          "  port out int a;" +
          "  component Inner inner [x -> a;] { " +
          "    port out int x;" +
          "  }" +
          "}", "Inner inner [x -> a;];")
    );
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldApplyTrafo(@NotNull String model,
                        @NotNull String expectedNewInstantiation) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(expectedNewInstantiation);
    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    ASTComponentInstantiation expectedTransformedAstNode = MontiArcMill.parser()
      .parse_StringComponentInstantiation(expectedNewInstantiation)
      .orElseThrow();
    MASeparateCompInstantiationFromTypeDeclTrafo trafo = new MASeparateCompInstantiationFromTypeDeclTrafo();

    // When
    trafo.apply(ast);

    // Then
    SoftAssertions.assertSoftly(s -> {
      s.assertThat(ast.getComponentType().getSubComponentInstantiations())
        .as("Instantiation declarations")
        .hasSize(1);
      s.assertThat(ast.getComponentType().getInnerComponents().get(0).getComponentInstanceList())
        .as("Instantiations paired with their type's declaration")
        .isEmpty();
    });

    ASTComponentInstantiation realInstantiation = ast.getComponentType().getSubComponentInstantiations().get(0);

    Assertions.assertThat(realInstantiation)
      .as("Instantiation should equal %s", expectedNewInstantiation)
      .matches(expectedTransformedAstNode::deepEquals);
  }

  static Stream<Arguments> validModelsWithNoTrafo() {
    return Stream.of(
      Arguments.of(
        "component NoTransformation {" +
          "  Inner inner;" +
          "  component Inner { }" +
          "}"),
      Arguments.of(
        "component NoTransformationWithArg {" +
          "  Inner inner(1);" +
          "  component Inner(int a) { }" +
          "}"),
      Arguments.of(
        "component NoTransformationWithGenerics {" +
          "  Inner<int> inner;" +
          "  component Inner<T> { }" +
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
    ASTComponentBody originalBody = ast.getComponentType().getBody();
    ASTComponentBody spyBody = Mockito.spy(originalBody);
    ast.getComponentType().setBody(spyBody);

    MASeparateCompInstantiationFromTypeDeclTrafo trafo = new MASeparateCompInstantiationFromTypeDeclTrafo();

    // When
    trafo.apply(ast);

    // Then
    org.junit.jupiter.api.Assertions.assertAll(
      () -> Mockito.verify(spyBody, Mockito.never()).addArcElement(Mockito.any()),
      () -> Mockito.verify(spyBody, Mockito.never()).addArcElement(Mockito.anyInt(), Mockito.any()),
      () -> Mockito.verify(spyBody, Mockito.never()).addAllArcElements(Mockito.anyCollection()),
      () -> Mockito.verify(spyBody, Mockito.never()).addAllArcElements(Mockito.anyInt(), Mockito.anyCollection())
    );
  }

  @Test
  void shouldApplyTrafoInInnerCompTypes() throws IOException {
    String model =
      "component OuterMost {" +
        "  component Middle { " +
        "    component Inner {" +
        "      component ToInstantiate toInst { }" +
        "    }" +
        "  }" +
        "}";
    String expectedTransformedDecl = "ToInstantiate toInst;";
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    ASTComponentInstantiation expectedTransformedAstNode = MontiArcMill.parser()
      .parse_StringComponentInstantiation(expectedTransformedDecl)
      .orElseThrow();
    MASeparateCompInstantiationFromTypeDeclTrafo trafo = new MASeparateCompInstantiationFromTypeDeclTrafo();

    // When
    trafo.apply(ast);

    // Then
    ASTComponentType innerType = ast.getComponentType() // yields OuterMost
      .getInnerComponents().get(0)  // yields Middle
      .getInnerComponents().get(0);  // yields Inner

    SoftAssertions.assertSoftly(s -> {
      s.assertThat(innerType.getSubComponentInstantiations())
        .as("Instantiation declarations")
        .hasSize(1);
      s.assertThat(innerType.getInnerComponents().get(0).getComponentInstanceList())
        .as("Instantiations paired with their type's declaration")
        .isEmpty();
    });

    ASTComponentInstantiation realInstantiation = innerType.getSubComponentInstantiations().get(0);

    Assertions.assertThat(realInstantiation)
      .as("Instantiation should equal %s", expectedTransformedAstNode)
      .matches(expectedTransformedAstNode::deepEquals);
  }
}
