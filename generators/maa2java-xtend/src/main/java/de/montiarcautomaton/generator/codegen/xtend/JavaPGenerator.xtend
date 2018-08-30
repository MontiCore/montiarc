/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.montiarcautomaton.generator.codegen.xtend

import montiarc._symboltable.ComponentSymbol
import de.montiarcautomaton.generator.helper.ComponentHelper
import java.util.Optional
import montiarc._ast.ASTJavaPInitializer
import java.util.Arrays
import java.util.Collections
import montiarc._ast.ASTBehaviorElement
import java.util.List
import montiarc._ast.ASTElement
import montiarc._ast.ASTComponent
import montiarc._ast.ASTJavaPBehavior
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter
import de.monticore.prettyprint.IndentPrinter
import de.monticore.java.javadsl._ast.ASTBlockStatement

class JavaPGenerator extends BehaviorGenerator {

  override String hook(ComponentSymbol comp) {
    return ''''''
  }

  override String generateCompute(ComponentSymbol comp) {
    return '''
      @Override
      public «comp.name»Result compute(«comp.name»Input input) {
        // inputs
        «FOR portIn : comp.incomingPorts»
          final «portIn.typeReference.referencedSymbol.fullName» «portIn.name» = input.get«portIn.name.toFirstUpper»();
        «ENDFOR»
      
        final «comp.name»Result result = new «comp.name»Result();
        
        «FOR portOut : comp.outgoingPorts»
          «portOut.typeReference.referencedSymbol.fullName» «portOut.name» = result.get«portOut.name.toFirstUpper»();
        «ENDFOR»
        
        <#-- print java statements here -->
        «getJavaP(comp)»
        
        <#-- always add all outgoing values to result -->
        «FOR portOut : comp.outgoingPorts»
          result.set«portOut.name.toFirstUpper»(«portOut.name»);
        «ENDFOR»
        return result;
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
        return Optional.of(e as ASTJavaPBehavior);
      }
    }
    return Optional.empty();
  }

  override String generateGetInitialValues(ComponentSymbol comp) {
    return '''
      @Override
       public «comp.name»Result getInitialValues() {
         final «comp.name»Result result = new «comp.name»Result();
         
         try {
         «FOR portOut : comp.outgoingPorts»
           «portOut.typeReference.referencedSymbol.fullName» «portOut.name» = null;
         «ENDFOR»
         
         «FOR init : getInitializations(comp)»
           «ComponentHelper.printInit(init)»    
         «ENDFOR»
      
         «FOR portOut : comp.outgoingPorts»
           result.set«portOut.name.toFirstUpper»(«portOut.name»);
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
