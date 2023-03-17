/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class Port2VariableAdapterTest {

  @BeforeAll
  static void setUp() {
    ArcBasisMill.globalScope().clear();
    ArcBasisMill.reset();
    ArcBasisMill.init();
    BasicSymbolsMill.initializePrimitives();
  }

  @ParameterizedTest
  @MethodSource("portSymbolProvider")
  void shouldAdaptFields(@NotNull PortSymbol adaptee) {
    // Given
    Port2VariableAdapter adapter = new Port2VariableAdapter(adaptee);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals(adaptee.getName(), adapter.getName(),
        "The adapter's name should match the adaptee's name."),
      () -> Assertions.assertEquals(adaptee.getFullName(), adapter.getFullName(),
        "The adapter's full name should match the adaptee's full name."),
      () -> Assertions.assertEquals(adaptee.getType(), adapter.getType(),
        "The adapter's type should match the adaptee's type."),
      () -> Assertions.assertEquals(adaptee.isIncoming(), adapter.isIsReadOnly(),
        "The adapter should be read only if the adaptee is an incoming port."),
      () -> Assertions.assertEquals(adaptee.getEnclosingScope(), adapter.getEnclosingScope(),
        "The adapter's enclosing scope should match the adaptee's enclosing scope."),
      () -> Assertions.assertEquals(adaptee.getSourcePosition(), adapter.getSourcePosition(),
        "The adapter's source position should match the adaptee's source position."),
      () -> Assertions.assertEquals(BasicAccessModifier.PUBLIC, adapter.getAccessModifier(),
        "The adapter should have a public access modifier as ports are the public interface of a component.")
    );
  }

  @ParameterizedTest
  @MethodSource("portSymbolProvider")
  void shouldDeepClone(@NotNull PortSymbol adaptee) {
    // Given
    Port2VariableAdapter adapter = new Port2VariableAdapter(adaptee);

    // When
    Port2VariableAdapter clone = adapter.deepClone();

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals(adapter.getAdaptee(), clone.getAdaptee(),
        "The clone's adaptee should match the adapter's adaptee."),
      () -> Assertions.assertEquals(adapter.getName(), clone.getName(),
        "The clone's name should match the adapter's name."),
      () -> Assertions.assertEquals(adapter.getFullName(), clone.getFullName(),
        "The clone's full name should match the adapter's full name."),
      () -> Assertions.assertEquals(adapter.getType(), clone.getType(),
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

  protected static Stream<PortSymbol> portSymbolProvider() {
    IArcBasisScope scope = ArcBasisMill.scope();

    // incoming port
    PortSymbol port1 = ArcBasisMill.portSymbolBuilder()
      .setName("i")
      .setIncoming(true)
      .setType(SymTypeExpressionFactory.createPrimitive("int"))
      .build();
    SymbolService.link(scope, port1);

    // outgoing port
    PortSymbol port2 = ArcBasisMill.portSymbolBuilder()
      .setName("o")
      .setIncoming(false)
      .setType(SymTypeExpressionFactory.createPrimitive("int"))
      .build();
    SymbolService.link(scope, port2);

    return Stream.of(port1, port2);
  }
}
