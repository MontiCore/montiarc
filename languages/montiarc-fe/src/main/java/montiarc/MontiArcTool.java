/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import com.google.common.base.Preconditions;
<<<<<<< HEAD
import com.google.common.collect.Sets;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.io.paths.ModelPath;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
=======
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCreatorDelegator;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.ICD4CodeScope;
import de.monticore.cd4code.cocos.CD4CodeCoCos;
import de.monticore.cd4code.resolver.CD4CodeResolver;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.io.paths.ModelPath;
import de.monticore.types.check.DefsTypeBasic;
>>>>>>> bb276d4fcc3784a5352ae1a8711ede81331f4772
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._parser.MontiArcParser;
import montiarc._symboltable.IMontiArcGlobalScope;
import montiarc._symboltable.IMontiArcScope;
<<<<<<< HEAD
import montiarc._symboltable.MontiArcGlobalScope;
import montiarc._symboltable.adapters.Field2CDFieldResolvingDelegate;
import montiarc._symboltable.adapters.Type2CDTypeResolvingDelegate;
=======
import montiarc._symboltable.MontiArcSymbolTableCreatorDelegator;
>>>>>>> bb276d4fcc3784a5352ae1a8711ede81331f4772
import montiarc.cocos.MontiArcCoCos;
import montiarc.util.MontiArcError;
import org.apache.commons.io.FilenameUtils;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MontiArcTool implements IMontiArcTool {
  protected MontiArcCoCoChecker maChecker;
  protected CD4CodeCoCoChecker cdChecker;
  protected MontiArcParser maParser;
  protected CD4CodeParser cdParser;
  protected String maFileExtension = "arc";
  protected String cdFileExtension = "cd";

<<<<<<< HEAD
  protected MontiArcCoCoChecker checker;
=======
  public MontiArcTool() {
    this(MontiArcCoCos.createChecker(), new CD4CodeCoCos().createNewChecker());
  }

  public MontiArcTool(@NotNull MontiArcCoCoChecker maChecker, @NotNull CD4CodeCoCoChecker cdChecker) {
    this(maChecker, cdChecker, new MontiArcParser(), new CD4CodeParser());
  }
>>>>>>> bb276d4fcc3784a5352ae1a8711ede81331f4772

  protected MontiArcTool(@NotNull MontiArcCoCoChecker maChecker, @NotNull CD4CodeCoCoChecker cdChecker,
                         @NotNull MontiArcParser maParser, @NotNull CD4CodeParser cdParser) {
    Preconditions.checkArgument(maChecker != null);
    Preconditions.checkArgument(cdChecker != null);
    this.maParser = maParser;
    this.cdParser = cdParser;
    this.maChecker = maChecker;
    this.cdChecker = cdChecker;
  }

<<<<<<< HEAD
  public MontiArcTool() {
    this(MontiArcCoCos.createChecker());
  }

  public MontiArcTool(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkArgument(checker != null);
    this.checker = checker;
    this.isSymTabInitialized = false;
=======
  protected String getMAFileExtension() {
    return this.maFileExtension;
  }

  protected String getCDFileExtension() {
    return this.cdFileExtension;
>>>>>>> bb276d4fcc3784a5352ae1a8711ede81331f4772
  }

  protected MontiArcCoCoChecker getMAChecker() {
    return this.maChecker;
  }

  protected CD4CodeCoCoChecker getCDChecker() {
    return this.cdChecker;
  }

  protected MontiArcParser getMAParser() {
    return this.maParser;
  }

  protected CD4CodeParser getCDParser() {
    return this.cdParser;
  }

  @Override
  public Optional<ASTMACompilationUnit> parseMAModel(@NotNull Path file) {
    Preconditions.checkArgument(file != null);
    Preconditions.checkArgument(file.toFile().exists());
    Preconditions.checkArgument(file.toFile().isFile());
    Preconditions.checkArgument(FilenameUtils.getExtension(file.getFileName().toString()).equals(this.getMAFileExtension()));
    try {
      return this.getMAParser().parse(file.toString());
    } catch (IOException e) {
      Log.error(String.format(MontiArcError.TOOL_PARSE_IOEXCEPTION.toString(), file.toString()), e);
    }
    return Optional.empty();
  }

  @Override
  public Optional<ASTCDCompilationUnit> parseCDModel(@NotNull Path file) {
    Preconditions.checkArgument(file != null);
    Preconditions.checkArgument(file.toFile().exists());
    Preconditions.checkArgument(file.toFile().isFile());
    Preconditions.checkArgument(FilenameUtils.getExtension(file.getFileName().toString()).equals(this.getCDFileExtension()));
    try {
      return this.getCDParser().parse(file.toString());
    } catch (IOException e) {
      Log.error(String.format(MontiArcError.TOOL_PARSE_IOEXCEPTION.toString(), file.toString()), e);
    }
    return Optional.empty();
  }

  @Override
  public Optional<ASTMACompilationUnit> parseMAModel(@NotNull String filename) {
    Preconditions.checkArgument(filename != null);
    return this.parseMAModel(Paths.get(filename));
  }

  @Override
  public Optional<ASTCDCompilationUnit> parseCDModel(@NotNull String filename) {
    Preconditions.checkArgument(filename != null);
    return this.parseCDModel(Paths.get(filename));
  }

  @Override
  public Collection<ASTMACompilationUnit> parseMAModels(@NotNull Path directory) {
    Preconditions.checkArgument(directory != null);
    Preconditions.checkArgument(directory.toFile().exists());
    Preconditions.checkArgument(directory.toFile().isDirectory());
    try (Stream<Path> paths = Files.walk(directory)) {
      return paths.filter(Files::isRegularFile)
        .filter(file -> file.getFileName().toString().endsWith(this.getMAFileExtension())).map(this::parseMAModel)
        .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
    } catch (IOException e) {
      Log.error(String.format(MontiArcError.TOOL_FILE_WALK_IOEXCEPTION.toString(), directory.toString()), e);
    }
    return Collections.emptySet();
  }

  @Override
  public Collection<ASTCDCompilationUnit> parseCDModels(@NotNull Path directory) {
    Preconditions.checkArgument(directory != null);
    Preconditions.checkArgument(directory.toFile().exists());
    Preconditions.checkArgument(directory.toFile().isDirectory());
    try (Stream<Path> paths = Files.walk(directory)) {
      return paths.filter(Files::isRegularFile)
        .filter(file -> file.getFileName().toString().endsWith(this.getCDFileExtension())).map(this::parseCDModel)
        .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
    } catch (IOException e) {
      Log.error(String.format(MontiArcError.TOOL_FILE_WALK_IOEXCEPTION.toString(), directory.toString()), e);
    }
    return Collections.emptySet();
  }

  @Override
  public Collection<ASTMACompilationUnit> parseModels(@NotNull IMontiArcGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    return scope.getModelPath().getFullPathOfEntries().stream().flatMap(path -> this.parseMAModels(path).stream()).collect(Collectors.toSet());
  }

  @Override
  public Collection<ASTCDCompilationUnit> parseModels(@NotNull ICD4CodeGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    return scope.getModelPath().getFullPathOfEntries().stream().flatMap(path -> this.parseCDModels(path).stream()).collect(Collectors.toSet());
  }

  @Override
  public IMontiArcScope createSymbolTable(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkArgument(ast != null);
    MontiArcSymbolTableCreatorDelegator symTab = new MontiArcSymbolTableCreatorDelegator();
    return symTab.createFromAST(ast);
  }

<<<<<<< HEAD
    final ModelPath mp = new ModelPath(p);

    CD4AnalysisGlobalScope cd4AGlobalScope = CD4AnalysisMill.cD4AnalysisGlobalScopeBuilder().setModelPath(mp).build();

    Field2CDFieldResolvingDelegate fieldDelegate =
      new Field2CDFieldResolvingDelegate(cd4AGlobalScope);
    Type2CDTypeResolvingDelegate typeDelegate =
      new Type2CDTypeResolvingDelegate(cd4AGlobalScope);

    MontiArcGlobalScope montiArcGlobalScope = new MontiArcGlobalScope(mp, "arc");
    montiArcGlobalScope.addAdaptedFieldSymbolResolvingDelegate(fieldDelegate);
    montiArcGlobalScope.addAdaptedTypeSymbolResolvingDelegate(typeDelegate);
    addBasicTypes(montiArcGlobalScope);
    
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
  public IMontiArcScope initSymbolTable(String modelPath) {
    return initSymbolTable(Paths.get(modelPath).toFile());
  }
}
=======
  @Override
  public ICD4CodeScope createSymbolTable(@NotNull ASTCDCompilationUnit ast) {
    Preconditions.checkArgument(ast != null);
    CD4CodeSymbolTableCreatorDelegator symTab = new CD4CodeSymbolTableCreatorDelegator();
    return symTab.createFromAST(ast);
  }

  @Override
  public Collection<IMontiArcScope> createSymbolTable(@NotNull IMontiArcGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    MontiArcSymbolTableCreatorDelegator symTab = new MontiArcSymbolTableCreatorDelegator(scope);
    return this.parseModels(scope).stream().map(symTab::createFromAST).collect(Collectors.toSet());
  }

  @Override
  public Collection<ICD4CodeScope> createSymbolTable(@NotNull ICD4CodeGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    CD4CodeSymbolTableCreatorDelegator symTab = new CD4CodeSymbolTableCreatorDelegator(scope);
    return this.parseModels(scope).stream().map(symTab::createFromAST).collect(Collectors.toSet());
  }

  @Override
  public IMontiArcGlobalScope createMAGlobalScope(@NotNull Path... directories) {
    Preconditions.checkArgument(directories != null);
    return this.createMAGlobalScope(new ModelPath(directories));
  }

  @Override
  public ICD4CodeGlobalScope createCDGlobalScope(@NotNull Path... directories) {
    Preconditions.checkArgument(directories != null);
    return this.createCDGlobalScope(new ModelPath(directories));
  }

  protected IMontiArcGlobalScope createMAGlobalScope(@NotNull ModelPath modelPath) {
    Preconditions.checkArgument(modelPath != null);
    return this.createMAGlobalScope(this.createCDGlobalScope(modelPath), modelPath);
  }

  protected ICD4CodeGlobalScope createCDGlobalScope(@NotNull ModelPath modelPath) {
    Preconditions.checkArgument(modelPath != null);
    return CD4CodeMill.cD4CodeGlobalScopeBuilder().setModelPath(modelPath)
      .setModelFileExtension(this.getCDFileExtension()).build();
  }

  @Override
  public IMontiArcGlobalScope createMAGlobalScope(@NotNull ICD4CodeGlobalScope cdScope, @NotNull Path... directories) {
    Preconditions.checkArgument(cdScope != null);
    Preconditions.checkArgument(directories != null);
    return this.createMAGlobalScope(cdScope, new ModelPath(directories));
  }

  protected IMontiArcGlobalScope createMAGlobalScope(@NotNull ICD4CodeGlobalScope cdScope,
                                                     @NotNull ModelPath modelPath) {
    Preconditions.checkArgument(cdScope != null);
    Preconditions.checkArgument(modelPath != null);
    IMontiArcGlobalScope maScope = MontiArcMill.montiArcGlobalScopeBuilder().setModelPath(modelPath)
      .setModelFileExtension(this.getMAFileExtension()).build();
    this.resolvingDelegates(maScope, cdScope);
    this.addBasicTypes(maScope);
    return maScope;
  }

  @Override
  public void addBasicTypes(@NotNull IMontiArcScope scope) {
    Preconditions.checkArgument(scope != null);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._boolean);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._char);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._short);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._String);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._int);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._long);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._float);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._double);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._null);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._Object);
    DefsTypeBasic.add2scope(scope, DefsTypeBasic._array);
  }

  @Override
  public void checkCoCos(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkArgument(ast != null);
    ast.accept(this.getMAChecker());
  }

  @Override
  public void checkCoCos(@NotNull ASTCDCompilationUnit ast) {
    Preconditions.checkArgument(ast != null);
    ast.accept(this.getCDChecker());
  }

  public void checkCoCos(@NotNull ASTCDPackage ast) {
    Preconditions.checkArgument(ast != null);
    ast.accept(this.getCDChecker());
  }

  @Override
  public void processModels(@NotNull IMontiArcGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    this.createSymbolTable(scope).stream().map(artifactScope -> (ASTMACompilationUnit) artifactScope.getAstNode())
      .forEach(this::checkCoCos);
  }

  @Override
  public void processModels(@NotNull ICD4CodeGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    this.createSymbolTable(scope).stream().flatMap(artifactScope -> artifactScope.getSubScopes().stream())
      .map(artifactScope -> (ASTCDPackage) artifactScope.getSpanningSymbol().getAstNode())
      .forEach(this::checkCoCos);
  }

  @Override
  public IMontiArcGlobalScope processModels(@NotNull Path... directories) {
    Preconditions.checkArgument(directories != null);
    Preconditions.checkArgument(!Arrays.asList(directories).contains(null));
    ModelPath modelPath = new ModelPath(directories);
    ICD4CodeGlobalScope cdScope = this.createCDGlobalScope(modelPath);
    IMontiArcGlobalScope maScope = this.createMAGlobalScope(cdScope, modelPath);
    this.processModels(cdScope);
    this.processModels(maScope);
    return maScope;
  }

  protected void resolvingDelegates(@NotNull IMontiArcGlobalScope montiArcGlobalScope,
                                    @NotNull ICD4CodeGlobalScope cd4CGlobalScope) {
    CD4CodeResolver cd4CodeResolver = new CD4CodeResolver(cd4CGlobalScope);
    montiArcGlobalScope.addAdaptedFieldSymbolResolver(cd4CodeResolver);
    montiArcGlobalScope.addAdaptedTypeSymbolResolver(cd4CodeResolver);
  }
}
>>>>>>> bb276d4fcc3784a5352ae1a8711ede81331f4772
