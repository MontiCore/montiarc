/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import variablearc.AbstractTest;
import variablearc.VariableArcMill;
import variablearc._symboltable.IVariableArcScope;
import variablearc._symboltable.VariableArcVariationPoint;

import java.util.List;

public class CompTypeExpressionTypeInfoBindingTest extends AbstractTest {

  protected static ComponentTypeSymbol createComponentWithVariationPoints(@NotNull String compName,
                                                                          @NotNull VariableArcVariationPoint... variationPoints) {
    Preconditions.checkArgument(compName != null);
    Preconditions.checkArgument(variationPoints != null);

    IVariableArcScope scope = VariableArcMill.scope();

    for (VariableArcVariationPoint variationPoint : variationPoints) {
      scope.add(variationPoint);
    }

    ASTComponentType astComponentType = Mockito.mock(ASTComponentType.class);
    Mockito.when(astComponentType.getBody()).thenReturn(Mockito.mock(ASTComponentBody.class));

    return VariableArcMill.componentTypeSymbolBuilder().setName(compName).setAstNode(astComponentType)
      .setSpannedScope(scope).build();
  }

  @Test
  public void shouldGetMultiplePortInVariations() {
    // Given ports with equal names in different VariationPoints
    SymTypeExpression portType = Mockito.mock(SymTypeExpression.class);
    PortSymbol port = VariableArcMill.portSymbolBuilder().setName("a")
      .setType(portType).setIncoming(false).build();
    SymTypeExpression portType2 = Mockito.mock(SymTypeExpression.class);
    PortSymbol port2 = VariableArcMill.portSymbolBuilder().setName("a")
      .setType(portType2).setIncoming(false).build();

    VariableArcVariationPoint variationPoint = new VariableArcVariationPoint(Mockito.mock(ASTExpression.class));
    variationPoint.add(port);
    VariableArcVariationPoint variationPoint2 = new VariableArcVariationPoint(Mockito.mock(ASTExpression.class));
    variationPoint.add(port2);

    ComponentTypeSymbol comp = createComponentWithVariationPoints("Comp", variationPoint, variationPoint2);
    comp.getSpannedScope().add(port);
    comp.getSpannedScope().add(port2);

    TypeExprOfVariableComponent typeExpression = new TypeExprOfVariableComponent(comp);

    // When
    List<PortSymbol> returnedPorts = CompTypeExpressionTypeInfoBinding.getPorts(typeExpression, "a", false);

    // Then
    Assertions.assertNotNull(returnedPorts);
    Assertions.assertEquals(2, returnedPorts.size());
    Assertions.assertTrue(returnedPorts.contains(port));
    Assertions.assertTrue(returnedPorts.contains(port2));
  }

  @Test
  public void shouldGetPorts() {
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
    List<PortSymbol> ports = CompTypeExpressionTypeInfoBinding.getPorts(compTypeExpr);

    // Then
    Assertions.assertEquals(comp.getPorts().size(), ports.size());
    Assertions.assertIterableEquals(comp.getPorts(), ports);
  }

  @Test
  public void shouldGetAllPorts() {
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
    List<PortSymbol> ports = CompTypeExpressionTypeInfoBinding.getAllPorts(compTypeExpr);

    // Then
    Assertions.assertEquals(component.getAllPorts().size(), ports.size());
    Assertions.assertIterableEquals(component.getAllPorts(), ports);
  }
}
