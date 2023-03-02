/* (c) https://github.com/MontiCore/monticore */
package arcbasis._visitor;

import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

public class ConfigurationArgumentAssignmentVisitor implements AssignmentExpressionsVisitor2 {

  /**
   * Counts the number of assignment expression found inside the expression
   */
  protected int assignmentCount = 0;

  /**
   * @return {@link #assignmentCount all variables} that were found.
   */
  public boolean hasAssignment() {
    return assignmentCount > 0;
  }

  /**
   * Resets the internal state, so this element behaves like new.
   */
  public void reset() {
    this.assignmentCount = 0;
  }

  @Override
  public void visit(@NotNull ASTAssignmentExpression node) {
    assignmentCount++;
  }

}
