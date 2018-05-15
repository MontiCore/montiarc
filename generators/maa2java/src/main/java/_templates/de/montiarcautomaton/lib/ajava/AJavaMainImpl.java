/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package _templates.de.montiarcautomaton.lib.ajava;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.montiarcautomaton.generator.helper.ComponentHelper;
import de.montiarcautomaton.generator.visitor.BehaviorVisitor;
import de.monticore.ast.ASTNode;
import de.monticore.java.javadsl._ast.ASTBlockStatement;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.CommonSymbol;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTJavaPBehavior;
import montiarc._ast.ASTJavaPInitializer;
import montiarc._ast.ASTMontiArcNode;
import montiarc._ast.ASTValueInitialization;
import montiarc._symboltable.ComponentSymbol;

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
      
      String inputName = comp.getName() + "Input";
      String resultName = comp.getName() + "Result";
      String implName = comp.getName() + "Impl";
      
      Optional<ASTJavaPInitializer> init = ComponentHelper.getComponentInitialization(comp);
      List<ASTValueInitialization> varInits = new ArrayList<>();
      
      if (init.isPresent()) {
        varInits = init.get().getValueInitializations();
      }
      
      BehaviorVisitor visitor = new BehaviorVisitor();
      visitor.handle((ASTComponent)node);
      JavaDSLPrettyPrinter printer = new JavaDSLPrettyPrinter(new IndentPrinter());
      Optional<ASTJavaPBehavior> ajavaNode = visitor.getJavaPBehavior();
      
      if (ajavaNode.isPresent()) {
        StringBuilder sb = new StringBuilder();
        for (ASTBlockStatement s : ajavaNode.get().getBlockStatements()) {
          sb.append(printer.prettyprint(s));
        }
        
        ComponentHelper helper = new ComponentHelper(comp);
        
        AJavaMain.generate(filepath, node, helper, comp.getPackageName(), comp.getImports(),
            ajavaNode.get().getName().orElse("JavaP"), resultName, inputName,
            implName, comp.getIncomingPorts(), comp.getOutgoingPorts(), comp.getConfigParameters(),
            comp.getVariables(), sb.toString(), varInits);
        
      }
    }
  }
  
}
