/* (c) https://github.com/MontiCore/monticore */
package arc._cocos;

import arc._ast.ASTArcField;
import arc.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.NoSuchElementException;

/**
 * Checks for each variable whether it type exists.
 */
public class FieldTypeExists implements ArcASTArcFieldCoCo {

  @Override
  public void check(@NotNull ASTArcField node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTArcField node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    FieldSymbol symbol = node.getSymbol();
    try {
      symbol.getType().getTypeInfo();
    }
    catch (NoSuchElementException e) {
      Log.error(String.format(ArcError.MISSING_TYPE_OF_FIELD.toString(), symbol.getName()),
        node.get_SourcePositionStart());
    }
  }
}