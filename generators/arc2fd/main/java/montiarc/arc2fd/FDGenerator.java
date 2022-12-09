/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.arc2fd.fd.FDConfiguration;
import montiarc.arc2fd.fd.FDConstructionStorage;
import montiarc.arc2fd.fd.MAExtractionHelper;
import org.sosy_lab.java_smt.api.BooleanFormula;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FDGenerator {

  private static final String CONVERTED_FOLDER_EXTENSION = "Converted";

  /**
   * Main Function for Converting .arc-Files
   *
   * @param args First Argument: Model Path where we should look out for
   *             .arc-Files
   */
  public static void main(String[] args) {
    Log.init();
    Log.enableFailQuick(false);

    // Get the model path (to find the correct MontiArc model files)
    List<Path> modelPath =
      Arrays.stream(args[0].split(",\\s+")).map(Path::of).collect(Collectors.toList());
    Path relevantPath = modelPath.get(0);
    List<Path> outputPath =
      Arrays.stream(args[1].split(",\\s+")).map(Path::of).collect(Collectors.toList());
    Path outputDir = outputPath.get(0);

    // Get ASTs
    MAExtractionHelper<BooleanFormula> maExtractionHelper =
      new MAExtractionHelper<>();
    Optional<List<MAExtractionHelper.AST2FDResult<BooleanFormula>>> results =
      maExtractionHelper.processASTsFromFile(relevantPath);

    // Based on the FDConstructionStorage, export the Model
    results.ifPresent(ast2fdResult -> ast2fdResult.forEach(res ->
      generateFile(outputDir, res.ast, res.storage)));

    Log.enableFailQuick(true);
  }

  public static Path getOutputDir(Path inputDir) {
    Path realPath = inputDir;

    // Check that we have a directory and not a folder => Otherwise use the
    // parent directory
    if (!Files.exists(realPath)) {
      try {
        Files.createDirectories(realPath);
      } catch (
        IOException e) {
        throw new RuntimeException(e);
      }
    }
    if (!Files.isDirectory(realPath))
      realPath = inputDir.getParent();
    return Path.of(String.valueOf(realPath), CONVERTED_FOLDER_EXTENSION);
  }


  /**
   * Generates a File out of the given parameters.
   *
   * @param inputDir (Folder-)Path where the file comes from
   * @param ast      AST we're currently processing
   * @param storage  Construction Storage which contains all the relevant
   *                 information for generating the final
   *                 file with a FreeMarker Template.
   */
  private static void generateFile(Path inputDir, ASTMACompilationUnit ast,
                                   FDConstructionStorage<BooleanFormula> storage) {
    Path output = getOutputDir(inputDir);

    // Create a Generator Engine
    GeneratorEngine g = new GeneratorEngine(getGeneratorSetup(output));

    FDConfiguration fdConfig = new FDConfiguration();
    Path fileName = Paths.get(ast.getComponentType().getName() + ".fd");

    g.generate("templates/FDGenerationTemplate.ftl", fileName, ast, storage,
      fdConfig);
  }

  /**
   * Create the desired Generator Setup
   *
   * @param outputDir Director where we want "to generate to"
   * @return GeneratorSetup
   */
  private static GeneratorSetup getGeneratorSetup(Path outputDir) {
    GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(outputDir.toFile());
    setup.setCommentStart("/*");
    setup.setCommentEnd("*/");
    setup.setTracing(true);
    setup.setDefaultFileExtension(".fd");
    return setup;
  }
}
