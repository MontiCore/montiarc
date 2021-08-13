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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Extends {@link MontiArcTool} with generate capabilities.
 */
public class MontiArcGeneratorTool extends MontiArcTool {

  public static final String LIBRARY_MODELS_FOLDER = "target/librarymodels/";

  private MAAGenerator generator;

  public static void main(@NotNull String[] args) {
    Preconditions.checkNotNull(args);
    Preconditions.checkArgument(args.length >= 3, "Argument size was " + args.length
      + " but the code generator expects at least three arguments: model path, target path, and hwc path;"
      + " but argument was " + args[0]);
      new MontiArcGeneratorTool().generate(
        Arrays.stream(args[0].split(",\\s+")).map(File::new).collect(Collectors.toList()),
        new File(args[1]),
        Arrays.stream(args[2].split(",\\s+")).map(File::new).collect(Collectors.toList()));
  }

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
   * @param models Path where MontiArc models are located.
   * @param output Path the code should be generated to.
   * @param hwc     Path where handwritten component implementations are located.
   */
  public void generate(@NotNull List<File> models, @NotNull File output, @NotNull List<File> hwc) {
    Preconditions.checkNotNull(output);
    Preconditions.checkNotNull(models);
    Preconditions.checkNotNull(hwc);
    Path target = output.toPath();
    MontiArcMill.init();
    IMontiArcGlobalScope globalScope = createMAGlobalScope(Stream.of(models, hwc, Collections.singletonList(output)).flatMap(Collection::stream).peek(File::mkdirs).map(File::toPath).toArray(Path[]::new));
    this.initializeBasicTypes();
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
        null, // unused parameter?
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
