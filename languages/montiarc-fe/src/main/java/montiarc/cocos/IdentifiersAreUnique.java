package montiarc.cocos;

import de.monticore.types.TypesPrinter;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.*;
import montiarc._cocos.MontiArcASTComponentBodyCoCo;
import montiarc._cocos.MontiArcASTComponentCoCo;

import java.util.HashSet;

public class IdentifiersAreUnique implements MontiArcASTComponentCoCo{

    @Override
    public void check(ASTComponent node) {
        HashSet<String> variableNames = new HashSet<>();
        HashSet<String> portNames = new HashSet<>();

        for (ASTElement e : node.getBody().getElements()) {

            // Check variable declarations
            if (e instanceof ASTComponentVariableDeclaration) {
                ASTComponentVariableDeclaration decl = (ASTComponentVariableDeclaration) e;
                for (ASTVariable var : decl.getVariables()) {
                    if (variableNames.contains(var.getName())) {
                        Log.error("0xMA035 Variable " + var.getName() + " is defined more than once.",
                                var.get_SourcePositionStart());
                    } else if(portNames.contains(var.getName())) {
                        Log.error("0xMA065 The name of variable " + var.getName() + " is " +
                                        "already used as the name of a port.",
                                var.get_SourcePositionStart());
                    } else {
                        variableNames.add(var.getName());
                    }
                }
            }

            //Check port names
            if (e instanceof ASTInterface) {
                ASTInterface decl = (ASTInterface) e;
                for (ASTPort port : decl.getPorts()) {
                    String name = "";
                    if (port.getName().isPresent()) {
                        name = port.getName().get();
                    }
                    else {
                        // calc implicit name
                        String implicitName = TypesPrinter.printType(port.getType());
                        name = StringTransformations.uncapitalize(implicitName);
                    }

                    if (portNames.contains(name)) {
                        Log.error(String.format("0xMA053 The name of port '%s' is ambiguos!", name),
                                port.get_SourcePositionStart());
                    } else if(variableNames.contains(name)){
                        Log.error(String.format("0xMA063 The name of port '%s' is already used as the name of a variable.", name),
                                port.get_SourcePositionStart());
                    } else {
                        portNames.add(name);
                    }
                }
            }
        }
    }
}
