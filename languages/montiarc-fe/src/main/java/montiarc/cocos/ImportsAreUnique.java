package montiarc.cocos;

import com.sun.xml.internal.ws.wsdl.writer.document.Import;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.types._ast.ASTImportStatement;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._cocos.MontiArcASTMACompilationUnitCoCo;
import montiarc._symboltable.ComponentSymbol;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Ensures, that there are no duplicate import statements.
 *
 * @author Michael Mutert
 */
public class ImportsAreUnique implements MontiArcASTComponentCoCo {
//
//    @Override
//    public void check(ASTMACompilationUnit node) {
//        List<ASTImportStatement> imports = node.getImportStatements();
//        HashSet<String> importStrings = new HashSet<>();
//        for(ASTImportStatement importStatement: imports) {
//            String name = importStatement.toString();
//            if(importStrings.contains(name)){
//                Log.error("0xC0002 The import statement " + name + " is declared multiple times", node.get_SourcePositionStart());
//            } else {
//                importStrings.add(name);
//            }
//        }
//    }

    @Override
    /*
        Checks for duplicate import statements in an artifact.
        The imports java.lang.* and java.util.* are automatic. Therefore, they are treated differently to allow a single import declaration in the model for user convenience.
     */
    public void check(ASTComponent node) {
        ComponentSymbol symbol = (ComponentSymbol) node.getSymbol().orElse(null);

        if(symbol == null){
            Log.error(String.format("0x9AF6C ASTComponent node \"%s\" has no symbol. Did you forget to "
                    + "run the SymbolTableCreator before checking cocos?", node.getName()));
            return;
        }

        List<ImportStatement> imports = symbol.getImports();
        List<String> checked = new ArrayList<>();
        for (int i = 0; i < imports.size() - 2; i++){
            ImportStatement imp = imports.get(i);
            String statement = imp.getStatement();
            if(imp.isStar()){
                statement += ".*";
            }
            if(checked.contains(statement)){
                Log.error(String.format("0xC000A The import statement " + statement + " is declared more than once.", node.getName()));
            } else {
                checked.add(statement);
            }
        }
    }
}
