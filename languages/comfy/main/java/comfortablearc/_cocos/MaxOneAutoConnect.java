/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._cocos;

import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentBody;
import arcbasis._cocos.ArcBasisASTComponentBodyCoCo;
import com.google.common.base.Preconditions;
import comfortablearc._ast.ASTArcAutoConnect;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ComfortableArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks that there is no more than one {@link ASTArcAutoConnect} in each component body.
 */
public class MaxOneAutoConnect implements ArcBasisASTComponentBodyCoCo {

  @Override
  public void check(@NotNull ASTComponentBody node) {
    Preconditions.checkNotNull(node);

    List<ASTArcElement> acs = node.streamArcElements()
      .filter(ASTArcAutoConnect.class::isInstance)
      .collect(Collectors.toList());

    if(acs.size() > 1) {
      Log.error(
        ComfortableArcError.MULTIPLE_AUTOCONNECTS.format(acs.size()),
        acs.get(1).get_SourcePositionStart(),
        acs.get(1).get_SourcePositionEnd()
      );
    }
  }
}
