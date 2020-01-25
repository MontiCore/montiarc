/* (c) https://github.com/MontiCore/monticore */
package arc._cocos;

import arc._ast.ASTPort;
import arc._symboltable.PortSymbol;
import arc.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.NoSuchElementException;

/**
 * Checks for each port whether its type exists.
 */
public class PortTypeExists implements ArcASTPortCoCo {

  @Override
  public void check(@NotNull ASTPort node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTPort node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    PortSymbol symbol = node.getSymbol();
    try {
      symbol.getTypeInfo();
    }
    catch (NoSuchElementException e) {
      Log.error(String.format(ArcError.MISSING_TYPE_OF_PORT.toString(), symbol.getName()),
        node.get_SourcePositionStart());
    }
  }
}