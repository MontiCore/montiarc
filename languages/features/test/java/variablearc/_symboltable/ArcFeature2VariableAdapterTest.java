/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import variablearc.AbstractTest;
import variablearc.VariableArcMill;

import java.util.stream.Stream;

/**
 * Tests for {@link ArcFeature2VariableAdapter}
 */
public class ArcFeature2VariableAdapterTest extends AbstractTest {

  @ParameterizedTest
  @MethodSource("featureSymbolProvider")
  void shouldAdaptFields(@NotNull ArcFeatureSymbol adaptee) {
    // Given
    ArcFeature2VariableAdapter adapter = new ArcFeature2VariableAdapter(adaptee);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals(adaptee.getName(), adapter.getName(),
        "The adapter's name should match the adaptee's name."),
      () -> Assertions.assertEquals(adaptee.getFullName(), adapter.getFullName(),
        "The adapter's full name should match the adaptee's full name."),
      () -> Assertions.assertTrue(SymTypeExpressionFactory.createPrimitive("boolean")
          .deepEquals(adapter.getType()),
        "The adapter's type should be boolean."),
      () -> Assertions.assertTrue(adapter.isIsReadOnly(),
        "The adapter should be read only."),
      () -> Assertions.assertEquals(adaptee.getEnclosingScope(), adapter.getEnclosingScope(),
        "The adapter's enclosing scope should match the adaptee's enclosing scope."),
      () -> Assertions.assertEquals(adaptee.getSourcePosition(), adapter.getSourcePosition(),
        "The adapter's source position should match the adaptee's source position."),
      () -> Assertions.assertEquals(BasicAccessModifier.PUBLIC, adapter.getAccessModifier(),
        "The adapter should have a public access modifier as features are part of the public interface of a component.")
    );
  }

  protected static Stream<ArcFeatureSymbol> featureSymbolProvider() {
    IVariableArcScope scope = VariableArcMill.scope();

    ArcFeatureSymbol feature1 = VariableArcMill.arcFeatureSymbolBuilder()
      .setName("f1")
      .build();
    scope.add(feature1);

    return Stream.of(feature1);
  }

}
