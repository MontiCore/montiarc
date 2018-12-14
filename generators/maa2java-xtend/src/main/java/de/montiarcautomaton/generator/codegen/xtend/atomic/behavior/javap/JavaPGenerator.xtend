/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.montiarcautomaton.generator.codegen.xtend.atomic.behavior.javap

import de.montiarcautomaton.generator.codegen.xtend.atomic.behavior.BehaviorGenerator
import de.montiarcautomaton.generator.codegen.xtend.util.Generics
import de.montiarcautomaton.generator.helper.ComponentHelper
import de.monticore.java.javadsl._ast.ASTBlockStatement
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter
import de.monticore.prettyprint.IndentPrinter
import java.util.Collections
import java.util.List
import java.util.Optional
import montiarc._ast.ASTComponent
import montiarc._ast.ASTElement
import montiarc._ast.ASTJavaPBehavior
import montiarc._ast.ASTJavaPInitializer
import montiarc._symboltable.ComponentSymbol

class JavaPGenerator extends BehaviorGenerator {

  override String hook(ComponentSymbol comp) {
    return ''''''
  }

  override String printCompute(ComponentSymbol comp) {
    var ComponentHelper helper = new ComponentHelper(comp);
    return '''
      @Override
      public «comp.name»Result«Generics.print(comp)»
                compute(«comp.name»Input«Generics.print(comp)» «helper.inputName») {
        // inputs
        «FOR portIn : comp.incomingPorts»
          final «helper.printPortType(portIn)» «portIn.name» = «helper.inputName».get«portIn.name.toFirstUpper»();
        «ENDFOR»
      
        final «comp.name»Result «helper.resultName» = new «comp.name»Result();
        
        «FOR portOut : comp.outgoingPorts»
          «helper.printPortType(portOut)» «portOut.name» = «helper.resultName».get«portOut.name.toFirstUpper»();
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
       public «comp.name»Result«Generics.print(comp)» getInitialValues() {
         final «comp.name»Result «helper.resultName» = new «comp.name»Result();
         
         try {
         «FOR portOut : comp.outgoingPorts»
           «helper.printPortType(portOut)» «portOut.name» = null;
         «ENDFOR»
         
         «FOR init : getInitializations(comp)»
           «ComponentHelper.printInit(init)»    
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
    var Optional<ASTJavaPInitializer> initialize = ComponentHelper.getComponentInitialization(comp);
    if (initialize.isPresent) {
      return initialize.get.valueInitializationList;
    }
    return Collections.EMPTY_LIST;
  }

}
