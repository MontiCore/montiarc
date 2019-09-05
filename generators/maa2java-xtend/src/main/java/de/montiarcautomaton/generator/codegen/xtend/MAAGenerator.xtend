/* (c) https://github.com/MontiCore/monticore */
package de.montiarcautomaton.generator.codegen.xtend

import de.montiarcautomaton.generator.codegen.xtend.behavior.ABehaviorGenerator
import de.montiarcautomaton.generator.codegen.xtend.behavior.AutomatonGenerator
import de.montiarcautomaton.generator.codegen.xtend.behavior.JavaPGenerator
import de.montiarcautomaton.generator.codegen.xtend.util.AbstractAtomicImplementation
import de.montiarcautomaton.generator.codegen.xtend.util.IMontiArcGenerator
import de.montiarcautomaton.generator.codegen.xtend.util.Identifier
import de.montiarcautomaton.generator.helper.ComponentHelper
import de.monticore.ast.ASTCNode
import de.monticore.io.FileReaderWriter
import de.se_rwth.commons.logging.Log
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.ArrayList
import java.util.List
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

  public var ComponentGenerator compGenerator = new ComponentGenerator;
  public var ABehaviorGenerator javaPGenerator = new JavaPGenerator;
  public var ABehaviorGenerator automatonGenerator = new AutomatonGenerator;
  public var Deploy deploy = new Deploy;
  public var Input inputGenerator = new Input;
  public var Result resultGenerator = new Result;
  public var AbstractAtomicImplementation abstractAtomicImplementation = new AbstractAtomicImplementation;

  List<IMontiArcGenerator> generators = new ArrayList;

  def void addGenerator(IMontiArcGenerator generator) {
    generators.add(generator);
  }
  

  def void generateAll(File targetPath, File hwc, ComponentSymbol comp) {
    Identifier.createInstance(comp)
    var tmp = new ArrayList(generators)
    

    tmp.add(inputGenerator);
    tmp.add(resultGenerator);
    tmp.add(compGenerator);

    for (IMontiArcGenerator generator : tmp) {
      val code = generator.generate(comp)
      if (code !== null && !code.isEmpty) {
        toFile(targetPath, generator.getArtifactName(comp), code, generator.fileEnding)
      }
    }

    var boolean existsHWC = ComponentHelper.existsHWCClass(hwc, comp.packageName + "." + comp.name + "Impl");

    if (!existsHWC && comp.isAtomic) {
      toFile(targetPath, comp.name + "Impl", generateBehaviorImplementation(comp), "java");
    }

    // Generate inner components
    for (innerComp : comp.innerComponents) {
      // TODO Fix hwc path for inner components
      generateAll(targetPath.toPath.resolve(comp.name + "gen").toFile, hwc, innerComp);
    }

    // Generate deploy class
    if (comp.getStereotype().containsKey("deploy")) {
      toFile(targetPath, deploy.getArtifactName(comp), deploy.generate(comp), deploy.fileEnding);
    }

  }

  def generateBehaviorImplementation(ComponentSymbol comp) {
    var compAST = comp.astNode.get as ASTComponent
    var boolean hasBehavior = false
    for (element : compAST.body.elementList) {
      if (element instanceof ASTBehaviorElement) {
        hasBehavior = true;
        return generateBehavior(element as ASTCNode, comp)
      }
    }

    if (!hasBehavior) {
      return abstractAtomicImplementation.generateAbstractAtomicImplementation(comp)
    }

  }

  def static private toFile(File targetPath, String name, String content, String fileEnding) {
    var Path path = Paths.get(targetPath.absolutePath + File.separator + name + "." + fileEnding)
    var FileReaderWriter writer = new FileReaderWriter()
    Log.info("Writing to file " + path + ".", "MAAGenerator");
    writer.storeInFile(path, content)
  }

  def protected dispatch generateBehavior(ASTJavaPBehavior ajava, ComponentSymbol comp) {
    return javaPGenerator.generate(comp)
  }

  def protected dispatch generateBehavior(ASTAutomatonBehavior automaton, ComponentSymbol comp) {
    return automatonGenerator.generate(comp)
  }

}
