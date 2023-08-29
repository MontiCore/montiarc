/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis.check.TypeExprOfComponent;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

/**
 * Holds tests for {@link ComponentInstance2VariableAdapter}.
 */
public class ComponentInstance2VariableAdapterTest {

  @BeforeAll
  static void setUp() {
    ArcBasisMill.globalScope().clear();
    ArcBasisMill.reset();
    ArcBasisMill.init();
    BasicSymbolsMill.initializePrimitives();
  }

  @ParameterizedTest
  @MethodSource("componentInstanceSymbolProvider")
  void shouldAdaptFields(@NotNull ComponentInstanceSymbol adaptee) {
    // Given
    ComponentInstance2VariableAdapter adapter = new ComponentInstance2VariableAdapter(adaptee);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals(adaptee.getName(), adapter.getName(),
        "The adapter's name should match the adaptee's name."),
      () -> Assertions.assertEquals(adaptee.getFullName(), adapter.getFullName(),
        "The adapter's full name should match the adaptee's full name."),
      () -> Assertions.assertEquals(adaptee.getType().getTypeInfo(), ((ComponentType2TypeSymbolAdapter) adapter.getType().getTypeInfo()).getAdaptee(),
        "The adapter's type should match the adaptee's type."),
      () -> Assertions.assertEquals(adaptee.getEnclosingScope(), adapter.getEnclosingScope(),
        "The adapter's enclosing scope should match the adaptee's enclosing scope."),
      () -> Assertions.assertEquals(adaptee.getSourcePosition(), adapter.getSourcePosition(),
        "The adapter's source position should match the adaptee's source position."),
      () -> Assertions.assertEquals(BasicAccessModifier.PRIVATE, adapter.getAccessModifier(),
        "The adapter should have a public access modifier as ports are the public interface of a component.")
    );
  }

  @ParameterizedTest
  @MethodSource("componentInstanceSymbolProvider")
  void shouldDeepClone(@NotNull ComponentInstanceSymbol adaptee) {
    // Given
    ComponentInstance2VariableAdapter adapter = new ComponentInstance2VariableAdapter(adaptee);

    // When
    ComponentInstance2VariableAdapter clone = adapter.deepClone();

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals(adapter.getAdaptee(), clone.getAdaptee(),
        "The clone's adaptee should match the adapter's adaptee."),
      () -> Assertions.assertEquals(adapter.getName(), clone.getName(),
        "The clone's name should match the adapter's name."),
      () -> Assertions.assertEquals(adapter.getFullName(), clone.getFullName(),
        "The clone's full name should match the adapter's full name."),
      () -> Assertions.assertEquals(((ComponentType2TypeSymbolAdapter) adapter.getType().getTypeInfo()).getAdaptee(), ((ComponentType2TypeSymbolAdapter) clone.getType().getTypeInfo()).getAdaptee(),
        "The clone's type should match the adapter's type."),
      () -> Assertions.assertEquals(adapter.isIsReadOnly(), clone.isIsReadOnly(),
        "The clone should be read only if the adapter is read only."),
      () -> Assertions.assertEquals(adapter.getEnclosingScope(), clone.getEnclosingScope(),
        "The clone's enclosing scope should match the adapter's enclosing scope."),
      () -> Assertions.assertEquals(adapter.isPresentAstNode(), clone.isPresentAstNode(),
        "The clone should have an ast node if the adapter has an ast node."),
      () -> Assertions.assertEquals(adapter.getAccessModifier(), clone.getAccessModifier(),
        "The clone's access modifier should match the adapter's access modifier.")
    );
  }

  protected static Stream<ComponentInstanceSymbol> componentInstanceSymbolProvider() {
    IArcBasisScope scope = ArcBasisMill.scope();

    // incoming port
    ComponentInstanceSymbol instance1 = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("c1")
      .setType(new TypeExprOfComponent(Mockito.mock(ComponentTypeSymbol.class)))
      .build();
    SymbolService.link(scope, instance1);

    // outgoing port
    ComponentInstanceSymbol instance2 = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("c2")
      .setType(new TypeExprOfComponent(Mockito.mock(ComponentTypeSymbol.class)))
      .build();
    SymbolService.link(scope, instance2);

    return Stream.of(instance1, instance2);
  }
}
