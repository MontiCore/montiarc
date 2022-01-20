/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTPortDirection;
import arcbasis._symboltable.PortSymbol;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.SymTypeExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import variablearc.AbstractTest;
import variablearc.VariableArcMill;

import java.util.Collections;
import java.util.Optional;

public class IVariableScopeTest extends AbstractTest {

  protected IVariableArcScope scope;

  @BeforeEach
  public void SetUpScope() {
    this.scope = VariableArcMill.scope();
  }

  /**
   * @return the test subject
   */
  public IVariableArcScope getScope() {
    return this.scope;
  }

  @Test
  public void shouldAddVariationPoint() {
    int variationPointSize = this.getScope().getRootVariationPoints().size();

    VariableArcVariationPoint variationPoint = new VariableArcVariationPoint(Mockito.mock(ASTExpression.class));
    this.getScope().add(variationPoint);

    Assertions.assertEquals(variationPointSize + 1, this.getScope().getRootVariationPoints().size());
    Assertions.assertTrue(this.getScope().getRootVariationPoints().contains(variationPoint));
  }

  @Test
  public void shouldContainSymbol() {
    VariableArcVariationPoint parent = new VariableArcVariationPoint(Mockito.mock(ASTExpression.class));
    VariableArcVariationPoint child = new VariableArcVariationPoint(Mockito.mock(ASTExpression.class),
      Optional.of(parent));
    this.getScope().add(parent);

    ISymbol symbol = Mockito.mock(ISymbol.class);
    parent.add(symbol);

    Assertions.assertTrue(this.getScope().getRootVariationPoints().contains(parent));
    Assertions.assertFalse(this.getScope().getRootVariationPoints().contains(child));
    Assertions.assertTrue(this.getScope().variationPointsContainsSymbol(Collections.singletonList(parent), symbol));
    Assertions.assertTrue(this.getScope().variationPointsContainsSymbol(Collections.singletonList(child), symbol));
  }

  @Test
  public void shouldResolvePortInConfiguration() {
    VariableArcVariationPoint variationPoint = new VariableArcVariationPoint(Mockito.mock(ASTExpression.class));
    this.getScope().add(variationPoint);

    PortSymbol symbol =
      ArcBasisMill.portSymbolBuilder().setName("Test")
        .setDirection(Mockito.mock(ASTPortDirection.class))
        .setType(Mockito.mock(SymTypeExpression.class)).build();
    this.getScope().add(symbol);
    variationPoint.add(symbol);

    Assertions.assertEquals(symbol,
      this.getScope().resolvePort("Test", Collections.singletonList(variationPoint)).get());
  }

  @Test
  public void shouldResolvePortInParentConfiguration() {
    VariableArcVariationPoint parent = new VariableArcVariationPoint(Mockito.mock(ASTExpression.class));
    VariableArcVariationPoint child = new VariableArcVariationPoint(Mockito.mock(ASTExpression.class),
      Optional.of(parent));
    this.getScope().add(parent);

    PortSymbol symbol = ArcBasisMill.portSymbolBuilder().setName("Test")
      .setDirection(Mockito.mock(ASTPortDirection.class))
      .setType(Mockito.mock(SymTypeExpression.class)).build();
    this.getScope().add(symbol);
    parent.add(symbol);

    Assertions.assertEquals(symbol, this.getScope().resolvePort("Test", Collections.singletonList(child)).get());
  }

  @Test
  public void shouldResolvePortInNoConfiguration() {
    VariableArcVariationPoint parent = new VariableArcVariationPoint(Mockito.mock(ASTExpression.class));
    VariableArcVariationPoint child = new VariableArcVariationPoint(Mockito.mock(ASTExpression.class),
      Optional.of(parent));
    this.getScope().add(parent);

    PortSymbol symbol =
      ArcBasisMill.portSymbolBuilder().setName("Test")
        .setDirection(Mockito.mock(ASTPortDirection.class))
        .setType(Mockito.mock(SymTypeExpression.class))
        .build();
    this.getScope().add(symbol); // Add Symbol only to scope

    Assertions.assertEquals(symbol, this.getScope().resolvePort("Test", Collections.singletonList(child)).get());
  }
}
