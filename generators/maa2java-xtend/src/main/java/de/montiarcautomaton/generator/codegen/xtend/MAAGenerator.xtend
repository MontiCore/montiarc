/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend

import de.montiarcautomaton.generator.codegen.xtend.behavior.AutomatonGenerator
import de.montiarcautomaton.generator.codegen.xtend.behavior.JavaPGenerator
import de.montiarcautomaton.generator.codegen.xtend.util.AbstractAtomicImplementation
import de.montiarcautomaton.generator.codegen.xtend.util.Identifier
import de.monticore.ast.ASTCNode
import de.monticore.io.FileReaderWriter
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import montiarc._ast.ASTAutomatonBehavior
import montiarc._ast.ASTBehaviorElement
import montiarc._ast.ASTComponent
import montiarc._ast.ASTJavaPBehavior
import montiarc._symboltable.ComponentSymbol
import de.montiarcautomaton.generator.helper.ComponentHelper
import de.se_rwth.commons.logging.Log

/**
 * Main entry point for generator. From this all target artifacts are generated for a component. 
 * It uses dispatching for calling the right implementation generator.
 * 
 * @author  Pfeiffer
 * @version $Revision$,
 *          $Date$
 */
class MAAGenerator {

  def static void generateAll(File targetPath, File hwc, ComponentSymbol comp) {
    Identifier.createInstance(comp)

    toFile(targetPath, comp.name + "Input", Input.generateInput(comp));
    toFile(targetPath, comp.name + "Result", Result.generateResult(comp));
    toFile(targetPath, comp.name, new ComponentGenerator().generate(comp));

    var boolean existsHWC = ComponentHelper.existsHWCClass(hwc, comp.packageName + "." + comp.name + "Impl");
      

    if (!existsHWC && comp.isAtomic) {
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

  def static private toFile(File targetPath, String name, String content) {
    var Path path = Paths.get(targetPath.absolutePath + File.separator + name + ".java")
    var FileReaderWriter writer = new FileReaderWriter()
    Log.info("Writing to file " + path + ".", "MAAGenerator");
    writer.storeInFile(path, content)
  }

  def private static dispatch generateBehavior(ASTJavaPBehavior ajava, ComponentSymbol comp) {
    return JavaPGenerator.newInstance.generate(comp)
  }

  def private static dispatch generateBehavior(ASTAutomatonBehavior automaton, ComponentSymbol comp) {
    return new AutomatonGenerator(comp).generate(comp)
  }
}
