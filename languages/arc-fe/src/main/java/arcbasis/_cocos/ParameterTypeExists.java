/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcParameter;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.NoSuchElementException;

/**
 * Checks for each configuration parameter whether its type exists.
 */
public class ParameterTypeExists implements ArcBasisASTArcParameterCoCo {

  @Override
  public void check(@NotNull ASTArcParameter node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTArcParameter node '%s' has no "
        + "symbol. Did you forget to run the SymbolTableCreator before checking cocos?",
      node.getName());
    VariableSymbol symbol = node.getSymbol();
    try {
      symbol.getType().getTypeInfo();
    }
    catch (NoSuchElementException e) {
      Log.error(String.format(ArcError.MISSING_TYPE_OF_PARAMETER.toString(), symbol.getName()),
        node.get_SourcePositionStart());
    }
  }
}