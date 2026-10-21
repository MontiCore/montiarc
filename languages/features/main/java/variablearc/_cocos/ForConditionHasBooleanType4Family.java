/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mccommonstatements._ast.ASTCommonForControl;
import de.monticore.statements.mccommonstatements.cocos.ForConditionHasBooleanType;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.check.TypeCheck4Family;

import java.util.List;

/**
 * Checks that the condition of a for-loop is boolean for every combination of
 * the symbols its names may refer to in the configurations in which the loop
 * is present.
 */
public class ForConditionHasBooleanType4Family extends ForConditionHasBooleanType {

  protected final TypeCheck4Family typeCheck;

  public ForConditionHasBooleanType4Family(@NotNull TypeCheck4Family typeCheck) {
    this.typeCheck = Preconditions.checkNotNull(typeCheck);
  }

  @Override
  public void check(@NotNull ASTCommonForControl node) {
    Preconditions.checkNotNull(node);
    if (!node.isPresentCondition()) {
      super.check(node);
      return;
    }
    this.typeCheck.check(node, node.getEnclosingScope(), List.of(node.getCondition()), () -> super.check(node));
  }
}
