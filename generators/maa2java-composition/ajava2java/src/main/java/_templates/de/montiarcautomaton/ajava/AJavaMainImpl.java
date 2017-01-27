/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package _templates.de.montiarcautomaton.ajava;

import java.nio.file.Path;

import de.montiarcautomaton.ajava.generator.codegen.AJavaGenerator;
import de.monticore.ast.ASTNode;
import de.monticore.symboltable.CommonSymbol;

/**
 * Implementation of the generator interface for AJava. 
 *
 * @author Jerome Pfeiffer
 * @version $Revision$, $Date$
 */
public class AJavaMainImpl extends AJavaMain {
  
  /**
   * @see _templates.de.montiarcautomaton.ajava.AJavaMain#doGenerate(java.nio.file.Path,
   * de.monticore.ast.ASTNode, de.monticore.symboltable.CommonSymbol)
   */
  @Override
  public void doGenerate(Path filepath, ASTNode node, CommonSymbol symbol) {
    AJavaGenerator.doGenerate(filepath, node, symbol);
  }
  
}
