/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import com.google.common.base.Preconditions;
import de.monticore.io.paths.ModelPath;
import de.monticore.types.check.DefsTypeBasic;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._parser.MontiArcParser;
import montiarc._symboltable.*;
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
    this.initMill();
    this.parser = parser;
    this.deSer = deSer;
    this.checker = checker;
  }

  protected void initMill() {
    MontiArcMill.init();
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
    Preconditions.checkArgument(file.toFile().exists());
    Preconditions.checkArgument(file.toFile().isFile());
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
    Preconditions.checkArgument(file.toFile().exists());
    Preconditions.checkArgument(file.toFile().isFile());
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
    Preconditions.checkArgument(directory.toFile().exists());
    Preconditions.checkArgument(directory.toFile().isDirectory());
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

  @Override
  public IMontiArcScope createSymbolTable(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkArgument(ast != null);
    MontiArcScopesGenitorDelegator symTab = new MontiArcScopesGenitorDelegator();
    return symTab.createFromAST(ast);
  }

  @Override
  public Collection<IMontiArcScope> createSymbolTable(@NotNull IMontiArcGlobalScope scope) {
    Preconditions.checkArgument(scope != null);
    MontiArcScopesGenitorDelegator symTab = new MontiArcScopesGenitorDelegator(scope);
    this.loadAll(scope).forEach(scope::addSubScope);
    return this.parseAll(scope).stream().map(symTab::createFromAST).collect(Collectors.toSet());
  }

  @Override
  public Collection<IMontiArcScope> createSymbolTable(@NotNull Path directory) {
    Preconditions.checkArgument(directory != null);
    Preconditions.checkArgument(directory.toFile().exists());
    Preconditions.checkArgument(directory.toFile().isDirectory());
    MontiArcMill.globalScope().setModelPath(new ModelPath(directory));
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
    maScope.clear();
    maScope.setModelPath(modelPath);
    maScope.setFileExt(this.getMAFileExtension());
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
    ast.accept(this.getChecker());
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