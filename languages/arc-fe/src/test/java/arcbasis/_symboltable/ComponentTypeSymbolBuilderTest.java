/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.util.ArcError;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.TypeVarSymbol;
import montiarc.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Holds tests for the handwritten methods of {@link ComponentTypeSymbolBuilder}.
 */
public class ComponentTypeSymbolBuilderTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @Test
  public void shouldBeValid() {
    ComponentTypeSymbolBuilder builder = new ComponentTypeSymbolBuilder();
    builder.setName("A").setSpannedScope(new ArcBasisScope());
    Assertions.assertTrue(builder.isValid());
  }

  @Test
  public void shouldBeInvalid() {
    ComponentTypeSymbolBuilder builder1 = new ComponentTypeSymbolBuilder();
    ComponentTypeSymbolBuilder builder2 = new ComponentTypeSymbolBuilder();
    builder2.setName("Comp");
    ComponentTypeSymbolBuilder builder3 = new ComponentTypeSymbolBuilder();
    builder3.setSpannedScope(new ArcBasisScope());
    Assertions.assertFalse(builder1.isValid());
    Assertions.assertFalse(builder2.isValid());
    Assertions.assertFalse(builder3.isValid());
  }

  @Test
  public void shouldHaveParent() {
    ComponentTypeSymbolLoader parentComp =
      ArcBasisSymTabMill.componentTypeSymbolLoaderBuilder().setName("A").build();
    ComponentTypeSymbol childComp = ArcBasisSymTabMill.componentTypeSymbolBuilder().setName("B")
      .setSpannedScope(new ArcBasisScope()).setParentComponentLoader(parentComp).build();
    Assertions.assertTrue(childComp.isPresentParentComponent());
  }

  @Test
  public void shouldNotHaveParent() {
    ComponentTypeSymbol symbol = ArcBasisSymTabMill.componentTypeSymbolBuilder().setName("A")
      .setSpannedScope(new ArcBasisScope()).build();
    Assertions.assertFalse(symbol.isPresentParentComponent());
  }

  @Test
  public void shouldHaveOuter() {
    ComponentTypeSymbol outerComp = ArcBasisSymTabMill.componentTypeSymbolBuilder().setName("A")
      .setSpannedScope(new ArcBasisScope()).build();
    ComponentTypeSymbol innerComp = ArcBasisSymTabMill.componentTypeSymbolBuilder().setName("B")
      .setSpannedScope(new ArcBasisScope()).setOuterComponent(outerComp).build();
    Assertions.assertTrue(innerComp.getOuterComponent().isPresent());
  }

  @Test
  public void shouldNotHaveOuter() {
    ComponentTypeSymbol symbol = ArcBasisSymTabMill.componentTypeSymbolBuilder().setName("A")
      .setSpannedScope(new ArcBasisScope()).build();
    Assertions.assertFalse(symbol.getOuterComponent().isPresent());
  }

  @ParameterizedTest
  @MethodSource("compNameAndParametersProvider")
  public void shouldBuildWithExpectedParameters(String name, List<FieldSymbol> parameters) {
    ComponentTypeSymbol symbol = ArcBasisSymTabMill.componentTypeSymbolBuilder().setName(name)
      .setSpannedScope(new ArcBasisScope()).setParameters(parameters).build();
    Assertions.assertEquals(symbol.getName(), name);
    Assertions.assertIterableEquals(symbol.getParameters(), parameters);
  }

  static Stream<Arguments> compNameAndParametersProvider() {
    return Stream.of(arguments("Comp1", Collections.emptyList()),
      arguments("Comp2", Arrays.asList(
        ArcBasisSymTabMill.fieldSymbolBuilder().setName("a").build(),
        ArcBasisSymTabMill.fieldSymbolBuilder().setName("b").build(),
        ArcBasisSymTabMill.fieldSymbolBuilder().setName("c").build())),
      arguments("Comp3", Arrays.asList(
        ArcBasisSymTabMill.fieldSymbolBuilder().setName("c").build(),
        ArcBasisSymTabMill.fieldSymbolBuilder().setName("d").build())));
  }

  @ParameterizedTest
  @MethodSource("compNameAndTypeParametersProvider")
  public void shouldBuildWithExpectedTypeParameters(String name,
    List<TypeVarSymbol> typeParameters) {
    ComponentTypeSymbol symbol = ArcBasisSymTabMill.componentTypeSymbolBuilder().setName(name)
      .setSpannedScope(new ArcBasisScope()).setTypeParameters(typeParameters).build();
    Assertions.assertEquals(symbol.getName(), name);
    Assertions.assertIterableEquals(symbol.getTypeParameters(), typeParameters);
  }

  static Stream<Arguments> compNameAndTypeParametersProvider() {
    return Stream.of(
      arguments("Comp1", Collections.emptyList()),
      arguments("Comp2", Arrays.asList(
        ArcBasisSymTabMill.typeVarSymbolBuilder().setName("A").build(),
        ArcBasisSymTabMill.typeVarSymbolBuilder().setName("B").build(),
        ArcBasisSymTabMill.typeVarSymbolBuilder().setName("C").build())),
      arguments("Comp3", Collections.singletonList(
        ArcBasisSymTabMill.typeVarSymbolBuilder().setName("D").build())));
  }
}