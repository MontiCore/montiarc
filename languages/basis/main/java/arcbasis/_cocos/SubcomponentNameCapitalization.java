/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentInstance;
import com.google.common.base.Preconditions;
import montiarc.util.ArcError;
import montiarc.util.NameCapitalizationHelper;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * {@code implements} [Hab16] CV1: Instance names start with a lower-case letter.
 * (p. 71, Lst. 3.51)
 */
public class SubcomponentNameCapitalization implements ArcBasisASTComponentInstanceCoCo {

  @Override
  public void check(@NotNull ASTComponentInstance node) {
    Preconditions.checkNotNull(node);

    if (NameCapitalizationHelper.isNotLowerCase(node.getName())) {
      NameCapitalizationHelper.warning(ArcError.SUBCOMPONENT_UPPER_CASE, node, node.getName());
    }
  }
}
