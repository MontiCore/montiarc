package montiarc.cocos;

import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.MontiArcArtifactScope;
import montiarc._symboltable.ValueSymbol;

/**
 * Ensures, that outer components have no instance name.
 *
 * @author Michael Mutert
 */
public class OuterComponentIsUnnamed implements MontiArcASTComponentCoCo {

    @Override
    public void check(ASTComponent node) {
        if(node.getEnclosingScope().isPresent()){
            if(node.getEnclosingScope().get() instanceof MontiArcArtifactScope){
                if(node.getInstanceName().isPresent()) {
                    if(!node.getInstanceName().get().isEmpty()){
                        Log.error("0xC0003: Outer component has an instance name", node.get_SourcePositionStart());
                    }
                }

            }
        }
    }
}
