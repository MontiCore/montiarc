/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.SymTypeExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

/**
 * Holds tests for the handwritten methods of {@link InstanceVisitor}.
 */
public class InstanceVisitorTest extends ArcBasisAbstractTest {

  @Test
  public void shouldGetComponentType() {
    // Given
    ISymbol symbol =
      ArcBasisMill.componentTypeSymbolBuilder().setName("C").setSpannedScope(ArcBasisMill.scope()).build();

    // When
    Optional<PortSymbol> port = new InstanceVisitor().asPort(symbol);
    Optional<ComponentInstanceSymbol> instance = new InstanceVisitor().asSubcomponent(symbol);
    Optional<ComponentTypeSymbol> component = new InstanceVisitor().asComponent(symbol);

    // Then
    Assertions.assertFalse(port.isPresent());
    Assertions.assertFalse(instance.isPresent());
    Assertions.assertTrue(component.isPresent());
    Assertions.assertEquals(symbol, component.get());
  }

  @Test
  public void shouldGetPort() {
    // Given
    ISymbol symbol = ArcBasisMill.portSymbolBuilder().setName("P")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setIncoming(true).build();

    // When
    Optional<ComponentInstanceSymbol> instance = new InstanceVisitor().asSubcomponent(symbol);
    Optional<ComponentTypeSymbol> component = new InstanceVisitor().asComponent(symbol);
    Optional<PortSymbol> port = new InstanceVisitor().asPort(symbol);

    // Then
    Assertions.assertFalse(component.isPresent());
    Assertions.assertFalse(instance.isPresent());
    Assertions.assertTrue(port.isPresent());
    Assertions.assertEquals(symbol, port.get());
  }

  @Test
  public void shouldGetComponentInstance() {
    // Given
    ISymbol symbol = ArcBasisMill.componentInstanceSymbolBuilder().setName("P").build();

    // When
    Optional<ComponentTypeSymbol> component = new InstanceVisitor().asComponent(symbol);
    Optional<PortSymbol> port = new InstanceVisitor().asPort(symbol);
    Optional<ComponentInstanceSymbol> instance = new InstanceVisitor().asSubcomponent(symbol);

    // Then
    Assertions.assertFalse(component.isPresent());
    Assertions.assertFalse(port.isPresent());
    Assertions.assertTrue(instance.isPresent());
    Assertions.assertEquals(symbol, instance.get());
  }

  @Test
  public void shouldNotGetAnything() {
    // Given
    ISymbol symbol = ArcBasisMill.functionSymbolBuilder().setName("P").build();

    // When
    Optional<ComponentTypeSymbol> component = new InstanceVisitor().asComponent(symbol);
    Optional<PortSymbol> port = new InstanceVisitor().asPort(symbol);
    Optional<ComponentInstanceSymbol> instance = new InstanceVisitor().asSubcomponent(symbol);

    // Then
    Assertions.assertFalse(component.isPresent());
    Assertions.assertFalse(port.isPresent());
    Assertions.assertFalse(instance.isPresent());
  }
}
