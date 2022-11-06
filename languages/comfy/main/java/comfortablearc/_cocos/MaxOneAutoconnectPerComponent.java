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
 * Checks that no there is one or none {@link ASTArcAutoConnect} in each component body.
 */
public class MaxOneAutoconnectPerComponent implements ArcBasisASTComponentBodyCoCo {

  @Override
  public void check(@NotNull ASTComponentBody node) {
    Preconditions.checkNotNull(node);

    List<ASTArcElement> autoConnectDeclarations = node.streamArcElements()
      .filter(ASTArcAutoConnect.class::isInstance)
      .collect(Collectors.toList());

    int numOfDeclarations = autoConnectDeclarations.size();
    if(numOfDeclarations > 1) {
      ASTArcElement secondDecl = autoConnectDeclarations.get(1);
      Log.error(
        ComfortableArcError.MULTIPLE_AUTOCONNECTS.format(numOfDeclarations),
        secondDecl.get_SourcePositionStart(),
        secondDecl.get_SourcePositionEnd()
      );
    }
  }
}
