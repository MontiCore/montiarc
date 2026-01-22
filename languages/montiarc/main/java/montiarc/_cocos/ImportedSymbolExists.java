/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._cocos.MCBasicTypesASTMCImportStatementCoCo;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.IMontiArcScope;
import montiarc.util.MontiArcError;
import org.codehaus.commons.nullanalysis.NotNull;

public class ImportedSymbolExists implements MCBasicTypesASTMCImportStatementCoCo {

  @Override
  public void check(@NotNull ASTMCImportStatement node) {
    Preconditions.checkNotNull(node);

    if (!node.isStar()) {
      IMontiArcScope scope = (IMontiArcScope) node.getEnclosingScope();

      if ((scope.resolveComponentTypeMany(node.getQName()).isEmpty()
        && scope.resolveTypeMany(node.getQName()).isEmpty()
        && scope.resolveVariableMany(node.getQName()).isEmpty())
        && scope.resolveFunctionMany(node.getQName()).isEmpty()) {
        Log.warn(MontiArcError.IMPORTED_SYMBOL_MISSING.format(node.getQName()),
          node.get_SourcePositionStart(), node.get_SourcePositionEnd()
        );
      }
    }
  }
}
