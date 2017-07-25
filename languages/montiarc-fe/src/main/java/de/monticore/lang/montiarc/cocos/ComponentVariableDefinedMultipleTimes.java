package de.monticore.lang.montiarc.cocos;

import java.util.HashSet;

import de.monticore.lang.montiarc.montiarc._ast.ASTComponent;
import de.monticore.lang.montiarc.montiarc._ast.ASTComponentVariable;
import de.monticore.lang.montiarc.montiarc._ast.ASTComponentVariableDeclaration;
import de.monticore.lang.montiarc.montiarc._ast.ASTElement;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if a component variable is defined multiple times
 * with different types.
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class ComponentVariableDefinedMultipleTimes implements MontiArcASTComponentCoCo {

	@Override
	public void check(ASTComponent node) {
		HashSet<String> names = new HashSet<>();
		for (ASTElement e : node.getBody().getElements()) {
			if (e instanceof ASTComponentVariableDeclaration) {
				ASTComponentVariableDeclaration decl = (ASTComponentVariableDeclaration) e;
				for (ASTComponentVariable var : decl.getComponentVariables()) {
					if (names.contains(var.getName())) {
						Log.error("0xAA360 Variable " + var.getName() + " is defined more than once.",
								var.get_SourcePositionStart());
					} else {
						names.add(var.getName());
					}
				}
			}
		}
	}
}
