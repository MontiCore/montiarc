package montiarc.cocos;

import java.util.HashSet;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTComponentVariableDeclaration;
import montiarc._ast.ASTElement;
import montiarc._ast.ASTVariable;
import montiarc._cocos.MontiArcASTComponentCoCo;

/**
 * Context condition for checking, if a component variable is defined multiple times
 * with different types.
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class VariableDefinedMultipleTimes implements MontiArcASTComponentCoCo {

	@Override
	public void check(ASTComponent node) {
		HashSet<String> names = new HashSet<>();
		for (ASTElement e : node.getBody().getElements()) {
			if (e instanceof ASTComponentVariableDeclaration) {
				ASTComponentVariableDeclaration decl = (ASTComponentVariableDeclaration) e;
				for (ASTVariable var : decl.getVariables()) {
					if (names.contains(var.getName())) {
						Log.error("0xMA035 Variable " + var.getName() + " is defined more than once.",
								var.get_SourcePositionStart());
					} else {
						names.add(var.getName());
					}
				}
			}
		}
	}
}
