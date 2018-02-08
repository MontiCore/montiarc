package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTAutomatonBehavior;
import montiarc._ast.ASTBehaviorElement;
import montiarc._cocos.MontiArcASTBehaviorElementCoCo;

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
					Log.error("0xMA049 The name of the automaton should start with an uppercase letter.",
							node.get_SourcePositionStart());
				}
			}
		}
	}

}
