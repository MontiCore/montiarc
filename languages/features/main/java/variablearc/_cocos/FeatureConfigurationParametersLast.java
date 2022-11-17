/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTComponentInstance;
import arcbasis._cocos.ArcBasisASTComponentInstanceCoCo;
import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Asserts that the optional feature assignments of a component instance always
 * succeed ordered assignments.
 */
public class FeatureConfigurationParametersLast implements ArcBasisASTComponentInstanceCoCo {

  /**
   * @return Whether the parameter is ordered, i.e. not a named feature
   * assignment.
   */
  protected static boolean isOrdered(@NotNull ASTExpression arg) {
    Preconditions.checkNotNull(arg);
    return !(arg instanceof ASTAssignmentExpression);
  }

  @Override
  public void check(@NotNull ASTComponentInstance node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol(),
      "ASTComponentInstance node '%s' has no symbol. " +
        "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());

    if (!node.isPresentArguments())
      return; // ignore instances without arguments

    List<ASTExpression> arguments = node.getArguments().getExpressionList();

    boolean alreadySawNamedArgument = false;
    Optional<Integer> firstOptionalArgPosition = Optional.empty();  // measured in [0; length-1]

    for (int i = 0; i < arguments.size(); i++) {
      ASTExpression arg = arguments.get(i);
      if (isOrdered(arg) && alreadySawNamedArgument) {
        Log.error(VariableArcError.NAMED_ARGUMENTS_LAST.format(i + 1, 1 + firstOptionalArgPosition.get()),
          arg.get_SourcePositionStart(), arg.get_SourcePositionEnd());
      } else if (!isOrdered(arg)) {
        alreadySawNamedArgument = true;
        firstOptionalArgPosition = Optional.of(i);
      }
    }
  }
}
