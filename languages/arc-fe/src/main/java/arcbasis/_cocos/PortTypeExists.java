/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTPort;
import arcbasis._symboltable.PortSymbol;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.NoSuchElementException;

/**
 * Checks for each port whether its type exists.
 */
public class PortTypeExists implements ArcBasisASTPortCoCo {

  @Override
  public void check(@NotNull ASTPort node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTPort node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    PortSymbol symbol = node.getSymbol();
    try {
      if (symbol.getType().getTypeInfo() instanceof TypeSymbolSurrogate &&
        ((TypeSymbolSurrogate) symbol.getType().getTypeInfo()).lazyLoadDelegate() instanceof TypeSymbolSurrogate) {
        Log.error(ArcError.MISSING_TYPE_OF_PORT.format(symbol.getName()),
          node.get_SourcePositionStart());
      }
    } catch (NoSuchElementException e) {
      Log.error(ArcError.MISSING_TYPE_OF_PORT.format(symbol.getName()),
        node.get_SourcePositionStart());
    }
  }
}