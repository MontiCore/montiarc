/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import variablearc.AbstractTest;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcIfStatement;

/**
 * Holds tests for the handwritten methods of {@link VariableArcScopesGenitor}.
 */
public class VariableArcScopesGenitorTest extends AbstractTest {

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
  public void shouldVisitIfStatement() {
    ASTArcIfStatement ast = VariableArcMill.arcIfStatementBuilder()
      .setCondition(Mockito.mock(ASTExpression.class))
      .setThenStatement(VariableArcMill.arcBlockBuilder().build())
      .setElseStatement(VariableArcMill.arcBlockBuilder().build())
      .build();
    IVariableArcScope scope = VariableArcMill.scope();
    this.getSymTab().putOnStack(scope);
    this.getSymTab().visit(ast);
    Assertions.assertEquals(scope, ast.getEnclosingScope());
  }

  @Test
  public void shouldTraverseIfStatement() {
    Preconditions.checkArgument(this.getSymTab().getCurrentScope().isPresent());
    IVariableArcScope scope = this.getSymTab().getCurrentScope().get();
    int size = this.getSymTab().getVariationPointStack().size();
    int variationPointSize = scope.getRootVariationPoints().size();
    ASTArcIfStatement ast = VariableArcMill.arcIfStatementBuilder()
      .setCondition(Mockito.mock(ASTExpression.class))
      .setThenStatement(VariableArcMill.arcBlockBuilder().build())
      .setElseStatement(VariableArcMill.arcBlockBuilder().build())
      .build();
    this.getSymTab().traverse(ast);
    Assertions.assertEquals(scope, this.getSymTab().getCurrentScope()
      .orElse(null));
    Assertions.assertEquals(size, this.getSymTab().getVariationPointStack()
      .size());
    Assertions.assertEquals(variationPointSize + 2, scope.getRootVariationPoints()
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
