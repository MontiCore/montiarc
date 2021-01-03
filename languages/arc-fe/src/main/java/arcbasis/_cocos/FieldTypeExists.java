/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcField;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
<<<<<<< HEAD
=======
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
>>>>>>> bb276d4fcc3784a5352ae1a8711ede81331f4772
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.NoSuchElementException;

/**
 * Checks for each variable whether it type exists.
 */
public class FieldTypeExists implements ArcBasisASTArcFieldCoCo {

  @Override
  public void check(@NotNull ASTArcField node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTArcField node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    VariableSymbol symbol = node.getSymbol();
<<<<<<< HEAD
=======
    symbol.getType().getTypeInfo();
>>>>>>> bb276d4fcc3784a5352ae1a8711ede81331f4772
    try {
      if (symbol.getType().getTypeInfo() instanceof TypeSymbolSurrogate &&
        ((TypeSymbolSurrogate) symbol.getType().getTypeInfo()).lazyLoadDelegate() instanceof TypeSymbolSurrogate) {
        Log.error(String.format(ArcError.MISSING_TYPE_OF_FIELD.toString(), symbol.getName()),
          node.get_SourcePositionStart());
      }
    } catch (NoSuchElementException e) {
      Log.error(String.format(ArcError.MISSING_TYPE_OF_FIELD.toString(), symbol.getName()),
        node.get_SourcePositionStart());
    }
  }
}