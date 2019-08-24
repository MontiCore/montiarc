/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import java.util.ArrayList;
import java.util.List;

import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;

/**
 * Ensures, that there are no duplicate import statements.
 *
 * @implements [Hab16] CV3: Duplicated imports should be avoided. (p.71, no listing)
 * @author Michael Mutert
 */
public class ImportsAreUnique implements MontiArcASTComponentCoCo {
  
  /* Checks for duplicate import statements in an artifact. The imports java.lang.* and java.util.*
   * are automatic. Therefore, they are treated differently to allow a single import declaration in
   * the model for user convenience. */
  @Override
  public void check(ASTComponent node) {
    if (!node.getSymbolOpt().isPresent()) {
      Log.error(
          String.format("0xMA010 ASTComponent node \"%s\" has no " +
                            "symbol. Did you forget to run the " +
                            "SymbolTableCreator before checking cocos?",
              node.getName()));
      return;
    }
    ComponentSymbol symbol = (ComponentSymbol) node.getSymbolOpt().get();
    
    List<ImportStatement> imports = symbol.getImports();
    List<String> checked = new ArrayList<>();
    for (int i = 0; i < imports.size() - 2; i++) {
      ImportStatement imp = imports.get(i);
      String statement = imp.getStatement();
      if (imp.isStar()) {
        statement += ".*";
      }
      if (checked.contains(statement)) {
        Log.warn(String.format("0xMA074 The import statement " + statement + " is " +
            "declared more than once.", node.getName()));
      }
      else {
        checked.add(statement);
      }
    }
  }
}
