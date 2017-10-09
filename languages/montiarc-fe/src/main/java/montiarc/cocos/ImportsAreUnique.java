package montiarc.cocos;

import de.monticore.types.types._ast.ASTImportStatement;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcASTMACompilationUnitCoCo;

import java.util.HashSet;
import java.util.List;

/**
 * Ensures, that there are no duplicate import statements.
 *
 * @author Michael Mutert
 */
public class ImportsAreUnique implements MontiArcASTMACompilationUnitCoCo {

    @Override
    public void check(ASTMACompilationUnit node) {
        List<ASTImportStatement> imports = node.getImportStatements();
        HashSet<String> importStrings = new HashSet<>();
        for(ASTImportStatement importStatement: imports) {
            String name = importStatement.toString();
            if(importStrings.contains(name)){
                Log.error("0xC0002 The import statement " + name + " is declared multiple times", node.get_SourcePositionStart());
            } else {
                importStrings.add(name);
            }
        }
    }
}
