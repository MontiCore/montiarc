/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class TypeExprOfComponentTest extends AbstractTest {

  /**
   * Method under test {@link TypeExprOfComponent#getParentTypeExpr()}
   */
  @Test
  public void getParentShouldReturnExpected() {
    // Given
    ComponentTypeSymbol parent = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Parent")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    TypeExprOfComponent parentTypeExpr = new TypeExprOfComponent(parent);

    ComponentTypeSymbol component = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Comp")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    component.setParent(parentTypeExpr);
    TypeExprOfComponent compTypeExpr = new TypeExprOfComponent(component);

    // When
    Optional<CompTypeExpression> parentOfTypeExpr = compTypeExpr.getParentTypeExpr();

    // Then
    Assertions.assertTrue(parentOfTypeExpr.isPresent());
    Assertions.assertEquals(parentTypeExpr, parentOfTypeExpr.get());
  }

  /**
   * Method under test {@link TypeExprOfComponent#getParentTypeExpr()}
   */
  @Test
  public void getParentShouldReturnOptionalEmpty() {
    // Given
    ComponentTypeSymbol component = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Comp")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    TypeExprOfComponent compTypeExpr = new TypeExprOfComponent(component);

    // When
    Optional<CompTypeExpression> parentOfTypeExpr = compTypeExpr.getParentTypeExpr();

    // Then
    Assertions.assertFalse(parentOfTypeExpr.isPresent());
  }

  @Test
  public void shouldGetTypeExprOfPort() {
    // Given
    ComponentTypeSymbol comp = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Comp")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    String portName = "port";
    PortSymbol port = ArcBasisMill.portSymbolBuilder()
      .setName(portName)
      .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT))
      .setIncoming(true)
      .build();
    comp.getSpannedScope().add(port);

    TypeExprOfComponent compTypeExpr = new TypeExprOfComponent(comp);

    // When
    Optional<SymTypeExpression> portsType = compTypeExpr.getTypeExprOfPort(portName);

    // Then
    Assertions.assertTrue(portsType.isPresent());
    Assertions.assertTrue(portsType.get() instanceof SymTypePrimitive);
    Assertions.assertEquals(BasicSymbolsMill.INT, portsType.get().print());
  }

  @Test
  public void shouldGetTypeExprOfInheritedPort() {
    // Given
    ComponentTypeSymbol parent = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Parent")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    String portName = "port";
    PortSymbol port = ArcBasisMill.portSymbolBuilder()
      .setName(portName)
      .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT))
      .setIncoming(true)
      .build();
    parent.getSpannedScope().add(port);

    ComponentTypeSymbol component = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Comp")
      .setParentComponent(new TypeExprOfComponent(parent))
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    TypeExprOfComponent compTypeExpr = new TypeExprOfComponent(component);

    // When
    Optional<SymTypeExpression> portsType = compTypeExpr.getTypeExprOfPort(portName);

    // Then
    Assertions.assertTrue(portsType.isPresent());
    Assertions.assertTrue(portsType.get() instanceof SymTypePrimitive);
    Assertions.assertEquals(BasicSymbolsMill.INT, portsType.get().print());
  }

  @Test
  public void shouldGetTypeExprOfParameter() {
    // Given
    ComponentTypeSymbol component = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Comp")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    String paramName = "para";
    VariableSymbol param = ArcBasisMill.variableSymbolBuilder()
      .setName(paramName)
      .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT))
      .build();
    component.getSpannedScope().add(param);
    component.addParameter(param);

    TypeExprOfComponent compTypeExpr = new TypeExprOfComponent(component);

    // When
    Optional<SymTypeExpression> paramType = compTypeExpr.getTypeExprOfParameter(paramName);

    // Then
    Assertions.assertTrue(paramType.isPresent());
    Assertions.assertTrue(paramType.get() instanceof SymTypePrimitive);
    Assertions.assertEquals(BasicSymbolsMill.INT, paramType.get().print());
  }
}
