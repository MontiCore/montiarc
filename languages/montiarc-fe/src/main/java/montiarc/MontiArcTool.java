/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import arcbasis._ast.ASTArcBasisNode;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeGlobalScope;
import de.monticore.io.paths.ModelPath;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._parser.MontiArcParser;
import montiarc._symboltable.IMontiArcScope;
import montiarc._symboltable.MontiArcArtifactScope;
import montiarc._symboltable.MontiArcGlobalScope;
import montiarc._symboltable.MontiArcSymbolTableCreatorDelegator;
import montiarc._symboltable.adapters.Field2CDFieldResolvingDelegate;
import montiarc._symboltable.adapters.Type2CDTypeResolvingDelegate;
import montiarc.cocos.MontiArcCoCos;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class MontiArcTool {

  protected MontiArcCoCoChecker checker;
  protected boolean isSymTabInitialized;
  protected String fileExtension = "arc";

  public MontiArcTool() {
    this(MontiArcCoCos.createChecker());
  }

  public MontiArcTool(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkArgument(checker != null);
    this.checker = checker;
    this.isSymTabInitialized = false;
  }

  protected String getFileExtension() {
    return fileExtension;
  }

  protected MontiArcCoCoChecker getChecker() {
    return this.checker;
  }

  public MontiArcGlobalScope processModels(@NotNull Path... modelPaths) {
    Preconditions.checkArgument(modelPaths != null);
    Preconditions.checkArgument(!Arrays.asList(modelPaths).contains(null));
    MontiArcGlobalScope scope = createGlobalScope(new ModelPath(Arrays.asList(modelPaths)));
    this.processModels(scope);
    return scope;
  }

  public MontiArcGlobalScope createGlobalScope(@NotNull ModelPath mp) {
    CD4CodeGlobalScope cd4CGlobalScope = CD4CodeMill.cD4CodeGlobalScopeBuilder().setModelPath(mp).build();

    Field2CDFieldResolvingDelegate fieldDelegate = new Field2CDFieldResolvingDelegate(cd4CGlobalScope);
    Type2CDTypeResolvingDelegate typeDelegate = new Type2CDTypeResolvingDelegate(cd4CGlobalScope);

    MontiArcGlobalScope montiArcGlobalScope = new MontiArcGlobalScope(mp, fileExtension);
    montiArcGlobalScope.addAdaptedFieldSymbolResolvingDelegate(fieldDelegate);
    montiArcGlobalScope.addAdaptedOOTypeSymbolResolvingDelegate(typeDelegate);
    addBasicTypes(montiArcGlobalScope);
    return montiArcGlobalScope;
  }

  public void processModels(@NotNull MontiArcGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    this.createSymbolTable(scope).stream().map(as -> (ASTMACompilationUnit) as.getAstNode()).forEach(a -> a.accept(this.getChecker()));
  }

  public Collection<MontiArcArtifactScope> createSymbolTable(@NotNull MontiArcGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    MontiArcSymbolTableCreatorDelegator symTab = MontiArcMill.montiArcSymbolTableCreatorDelegatorBuilder().setGlobalScope(scope).build();
    return this.parseModels(scope).stream().map(symTab::createFromAST).collect(Collectors.toSet());
  }

  public Collection<ASTMACompilationUnit> parseModels(@NotNull MontiArcGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    return scope.getModelPath().getFullPathOfEntries().stream().flatMap(p -> parse(p).stream()).collect(Collectors.toSet());
  }

  public Collection<ASTMACompilationUnit> parse(@NotNull Path path) {
    Preconditions.checkArgument(path != null);
    try {
      return Files.walk(path).filter(Files::isRegularFile).filter(f -> f.getFileName().toString().endsWith(this.getFileExtension())).map(f -> parse(f.toString()))
        .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
    } catch (IOException e) {
      Log.error("Could not access " + path.toString() + ", there were I/O exceptions.");
    }
    return new HashSet<>();
  }

  public Optional<ASTMACompilationUnit> parse(@NotNull String filename) {
    Preconditions.checkArgument(filename != null);
    MontiArcParser p = new MontiArcParser();
    Optional<ASTMACompilationUnit> compUnit;
    try {
      compUnit = p.parse(filename);
      return compUnit;
    } catch (IOException e) {
      Log.error("Could not access " + filename + ", there were I/O exceptions.");
    }
    return Optional.empty();
  }

  @Deprecated
  public boolean checkCoCos(@NotNull ASTArcBasisNode node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.isSymTabInitialized, "Please initialize symbol-table before "
      + "checking cocos.");
    this.getChecker().checkAll(node);
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
   * @param modelPaths    Folders containing the packages with models
   * @return an {@code Optional} of the loaded component type
   */
  @Deprecated
  public Optional<ComponentTypeSymbol> loadComponentSymbolWithoutCocos(String componentName,
                                                                       File... modelPaths) {
    IMontiArcScope s = initSymbolTable(modelPaths);
    return s.resolveComponentType(componentName);
  }

  @Deprecated
  public Optional<ComponentTypeSymbol> loadComponentSymbolWithCocos(String componentName,
                                                                    File... modelPaths) {
    Optional<ComponentTypeSymbol> compSym = loadComponentSymbolWithoutCocos(componentName,
      modelPaths);

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
   * @param model     the fully qualified model name
   * @return the AST node of the model
   */
  @Deprecated
  public Optional<ASTComponentType> getAstNode(String modelPath, String model) {
    // ensure an empty log
    Log.getFindings().clear();
    Optional<ComponentTypeSymbol> comp = loadComponentSymbolWithoutCocos(model,
      Paths.get(modelPath).toFile());

    if (!comp.isPresent()) {
      Log.error("Model could not be resolved!");
      return Optional.empty();
    }

    if (!comp.get().isPresentAstNode()) {
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
  @Deprecated
  public IMontiArcScope initSymbolTable(File... modelPaths) {
    Set<Path> p = Sets.newHashSet();
    for (File mP : modelPaths) {
      p.add(Paths.get(mP.getAbsolutePath()));
    }
    final ModelPath mp = new ModelPath(p);
    MontiArcGlobalScope montiArcGlobalScope = this.createGlobalScope(mp);
    isSymTabInitialized = true;
    return montiArcGlobalScope;
  }

  public IMontiArcScope addBasicTypes(@NotNull IMontiArcScope scope) {
    scope.add(new OOTypeSymbol("String"));
    scope.add(new OOTypeSymbol("Integer"));
    scope.add(new OOTypeSymbol("Map"));
    scope.add(new OOTypeSymbol("Set"));
    scope.add(new OOTypeSymbol("List"));
    scope.add(new OOTypeSymbol("Boolean"));
    scope.add(new OOTypeSymbol("Character"));
    scope.add(new OOTypeSymbol("Double"));
    scope.add(new OOTypeSymbol("Float"));

    //primitives
    scope.add(new OOTypeSymbol("int"));
    scope.add(new OOTypeSymbol("boolean"));
    scope.add(new OOTypeSymbol("float"));
    scope.add(new OOTypeSymbol("double"));
    scope.add(new OOTypeSymbol("char"));
    scope.add(new OOTypeSymbol("long"));
    scope.add(new OOTypeSymbol("short"));
    scope.add(new OOTypeSymbol("byte"));

    return scope;
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
  @Deprecated
  public IMontiArcScope initSymbolTable(String modelPath) {
    return initSymbolTable(Paths.get(modelPath).toFile());
  }
}
