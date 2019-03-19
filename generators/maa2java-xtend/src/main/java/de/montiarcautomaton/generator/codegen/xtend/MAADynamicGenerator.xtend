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
import de.montiarcautomaton.generator.codegen.xtend.compinst.DynamicComponentGenerator
import de.montiarcautomaton.generator.codegen.xtend.compinst.DynamicDeploy

/**

 */
 
 
class MAADynamicGenerator {

  def static generateAll(File targetPath, File hwc, ComponentSymbol comp) {
    Identifier.createInstance(comp)


    toFile(targetPath, "Dynamic" + comp.name, new DynamicComponentGenerator().generate(comp));
    
    if (comp.getStereotype().containsKey("deploy")) {
       toFile(targetPath, "DynamicDeploy" + comp.name, DynamicDeploy.generateDeploy(comp)); 	
    }
    
    
    
  }

  def static private toFile(File targetPath, String name, String content) {
    var Path path = Paths.get(targetPath.absolutePath + File.separator + name + ".java")
    var FileReaderWriter writer = new FileReaderWriter()
    println("Writing to file " + path + ".");
    writer.storeInFile(path, content);
  }


}
