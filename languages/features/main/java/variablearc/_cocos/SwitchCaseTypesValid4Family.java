/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.statements.mccommonstatements._ast.ASTConstantExpressionSwitchLabel;
import de.monticore.statements.mccommonstatements._ast.ASTSwitchBlockStatementGroup;
import de.monticore.statements.mccommonstatements._ast.ASTSwitchStatement;
import de.monticore.statements.mccommonstatements.cocos.SwitchCaseTypesValid;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.check.TypeCheck4Family;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Checks that the case values of a switch statement are compatible with the
 * type of its expression for every combination of the symbols their names
 * may refer to in the configurations in which the statement is present.
 */
public class SwitchCaseTypesValid4Family extends SwitchCaseTypesValid {

  protected final TypeCheck4Family typeCheck;

  public SwitchCaseTypesValid4Family(@NotNull TypeCheck4Family typeCheck) {
    this.typeCheck = Preconditions.checkNotNull(typeCheck);
  }

  @Override
  public void check(@NotNull ASTSwitchStatement node) {
    Preconditions.checkNotNull(node);
    List<ASTNode> typed = new ArrayList<>();
    typed.add(node.getExpression());
    Stream.concat(
        node.getSwitchBlockStatementGroupList().stream().map(ASTSwitchBlockStatementGroup::getSwitchLabelList)
          .flatMap(List::stream),
        node.getSwitchLabelList().stream())
      .filter(ASTConstantExpressionSwitchLabel.class::isInstance)
      .map(label -> ((ASTConstantExpressionSwitchLabel) label).getConstant())
      .forEach(typed::add);
    this.typeCheck.check(node, node.getEnclosingScope(), typed, () -> super.check(node));
  }
}
