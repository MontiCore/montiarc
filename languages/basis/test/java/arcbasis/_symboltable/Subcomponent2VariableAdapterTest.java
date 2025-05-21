/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis.check.TypeExprOfComponent;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Holds tests for {@link Subcomponent2VariableAdapter}.
 */
public class Subcomponent2VariableAdapterTest extends ArcBasisTestBase {

  @Test
  void shouldAdaptFields() {
    // Given
    SubcomponentSymbol adaptee = ArcBasisMill.subcomponentSymbolBuilder()
      .setName("sub")
      .setType(new TypeExprOfComponent(Mockito.mock(ArcComponentTypeSymbol.class)))
      .build();
    SymbolService.link(ArcBasisMill.scope(), adaptee);

    Subcomponent2VariableAdapter adapter = new Subcomponent2VariableAdapter(adaptee);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals(adaptee.getName(), adapter.getName(),
        "The adapter's name should match the adaptee's name."),
      () -> Assertions.assertEquals(adaptee.getFullName(), adapter.getFullName(),
        "The adapter's full name should match the adaptee's full name."),
      () -> Assertions.assertEquals(adaptee.getType().getTypeInfo(), ((Component2TypeSymbolAdapter) adapter.getType().getTypeInfo()).getAdaptee(),
        "The adapter's type should match the adaptee's type."),
      () -> Assertions.assertEquals(adaptee.getEnclosingScope(), adapter.getEnclosingScope(),
        "The adapter's enclosing scope should match the adaptee's enclosing scope."),
      () -> Assertions.assertEquals(adaptee.getSourcePosition(), adapter.getSourcePosition(),
        "The adapter's source position should match the adaptee's source position."),
      () -> Assertions.assertEquals(BasicAccessModifier.PRIVATE, adapter.getAccessModifier(),
        "The adapter should have a public access modifier as ports are the public interface of a component.")
    );
  }

  @Test
  void shouldDeepClone() {
    // Given
    SubcomponentSymbol adaptee = ArcBasisMill.subcomponentSymbolBuilder()
      .setName("sub")
      .setType(new TypeExprOfComponent(Mockito.mock(ArcComponentTypeSymbol.class)))
      .build();
    SymbolService.link(ArcBasisMill.scope(), adaptee);

    Subcomponent2VariableAdapter adapter = new Subcomponent2VariableAdapter(adaptee);

    // When
    Subcomponent2VariableAdapter clone = adapter.deepClone();

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals(adapter.getAdaptee(), clone.getAdaptee(),
        "The clone's adaptee should match the adapter's adaptee."),
      () -> Assertions.assertEquals(adapter.getName(), clone.getName(),
        "The clone's name should match the adapter's name."),
      () -> Assertions.assertEquals(adapter.getFullName(), clone.getFullName(),
        "The clone's full name should match the adapter's full name."),
      () -> Assertions.assertEquals(((Component2TypeSymbolAdapter) adapter.getType().getTypeInfo()).getAdaptee(), ((Component2TypeSymbolAdapter) clone.getType().getTypeInfo()).getAdaptee(),
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

  @Test
  void shouldNotThrowErrorIfTypeIsMissing() {
    // Given
    SubcomponentSymbol adaptee = ArcBasisMill.subcomponentSymbolBuilder()
      .setName("sub")
      .build();
    SymbolService.link(ArcBasisMill.scope(), adaptee);

    // When
    Subcomponent2VariableAdapter adapter = new Subcomponent2VariableAdapter(adaptee);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals(adaptee.getName(), adapter.getName(),
        "The adapter's name should match the adaptee's name."),
      () -> Assertions.assertEquals(adaptee.getFullName(), adapter.getFullName(),
        "The adapter's full name should match the adaptee's full name."),
      () -> Assertions.assertTrue(adapter.getType().isObscureType(),
        "The adapter's type should be obscure."),
      () -> Assertions.assertEquals(adaptee.getEnclosingScope(), adapter.getEnclosingScope(),
        "The adapter's enclosing scope should match the adaptee's enclosing scope."),
      () -> Assertions.assertEquals(adaptee.getSourcePosition(), adapter.getSourcePosition(),
        "The adapter's source position should match the adaptee's source position."),
      () -> Assertions.assertEquals(BasicAccessModifier.PRIVATE, adapter.getAccessModifier(),
        "The adapter should have a public access modifier as ports are the public interface of a component.")
    );
  }
}
