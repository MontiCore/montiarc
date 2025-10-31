/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import arcautomaton._cocos.PortReadWriteInTransition;
import arcbasis._cocos.PortReadWriteHandler4AssignmentExpressions;
import arcbasis._cocos.PortReadWriteHandler4CommonExpressions;
import arcbasis._cocos.PortReadWriteHandler4MCCommonStatements;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsHandler;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.Map;

import static arcbasis._cocos.PortReadWriteHandler4ExpressionsBasis.ContextState;

public class PortReadWriteInTransition4FamilyCollector extends PortReadWriteInTransition {

  protected ExpressionsBasisHandler expressionBasis;

  protected void setExpressionBasisHandler(ExpressionsBasisHandler expressionBasis) {
    this.expressionBasis = expressionBasis;
  }

  protected ExpressionsBasisHandler getExpressionBasisHandler(){
    return this.expressionBasis;
  }

  public PortReadWriteInTransition4FamilyCollector() {
    this(MontiArcMill.traverser(), new ContextState());
  }

  /**
   * @param t  The traverser to traverse the AST
   * @param c The context object to share state between handlers
   */
  protected PortReadWriteInTransition4FamilyCollector(@NotNull MontiArcTraverser t,
                                                    @NotNull ContextState c) {
    this(t,
      new PortReadWriteHandler4ExpressionBasis4Family(c),
      new PortReadWriteHandler4AssignmentExpressions(c),
      new PortReadWriteHandler4CommonExpressions(c),
      new PortReadWriteHandler4MCCommonStatements(c)
    );
  }

  /**
   * @param t  The traverser to traverse the AST
   * @param be The expression basis handler to report read and
   *           write violations for expression basis.
   * @param ae The assignment expression handler to report
   *           read and write violations for assignment expressions.
   * @param ce The common expression handler to report
   *           read and write violations for common expressions.
   * @param cs The common statement handler to report read and
   *           write violations for common statements.
   */
  protected PortReadWriteInTransition4FamilyCollector(@NotNull MontiArcTraverser t,
                                                    @NotNull ExpressionsBasisHandler be,
                                                    @NotNull AssignmentExpressionsHandler ae,
                                                    @NotNull CommonExpressionsHandler ce,
                                                    @NotNull MCCommonStatementsHandler cs) {
    super(t, be);
    t.setAssignmentExpressionsHandler(ae);
    t.setCommonExpressionsHandler(ce);
    t.setMCCommonStatementsHandler(cs);
    setExpressionBasisHandler(be);
  }

  public List<PortReadWriteHandler4ExpressionBasis4Family.PortWithState> getPortWithContext(){
    var exprBasisHandler = getExpressionBasisHandler();
    if(exprBasisHandler instanceof PortReadWriteHandler4ExpressionBasis4Family)
      return ((PortReadWriteHandler4ExpressionBasis4Family) exprBasisHandler).getPortWithContext();

    return null;
  }

}
