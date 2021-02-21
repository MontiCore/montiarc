/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisScope;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.IScope;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTool;
import montiarc._symboltable.IMontiArcGlobalScope;
import montiarc.generator.codegen.xtend.MAAGenerator;
import montiarc.util.DirectoryUtil;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * Extends {@link MontiArcTool} with generate capabilities.
 */
public class MontiArcGeneratorTool extends MontiArcTool {
  
  public static final String LIBRARY_MODELS_FOLDER = "target/librarymodels/";
  
  private MAAGenerator generator;
  
  /**
   * @return the current generator instance
   */
  public MAAGenerator getGenerator() {
    if (generator == null) {
      generator = new MAAGenerator();
    }
    return generator;
  }
  
  /**
   * @param generator the generator instance to set
   */
  public void setGenerator(MAAGenerator generator) {
    this.generator = generator;
  }
  
  /**
   * Parses and checks cocos for all MontiArc models in the provided model path. If the models are well formed,
   * generates target code for these.
   *
   * @param modelPath Path where MontiArc models are located.
   * @param target    Path the code should be generated to.
   * @param hwcPath   Path where handwritten component implementations are located.
   */
  public void generate(@NotNull Path modelPath, @NotNull Path target, @NotNull Path hwcPath) {
    Preconditions.checkArgument(modelPath != null);
    Preconditions.checkArgument(target != null);
    Preconditions.checkArgument(hwcPath != null);
    IMontiArcGlobalScope globalScope = createMAGlobalScope(modelPath, target, hwcPath);
    globalScope.add(MontiArcMill.typeSymbolBuilder().setName("Integer").setPackageName("java.lang").build());
    processModels(globalScope);
    if (Log.getErrorCount() > 0) {
      return;
    }
    globalScope.getSubScopes().stream()
      .flatMap(artifactScope -> artifactScope.getSubScopes().stream()).map(IScope::getSpanningSymbol)
      .filter(scopeSpanningSymbol -> scopeSpanningSymbol instanceof ComponentTypeSymbol)
      .forEach(componentTypeSymbol -> getGenerator().generateAll(
        Paths.get(
          target.toString(), Names.getPathFromPackage(componentTypeSymbol.getPackageName())).toFile(),
        hwcPath.toFile(),
        (ComponentTypeSymbol) componentTypeSymbol));
  }
  
  /**
   * Checks cocos and generates code for all MontiArc models in modelpath to folder target.
   *
   * @param modelPath Path where MontiArc models are located.
   * @param target    Path the code should be generated to.
   * @param hwcPath   Path where handwritten component implementations are located.
   */
  @Deprecated
  public void generate(File modelPath, File target, File hwcPath) {
    // 1. create symboltable
    Log.info("Initializing symboltable", "MontiArcGeneratorTool");
    String basedir = DirectoryUtil.getBasedirFromModelAndTargetPath(modelPath.getAbsolutePath(),
      target.getAbsolutePath());
    IMontiArcGlobalScope symTab = this.processModels(modelPath.toPath(), Paths.get(basedir + LIBRARY_MODELS_FOLDER),
      hwcPath.toPath(), target.toPath());
    
    symTab.getSubScopes().stream().map(IArcBasisScope::getLocalComponentTypeSymbols).flatMap(Collection::stream)
      .forEach(comp -> this.getGenerator()
        .generateAll(Paths.get(target.getAbsolutePath(), Names.getPathFromPackage(comp.getPackageName())).toFile(),
          hwcPath, comp));
  }
}
