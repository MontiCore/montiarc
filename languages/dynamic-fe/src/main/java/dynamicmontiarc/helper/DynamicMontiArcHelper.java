/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.helper;

import dynamicmontiarc._ast.ASTModeDeclaration;
import dynamicmontiarc._ast.ASTModeAutomaton;
import dynamicmontiarc._ast.ASTModeTransition;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTConnector;
import montiarc._ast.ASTElement;
import montiarc._ast.ASTTransition;
import montiarc._symboltable.TransitionSymbol;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO
 *
 */
public class DynamicMontiArcHelper {

    /**
     * A node is dynamic if it contains at least one of the elements needed for
     * dynamic reconfiguration.
     * This means that one of mode controller, a mode declaration, or initial mode
     * declaration have to be present.
     *
     * @param node The component to be checked.
     * @return Returns whether the component has any dynamic elements.
     */
    public static boolean isDynamic(ASTComponent node) {

        for (ASTElement element : node.getBody().getElementList()) {
            if (element instanceof ASTModeAutomaton) {
                return true;
            }
        }
        return false;
    }

    public static ASTModeAutomaton getModeAutomaton(ASTComponent node) {
        for (ASTElement element : node.getBody().getElementList()) {
            if (element instanceof ASTModeAutomaton) {
                return (ASTModeAutomaton) element;
            }
        }
        return null;
    }

    public List<TransitionSymbol> getTransitionsForMode(
        ASTModeAutomaton automaton,
        String mode) {

        List<TransitionSymbol> symbols = new ArrayList<>();
        for (ASTModeTransition transition : automaton.getModeTransitionList()) {

            if (transition.getSource().equals(mode)) {
                symbols.add((TransitionSymbol) transition.getSymbol().get());

            }
        }
        return symbols;
    }

}
