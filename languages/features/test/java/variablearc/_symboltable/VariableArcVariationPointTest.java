/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symboltable.ISymbol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import variablearc.VariableArcAbstractTest;
import variablearc.evaluation.Expression;

public class VariableArcVariationPointTest extends VariableArcAbstractTest {

  protected VariableArcVariationPoint variationPoint;

  @BeforeEach
  public void SetUpVariationPoint() {
    this.variationPoint = new VariableArcVariationPoint(new Expression(Mockito.mock(ASTExpression.class)));
  }

  /**
   * @return the test subject
   */
  public VariableArcVariationPoint getVariationPoint() {
    return this.variationPoint;
  }

  @Test
  public void shouldAddChild() {
    VariableArcVariationPoint child = new VariableArcVariationPoint(new Expression(Mockito.mock(ASTExpression.class)), this.getVariationPoint());

    Assertions.assertEquals(child.getDependsOn().orElseThrow(), this.getVariationPoint());
    Assertions.assertEquals(1, this.getVariationPoint()
      .getChildVariationPoints().size());
    Assertions.assertTrue(this.getVariationPoint().getChildVariationPoints()
      .contains(child));
  }

  @Test
  public void shouldContainSymbol() {
    ISymbol symbol = Mockito.mock(ISymbol.class);
    this.getVariationPoint().add(symbol);

    Assertions.assertTrue(this.getVariationPoint().containsSymbol(symbol));
  }
}
