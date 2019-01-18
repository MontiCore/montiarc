/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.montiarcautomaton.generator.codegen.xtend

import de.montiarcautomaton.generator.codegen.xtend.behavior.AutomatonGenerator
import de.montiarcautomaton.generator.codegen.xtend.behavior.JavaPGenerator
import de.montiarcautomaton.generator.codegen.xtend.util.AbstractAtomicImplementation
import de.montiarcautomaton.generator.codegen.xtend.util.Identifier
import de.monticore.ast.ASTCNode
import de.monticore.codegen.mc2cd.TransformationHelper
import de.monticore.io.FileReaderWriter
import de.monticore.io.paths.IterablePath
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import montiarc._ast.ASTAutomatonBehavior
import montiarc._ast.ASTBehaviorElement
import montiarc._ast.ASTComponent
import montiarc._ast.ASTJavaPBehavior
import montiarc._symboltable.ComponentSymbol

/**
 * Main entry point for generator. From this all target artifacts are generated for a component. 
 * It uses dispatching for calling the right implementation generator.
 * 
 * @author  Pfeiffer
 * @version $Revision$,
 *          $Date$
 */
class MAAGenerator {

  def static generateAll(File targetPath, File hwc, ComponentSymbol comp) {
    Identifier.createInstance(comp)

    toFile(targetPath, comp.name + "Input", Input.generateInput(comp));
    toFile(targetPath, comp.name + "Result", Result.generateResult(comp));
    toFile(targetPath, comp.name, new ComponentGenerator().generate(comp));

    var boolean existsHWCClass = TransformationHelper.existsHandwrittenClass(
    	IterablePath.from(hwc, ".java"),
      comp.packageName + "." + comp.name + "Impl");

    if (!existsHWCClass && comp.isAtomic) {
      toFile(targetPath, comp.name + "Impl", generateBehaviorImplementation(comp));
    }
    
	// Generate inner components
    for(innerComp : comp.innerComponents) {
    	//TODO Fix hwc path for inner components
    	generateAll(targetPath.toPath.resolve(comp.name + "gen").toFile, hwc, innerComp);
    }
    
	// Generate deploy class
    if (comp.getStereotype().containsKey("deploy")) {
      toFile(targetPath, "Deploy" + comp.name, Deploy.generateDeploy(comp));
    }

  }

  def static generateBehaviorImplementation(ComponentSymbol comp) {
    var compAST = comp.astNode.get as ASTComponent
    var boolean hasBehavior = false
    for (element : compAST.body.elementList) {
      if (element instanceof ASTBehaviorElement) {
        hasBehavior = true;
        return generateBehavior(element as ASTCNode, comp)
      }
    }

    if (!hasBehavior) {
      return AbstractAtomicImplementation.generateAbstractAtomicImplementation(comp)
    }

  }

  def static public toFile(File targetPath, String name, String content) {
    var Path path = Paths.get(targetPath.absolutePath + "\\" + name + ".java")
    var FileReaderWriter writer = new FileReaderWriter()
    println("Writing to file " + path + ".");
    writer.storeInFile(path, content)
  }

  def public static dispatch generateBehavior(ASTJavaPBehavior ajava, ComponentSymbol comp) {
    return JavaPGenerator.newInstance.generate(comp)
  }

  def public static dispatch generateBehavior(ASTAutomatonBehavior automaton, ComponentSymbol comp) {
    return new AutomatonGenerator(comp).generate(comp)
  }
}
