/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._symboltable.ArcComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import arcbasis._symboltable.SymbolService;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypePrimitive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TypeExprOfComponentTest extends ArcBasisTestBase {

  /**
   * Method under test {@link TypeExprOfComponent#getSuperComponents()}
   */
  @Test
  public void getParentShouldReturnExpected() {
    // Given
    ArcComponentTypeSymbol symbolWithDefinitions = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName("Comp")
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ArcComponentTypeSymbol symbolVersionForTypeExpr = ArcBasisMill
      .arcComponentTypeSymbolSurrogateBuilder()
      .setName(symbolWithDefinitions.getFullName())
      .build();

    // Given
    SymbolService.link(ArcBasisMill.globalScope(), symbolWithDefinitions);
    symbolVersionForTypeExpr.setEnclosingScope(ArcBasisMill.globalScope());

    ArcComponentTypeSymbol parent = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName("Parent")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    TypeExprOfComponent parentTypeExpr = new TypeExprOfComponent(parent);

    symbolWithDefinitions.setSuperComponentsList(Collections.singletonList(parentTypeExpr));
    TypeExprOfComponent compTypeExpr = new TypeExprOfComponent(symbolVersionForTypeExpr);

    // When
    List<CompKindExpression> parentOfTypeExpr = compTypeExpr.getSuperComponents();

    // Then
    Assertions.assertFalse(parentOfTypeExpr.isEmpty(), "Parent not present.");
    Assertions.assertEquals(parentTypeExpr, parentOfTypeExpr.get(0));
  }

  /**
   * Method under test {@link TypeExprOfComponent#getSuperComponents()}
   */
  @Test
  public void getParentShouldReturnOptionalEmpty() {
    // Given
    ArcComponentTypeSymbol component = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName("Comp")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    TypeExprOfComponent compTypeExpr = new TypeExprOfComponent(component);

    // When
    List<CompKindExpression> parentOfTypeExpr = compTypeExpr.getSuperComponents();

    // Then
    Assertions.assertTrue(parentOfTypeExpr.isEmpty());
  }

  @Test
  public void shouldGetTypeExprOfPort() {
    // Given
    ArcComponentTypeSymbol symbolWithDefinitions = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName("Comp")
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ArcComponentTypeSymbol symbolVersionForTypeExpr = ArcBasisMill
      .arcComponentTypeSymbolSurrogateBuilder()
      .setName(symbolWithDefinitions.getFullName())
      .build();

    // Given
    SymbolService.link(ArcBasisMill.globalScope(), symbolWithDefinitions);
    symbolVersionForTypeExpr.setEnclosingScope(ArcBasisMill.globalScope());

    String portName = "port";
    PortSymbol port = ArcBasisMill.portSymbolBuilder()
      .setName(portName)
      .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT))
      .setIncoming(true)
      .build();
    symbolWithDefinitions.getSpannedScope().add(port);

    TypeExprOfComponent compTypeExpr = new TypeExprOfComponent(symbolVersionForTypeExpr);

    // When
    Optional<SymTypeExpression> portsType = compTypeExpr.getTypeOfPort(portName);

    // Then
    Assertions.assertTrue(portsType.isPresent(), "Port not present");
    Assertions.assertInstanceOf(SymTypePrimitive.class, portsType.get());
    Assertions.assertEquals(BasicSymbolsMill.INT, portsType.get().print());
  }

  @Test
  public void shouldGetTypeExprOfInheritedPort() {
    // Given
    ArcComponentTypeSymbol parent = ArcBasisMill.arcComponentTypeSymbolBuilder()
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

    ArcComponentTypeSymbol component = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName("Comp")
      .setSuperComponentsList(Collections.singletonList(new TypeExprOfComponent(parent)))
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    TypeExprOfComponent compTypeExpr = new TypeExprOfComponent(component);

    // When
    Optional<SymTypeExpression> portsType = compTypeExpr.getTypeOfPort(portName);

    // Then
    Assertions.assertTrue(portsType.isPresent());
    Assertions.assertTrue(portsType.get() instanceof SymTypePrimitive);
    Assertions.assertEquals(BasicSymbolsMill.INT, portsType.get().print());
  }

  @Test
  public void shouldGetTypeExprOfParameter() {
    // Given
    ArcComponentTypeSymbol symbolWithDefinitions = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName("Comp")
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ArcComponentTypeSymbol symbolVersionForTypeExpr = ArcBasisMill
      .arcComponentTypeSymbolSurrogateBuilder()
      .setName(symbolWithDefinitions.getFullName())
      .build();

    // Given
    SymbolService.link(ArcBasisMill.globalScope(), symbolWithDefinitions);
    symbolVersionForTypeExpr.setEnclosingScope(ArcBasisMill.globalScope());

    String paramName = "para";
    VariableSymbol param = ArcBasisMill.variableSymbolBuilder()
      .setName(paramName)
      .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT))
      .build();
    symbolWithDefinitions.getSpannedScope().add(param);
    symbolWithDefinitions.addParameter(param);

    TypeExprOfComponent compTypeExpr = new TypeExprOfComponent(symbolVersionForTypeExpr);

    // When
    Optional<SymTypeExpression> paramType = compTypeExpr.getTypeOfParameter(paramName);

    // Then
    Assertions.assertTrue(paramType.isPresent(), "Param not present");
    Assertions.assertInstanceOf(SymTypePrimitive.class, paramType.get());
    Assertions.assertEquals(BasicSymbolsMill.INT, paramType.get().print());
  }
}
