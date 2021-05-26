/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Holds tests for the handwritten methods of {@link ComponentTypeSymbolBuilder}.
 */
public class ComponentTypeSymbolBuilderTest extends AbstractTest {

  @Test
  public void shouldBeValid() {
    ComponentTypeSymbolBuilder builder = new ComponentTypeSymbolBuilder();
    builder.setName("A").setSpannedScope(ArcBasisMill.scope());
    Assertions.assertTrue(builder.isValid());
  }

  @Test
  public void shouldBeInvalid() {
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
  public void shouldHaveParent() {
    ComponentTypeSymbolSurrogate parentComp =
      ArcBasisMill.componentTypeSymbolSurrogateBuilder().setName("A").build();
    ComponentTypeSymbol childComp = ArcBasisMill.componentTypeSymbolBuilder().setName("B")
      .setSpannedScope(ArcBasisMill.scope()).setParentComponent(parentComp).build();
    Assertions.assertTrue(childComp.isPresentParentComponent());
  }

  @Test
  public void shouldNotHaveParent() {
    ComponentTypeSymbol symbol = ArcBasisMill.componentTypeSymbolBuilder().setName("A")
      .setSpannedScope(ArcBasisMill.scope()).build();
    Assertions.assertFalse(symbol.isPresentParentComponent());
  }

  @Test
  public void shouldHaveOuter() {
    ComponentTypeSymbol outerComp = ArcBasisMill.componentTypeSymbolBuilder().setName("A")
      .setSpannedScope(ArcBasisMill.scope()).build();
    ComponentTypeSymbol innerComp = ArcBasisMill.componentTypeSymbolBuilder().setName("B")
      .setSpannedScope(ArcBasisMill.scope()).setOuterComponent(outerComp).build();
    Assertions.assertTrue(innerComp.getOuterComponent().isPresent());
  }

  @Test
  public void shouldNotHaveOuter() {
    ComponentTypeSymbol symbol = ArcBasisMill.componentTypeSymbolBuilder().setName("A")
      .setSpannedScope(ArcBasisMill.scope()).build();
    Assertions.assertFalse(symbol.getOuterComponent().isPresent());
  }

  @ParameterizedTest
  @MethodSource("compNameAndParametersProvider")
  public void shouldBuildWithExpectedParameters(String name, List<VariableSymbol> parameters) {
    ComponentTypeSymbol symbol = ArcBasisMill.componentTypeSymbolBuilder().setName(name)
      .setSpannedScope(ArcBasisMill.scope()).setParameters(parameters).build();
    Assertions.assertEquals(symbol.getName(), name);
    Assertions.assertIterableEquals(symbol.getParameters(), parameters);
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

  @ParameterizedTest
  @MethodSource("compNameAndTypeParametersProvider")
  public void shouldBuildWithExpectedTypeParameters(String name,
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