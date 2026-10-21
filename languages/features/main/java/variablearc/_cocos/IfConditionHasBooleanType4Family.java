/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mccommonstatements._ast.ASTIfStatement;
import de.monticore.statements.mccommonstatements.cocos.IfConditionHasBooleanType;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.check.TypeCheck4Family;

import java.util.List;

/**
 * Checks that the condition of an if-statement is boolean for every combination
 * of the symbols its names may refer to in the configurations in which the
 * statement is present.
 */
public class IfConditionHasBooleanType4Family extends IfConditionHasBooleanType {

  protected final TypeCheck4Family typeCheck;

  public IfConditionHasBooleanType4Family(@NotNull TypeCheck4Family typeCheck) {
    this.typeCheck = Preconditions.checkNotNull(typeCheck);
  }

  @Override
  public void check(@NotNull ASTIfStatement node) {
    Preconditions.checkNotNull(node);
    this.typeCheck.check(node, node.getEnclosingScope(), List.of(node.getCondition()), () -> super.check(node));
  }
}
