/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.cd2pojo.POJOGeneratorTool;
import de.monticore.symboltable.IScope;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._symboltable.IMontiArcScope;
import montiarc._symboltable.MontiArcGlobalScope;
import montiarc.generator.codegen.xtend.MAAGenerator;
import montiarc.util.DirectoryUtil;
import montiarc.util.Modelfinder;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Extends {@link MontiArcTool} with generate capabilities.
 */
public class MontiArcGeneratorTool extends MontiArcTool {

  public static final String LIBRARY_MODELS_FOLDER = "target/librarymodels/";

  private MAAGenerator instance;

  /**
   * @return instance
   */
  public MAAGenerator getInstance() {
    if (instance == null) {
      instance = new MAAGenerator();
    }
    return instance;
  }

  /**
   * @param instance the instance to set
   */
  public void setInstance(MAAGenerator instance) {
    this.instance = instance;
  }

  /**
   * Parses and checks cocos for all MontiArc models in the provided model path.
   * If the models are well formed, generates target code for these.
   *
   * @param modelPath Path where MontiArc models are located.
   * @param target    Path the code should be generated to.
   * @param hwcPath   Path where handwritten component implementations are
   *                  located.
   */
  public void generate(@NotNull Path modelPath, @NotNull Path target, @NotNull Path hwcPath) {
    Preconditions.checkArgument(modelPath != null);
    Preconditions.checkArgument(target != null);
    Preconditions.checkArgument(hwcPath != null);
    MontiArcGlobalScope scope = processModels(modelPath);
    if (Log.getErrorCount() > 0) {
      return;
    }
    scope.getSubScopes().stream().flatMap(s -> s.getSubScopes().stream()).map(IScope::getSpanningSymbol).filter(s -> s instanceof ComponentTypeSymbol).forEach(c -> getInstance().generateAll(Paths.get(target.toString(), Names.getPathFromPackage(c.getPackageName())).toFile(), hwcPath.toFile(), (ComponentTypeSymbol) c));
    this.generatePOJOs(modelPath.toFile(), target.toFile());
  }

  /**
   * Checks cocos and generates code for all MontiArc models in modelpath to
   * folder target.
   *
   * @param modelPath Path where MontiArc models are located.
   * @param target Path the code should be generated to.
   * @param hwcPath Path where handwritten component implementations are
   *          located.
   */
  @Deprecated
  public void generate(File modelPath, File target, File hwcPath) {
    List<String> foundModels = Modelfinder.getModelsInModelPath(modelPath, "arc");

    // 1. create symboltable
    Log.info("Initializing symboltable", "MontiArcGeneratorTool");
    String basedir = DirectoryUtil.getBasedirFromModelAndTargetPath(modelPath.getAbsolutePath(), target.getAbsolutePath());
    IMontiArcScope symTab = initSymbolTable(modelPath, Paths.get(basedir + LIBRARY_MODELS_FOLDER).toFile(),
      hwcPath);

    for (String model : foundModels) {
      String qualifiedModelName = Names.getQualifier(model) + "." + Names.getSimpleName(model);

      // 2. parse + resolve model
      Log.info("Parsing model:" + qualifiedModelName, "MontiArcGeneratorTool");
      ComponentTypeSymbol comp =
        symTab.resolveComponentType(qualifiedModelName).get();

      // 3. check cocos
      Log.info("Check model: " + qualifiedModelName, "MontiArcGeneratorTool");
      checkCoCos(comp.getAstNode());

      // 4. generate
      Log.info("Generate model: " + qualifiedModelName, "MontiArcGeneratorTool");
      getInstance().generateAll(Paths.get(target.getAbsolutePath(), Names.getPathFromPackage(comp.getPackageName())).toFile(), hwcPath, comp);
    }

    // gen cd
    generatePOJOs(modelPath, target);

  }

  private void generatePOJOs(File modelPath, File targetFilepath) {
    Path outDir = Paths.get(targetFilepath.getAbsolutePath());
    new POJOGeneratorTool(outDir, Paths.get(modelPath.getAbsolutePath())).generateAllTypesInPath(modelPath.toPath());
  }
}
