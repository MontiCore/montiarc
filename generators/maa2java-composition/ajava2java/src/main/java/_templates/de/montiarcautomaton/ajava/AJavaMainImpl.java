/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package _templates.de.montiarcautomaton.ajava;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

import de.montiarcautomaton.ajava.generator.helper.AJavaHelper;
import de.monticore.ast.ASTNode;
import de.monticore.java.javadsl._ast.ASTBlockStatement;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.lang.montiarc.ajava._ast.ASTAJavaDefinition;
import de.monticore.lang.montiarc.ajava._symboltable.AJavaDefinitionSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.CommonSymbol;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class AJavaMainImpl extends AJavaMain {
  
  /**
   * @see _templates.de.montiarcautomaton.ajava.AJavaMain#doGenerate(java.nio.file.Path,
   * de.monticore.ast.ASTNode, de.monticore.symboltable.CommonSymbol)
   */
  @Override
  public void doGenerate(Path filepath, ASTNode node, CommonSymbol symbol) {
    if (symbol.getKind().isKindOf(ComponentSymbol.KIND)) {
      ComponentSymbol comp = (ComponentSymbol) symbol;
      Collection<AJavaDefinitionSymbol> ajava = comp.getSpannedScope()
          .<AJavaDefinitionSymbol> resolveLocally(AJavaDefinitionSymbol.KIND);
      AJavaDefinitionSymbol ajavaDef = ajava.iterator().next();
      
      String inputName = comp.getName() + "Input";
      String resultName = comp.getName() + "Result";
      String implName = comp.getName() + "Impl";
      
      JavaDSLPrettyPrinter printer = new JavaDSLPrettyPrinter(new IndentPrinter());
      ASTAJavaDefinition ajavaNode = (ASTAJavaDefinition) ajavaDef.getAstNode().get();
      
      StringBuilder sb = new StringBuilder();
      for (ASTBlockStatement s : ajavaNode.getBlockStatements()) {
        sb.append(printer.prettyprint(s));
      }
      
      generate(filepath, node, new AJavaHelper(), comp.getPackageName(), comp.getImports(),
          ajavaDef.getName(), resultName, inputName,
          implName, comp.getIncomingPorts(), comp.getConfigParameters(), sb.toString());
      
    }
  }
  
}
