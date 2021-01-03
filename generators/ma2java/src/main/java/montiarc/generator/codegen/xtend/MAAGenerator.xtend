/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend

import arcbasis._symboltable.ComponentTypeSymbol
import montiarc.generator.codegen.xtend.util.AtomicAbstractImplementation
import montiarc.generator.codegen.xtend.util.IMontiArcGenerator
import montiarc.generator.codegen.xtend.util.Identifier
import de.monticore.io.FileReaderWriter
import de.se_rwth.commons.logging.Log
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.ArrayList
import java.util.List

/**
 * Main entry point for generator. From this all target artifacts are generated for a component. 
 * It uses dispatching for calling the right implementation generator.
 */
class MAAGenerator {

  public var ComponentGenerator compGenerator = new ComponentGenerator;
  public var Deploy deploy = new Deploy;
  public var Input inputGenerator = new Input;
  public var Result resultGenerator = new Result;
  public var AtomicAbstractImplementation atomicImpl = new AtomicAbstractImplementation;

  List<IMontiArcGenerator> generators = new ArrayList;

  def void addGenerator(IMontiArcGenerator generator) {
    generators.add(generator);
  }

  def void generateAll(File targetPath, File hwc, ComponentTypeSymbol comp) {
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

    // Generate inner components
    for (innerComp : comp.innerComponents) {
      // TODO Fix hwc path for inner components
      generateAll(targetPath.toPath.resolve(comp.name + "gen").toFile, hwc, innerComp);
    }

    // Generate deploy class
    if (comp.getPorts().isEmpty()) {
      toFile(targetPath, deploy.getArtifactName(comp), deploy.generate(comp), deploy.fileEnding);
    }
  }

  def static private toFile(File targetPath, String name, String content, String fileEnding) {
    var Path path = Paths.get(targetPath.absolutePath + File.separator + name + "." + fileEnding)
    Log.info("Writing to file " + path + ".", "MAAGenerator");
    FileReaderWriter.storeInFile(path, content)
  }
}