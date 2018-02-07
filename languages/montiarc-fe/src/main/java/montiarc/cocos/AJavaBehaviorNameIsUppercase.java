package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTJavaPBehavior;
import montiarc._cocos.MontiArcASTJavaPBehaviorCoCo;

/**
 * Context condition for checking, if the name of an AJava compute block
 * 
 * @author Andreas Wortmann
 */
public class AJavaBehaviorNameIsUppercase implements MontiArcASTJavaPBehaviorCoCo {

	@Override
	public void check(ASTJavaPBehavior node) {
		if (node.getName().isPresent()) {
			if (!Character.isUpperCase(node.getName().get().charAt(0))) {
				Log.error("0xMA174 The name of the AJava compute block '" + node.getName() + "' should start with an uppercase letter.",
						node.get_SourcePositionStart());
			}
		}
	}

}
