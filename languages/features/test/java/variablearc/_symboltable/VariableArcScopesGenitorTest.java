/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import variablearc.VariableArcAbstractTest;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcVarIf;

/**
 * Holds tests for the handwritten methods of {@link VariableArcScopesGenitor}.
 */
public class VariableArcScopesGenitorTest extends VariableArcAbstractTest {

  protected VariableArcScopesGenitorTestDelegator symTab;

  @BeforeEach
  public void SetUpSymTab() {
    this.symTab = new VariableArcScopesGenitorTestDelegator();
  }

  /**
   * @return the test subject
   */
  public VariableArcScopesGenitor getSymTab() {
    return this.symTab.getGenitor();
  }

  @Test
  public void shouldVisitVarIf() {
    // Given
    ASTArcVarIf ast = VariableArcMill.arcVarIfBuilder()
      .setCondition(Mockito.mock(ASTExpression.class))
      .setThen(VariableArcMill.componentBodyBuilder().build())
      .setOtherwise(VariableArcMill.componentBodyBuilder().build())
      .build();
    IVariableArcScope scope = VariableArcMill.scope();
    this.getSymTab().putOnStack(scope);
    this.getSymTab().putOnStack(
      (VariableComponentTypeSymbol) VariableArcMill.componentTypeSymbolBuilder()
        .setName("A")
        .setSpannedScope(scope)
        .build());

    // When
    this.getSymTab().visit(ast);

    // Then
    Assertions.assertEquals(scope, ast.getEnclosingScope());
  }

  @Test
  public void shouldTraverseVarIf() {
    // Given
    VariableComponentTypeSymbol typeSymbol = (VariableComponentTypeSymbol) VariableArcMill.componentTypeSymbolBuilder()
      .setName("A")
      .setSpannedScope(this.getSymTab().createScope(false))
      .build();
    this.getSymTab().putOnStack(typeSymbol);
    int size = this.getSymTab().getVariationPointStack().size();
    int variationPointSize = typeSymbol.getAllVariationPoints().size();
    ASTArcVarIf ast = VariableArcMill.arcVarIfBuilder()
      .setCondition(Mockito.mock(ASTExpression.class))
      .setThen(VariableArcMill.componentBodyBuilder().build())
      .setOtherwise(VariableArcMill.componentBodyBuilder().build())
      .build();

    // When
    this.getSymTab().traverse(ast);

    // Then
    Assertions.assertEquals(typeSymbol, this.getSymTab().getCurrentComponent()
      .orElse(null));
    Assertions.assertEquals(size, this.getSymTab().getVariationPointStack()
      .size());
    Assertions.assertEquals(variationPointSize + 2, typeSymbol.getAllVariationPoints()
      .size());
  }

  /**
   * grants access to the usually encapsulated genitor
   */
  protected static class VariableArcScopesGenitorTestDelegator extends VariableArcScopesGenitorDelegator {

    public VariableArcScopesGenitor getGenitor() {
      return symbolTable;
    }
  }
}
