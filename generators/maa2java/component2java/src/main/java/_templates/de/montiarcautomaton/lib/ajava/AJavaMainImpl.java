/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package _templates.de.montiarcautomaton.lib.ajava;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import de.montiarcautomaton.generator.helper.AJavaHelper;
import de.monticore.ast.ASTNode;
import de.monticore.java.javadsl._ast.ASTBlockStatement;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.CommonSymbol;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTElement;
import montiarc._ast.ASTJavaPBehavior;
import montiarc._ast.ASTJavaPInitializer;
import montiarc._ast.ASTValueInitialization;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.JavaBehaviorSymbol;
import montiarc._symboltable.VariableSymbol;

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
  public void generate(Path filepath, ASTNode node, CommonSymbol symbol) {
    if (symbol.getKind().isKindOf(ComponentSymbol.KIND)) {
      ComponentSymbol comp = (ComponentSymbol) symbol;
      Collection<JavaBehaviorSymbol> ajava = comp.getSpannedScope()
          .<JavaBehaviorSymbol> resolveLocally(JavaBehaviorSymbol.KIND);
      JavaBehaviorSymbol ajavaDef = ajava.iterator().next();
      
      String inputName = comp.getName() + "Input";
      String resultName = comp.getName() + "Result";
      String implName = comp.getName() + "Impl";
      
      Optional<ASTJavaPInitializer> init = getComponentInitialization(comp);
      List<ASTValueInitialization> varInits = new ArrayList<>();
      
      if (init.isPresent()) {
        varInits = init.get().getValueInitializations();
      }
      
      JavaDSLPrettyPrinter printer = new JavaDSLPrettyPrinter(new IndentPrinter());
      ASTJavaPBehavior ajavaNode = (ASTJavaPBehavior) ajavaDef.getAstNode().get();
      
      StringBuilder sb = new StringBuilder();
      for (ASTBlockStatement s : ajavaNode.getBlockStatements()) {
        sb.append(printer.prettyprint(s));
      }
      
      AJavaHelper helper = new AJavaHelper(comp);
      
      AJavaMain.generate(filepath, node, helper, comp.getPackageName(), comp.getImports(),
          ajavaDef.getName(), resultName, inputName,
          implName, comp.getIncomingPorts(), comp.getOutgoingPorts(), comp.getConfigParameters(),
          comp.getVariables(), sb.toString(), varInits);
      
    }
  }
  private static Optional<ASTJavaPInitializer> getComponentInitialization(ComponentSymbol comp) {
    Optional<ASTJavaPInitializer> ret = Optional.empty();
    Optional<ASTNode> ast = comp.getAstNode();
    if(ast.isPresent()) {
      ASTComponent compAST = (ASTComponent) ast.get();
      for(ASTElement e : compAST.getBody().getElements()) {
        if(e instanceof ASTJavaPInitializer) {
          ret = Optional.of((ASTJavaPInitializer) e);
          
        }
      }
    }    
    return ret;
  }
  
}
