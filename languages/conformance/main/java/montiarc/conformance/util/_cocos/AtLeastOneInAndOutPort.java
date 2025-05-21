/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.util._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcPort;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import de.se_rwth.commons.logging.Log;

public class AtLeastOneInAndOutPort implements ArcBasisASTArcComponentTypeCoCo {
  private final String errorMessage =
      "SCC002 The MontiArc component must have at least one incoming and one outgoing port";

  @Override
  public void check(ASTArcComponentType node) {
    boolean hasInPort = false;
    boolean hasOutPort = false;
    for (ASTArcPort p : node.getPorts()) {
      hasInPort = hasInPort || p.getSymbol().isIncoming();
      hasOutPort = hasOutPort || p.getSymbol().isOutgoing();
    }

    if (!hasOutPort || !hasInPort) {
      Log.error(errorMessage);
    }
  }
}
