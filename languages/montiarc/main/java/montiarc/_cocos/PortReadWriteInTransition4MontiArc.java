/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._cocos.PortReadWriteInTransition;
import arcbasis._cocos.PortReadWriteHandler4AssignmentExpressions;
import arcbasis._cocos.PortReadWriteHandler4CommonExpressions;
import arcbasis._cocos.PortReadWriteHandler4ExpressionsBasis;
import arcbasis._cocos.PortReadWriteHandler4MCCommonStatements;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsHandler;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import static arcbasis._cocos.PortReadWriteHandler4ExpressionsBasis.ContextState;

public class PortReadWriteInTransition4MontiArc extends PortReadWriteInTransition {

  public PortReadWriteInTransition4MontiArc() {
    this(MontiArcMill.traverser(), new ContextState());
  }

  /**
   * @param t  The traverser to traverse the AST
   * @param c The context object to share state between handlers
   */
  protected PortReadWriteInTransition4MontiArc(@NotNull MontiArcTraverser t,
                                               @NotNull ContextState c) {
    this(t,
      new PortReadWriteHandler4ExpressionsBasis(c),
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
  protected PortReadWriteInTransition4MontiArc(@NotNull MontiArcTraverser t,
                                               @NotNull ExpressionsBasisHandler be,
                                               @NotNull AssignmentExpressionsHandler ae,
                                               @NotNull CommonExpressionsHandler ce,
                                               @NotNull MCCommonStatementsHandler cs) {
    super(t, be);
    t.setAssignmentExpressionsHandler(ae);
    t.setCommonExpressionsHandler(ce);
    t.setMCCommonStatementsHandler(cs);
  }
}
