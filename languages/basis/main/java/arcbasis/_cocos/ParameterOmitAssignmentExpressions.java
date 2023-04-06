/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._visitor.ConfigurationArgumentAssignmentVisitor;
import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions.AssignmentExpressionsMill;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsTraverser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A parameter assignment should not allow assignment expressions apart from keyword based assignments.
 * This CoCo checks that assignment expressions are only used for assigning a parameter per keyword.
 */
public class ParameterOmitAssignmentExpressions implements ArcBasisASTComponentInstanceCoCo {

  /**
   * Checks that keyword instantiations do not use multiple nested assignment expressions.
   * Also checks that only assignment expressions using '=' are used.
   *
   * @param instance The AST node of the component instance whose instantiation arguments should be checked.
   */
  @Override
  public void check(@NotNull ASTComponentInstance instance) {
    Preconditions.checkNotNull(instance);

    if (!instance.isPresentArcArguments()) {
      return;
    }

    List<ASTExpression> args = instance
      .getArcArguments().getArcArgumentList().stream()
      .map(ASTArcArgument::getExpression)
      .collect(Collectors.toList());

    ConfigurationArgumentAssignmentVisitor visitor = new ConfigurationArgumentAssignmentVisitor();
    AssignmentExpressionsTraverser traverser = AssignmentExpressionsMill.traverser();
    traverser.add4AssignmentExpressions(visitor);

    for (ASTExpression arg : args) {
      arg.accept(traverser);
      if (visitor.hasAssignment()) {
        Log.error(ArcError.COMP_ARG_MULTI_ASSIGNMENT.toString(),
          arg.get_SourcePositionStart(), arg.get_SourcePositionEnd()
        );
      }
      visitor.reset();
    }
  }
}