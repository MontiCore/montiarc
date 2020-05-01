/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.cocos;

import de.se_rwth.commons.logging.Log;
import dynamicmontiarc._ast.ASTModeAutomaton;
import montiarc._ast.ASTComponentBody;
import montiarc._ast.ASTElement;
import montiarc._cocos.MontiArcASTComponentBodyCoCo;

import java.util.ArrayList;

/**
 * There should be at most one Mode controller in a decomposed component
 *
 */
public class AtMostOneModeAutomaton implements MontiArcASTComponentBodyCoCo {

    @Override
    public void check(ASTComponentBody node) {
        ArrayList<ASTModeAutomaton> modeAutomata = new ArrayList<>();

        for (ASTElement astElement : node.getElementList()) {
            if(astElement instanceof ASTModeAutomaton) {
                modeAutomata.add((ASTModeAutomaton) astElement);
            }
        }

        if(modeAutomata.size() > 1){
            for (ASTModeAutomaton modeAutomaton : modeAutomata) {
                Log.error("0xMA206 More than one mode automaton found. " +
                        "Please remove all but one of the mode automata.",
                        modeAutomaton.get_SourcePositionStart());
            }
        }
    }
}
