/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis.check.TypeExprOfComponent;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.CompKindExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Holds tests for the handwritten methods of {@link ArcComponentTypeSymbolBuilder}.
 */
class ArcComponentTypeSymbolBuilderTest extends ArcBasisTestBase {

  @Test
  void shouldBeValid() {
    ArcComponentTypeSymbolBuilder builder = new ArcComponentTypeSymbolBuilder();
    builder.setName("A").setSpannedScope(ArcBasisMill.scope());
    Assertions.assertTrue(builder.isValid());
  }

  @Test
  void shouldBeInvalid() {
    ArcComponentTypeSymbolBuilder builder1 = new ArcComponentTypeSymbolBuilder();
    ArcComponentTypeSymbolBuilder builder2 = new ArcComponentTypeSymbolBuilder();
    builder2.setName("Comp");
    ArcComponentTypeSymbolBuilder builder3 = new ArcComponentTypeSymbolBuilder();
    builder3.setSpannedScope(ArcBasisMill.scope());
    Assertions.assertFalse(builder1.isValid());
    Assertions.assertFalse(builder2.isValid());
    Assertions.assertFalse(builder3.isValid());
  }

  @Test
  void shouldHaveParent() {
    ArcComponentTypeSymbol parentComp = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setSpannedScope(Mockito.mock(IArcBasisScope.class)).setName("A").build();
    ArcComponentTypeSymbol childComp = ArcBasisMill.arcComponentTypeSymbolBuilder().setName("B")
      .setSpannedScope(ArcBasisMill.scope()).setSuperComponentsList(Collections.singletonList(new TypeExprOfComponent(parentComp))).build();
    Assertions.assertFalse(childComp.isEmptySuperComponents());
  }

  @Test
  void shouldNotHaveParent() {
    ArcComponentTypeSymbol symbol = ArcBasisMill.arcComponentTypeSymbolBuilder().setName("A")
      .setSpannedScope(ArcBasisMill.scope()).build();
    Assertions.assertTrue(symbol.isEmptySuperComponents());
  }

  @Test
  void shouldHaveSpec() {
    // Given
    ArcComponentTypeSymbol parentComp = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName("A")
      .setSpannedScope(Mockito.mock(IArcBasisScope.class))
      .build();
    CompKindExpression parentExpr = new TypeExprOfComponent(parentComp);
    ArcComponentTypeSymbolBuilder childBuilder = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName("B")
      .setSpannedScope(ArcBasisMill.scope())
      .setRefinementsList(Collections.singletonList(parentExpr));

    // When
    ArcComponentTypeSymbol child = childBuilder.build();

    // Then
    Assertions.assertEquals(1, child.sizeRefinements());
    Assertions.assertEquals(parentExpr, child.getRefinements(0));
  }

  @Test
  void shouldHaveSpecs() {
    // Given
    ArcComponentTypeSymbol parentComp1 = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName("A1")
      .setSpannedScope(Mockito.mock(IArcBasisScope.class))
      .build();
    ArcComponentTypeSymbol parentComp2 = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName("A2")
      .setSpannedScope(Mockito.mock(IArcBasisScope.class))
      .build();

    CompKindExpression parentExpr1 = new TypeExprOfComponent(parentComp1);
    CompKindExpression parentExpr2 = new TypeExprOfComponent(parentComp2);

    ArcComponentTypeSymbolBuilder childBuilder = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName("B")
      .setSpannedScope(ArcBasisMill.scope())
      .setRefinementsList(List.of(parentExpr1, parentExpr2));

    // When
    ArcComponentTypeSymbol child = childBuilder.build();

    // Then
    Assertions.assertEquals(2, child.sizeRefinements());
    Assertions.assertAll(
      () -> Assertions.assertEquals(parentExpr1, child.getRefinements(0)),
      () -> Assertions.assertEquals(parentExpr2, child.getRefinements(1))
    );
  }

  @Test
  void shouldNotHaveSpecs() {
    // Given
    ArcComponentTypeSymbolBuilder childBuilder = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName("A")
      .setSpannedScope(ArcBasisMill.scope());

    // When
    ArcComponentTypeSymbol child = childBuilder.build();

    // Then
    Assertions.assertTrue(child.isEmptyRefinements());
  }

