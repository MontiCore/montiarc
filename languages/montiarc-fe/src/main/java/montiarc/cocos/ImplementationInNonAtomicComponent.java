/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTBehaviorElement;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTElement;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;

/**
 * Prevents composed components with embedded behavior models
 *
 * @author Andreas Wortmann
 */
public class ImplementationInNonAtomicComponent implements MontiArcASTComponentCoCo {

	/**
	 * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
	 */
	@Override
	public void check(ASTComponent node) {
		ComponentSymbol cs = (ComponentSymbol) node.getSymbol().get();
		if (cs.isDecomposed() && hasBehavior(node)) {
			Log.error("0xMA051 There must not be a behavior embedding in composed components.",
					node.get_SourcePositionStart());
		}

	}

	public boolean hasBehavior(ASTComponent node) {
		for (ASTElement e : node.getBody().getElements()) {
			if (e instanceof ASTBehaviorElement) {
				return true;
			}
		}
		return false;
	}

}
