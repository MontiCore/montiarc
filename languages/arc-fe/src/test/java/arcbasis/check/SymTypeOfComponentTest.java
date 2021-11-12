/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeConstant;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class SymTypeOfComponentTest extends AbstractTest {

  /**
   * Method under test {@link SymTypeOfComponent#getParentTypeExpr()}
   */
  @Test
  public void getParentShouldReturnExpected() {
    // Given
    ComponentTypeSymbol parent = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Parent")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    SymTypeOfComponent parentTypeExpr = new SymTypeOfComponent(parent);

    ComponentTypeSymbol component = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Comp")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    component.setParent(parentTypeExpr);
    SymTypeOfComponent compTypeExpr = new SymTypeOfComponent(component);

    // When
    Optional<CompSymTypeExpression> parentOfTypeExpr = compTypeExpr.getParentTypeExpr();

    // Then
    Assertions.assertTrue(parentOfTypeExpr.isPresent());
    Assertions.assertEquals(parentTypeExpr, parentOfTypeExpr.get());
  }

  /**
   * Method under test {@link SymTypeOfComponent#getParentTypeExpr()}
   */
  @Test
  public void getParentShouldReturnOptionalEmpty() {
    // Given
    ComponentTypeSymbol component = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Comp")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    SymTypeOfComponent compTypeExpr = new SymTypeOfComponent(component);

    // When
    Optional<CompSymTypeExpression> parentOfTypeExpr = compTypeExpr.getParentTypeExpr();

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
      .setType(SymTypeExpressionFactory.createTypeConstant(BasicSymbolsMill.INT))
      .setIncoming(true)
      .build();
    comp.getSpannedScope().add(port);

    SymTypeOfComponent compTypeExpr = new SymTypeOfComponent(comp);

    // When
    Optional<SymTypeExpression> portsType = compTypeExpr.getTypeExprOfPort(portName);

    // Then
    Assertions.assertTrue(portsType.isPresent());
    Assertions.assertTrue(portsType.get() instanceof SymTypeConstant);
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
      .setType(SymTypeExpressionFactory.createTypeConstant(BasicSymbolsMill.INT))
      .setIncoming(true)
      .build();
    parent.getSpannedScope().add(port);

    ComponentTypeSymbol component = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Comp")
      .setParentComponent(new SymTypeOfComponent(parent))
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    SymTypeOfComponent compTypeExpr = new SymTypeOfComponent(component);

    // When
    Optional<SymTypeExpression> portsType = compTypeExpr.getTypeExprOfPort(portName);

    // Then
    Assertions.assertTrue(portsType.isPresent());
    Assertions.assertTrue(portsType.get() instanceof SymTypeConstant);
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
      .setType(SymTypeExpressionFactory.createTypeConstant(BasicSymbolsMill.INT))
      .build();
    component.getSpannedScope().add(param);
    component.addParameter(param);

    SymTypeOfComponent compTypeExpr = new SymTypeOfComponent(component);

    // When
    Optional<SymTypeExpression> paramType = compTypeExpr.getTypeExprOfParameter(paramName);

    // Then
    Assertions.assertTrue(paramType.isPresent());
    Assertions.assertTrue(paramType.get() instanceof SymTypeConstant);
    Assertions.assertEquals(BasicSymbolsMill.INT, paramType.get().print());
  }
}
