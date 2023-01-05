/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import arcbasis._symboltable.SymbolService;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCos;
import montiarc._symboltable.IMontiArcArtifactScope;
import montiarc._visitor.MontiArcFullPrettyPrinter;
import montiarc.util.MontiArcError;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MontiArcTool extends MontiArcToolTOP {

  public static void main(@NotNull String[] args) {
    Preconditions.checkNotNull(args);
    MontiArcTool tool = new MontiArcTool();
    tool.init();
    tool.run(args);
  }

  @Override
  public void run(@NotNull String[] args) {
    Preconditions.checkNotNull(args);

    Options options = this.initOptions();

    try {
      //parse input options from the command line
      CommandLineParser cliParser = new DefaultParser();
      CommandLine cl = cliParser.parse(options, args);

      // if --help: print help and stop
      if (cl.hasOption("h")) {
        this.printHelp(options);
        return;
      }

      // if --version: print version and stop
      if (cl.hasOption("v")) {
        this.printVersion();
        return;
      }

      // if neither --modelpath nor --i: print help and stop
      if (!cl.hasOption("modelpath")) {
        this.printHelp(options);
        return;
      }

      this.initGlobalScope(cl);
      this.initializeBasicTypes();
      this.initializeClass2MC(cl);

      this.runTasks(cl);

    } catch (ParseException e) {
      Log.error(String.format(MontiArcError.TOOL_PARSE_IOEXCEPTION.toString(), e.getMessage()));
    }
  }

  protected void runTasks(@NotNull CommandLine cl) {
    Preconditions.checkNotNull(cl);

    Log.info("Parse the input models", "MontiArcTool");
    Log.enableFailQuick(false);
    Collection<ASTMACompilationUnit> libAsts = this.parse(".arc", this.createLibraryPath(cl).getEntries());
    Collection<ASTMACompilationUnit> ourAsts = this.parse(".arc", this.createModelPath(cl).getEntries());
    Log.enableFailQuick(true);
    if (cl.hasOption("c2mc")) {
      this.defaultImportTrafo(libAsts);
      this.defaultImportTrafo(ourAsts);
    }

    this.runDefaultTasks(libAsts);
    this.runDefaultTasks(ourAsts);
    this.runAdditionalTasks(ourAsts, cl);
  }

  protected MCPath createModelPath(@NotNull CommandLine cl) {
    Preconditions.checkNotNull(cl);

    if (cl.hasOption("modelpath")) {
      // `new MCPath(String...)` fails if *one* of the Paths that we pass is composed of multiple paths with a path
      // separator in between, e.g.: foo/bar:goo/rar on Linux. Therefore, we manually separate these paths first.
      return new MCPath(splitPathEntries(cl.getOptionValues("modelpath")));
    } else {
      return new MCPath();
    }
  }

  protected MCPath createLibraryPath(@NotNull CommandLine cl) {
    Preconditions.checkNotNull(cl);

    if (cl.hasOption("library-models")) {
      // `new MCPath(String...)` fails if *one* of the Paths that we pass is composed of multiple paths with a path
      // separator in between, e.g.: foo/bar:goo/rar on Linux. Therefore, we manually separate these paths first.
      return new MCPath(splitPathEntries(cl.getOptionValues("library-models")));
    } else {
      return new MCPath();
    }
  }

  public void defaultImportTrafo(@NotNull Collection<ASTMACompilationUnit> asts) {
    Preconditions.checkNotNull(asts);
    asts.forEach(this::defaultImportTrafo);
  }

  public void defaultImportTrafo(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);
    ast.addImportStatement(MontiArcMill.mCImportStatementBuilder()
      .setMCQualifiedName(MontiArcMill.mCQualifiedNameBuilder()
        .setPartsList(ImmutableList.of("java", "lang"))
        .build())
      .setStar(true)
      .build());
  }

  public void runDefaultTasks(@NotNull Collection<ASTMACompilationUnit> asts) {
    Preconditions.checkNotNull(asts);

    Log.info("Create the symbol table", "MontiArcTool");
    Log.enableFailQuick(false);
    this.createSymbolTable(asts);

    Log.info("Perform initial context-condition checks", "MontiArcTool");
    this.runDefaultCoCos(asts);

    Log.info("Complete the symbol table", "MontiArcTool");
    this.completeSymbolTable(asts);

    Log.info("Perform remaining context-condition checks", "MontiArcTool");
    this.runAdditionalCoCos(asts);
    Log.enableFailQuick(true);
  }

  public void runAdditionalTasks(@NotNull Collection<ASTMACompilationUnit> asts, @NotNull CommandLine cl) {
    Preconditions.checkNotNull(asts);
    Preconditions.checkNotNull(cl);

    if (cl.hasOption("pp")) {
      Log.info("Pretty print models", "MontiArcTool");
      this.prettyPrint(asts, Optional.ofNullable(cl.getOptionValue("pp")).orElse(""));
    }

    if (cl.hasOption("symboltable")) {
      Log.info("Print symbol table", "MontiArcTool");
      this.storeSymbols(asts, cl.getOptionValue("symboltable"));
    }
  }

  public Collection<ASTMACompilationUnit> parse(@NotNull String fileExt, @NotNull Collection<Path> directories) {
    Preconditions.checkNotNull(fileExt);
    Preconditions.checkNotNull(directories);
    Preconditions.checkArgument(!fileExt.isEmpty());
    return directories.stream().flatMap(directory -> this.parse(fileExt, directory).stream()).collect(Collectors.toList());
  }

  public Collection<ASTMACompilationUnit> parse(@NotNull String fileExt, @NotNull Path directory) {
    Preconditions.checkNotNull(fileExt);
    Preconditions.checkNotNull(directory);
    Preconditions.checkArgument(!fileExt.isEmpty());
    Preconditions.checkArgument(directory.toFile().exists(), "Directory does not exist: "+directory);
    Preconditions.checkArgument(directory.toFile().isDirectory(), "Directory is file: "+directory);
    try (Stream<Path> paths = Files.walk(directory)) {
      return paths.filter(Files::isRegularFile)
        .filter(file -> file.getFileName().toString().endsWith(fileExt)).map(this::parse)
        .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
    } catch (IOException e) {
      Log.error(String.format(MontiArcError.TOOL_FILE_WALK_IOEXCEPTION.toString(), directory), e);
    }
    return Collections.emptySet();
  }

  public Optional<ASTMACompilationUnit> parse(@NotNull Path file) {
    Preconditions.checkNotNull(file);
    Preconditions.checkArgument(file.toFile().exists(), file.toString());
    Preconditions.checkArgument(file.toFile().isFile(), file.toString());
    try {
      return MontiArcMill.parser().parse(file.toString());
    } catch (IOException e) {
      Log.error(String.format(MontiArcError.TOOL_PARSE_IOEXCEPTION.toString(), file), e);
    }
    return Optional.empty();
  }

  public Collection<IMontiArcArtifactScope> createSymbolTable(@NotNull Collection<ASTMACompilationUnit> asts) {
    Preconditions.checkNotNull(asts);
    return asts.stream().map(this::createSymbolTable).collect(Collectors.toList());
  }

  @Override
  public IMontiArcArtifactScope createSymbolTable(@NotNull ASTMACompilationUnit model) {
    Preconditions.checkNotNull(model);
    return MontiArcMill.scopesGenitorDelegator().createFromAST(model);
  }

  public void completeSymbolTable(@NotNull Collection<ASTMACompilationUnit> asts) {
    Preconditions.checkNotNull(asts);
    asts.forEach(this::completeSymbolTable);
  }

  @Override
  public void completeSymbolTable(@NotNull ASTMACompilationUnit node) {
    Preconditions.checkNotNull(node);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(node);
  }

  public void runDefaultCoCos(@NotNull Collection<ASTMACompilationUnit> asts) {
    Preconditions.checkNotNull(asts);
    asts.forEach(this::runDefaultCoCos);
  }

  @Override
  public void runDefaultCoCos(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);
    //TODO: Separate cocos and run cocos that can be executed before the symbol table completer here
  }

  public void runAdditionalCoCos(@NotNull Collection<ASTMACompilationUnit> asts) {
    Preconditions.checkNotNull(asts);
    asts.forEach(this::runAdditionalCoCos);
  }

  @Override
  public void runAdditionalCoCos(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);
    MontiArcCoCos.createChecker().checkAll(ast);
  }

  public void prettyPrint(@NotNull Collection<ASTMACompilationUnit> asts, @NotNull String file) {
    Preconditions.checkNotNull(asts);
    Preconditions.checkNotNull(file);
    asts.forEach(ast -> this.prettyPrint(ast, file));
  }

  @Override
  public void prettyPrint(@NotNull ASTMACompilationUnit ast, @NotNull String file) {
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(file);
    if (!file.isEmpty()) {
      file = Paths.get(file,
        Names.getPathFromQualifiedName(ast.getPackage().getQName()),
        ast.getComponentType().getName() + ".arc").toString();
    }
    MontiArcFullPrettyPrinter prettyPrinter = new MontiArcFullPrettyPrinter();
    this.print(prettyPrinter.prettyprint(ast), file);
  }

  @Override
  public void print(@NotNull String content, @NotNull String path) {
    Preconditions.checkNotNull(content);
    Preconditions.checkNotNull(path);
    super.print(content, path);
  }

  public void storeSymbols(@NotNull Collection<ASTMACompilationUnit> asts, @NotNull String path) {
    Preconditions.checkNotNull(asts);
    Preconditions.checkNotNull(path);
    Preconditions.checkArgument(!path.isEmpty());
    asts.forEach(ast -> this.storeSymbols(ast, path));
  }

  public void storeSymbols(@NotNull ASTMACompilationUnit ast, @NotNull String path) {
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(path);
    Preconditions.checkNotNull(ast.getSpannedScope());
    Preconditions.checkArgument(ast.getSpannedScope() instanceof IMontiArcArtifactScope);
    Preconditions.checkArgument(ast.getComponentType().isPresentSymbol());
    Preconditions.checkArgument(!path.isEmpty());
    this.storeSymbols((IMontiArcArtifactScope) ast.getSpannedScope(),
      path + "/" + ast.getComponentType().getSymbol().getFullName() + ".arcsym");
  }

  @Override
  public void storeSymbols(@NotNull IMontiArcArtifactScope scope, @NotNull String path) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(path);
    Preconditions.checkArgument(!path.isEmpty());
    super.storeSymbols(scope, path);
  }

  protected void initGlobalScope(@NotNull CommandLine cl) {
    Preconditions.checkNotNull(cl);
    if (cl.hasOption("path")) {
      this.initGlobalScope(cl.getOptionValues("path"));
    } else {
      this.initGlobalScope();
    }
  }

  public void initGlobalScope() {
    MontiArcMill.globalScope().clear();
    MontiArcMill.globalScope().init();
  }

  public void initGlobalScope(@NotNull String... entries) {
    Preconditions.checkNotNull(entries);

    // `new MCPath(String...)` fails if *one* of the Paths that we pass is composed of multiple paths with a path
    // separator in between, e.g.: foo/bar:goo/rar on Linux. Therefore, we manually separate these paths first.
    String[] paths = splitPathEntries(entries);

    this.initGlobalScope(Arrays.stream(paths).map(Paths::get).collect(Collectors.toList()));
  }

  public void initGlobalScope(@NotNull Path... entries) {
    Preconditions.checkNotNull(entries);
    this.initGlobalScope(Arrays.stream(entries).collect(Collectors.toList()));
  }

  public void initGlobalScope(@NotNull Collection<Path> entries) {
    Preconditions.checkNotNull(entries);
    Preconditions.checkArgument(!entries.contains(null));
    this.initGlobalScope();
    entries.forEach(entry -> MontiArcMill.globalScope().getSymbolPath().addEntry(entry));
  }

  public void initializeBasicTypes() {
    BasicSymbolsMill.initializePrimitives();
  }

  public void initializeClass2MC() {
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
  }

  protected void initializeClass2MC(@NotNull CommandLine cl) {
    Preconditions.checkNotNull(cl);
    if (cl.hasOption("c2mc")) {
      this.initializeClass2MC();
    }
  }

  @Deprecated
  public void initializeBasicOOTypes() {
    SymbolService.link(MontiArcMill.globalScope(), MontiArcMill.oOTypeSymbolBuilder()
      .setName("Object")
      .setEnclosingScope(MontiArcMill.artifactScope())
      .setSpannedScope(MontiArcMill.scope()).build());
    SymbolService.link(MontiArcMill.globalScope(), MontiArcMill.oOTypeSymbolBuilder()
      .setName("String")
      .setEnclosingScope(MontiArcMill.artifactScope())
      .setSpannedScope(MontiArcMill.scope())
      .addSuperTypes(SymTypeExpressionFactory.createTypeObject("Object", MontiArcMill.globalScope()))
      .build());
    SymbolService.link(MontiArcMill.globalScope(), MontiArcMill.oOTypeSymbolBuilder()
      .setName("Integer")
      .setEnclosingScope(MontiArcMill.artifactScope())
      .setSpannedScope(MontiArcMill.scope())
      .addSuperTypes(SymTypeExpressionFactory.createTypeObject("Object", MontiArcMill.globalScope()))
      .build());
  }

  @Override
  public Options addStandardOptions(@NotNull Options options) {
    Preconditions.checkNotNull(options);
    options.addOption(Option.builder("mp")
      .longOpt("modelpath")
      .argName("dirlist")
      .hasArgs()
      .desc("Sets the artifact path for the input component models, space separated.")
      .build());

    //help
    options.addOption(Option.builder("h")
      .longOpt("help")
      .desc("Prints this help dialog.")
      .build());

    //version
    options.addOption(Option.builder("v")
      .longOpt("version")
      .desc("Prints version information.")
      .build());

    // pretty print
    options.addOption(Option.builder("pp")
      .longOpt("prettyprint")
      .argName("dir")
      .optionalArg(true)
      .numberOfArgs(1)
      .desc("Prints the ASTs to stdout or the specified directory (optional).")
      .build());

    // store symbol table
    options.addOption(Option.builder("s")
      .longOpt("symboltable")
      .argName("dir")
      .hasArg()
      .desc("Serializes and prints the symbol table to stdout or the specified output directory (optional).")
      .build());

    // library models
    options.addOption(org.apache.commons.cli.Option.builder("lib")
      .longOpt("library-models")
      .hasArgs()
      .argName("dirList")
      .desc("Models that can be referenced from any other model, but which do not get processed by further" +
        "tasks like pretty printing, serialization to .sym files, etc. (Optional)")
      .build());

    // symbol paths
    options.addOption(Option.builder("path")
      .hasArgs()
      .argName("dirlist")
      .desc("Sets the artifact path for imported symbols, space separated.")
      .build());

    // class2mc
    options.addOption(Option.builder("c2mc")
      .longOpt("class2mc")
      .desc("Enables to resolve java classes in the model path")
      .build());

    return options;
  }

  /**
   * Splits composedPath on their {@link File#pathSeparator}, e.g. {@code some/path:another/path} on Unix would return
   * {@code {some/path, another/path}} and {@code some\path;other\path} on Windows would return
   * {@code {some\path, other\path}}
   */
  protected final @NotNull String[] splitPathEntries(@NotNull String composedPath) {
    Preconditions.checkNotNull(composedPath);

    return composedPath.split(Pattern.quote(File.pathSeparator));
  }

  /**
   * {@link this#splitPathEntries(String)} on every entry of <i>composedPath</i>.
   */
  protected final @NotNull String[] splitPathEntries(@NotNull String[] composedPaths) {
    Preconditions.checkNotNull(composedPaths);
    return Arrays.stream(composedPaths)
      .map(this::splitPathEntries)
      .flatMap(Arrays::stream)
      .toArray(String[]::new);
  }
}
