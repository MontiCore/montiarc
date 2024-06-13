/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.IMontiArcArtifactScope;
import montiarc.generator.codegen.MA2JSimGen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/***
 * A MA2JSIM Tool which takes models as strings rather than files. Use @see APIContext
 */
public class MA2JSimApi {
  private APIContext context;
  // Flags
  private boolean useC2MC;
  private boolean fuse;

  private MontiArcTool tool;
  private MA2JSimGen generator;


  // Stuff for generation
  private static List<Finding> logs;
  private Collection<IMontiArcArtifactScope> scopes;
  private ArrayList<ASTMACompilationUnit> asts;

  public MA2JSimApi (APIContext context){
    Preconditions.checkNotNull(context);
    this.context = context;

    asts = new ArrayList<>();
    tool = new MontiArcTool();
    logs = new ArrayList<>();
    Log.enableFailQuick(false);
  }

  /***
   * Starts the generation process. In case of no models, it returns the empty log.
   * Otherwise returns the log of either a successfully or unsuccessful parsing and generating Log
   * @return List of all Findings during the process.
   * @throws IOException
   */
  public List<Finding> runGeneration() throws IOException {
    if(context.getModelList().isEmpty()) return Log.getFindings();

    // initialization of the MontiArcTool
    tool.init();

    // Global Scope init
    if(!context.getSymFiles().isEmpty()){
      // here add symfiles in
    }else {
      tool.initGlobalScope();
    }

    tool.initializeBasicTypes();
    tool.initializeTickEvent();

    if(useC2MC){
      tool.initializeClass2MC();
    }
    return runTasks();
  }

  /***
   * internal method for handling the java file creation
   * @return Returns the current Findings
   * @throws IOException
   */
  private List<Finding> runTasks() throws IOException {
    Log.info("Parse the input models", "MontiArcTool");
    Log.enableFailQuick(false);
    // parsing the input models
    for(String model: context.getModelList()){
      if(!model.isEmpty()){
        Optional<ASTMACompilationUnit> potential = MontiArcMill.parser().parse_StringMACompilationUnit(model);
        potential.ifPresent(astmaCompilationUnit -> asts.add(astmaCompilationUnit));
      }
    }
    // Class to MontiCore
    if (useC2MC) {
      tool.defaultImportTrafo(asts);
    }

    // running tasks (copy-pasted code)
    tool.runAfterParsingTrafos(asts);

    scopes = tool.createSymbolTable(asts);

    tool.runSymbolTablePhase2(asts);
    tool.runSymbolTablePhase3(asts);
    tool.runAfterSymbolTablePhase3Trafos(asts);
    tool.runDefaultCoCos(asts);
    tool.runAdditionalCoCos(asts);

    // cancels build process if errors are already present
    if(Log.getErrorCount() != 0){
      return Log.getFindings();
    }

    // ToDo Add HWC
    /* if(!hwc.isEmpty()){

    }
    */

    // starting generation process, feeding one ast at a time to the generator; Nom Nom Nom...
    for(ASTMACompilationUnit ast : asts){
      generator = new MA2JSimGen(Paths.get("build","generated","montiarc","test","java","API"), new ArrayList<Path>());
      generator.generate(ast);
    }

    // Setting fuse to true, generation has happend
    fuse = true;

    return Log.getFindings();
  }

  /***
   *
   * @return Returns all currently parsed files in a list
   * @throws IOException
   */
  public List<String> getGenerated () throws IOException {
    Log.enableFailQuick(false);
    if (!fuse) return new ArrayList<>();
    List<String> files = Stream.of(new File(Path.of("build","generated","montiarc","test","java","API").toString()).listFiles())
            .filter(File::isFile)
            .map(File::getName)
            .collect(Collectors.toList());
    List<String> gen = new ArrayList<>();

    for(String file: files){
      gen.add(Files.readString(Path.of("build","generated","montiarc","test","java","API", file)));
    }
    return gen;
  }
}