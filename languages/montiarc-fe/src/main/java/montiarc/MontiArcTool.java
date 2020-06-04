/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import arcbasis._ast.ASTArcBasisNode;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._parser.MontiArcParser;
import montiarc._symboltable.IMontiArcScope;
import montiarc._symboltable.MontiArcGlobalScope;
import montiarc._symboltable.MontiArcLanguage;
import montiarc.cocos.MontiArcCoCos;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

public class MontiArcTool {

  protected MontiArcLanguage language;

  protected MontiArcCoCoChecker checker;

  protected boolean isSymTabInitialized;
  
  public MontiArcTool() {
    this(MontiArcCoCos.createChecker(), new MontiArcLanguage("MontiArc", ".arc"));
  }

  public MontiArcTool(@NotNull MontiArcCoCoChecker checker, MontiArcLanguage language) {
    Preconditions.checkArgument(checker != null);
    this.checker = checker;
    this.isSymTabInitialized = false;
  }
  
  public Optional<ASTMACompilationUnit> parse(@NotNull String filename) {
    Preconditions.checkArgument(filename != null);
    MontiArcParser p = new MontiArcParser();
    Optional<ASTMACompilationUnit> compUnit;
    try {
      compUnit = p.parse(filename);
      return compUnit;
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return Optional.empty();
    
  }

  public boolean checkCoCos(@NotNull ASTArcBasisNode node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.isSymTabInitialized, "Please initialize symbol-table before "
      + "checking cocos.");
    this.checker.checkAll(node);
    if (Log.getErrorCount() != 0) {
      Log.debug("Found " + Log.getErrorCount() + " errors in node " + node + ".", "XX");
      return false;
    }
    return true;
  }
  
  /**
   * Loads a ComponentSymbol with the passed componentName. The componentName is the full qualified
   * name of the component model. Modelpaths are folders relative to the project path and containing
   * the packages the models are located in. When the ComponentSymbol is resolvable it is returned.
   * Otherwise the optional is empty.
   * 
   * @param componentName Name of the component
   * @param modelPaths Folders containing the packages with models
   * @return an {@code Optional} of the loaded component type
   */
  public Optional<ComponentTypeSymbol> loadComponentSymbolWithoutCocos(String componentName,
      File... modelPaths) {
    IMontiArcScope s = initSymbolTable(modelPaths);
    return s.resolveComponentType(componentName);
  }
  
  public Optional<ComponentTypeSymbol> loadComponentSymbolWithCocos(String componentName,
      File... modelPaths) {
    Optional<ComponentTypeSymbol> compSym = loadComponentSymbolWithoutCocos(componentName, modelPaths);

    compSym.ifPresent(componentTypeSymbol -> this.checkCoCos(componentTypeSymbol.getAstNode()));
    
    return compSym;
  }
  
  /**
   * Loads the AST of the passed model with name componentName. The componentName is the fully
   * qualified. Modelpaths are folders relative to the project path and containing the packages the
   * models are located in. When the ComponentSymbol is resolvable it is returned. Otherwise the
   * optional is empty.
   * 
   * @param modelPath The model path containing the package with the model
   * @param model the fully qualified model name
   * @return the AST node of the model
   */
  public Optional<ASTComponentType> getAstNode(String modelPath, String model) {
    // ensure an empty log
    Log.getFindings().clear();
    Optional<ComponentTypeSymbol> comp = loadComponentSymbolWithoutCocos(model,
        Paths.get(modelPath).toFile());
    
    if (!comp.isPresent()) {
      Log.error("Model could not be resolved!");
      return Optional.empty();
    }

    if(!comp.get().isPresentAstNode()){
      Log.debug("Symbol not linked with node.", "XX");
      return Optional.empty();
    }
    return Optional.of(comp.get().getAstNode());
  }
  
  /**
   * Initializes the Symboltable by introducing scopes for the passed modelpaths. It does not create
   * the symbol table! Symbols for models within the modelpaths are not added to the symboltable
   * until resolve() is called. Modelpaths are relative to the project path and do contain all the
   * packages the models are located in. E.g. if model with fqn a.b.C lies in folder
   * src/main/resources/models/a/b/C.arc, the modelpath is src/main/resources.
   * 
   * @param modelPaths paths of all folders containing models
   * @return The initialized symbol table
   */
  public IMontiArcScope initSymbolTable(File... modelPaths) {
    Set<Path> p = Sets.newHashSet();
    for (File mP : modelPaths) {
      p.add(Paths.get(mP.getAbsolutePath()));
    }
    
    final ModelPath mp = new ModelPath(p);

    MontiArcGlobalScope gs = new MontiArcGlobalScope(mp, language);
    isSymTabInitialized = true;
    return gs;
  }
  
  /**
   * Initializes the Symboltable by introducing scopes for the passed modelpaths. It does not create
   * the symbol table! Symbols for models within the modelpaths are not added to the symboltable
   * until resolve() is called. Modelpaths are relative to the project path and do contain all the
   * packages the models are located in. E.g. if model with fqn a.b.C lies in folder
   * src/main/resources/models/a/b/C.arc, the modelPath is src/main/resources.
   * 
   * @param modelPath The model path for the symbol table
   * @return the initialized symbol table
   */
  public IMontiArcScope initSymbolTable(String modelPath) {
    return initSymbolTable(Paths.get(modelPath).toFile());
  }
  
}
