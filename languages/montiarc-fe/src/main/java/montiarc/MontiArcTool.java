/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import com.google.common.base.Preconditions;
import de.monticore.io.paths.MCPath;
import de.monticore.io.paths.ModelPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._parser.MontiArcParser;
import montiarc._symboltable.*;
import montiarc._cocos.MontiArcCoCos;
import montiarc.util.MontiArcError;
import org.apache.commons.io.FilenameUtils;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
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

  protected MontiArcParser parser;
  protected MontiArcSymbols2Json deSer;
  protected MontiArcCoCoChecker checker;

  protected String maFileExtension = "arc";
  protected String symFileExtension = "sym";

  public MontiArcTool() {
    this(MontiArcCoCos.createChecker());
  }

  public MontiArcTool(@NotNull MontiArcCoCoChecker checker) {
    this(checker, new MontiArcParser(), new MontiArcSymbols2Json());
  }

  protected MontiArcTool(@NotNull MontiArcCoCoChecker checker, @NotNull MontiArcParser parser,
                         @NotNull MontiArcSymbols2Json deSer) {
    Preconditions.checkArgument(checker != null);
    Preconditions.checkArgument(deSer != null);
    Preconditions.checkArgument(parser != null);
    this.parser = parser;
    this.deSer = deSer;
    this.checker = checker;
  }

  protected MontiArcParser getParser() {
    return this.parser;
  }

  protected MontiArcSymbols2Json getDeSer() {
    return this.deSer;
  }

  protected MontiArcCoCoChecker getChecker() {
    return this.checker;
  }

  protected String getMAFileExtension() {
    return this.maFileExtension;
  }

  protected String getSymFileExtension() {
    return this.symFileExtension;
  }

  @Override
  public Optional<ASTMACompilationUnit> parse(@NotNull Path file) {
    Preconditions.checkArgument(file != null);
    Preconditions.checkArgument(file.toFile().exists(), file.toString());
    Preconditions.checkArgument(file.toFile().isFile(), file.toString());
    Preconditions.checkArgument(FilenameUtils.getExtension(file.getFileName().toString()).equals(this.getMAFileExtension()));
    try {
      return this.getParser().parse(file.toString());
    } catch (IOException e) {
      Log.error(String.format(MontiArcError.TOOL_PARSE_IOEXCEPTION.toString(), file.toString()), e);
    }
    return Optional.empty();
  }

  @Override
  public IMontiArcArtifactScope load(@NotNull Path file) {
    Preconditions.checkArgument(file != null);
    Preconditions.checkArgument(file.toFile().exists(), file.toString());
    Preconditions.checkArgument(file.toFile().isFile(), file.toString());
    Preconditions.checkArgument(FilenameUtils.getExtension(file.getFileName().toString()).equals(this.getSymFileExtension()));
    return this.getDeSer().load(file.toString());
  }

  @Override
  public Optional<ASTMACompilationUnit> parse(@NotNull String filename) {
    Preconditions.checkArgument(filename != null);
    return this.parse(Paths.get(filename));
  }

  @Override
  public IMontiArcArtifactScope load(@NotNull String filename) {
    Preconditions.checkArgument(filename != null);
    return this.load(Paths.get(filename));
  }

  @Override
  public Collection<ASTMACompilationUnit> parseAll(@NotNull Path directory) {
    Preconditions.checkArgument(directory != null);
    Preconditions.checkArgument(directory.toFile().exists());
    Preconditions.checkArgument(directory.toFile().isDirectory());
    try (Stream<Path> paths = Files.walk(directory)) {
      return paths.filter(Files::isRegularFile)
        .filter(file -> file.getFileName().toString().endsWith(this.getMAFileExtension())).map(this::parse)
        .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
    } catch (IOException e) {
      Log.error(String.format(MontiArcError.TOOL_FILE_WALK_IOEXCEPTION.toString(), directory.toString()), e);
    }
    return Collections.emptySet();
  }

  @Override
  public Collection<IMontiArcArtifactScope> loadAll(@NotNull Path directory) {
    Preconditions.checkArgument(directory != null);
    Preconditions.checkArgument(directory.toFile().exists(), directory.toAbsolutePath()+" does not exist");
    Preconditions.checkArgument(directory.toFile().isDirectory(), directory.toAbsolutePath()+" is not a directory");
    try (Stream<Path> paths = Files.walk(directory)) {
      return paths.filter(Files::isRegularFile)
        .filter(file -> file.getFileName().toString().endsWith(this.getSymFileExtension())).map(this::load).collect(Collectors.toSet());
    } catch (IOException e) {
      Log.error(String.format(MontiArcError.TOOL_FILE_WALK_IOEXCEPTION.toString(), directory.toString()), e);
    }
    return Collections.emptySet();
  }

  @Override
  public Collection<ASTMACompilationUnit> parseAll(@NotNull IMontiArcGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    return scope.getModelPath().getFullPathOfEntries().stream().flatMap(path -> this.parseAll(path).stream()).collect(Collectors.toSet());
  }

  @Override
  public Collection<IMontiArcArtifactScope> loadAll(@NotNull IMontiArcGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    return scope.getModelPath().getFullPathOfEntries().stream().flatMap(path -> this.loadAll(path).stream()).collect(Collectors.toSet());
  }

  public Collection<Path> findSymbolFiles(@NotNull Path directory) {
    Preconditions.checkArgument(directory != null);
    Preconditions.checkArgument(directory.toFile().exists(), directory.toAbsolutePath()+" does not exist");
    Preconditions.checkArgument(directory.toFile().isDirectory(), directory.toAbsolutePath()+" is not a directory");

    try (Stream<Path> paths = Files.walk(directory)) {
      return paths.filter(Files::isRegularFile)
          .filter(file -> file.getFileName().toString().endsWith(this.getSymFileExtension()))
          .collect(Collectors.toSet());
    } catch (IOException e) {
      Log.error(String.format(MontiArcError.TOOL_FILE_WALK_IOEXCEPTION.toString(), directory.toString()), e);
    }
    return Collections.emptySet();
  }

  /**
   * Loads all serialized models in the provided scope as deserialized montiarc models. Then the deserializes montiarc
   * models are added to the global scope. The global scope is returned afterwards.
   *
   * @param scope the scope under consideration
   * @return The scope passed as argument.
   */
  public IMontiArcGlobalScope loadAllIntoGlobalScope(@NotNull IMontiArcGlobalScope scope) {
    Preconditions.checkArgument(scope != null);

    Collection<Path> symFiles = scope.getModelPath().getFullPathOfEntries().stream()
      .map(this::findSymbolFiles)
      .flatMap(Collection::stream)
      .collect(Collectors.toSet());

    Collection<IMontiArcScope> loadedSymbols = symFiles.stream().map(this::load).collect(Collectors.toSet());
    loadedSymbols.forEach(scope::addSubScope);

    Collection<String> namesOfLoadedModels = symFiles.stream()
        .map(f -> this.getModelNameFromPath(f, scope.getSymbolPath()))
        .map(name -> name.orElseThrow(IllegalStateException::new))
        .collect(Collectors.toSet());
    namesOfLoadedModels.forEach(scope::addLoadedFile);

    return scope;
  }

  protected Optional<String> getModelNameFromPath(@NotNull Path pathToModel, @NotNull MCPath possiblePrefixes) {
    Preconditions.checkNotNull(pathToModel);
    Preconditions.checkNotNull(possiblePrefixes);

    for(Path possiblePrefix : possiblePrefixes.getEntries()) {
      if(pathToModel.startsWith(possiblePrefix)) {
        Path relativePath = possiblePrefix.relativize(pathToModel);
        Path relativePathWithoutExt = Paths.get(FilenameUtils.removeExtension(relativePath.toString()));
        String asModelName = relativePathWithoutExt.toString().replace(File.separator, ".");
        return Optional.of(asModelName);
      }
    }
    return  Optional.empty();
  }

  @Override
  public IMontiArcScope createSymbolTable(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkArgument(ast != null);
    MontiArcScopesGenitorDelegator genitor = MontiArcMill.scopesGenitorDelegator();
    IMontiArcScope scope = genitor.createFromAST(ast);
    MontiArcSymbolTableCompleterDelegator completer = MontiArcMill.symbolTableCompleterDelegator();
    completer.createFromAST(ast);
    return scope;
  }

  @Override
  public Collection<IMontiArcScope> createSymbolTable(@NotNull IMontiArcGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    MontiArcScopesGenitorDelegator genitor = MontiArcMill.scopesGenitorDelegator();
    MontiArcSymbolTableCompleterDelegator completer = MontiArcMill.symbolTableCompleterDelegator();
    MontiArcMill.globalScope();
    this.loadAllIntoGlobalScope(scope);
    Collection<ASTMACompilationUnit> models = this.parseAll(scope);
    Collection<IMontiArcScope> scopes = models.stream().map(genitor::createFromAST).collect(Collectors.toSet());
    models.forEach(ast -> completer.createFromAST(ast));
    return scopes;
  }

  @Override
  public Collection<IMontiArcScope> createSymbolTable(@NotNull Path directory) {
    Preconditions.checkArgument(directory != null);
    Preconditions.checkArgument(directory.toFile().exists());
    Preconditions.checkArgument(directory.toFile().isDirectory());
    return this.createSymbolTable(this.createMAGlobalScope(directory));
  }

  @Override
  public IMontiArcGlobalScope createMAGlobalScope(@NotNull Path... directories) {
    Preconditions.checkArgument(directories != null);
    return this.createMAGlobalScope(new ModelPath(directories));
  }

  protected IMontiArcGlobalScope createMAGlobalScope(@NotNull ModelPath modelPath) {
    Preconditions.checkArgument(modelPath != null);
    IMontiArcGlobalScope maScope = MontiArcMill.globalScope();
    maScope.setModelPath(modelPath);
    return maScope;
  }

  @Override
  public void initializeBasicTypes() {
    BasicSymbolsMill.initializePrimitives();
    this.initializeBasicOOTypes();
  }

  @Override
  public void initializeBasicOOTypes() {
    this.add2Scope(MontiArcMill.globalScope(), MontiArcMill.oOTypeSymbolBuilder()
      .setName("Object")
      .setEnclosingScope(MontiArcMill.artifactScope())
      .setSpannedScope(MontiArcMill.scope()).build());
    this.add2Scope(MontiArcMill.globalScope(), MontiArcMill.oOTypeSymbolBuilder()
      .setName("String")
      .setEnclosingScope(MontiArcMill.artifactScope())
      .setSpannedScope(MontiArcMill.scope())
      .addSuperTypes(SymTypeExpressionFactory.createTypeObject("Object", MontiArcMill.globalScope()))
      .build());
    this.add2Scope(MontiArcMill.globalScope(), MontiArcMill.oOTypeSymbolBuilder()
      .setName("Integer")
      .setEnclosingScope(MontiArcMill.artifactScope())
      .setSpannedScope(MontiArcMill.scope())
      .addSuperTypes(SymTypeExpressionFactory.createTypeObject("Object", MontiArcMill.globalScope()))
      .build());
  }

  protected void add2Scope(@NotNull IOOSymbolsScope scope, @NotNull OOTypeSymbol... symbols) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(symbols);
    Arrays.stream(symbols).forEach(symbol -> {
      symbol.setEnclosingScope(scope);
      scope.add(symbol);
    });
  }

  @Override
  public void checkCoCos(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkArgument(ast != null);
    this.checker.checkAll(ast);
  }

  @Override
  public void processModels(@NotNull IMontiArcGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    this.createSymbolTable(scope).stream().map(artifactScope -> (ASTMACompilationUnit) artifactScope.getAstNode())
      .forEach(this::checkCoCos);
  }

  @Override
  public IMontiArcGlobalScope processModels(@NotNull Path... directories) {
    Preconditions.checkArgument(directories != null);
    Preconditions.checkArgument(!Arrays.asList(directories).contains(null));
    ModelPath modelPath = new ModelPath(directories);
    IMontiArcGlobalScope maScope = this.createMAGlobalScope(modelPath);
    this.processModels(maScope);
    return maScope;
  }
}
