package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTPort;
import montiarc._cocos.MontiArcASTPortCoCo;

/**
 * Ensures, that names of ports start with a lowercase letter.
 *
 * @author Michael Mutert
 */
public class PortNameIsLowerCase implements MontiArcASTPortCoCo {

    /**
     * @see montiarc._cocos.MontiArcASTMACompilationUnitCoCo#check(montiarc._ast.ASTMACompilationUnit)
     */
    @Override
    public void check(ASTPort node) {
        if(node.getName().isPresent()) {
            if(!Character.isLowerCase(node.getName().get().charAt(0))){
                Log.error("0xMA077: The name of the port should start with a lowercase letter.",
                        node.get_SourcePositionStart());
            }
        }
    }
}
