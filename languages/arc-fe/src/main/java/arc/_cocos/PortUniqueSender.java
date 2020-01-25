/* (c) https://github.com/MontiCore/monticore */
package arc._cocos;

import arc._ast.ASTComponent;
import arc._ast.ASTConnector;
import arc.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @implements [Hab16] R1: Each outgoing port of a component type definition is
 * used at most once as target of a connector. (p. 63, Lst. 3.36)
 * @implements [Hab16] R2: Each incoming port of a subcomponent is used at most
 * once as target of a connector. (p. 62, Lst. 3.37)
 */
public class PortUniqueSender implements ArcASTComponentCoCo {

  @Override
  public void check(@NotNull ASTComponent node) {
    Preconditions.checkArgument(node != null);
    List<String> targets = new ArrayList<>();
    for (ASTConnector connector : node.getConnectors()) {
      for (String target : connector.getTargetsNames()) {
        if (targets.contains(target)) {
          Log.error(String.format(ArcError.PORT_MUlTIPPLE_SENDER.toString(), target),
            connector.get_SourcePositionStart());
        } else {
          targets.add(target);
        }
      }
    }
  }
}