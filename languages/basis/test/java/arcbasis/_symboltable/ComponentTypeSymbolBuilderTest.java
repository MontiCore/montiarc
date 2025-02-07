/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis.check.TypeExprOfComponent;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
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
 * Holds tests for the handwritten methods of {@link ComponentTypeSymbolBuilder}.
 */
class ComponentTypeSymbolBuilderTest extends ArcBasisTestBase {

  @Test
  void shouldBeValid() {
    ComponentTypeSymbolBuilder builder = new ComponentTypeSymbolBuilder();
    builder.setName("A").setSpannedScope(ArcBasisMill.scope());
    Assertions.assertTrue(builder.isValid());
  }

  @Test
  void shouldBeInvalid() {
    ComponentTypeSymbolBuilder builder1 = new ComponentTypeSymbolBuilder();
    ComponentTypeSymbolBuilder builder2 = new ComponentTypeSymbolBuilder();
    builder2.setName("Comp");
    ComponentTypeSymbolBuilder builder3 = new ComponentTypeSymbolBuilder();
    builder3.setSpannedScope(ArcBasisMill.scope());
    Assertions.assertFalse(builder1.isValid());
    Assertions.assertFalse(builder2.isValid());
    Assertions.assertFalse(builder3.isValid());
  }

  @Test
  void shouldHaveParent() {
    ComponentTypeSymbol parentComp = ArcBasisMill.componentTypeSymbolBuilder()
      .setSpannedScope(Mockito.mock(IArcBasisScope.class)).setName("A").build();
    ComponentTypeSymbol childComp = ArcBasisMill.componentTypeSymbolBuilder().setName("B")
      .setSpannedScope(ArcBasisMill.scope()).setSuperComponentsList(Collections.singletonList(new TypeExprOfComponent(parentComp))).build();
    Assertions.assertFalse(childComp.isEmptySuperComponents());
  }

  @Test
  void shouldNotHaveParent() {
    ComponentTypeSymbol symbol = ArcBasisMill.componentTypeSymbolBuilder().setName("A")
      .setSpannedScope(ArcBasisMill.scope()).build();
    Assertions.assertTrue(symbol.isEmptySuperComponents());
  }

  @Test
  void shouldHaveOuter() {
    ComponentTypeSymbol outerComp = ArcBasisMill.componentTypeSymbolBuilder().setName("A")
      .setSpannedScope(ArcBasisMill.scope()).build();
    ComponentTypeSymbol innerComp = ArcBasisMill.componentTypeSymbolBuilder().setName("B")
      .setSpannedScope(ArcBasisMill.scope()).setOuterComponent(outerComp).build();
    Assertions.assertTrue(innerComp.getOuterComponent().isPresent());
  }

  @Test
  void shouldNotHaveOuter() {
    ComponentTypeSymbol symbol = ArcBasisMill.componentTypeSymbolBuilder().setName("A")
      .setSpannedScope(ArcBasisMill.scope()).build();
    Assertions.assertFalse(symbol.getOuterComponent().isPresent());
  }

  @ParameterizedTest
  @MethodSource("compNameAndParametersProvider")
  void shouldBuildWithExpectedParameters(String name, List<VariableSymbol> parameters) {
    ComponentTypeSymbol symbol = ArcBasisMill.componentTypeSymbolBuilder().setName(name)
      .setSpannedScope(ArcBasisMill.scope()).setParameters(parameters).build();
    Assertions.assertEquals(symbol.getName(), name);
    Assertions.assertIterableEquals(parameters, symbol.getParameters());
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
    ComponentTypeSymbol symbol = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("A")
      .setParameters(List.of(symParamA, symParamB, symParamC, symParamD))
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
    ComponentTypeSymbol symbol = ArcBasisMill.componentTypeSymbolBuilder().setName(name)
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