  @Test
  void shouldHaveOuter() {
    ArcComponentTypeSymbol outerComp = ArcBasisMill.arcComponentTypeSymbolBuilder().setName("A")
      .setSpannedScope(ArcBasisMill.scope()).build();
    ArcComponentTypeSymbol innerComp = ArcBasisMill.arcComponentTypeSymbolBuilder().setName("B")
      .setSpannedScope(ArcBasisMill.scope()).setOuterComponent(outerComp).build();
    Assertions.assertTrue(innerComp.getOuterComponent().isPresent());
  }

  @Test
  void shouldNotHaveOuter() {
    ArcComponentTypeSymbol symbol = ArcBasisMill.arcComponentTypeSymbolBuilder().setName("A")
      .setSpannedScope(ArcBasisMill.scope()).build();
    Assertions.assertFalse(symbol.getOuterComponent().isPresent());
  }

  @ParameterizedTest
  @MethodSource("compNameAndParametersProvider")
  void shouldBuildWithExpectedParameters(String name, List<VariableSymbol> parameters) {
    ArcComponentTypeSymbol symbol = ArcBasisMill.arcComponentTypeSymbolBuilder().setName(name)
      .setSpannedScope(ArcBasisMill.scope()).setParameterList(parameters).build();
    Assertions.assertEquals(symbol.getName(), name);
    Assertions.assertIterableEquals(parameters, symbol.getParameterList());
  }

  static Stream<Arguments> compNameAndParametersProvider() {
    return Stream.of(arguments("Comp1", Collections.emptyList()),
      arguments("Comp2", Arrays.asList(
        ArcBasisMill.variableSymbolBuilder().setName("a").build(),
        ArcBasisMill.variableSymbolBuilder().setName("b").build(),
        ArcBasisMill.variableSymbolBuilder().setName("c").build())),
      arguments("Comp3", Arrays.asList(
        ArcBasisMill.variableSymbolBuilder().setName("c").build(),
        ArcBasisMill.variableSymbolBuilder().setName("d").build())));
  }

  @Test
  void shouldBuildWithExpectedNumberOfOptionalParameters() {
    // Given
    VariableSymbol symParamA = ArcBasisMill.variableSymbolBuilder().setName("A").build();
    VariableSymbol symParamB = ArcBasisMill.variableSymbolBuilder().setName("B").build();
    VariableSymbol symParamC = ArcBasisMill.variableSymbolBuilder().setName("C").build();
    VariableSymbol symParamD = ArcBasisMill.variableSymbolBuilder().setName("D").build();
    int numberOfOptionalParameters = 2;

    // When
    ArcComponentTypeSymbol symbol = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName("A")
      .setParameterList(List.of(symParamA, symParamB, symParamC, symParamD))
      .setNumOptParams(numberOfOptionalParameters)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    // Then
    Assertions.assertEquals(2, symbol.getNumOptParams());
  }

  @ParameterizedTest
  @MethodSource("compNameAndTypeParametersProvider")
  void shouldBuildWithExpectedTypeParameters(String name,
    List<TypeVarSymbol> typeParameters) {
    ArcComponentTypeSymbol symbol = ArcBasisMill.arcComponentTypeSymbolBuilder().setName(name)
      .setSpannedScope(ArcBasisMill.scope()).setTypeParameters(typeParameters).build();
    Assertions.assertEquals(symbol.getName(), name);
    Assertions.assertIterableEquals(symbol.getTypeParameters(), typeParameters);
  }

  static Stream<Arguments> compNameAndTypeParametersProvider() {
    return Stream.of(
      arguments("Comp1", Collections.emptyList()),
      arguments("Comp2", Arrays.asList(
        ArcBasisMill.typeVarSymbolBuilder().setName("A").build(),
        ArcBasisMill.typeVarSymbolBuilder().setName("B").build(),
        ArcBasisMill.typeVarSymbolBuilder().setName("C").build())),
      arguments("Comp3", Collections.singletonList(
        ArcBasisMill.typeVarSymbolBuilder().setName("D").build())));
  }
}