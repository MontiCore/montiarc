/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package _templates.mc.montiarcautomaton.automaton.lib;

import java.nio.file.Path;

import de.montiarcautomaton.automatongenerator.AutomatonGenerator;
import de.monticore.ast.ASTNode;
import de.monticore.symboltable.CommonSymbol;

/**
 * Generates an implementation class for components with automatons.
 *
 * @author Jerome Pfeiffer
 * @version $Revision$, $Date$
 */
public class AutomatonImplMainImpl extends AutomatonImplMain {
  
  /**
   * @see _templates.mc.montiarcautomaton.lejos.lib.AutomatonImplMain#doGenerate(java.nio.file.Path,
   * de.monticore.ast.ASTNode, de.monticore.symboltable.CommonSymbol)
   */
  @Override
  public void generate(Path filepath, ASTNode node, CommonSymbol symbol) {
    AutomatonGenerator.doGenerate(filepath, node, symbol);
  }
  
}
