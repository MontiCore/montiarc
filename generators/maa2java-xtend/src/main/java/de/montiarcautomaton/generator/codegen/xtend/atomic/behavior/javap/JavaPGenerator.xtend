/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.montiarcautomaton.generator.codegen.xtend.atomic.behavior.javap

import de.montiarcautomaton.generator.codegen.xtend.atomic.behavior.BehaviorGenerator
import de.montiarcautomaton.generator.codegen.xtend.util.TypeParameters
import de.montiarcautomaton.generator.helper.ComponentHelper
import de.monticore.ast.ASTNode
import de.monticore.java.javadsl._ast.ASTBlockStatement
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter
import de.monticore.prettyprint.IndentPrinter
import de.se_rwth.commons.Names
import java.util.Collections
import java.util.List
import java.util.Optional
import montiarc._ast.ASTComponent
import montiarc._ast.ASTElement
import montiarc._ast.ASTJavaPBehavior
import montiarc._ast.ASTJavaPInitializer
import montiarc._ast.ASTPort
import montiarc._ast.ASTValueInitialization
import montiarc._symboltable.ComponentSymbol

class JavaPGenerator extends BehaviorGenerator {

  override String hook(ComponentSymbol comp) {
    return ''''''
  }

  override String printCompute(ComponentSymbol comp) {
    var ComponentHelper helper = new ComponentHelper(comp);
    return '''
      @Override
      public «comp.name»Result«TypeParameters.printFormalTypeParameters(comp)»
                compute(«comp.name»Input«TypeParameters.printFormalTypeParameters(comp)» «helper.inputName») {
        // inputs
        «FOR portIn : comp.incomingPorts»
          final «ComponentHelper.printTypeName((portIn.astNode.get as ASTPort).type)» «portIn.name» = «helper.inputName».get«portIn.name.toFirstUpper»();
        «ENDFOR»
      
        final «comp.name»Result «helper.resultName» = new «comp.name»Result();
        
        «FOR portOut : comp.outgoingPorts»
          «ComponentHelper.printTypeName((portOut.astNode.get as ASTPort).type)» «portOut.name» = «helper.resultName».get«portOut.name.toFirstUpper»();
        «ENDFOR»
        
        «««  print java statements here
        «getJavaP(comp)»
        
        «««  always add all outgoing values to result
        «FOR portOut : comp.outgoingPorts»
          «helper.resultName».set«portOut.name.toFirstUpper»(«portOut.name»);
        «ENDFOR»
        return «helper.resultName»;
      }
      
    '''
  }

  def getJavaP(ComponentSymbol comp) {
    var Optional<ASTJavaPBehavior> behaviorEmbedding = getBehaviorEmbedding(comp);
    var JavaDSLPrettyPrinter prettyPrinter = new JavaDSLPrettyPrinter(new IndentPrinter());
    var StringBuilder sb = new StringBuilder();
    for (ASTBlockStatement s : behaviorEmbedding.get.blockStatementList) {
      sb.append(prettyPrinter.prettyprint(s));
    }
    return sb.toString;
  }

  def Optional<ASTJavaPBehavior> getBehaviorEmbedding(ComponentSymbol comp) {
    var ASTComponent compAST = comp.astNode.get as ASTComponent;
    var List<ASTElement> elements = compAST.getBody().getElementList();
    for (ASTElement e : elements) {
      if (e instanceof ASTJavaPBehavior) {
        return Optional.of(e);
      }
    }
    return Optional.empty();
  }

  override String printGetInitialValues(ComponentSymbol comp) {
    var ComponentHelper helper = new ComponentHelper(comp)
    return '''
      @Override
       public «comp.name»Result«TypeParameters.printFormalTypeParameters(comp)» getInitialValues() {
         final «comp.name»Result «helper.resultName» = new «comp.name»Result();
         
         try {
         «FOR portOut : comp.outgoingPorts»
           «ComponentHelper.printTypeName((portOut.astNode.get as ASTPort).type)» «portOut.name» = null;
         «ENDFOR»
         
         «FOR init : getInitializations(comp)»
           «printInit(init)»   
         «ENDFOR»
      
         «FOR portOut : comp.outgoingPorts»
           «helper.resultName».set«portOut.name.toFirstUpper»(«portOut.name»);
         «ENDFOR»
         } catch(Exception e) {
           e.printStackTrace();
          }
      
          return result;
       }
    '''
  }


  def getInitializations(ComponentSymbol comp) {
    var Optional<ASTJavaPInitializer> initialize = Optional.empty();
    var Optional<ASTNode> ast = comp.getAstNode();
    if (ast.isPresent()) {
      var ASTComponent compAST = ast.get() as ASTComponent;
      for (ASTElement e : compAST.getBody().getElementList()) {
        if (e instanceof ASTJavaPInitializer) {
          initialize = Optional.of(e);
        }
      }
    }
    if (initialize.isPresent) {
      return initialize.get.valueInitializationList;
    }
    return Collections.EMPTY_LIST;
  }

  def String printInit(ASTValueInitialization init) {
    var String ret = "";
    var JavaDSLPrettyPrinter printer = new JavaDSLPrettyPrinter(new IndentPrinter());
    var String name = Names.getQualifiedName(init.getQualifiedName().getPartList());
    ret += name;
    ret += " = ";
    ret += printer.prettyprint(init.getValuation().getExpression());
    ret += ";";

    return ret;
  }

}
