/* (c) https://github.com/MontiCore/monticore */
package arc._symboltable;

import arc.util.ArcError;
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
 * Holds tests for the handwritten methods of {@link ComponentSymbolBuilder}.
 */
public class ComponentSymbolBuilderTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @Test
  public void shouldBeValid() {
    ComponentSymbolBuilder builder = new ComponentSymbolBuilder();
    builder.setName("A").setSpannedScope(new ArcScope());
    Assertions.assertTrue(builder.isValid());
  }

  @Test
  public void shouldBeInvalid() {
    ComponentSymbolBuilder builder1 = new ComponentSymbolBuilder();
    ComponentSymbolBuilder builder2 = new ComponentSymbolBuilder();
    builder2.setName("Comp");
    ComponentSymbolBuilder builder3 = new ComponentSymbolBuilder();
    builder3.setSpannedScope(new ArcScope());
    Assertions.assertFalse(builder1.isValid());
    Assertions.assertFalse(builder2.isValid());
    Assertions.assertFalse(builder3.isValid());
  }

  @Test
  public void shouldHaveParent() {
    ComponentSymbolLoader parentComp =
      ArcSymTabMill.componentSymbolLoaderBuilder().setName("A").build();
    ComponentSymbol childComp = ArcSymTabMill.componentSymbolBuilder().setName("B")
      .setSpannedScope(new ArcScope()).setParentComponentLoader(parentComp).build();
    Assertions.assertTrue(childComp.isPresentParentComponent());
  }

  @Test
  public void shouldNotHaveParent() {
    ComponentSymbol symbol = ArcSymTabMill.componentSymbolBuilder().setName("A")
      .setSpannedScope(new ArcScope()).build();
    Assertions.assertFalse(symbol.isPresentParentComponent());
  }

  @Test
  public void shouldHaveOuter() {
    ComponentSymbol outerComp = ArcSymTabMill.componentSymbolBuilder().setName("A")
      .setSpannedScope(new ArcScope()).build();
    ComponentSymbol innerComp = ArcSymTabMill.componentSymbolBuilder().setName("B")
      .setSpannedScope(new ArcScope()).setOuterComponent(outerComp).build();
    Assertions.assertTrue(innerComp.getOuterComponent().isPresent());
  }

  @Test
  public void shouldNotHaveOuter() {
    ComponentSymbol symbol = ArcSymTabMill.componentSymbolBuilder().setName("A")
      .setSpannedScope(new ArcScope()).build();
    Assertions.assertFalse(symbol.getOuterComponent().isPresent());
  }

  @ParameterizedTest
  @MethodSource("compNameAndParametersProvider")
  public void shouldBuildWithExpectedParameters(String name, List<FieldSymbol> parameters) {
    ComponentSymbol symbol = ArcSymTabMill.componentSymbolBuilder().setName(name)
      .setSpannedScope(new ArcScope()).setParameters(parameters).build();
    Assertions.assertEquals(symbol.getName(), name);
    Assertions.assertIterableEquals(symbol.getParameters(), parameters);
  }

  static Stream<Arguments> compNameAndParametersProvider() {
    return Stream.of(arguments("Comp1", Collections.emptyList()),
      arguments("Comp2", Arrays.asList(
        ArcSymTabMill.fieldSymbolBuilder().setName("a").build(),
        ArcSymTabMill.fieldSymbolBuilder().setName("b").build(),
        ArcSymTabMill.fieldSymbolBuilder().setName("c").build())),
      arguments("Comp3", Arrays.asList(
        ArcSymTabMill.fieldSymbolBuilder().setName("c").build(),
        ArcSymTabMill.fieldSymbolBuilder().setName("d").build())));
  }

  @ParameterizedTest
  @MethodSource("compNameAndTypeParametersProvider")
  public void shouldBuildWithExpectedTypeParameters(String name,
    List<TypeVarSymbol> typeParameters) {
    ComponentSymbol symbol = ArcSymTabMill.componentSymbolBuilder().setName(name)
      .setSpannedScope(new ArcScope()).setTypeParameters(typeParameters).build();
    Assertions.assertEquals(symbol.getName(), name);
    Assertions.assertIterableEquals(symbol.getTypeParameters(), typeParameters);
  }

  static Stream<Arguments> compNameAndTypeParametersProvider() {
    return Stream.of(
      arguments("Comp1", Collections.emptyList()),
      arguments("Comp2", Arrays.asList(
        ArcSymTabMill.typeVarSymbolBuilder().setName("A").build(),
        ArcSymTabMill.typeVarSymbolBuilder().setName("B").build(),
        ArcSymTabMill.typeVarSymbolBuilder().setName("C").build())),
      arguments("Comp3", Collections.singletonList(
        ArcSymTabMill.typeVarSymbolBuilder().setName("D").build())));
  }
}