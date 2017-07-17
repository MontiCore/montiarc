/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.cocos.automaton;

import de.monticore.lang.montiarc.montiarc._ast.ASTBehaviorElement;
import de.monticore.lang.montiarc.montiarc._ast.ASTComponent;
import de.monticore.lang.montiarc.montiarc._ast.ASTElement;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentCoCo;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Prevents composed components with embedded behavior models
 *
 * @author Andreas Wortmann
 */
public class ImplementationInNonAtomicComponent implements MontiArcASTComponentCoCo {

	/**
	 * @see de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentCoCo#check(de.monticore.lang.montiarc.montiarc._ast.ASTComponent)
	 */
	@Override
	public void check(ASTComponent node) {
		ComponentSymbol cs = (ComponentSymbol) node.getSymbol().get();
		if (cs.isDecomposed() && hasBehavior(node)) {
			Log.error("0xAB141 There must not be a behavior embedding in composed components.",
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
