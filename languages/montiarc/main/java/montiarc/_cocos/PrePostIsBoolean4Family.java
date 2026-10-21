/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcprepost._ast.ASTArcPrePost;
import arcprepost._cocos.PrePostIsBoolean;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.check.TypeCheck4Family;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks that the pre and post conditions are boolean for every combination
 * of the symbols their names may refer to in the configurations in which they
 * are present.
 */
public class PrePostIsBoolean4Family extends PrePostIsBoolean {

  protected final TypeCheck4Family typeCheck;

  public PrePostIsBoolean4Family(@NotNull TypeCheck4Family typeCheck) {
    this.typeCheck = Preconditions.checkNotNull(typeCheck);
  }

  @Override
  public void check(@NotNull ASTArcPrePost node) {
    Preconditions.checkNotNull(node);
    List<ASTExpression> conditions = new ArrayList<>();
    if (node.isPresentPre()) {
      conditions.add(node.getPre());
    }
    conditions.add(node.getPost());
    this.typeCheck.check(node, node.getEnclosingScope(), conditions, () -> super.check(node));
  }
}
