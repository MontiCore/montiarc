package de.monticore.lang.montiarc.cocos.automaton;

import de.monticore.lang.montiarc.montiarc._ast.ASTAutomatonBehavior;
import de.monticore.lang.montiarc.montiarc._ast.ASTBehaviorElement;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTBehaviorElementCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if the name of an IO-Automaton starts with an
 * uppercase letter.
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class AutomatonUppercase implements MontiArcASTBehaviorElementCoCo {

	@Override
	public void check(ASTBehaviorElement node) {
		if (node instanceof ASTAutomatonBehavior) {
			ASTAutomatonBehavior ast = (ASTAutomatonBehavior) node;
			if (ast.getName().isPresent()) {
				if (!Character.isUpperCase(ast.getName().get().charAt(0))) {
					Log.error("0xAB130 The name of the automaton should start with an uppercase letter.",
							node.get_SourcePositionStart());
				}
			}
		}
	}

}
