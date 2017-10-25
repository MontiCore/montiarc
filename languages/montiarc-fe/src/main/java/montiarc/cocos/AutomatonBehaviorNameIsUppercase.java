package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTAutomatonBehavior;
import montiarc._cocos.MontiArcASTAutomatonBehaviorCoCo;

/**
 * Context condition for checking, if the name of an IO-Automaton starts with an
 * uppercase letter.
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class AutomatonBehaviorNameIsUppercase implements MontiArcASTAutomatonBehaviorCoCo {

	@Override
	public void check(ASTAutomatonBehavior node) {
		if (node.getName().isPresent()) {
			if (!Character.isUpperCase(node.getName().get().charAt(0))) {
				Log.error("0xMA015 The name of the automaton should start with an uppercase letter.",
						node.get_SourcePositionStart());
			}
		}
	}

}
