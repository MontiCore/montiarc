/* (c) https://github.com/MontiCore/monticore */
package modes._cocos;

import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import modes._ast.ASTArcMode;
import montiarc.util.ModesError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * This context-condition checks that modes do not define ports.
 */
public class ModeOmitPortDefinition implements ModesASTArcModeCoCo{

  @Override
  public void check(@NotNull ASTArcMode node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    for (PortSymbol port : node.getSpannedScope().getLocalPortSymbols()) {
      Log.error(ModesError.MODE_CONTAINS_PORT_DEFINITION.format(port.getName()), port.getSourcePosition());
    }
  }
}
