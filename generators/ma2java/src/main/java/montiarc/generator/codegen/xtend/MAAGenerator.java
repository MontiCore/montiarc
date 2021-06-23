/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import montiarc.generator.codegen.xtend.util.AtomicAbstractImplementation;
import montiarc.generator.codegen.xtend.util.IMontiArcGenerator;
import montiarc.generator.codegen.xtend.util.Identifier;

/**
 * Main entry point for generator. From this all target artifacts are generated for a component.
 * It uses dispatching for calling the right implementation generator.
 */

public class MAAGenerator {
  public ComponentGenerator compGenerator = new ComponentGenerator();
  
  public Deploy deploy = new Deploy();
  
  public Input inputGenerator = new Input();
  
  public Result resultGenerator = new Result();
  
  public AtomicAbstractImplementation atomicImpl = new AtomicAbstractImplementation();
  
  private final List<IMontiArcGenerator> generators = new ArrayList<>();
  
  public void addGenerator(final IMontiArcGenerator generator) {
    this.generators.add(generator);
  }

  /**
   * generates all java classes necessary to simulate this component
   * @param targetPath output directory
   * @param hwc unused variable
   * @param component type to generate
   */
  public void generateAll(final File targetPath, final File hwc, final ComponentTypeSymbol component) {
    Identifier.createInstance(component);
    List<IMontiArcGenerator> tmp = new ArrayList<>(this.generators);
    tmp.add(this.inputGenerator);
    tmp.add(this.resultGenerator);
    tmp.add(this.compGenerator);
    // generate input, result, component etc.
    for (final IMontiArcGenerator generator : tmp) {
      final String code = generator.generate(component);
      if ((code != null && !code.isEmpty())) {
        MAAGenerator.toFile(targetPath, generator.getArtifactName(component), code, generator.getFileEnding());
      }
    }
    // recursively generate all subcomponents
    List<ComponentTypeSymbol> innerComponents = component.getInnerComponents();
    for (final ComponentTypeSymbol innerComp : innerComponents) {
      this.generateAll(targetPath.toPath().resolve(component.getName()+"gen").toFile(), hwc, innerComp);
    }
    // generate deploy
    if (component.getPorts().isEmpty()) {
      MAAGenerator.toFile(targetPath, this.deploy.getArtifactName(component), this.deploy.generate(component), this.deploy.getFileEnding());
    }
  }

  /**
   * writes a string to a file
   * @param targetPath coordinates to the target file
   * @param name coordinates to the target file
   * @param content text to ouput
   * @param fileEnding coordinates to the target file
   */
  private static void toFile(final File targetPath, final String name, final String content, final String fileEnding) {
    String absolutePath = targetPath.getAbsolutePath();
    Path path = Paths.get(absolutePath, name + "." + fileEnding);
    Log.info("Writing to file " + path + ".", "MAAGenerator");
    FileReaderWriter.storeInFile(path, content);
  }
}