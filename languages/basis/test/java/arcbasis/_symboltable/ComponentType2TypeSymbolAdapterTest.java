/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis.check.TypeExprOfComponent;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

/**
 * Holds tests for {@link ComponentType2TypeSymbolAdapter}.
 */
public class ComponentType2TypeSymbolAdapterTest {

  @BeforeAll
  static void setUp() {
    ArcBasisMill.globalScope().clear();
    ArcBasisMill.reset();
    ArcBasisMill.init();
    BasicSymbolsMill.initializePrimitives();
  }

  @ParameterizedTest
  @MethodSource("componentTypeSymbolProvider")
  void shouldAdaptFields(@NotNull ComponentTypeSymbol adaptee) {
    // Given
    ComponentType2TypeSymbolAdapter adapter = new ComponentType2TypeSymbolAdapter(adaptee);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals(adaptee.getName(), adapter.getName(),
        "The adapter's name should match the adaptee's name."),
      () -> Assertions.assertEquals(adaptee.getFullName(), adapter.getFullName(),
        "The adapter's full name should match the adaptee's full name."),
      () -> Assertions.assertEquals(adaptee.getSpannedScope(), adapter.getSpannedScope(),
        "The adapter's spanned scope should match the adaptee's enclosing scope."),
      () -> Assertions.assertEquals(adaptee.getEnclosingScope(), adapter.getEnclosingScope(),
        "The adapter's enclosing scope should match the adaptee's enclosing scope."),
      () -> Assertions.assertEquals(adaptee.getSourcePosition(), adapter.getSourcePosition(),
        "The adapter's source position should match the adaptee's source position."),
      () -> Assertions.assertEquals(BasicAccessModifier.PUBLIC, adapter.getAccessModifier(),
        "The adapter should have a public access modifier as ports are the public interface of a component.")
    );
  }

  protected static Stream<ComponentTypeSymbol> componentTypeSymbolProvider() {
    IArcBasisScope scope = ArcBasisMill.scope();

    // incoming port
    ComponentTypeSymbol comp1 = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("c1")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    SymbolService.link(scope, comp1);

    // outgoing port
    ComponentTypeSymbol comp2 = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("c2")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    SymbolService.link(scope, comp2);

    return Stream.of(comp1, comp2);
  }
}
