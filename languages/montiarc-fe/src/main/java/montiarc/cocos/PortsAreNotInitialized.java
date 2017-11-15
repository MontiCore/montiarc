package montiarc.cocos;

import de.monticore.types.types._ast.ASTQualifiedName;
import montiarc._ast.ASTValueInitialization;
import montiarc._cocos.MontiArcASTValueInitializationCoCo;

public class PortsAreNotInitialized implements MontiArcASTValueInitializationCoCo {
    @Override
    public void check(ASTValueInitialization node) {
        // Resolve the name of the assigned variable
        // If it is a port symbol throw an error
        ASTQualifiedName qualifiedName = node.getQualifiedName();
    }
}
