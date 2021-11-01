/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import com.google.common.base.Preconditions;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCos;
import montiarc._symboltable.IMontiArcArtifactScope;
import montiarc._visitor.MontiArcFullPrettyPrinter;
import montiarc.util.MontiArcError;
import org.apache.commons.cli.*;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.FileFilter;
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

public class MontiArcCLI extends MontiArcCLITOP {

  public static void main(@NotNull String[] args) {
    Preconditions.checkNotNull(args);
    MontiArcCLI cli = new MontiArcCLI();
    cli.init();
    cli.run(args);
  }

  @Override
  public void run(@NotNull String[] args) {
    Preconditions.checkNotNull(args);

    Options options = this.initOptions();

    try {
      //parse input options from the command line
      CommandLineParser cliParser = new DefaultParser();
      CommandLine cli = cliParser.parse(options, args);

      // if --help: print help and stop
      if (cli.hasOption("h")) {
        this.printHelp(options);
        return;
      }

      // if --version: print version and stop
      if (cli.hasOption("v")) {
        this.printVersion();
        return;
      }

      // if neither --modelpath nor --i: print help and stop
      if (!cli.hasOption("modelpath")) {
        this.printHelp(options);
        return;
      }

      this.initGlobalScope(cli);
      this.initializeBasicTypes();

      this.runTasks(cli);

    } catch (ParseException e) {
      Log.error(String.format(MontiArcError.TOOL_PARSE_IOEXCEPTION.toString(), e.getMessage()));
    }
  }

  protected void runTasks(@NotNull CommandLine cli) {
    Preconditions.checkNotNull(cli);

    Log.info("Parse the input models", "MontiArcCLITool");
    Log.enableFailQuick(false);
    Collection<ASTMACompilationUnit> asts = this.parse(".arc", this.createModelPath(cli).getEntries());
    Log.enableFailQuick(true);

    this.loadSymbols();
    this.runDefaultTasks(asts);
    this.runAdditionalTasks(asts, cli);
  }

  protected MCPath createModelPath(@NotNull CommandLine cli) {
    Preconditions.checkNotNull(cli);
    return cli.hasOption("modelpath") ? new MCPath(cli.getOptionValues("modelpath")) : new MCPath();
  }

  protected void loadSymbols() {
    Log.info("Load symbols", "MontiArcCLITool");
    this.loadSymbols(MontiArcMill.globalScope().getFileExt(), MontiArcMill.globalScope().getSymbolPath());
  }

  public void loadSymbols(@NotNull String fileNameRegEx, @NotNull MCPath path) {
    Preconditions.checkNotNull(fileNameRegEx);
    Preconditions.checkNotNull(path);
    Preconditions.checkArgument(!fileNameRegEx.isEmpty());
    path.getEntries().forEach(entry -> this.loadSymbols(fileNameRegEx, entry));
  }


  public void loadSymbols(@NotNull String fileNameRegEx, @NotNull Path directory) {
    Preconditions.checkNotNull(fileNameRegEx);
    Preconditions.checkNotNull(directory);
    Preconditions.checkArgument(!fileNameRegEx.isEmpty());
    Preconditions.checkArgument(directory.toFile().exists(), directory.toAbsolutePath() + " does not exist.");
    Preconditions.checkArgument(directory.toFile().isDirectory(), directory.toAbsolutePath() + " is not a directory.");
    FileFilter filter = new RegexFileFilter(fileNameRegEx);
    try (Stream<Path> paths = Files.walk(directory)) {
      paths.filter(file -> filter.accept(file.toFile())).forEach(this::loadSymbols);
    } catch (IOException e) {
      Log.error(String.format(MontiArcError.TOOL_FILE_WALK_IOEXCEPTION.toString(), directory), e);
    }
  }

  public void loadSymbols(@NotNull Path file) {
    Log.info("Load symbols from " + file, "MontiArcCLITool");
    Preconditions.checkNotNull(file);
    Preconditions.checkArgument(file.toFile().exists(), file.toAbsolutePath() + " does not exist.");
    Preconditions.checkArgument(file.toFile().isFile(), file.toAbsolutePath() + " is not a file.");
    MontiArcMill.globalScope().loadFile(file.toString());
  }

  public void runDefaultTasks(@NotNull Collection<ASTMACompilationUnit> asts) {
    Preconditions.checkNotNull(asts);

    Log.info("Create the symbol table", "MontiArcCLITool");
    Log.enableFailQuick(false);
    this.createSymbolTable(asts);
    Log.enableFailQuick(true);

    Log.info("Perform initial context-condition checks", "MontiArcCLITool");
    Log.enableFailQuick(false);
    this.runDefaultCoCos(asts);
    Log.enableFailQuick(true);

    Log.info("Complete the symbol table", "MontiArcCLITool");
    Log.enableFailQuick(false);
    this.completeSymbolTable(asts);
    Log.enableFailQuick(true);

    Log.info("Check remaining context-condition checks", "MontiArcCLITool");
    Log.enableFailQuick(false);
    this.runAdditionalCoCos(asts);
    Log.enableFailQuick(true);
  }

  public void runAdditionalTasks(@NotNull Collection<ASTMACompilationUnit> asts, @NotNull CommandLine cli) {
    Preconditions.checkNotNull(asts);
    Preconditions.checkNotNull(cli);

    if (cli.hasOption("pp")) {
      Log.info("Pretty print models", "MontiArcCLITool");
      this.prettyPrint(asts, Optional.ofNullable(cli.getOptionValue("pp")).orElse(""));
    }

    if (cli.hasOption("symboltable")) {
      Log.info("Print symbol table", "MontiArcCLITool");
      this.storeSymbols(asts, cli.getOptionValue("symboltable"));
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
    Preconditions.checkArgument(directory.toFile().exists());
    Preconditions.checkArgument(directory.toFile().isDirectory());
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
    if (file.isEmpty()) {
      asts.forEach(ast -> this.prettyPrint(ast, file));
    } else {
      asts.forEach(ast -> this.prettyPrint(ast, file + ast.getComponentType().getName() + ".arc"));
    }
  }

  @Override
  public void prettyPrint(@NotNull ASTMACompilationUnit ast, @NotNull String file) {
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(file);
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
    Preconditions.checkArgument(ast.getSpannedScope() != null);
    Preconditions.checkArgument(ast.getSpannedScope() instanceof IMontiArcArtifactScope);
    Preconditions.checkArgument(ast.getComponentType().isPresentSymbol());
    Preconditions.checkArgument(!path.isEmpty());
    this.storeSymbols((IMontiArcArtifactScope) ast.getSpannedScope(),
      path + ast.getComponentType().getSymbol().getFullName() + ".arcsym");
  }

  @Override
  public void storeSymbols(@NotNull IMontiArcArtifactScope scope, @NotNull String path) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(path);
    Preconditions.checkArgument(!path.isEmpty());
    super.storeSymbols(scope, path);
  }

  protected void initGlobalScope(@NotNull CommandLine cli) {
    Preconditions.checkNotNull(cli);
    if (cli.hasOption("path")) {
      this.initGlobalScope(cli.getOptionValues("path"));
    } else {
      this.initGlobalScope("");
    }
  }

  public void initGlobalScope(@NotNull String... entries) {
    Preconditions.checkNotNull(entries);
    this.initGlobalScope(Arrays.stream(entries).map(Paths::get).collect(Collectors.toList()));
  }

  public void initGlobalScope(@NotNull Path... entries) {
    Preconditions.checkNotNull(entries);
    this.initGlobalScope(Arrays.stream(entries).collect(Collectors.toList()));
  }

  public void initGlobalScope(@NotNull Collection<Path> entries) {
    Preconditions.checkNotNull(entries);
    Preconditions.checkArgument(!entries.contains(null));
    MontiArcMill.globalScope().init();
    MCPath symbolPath = new MCPath(entries);
    MontiArcMill.globalScope().setSymbolPath(symbolPath);
  }

  public void initializeBasicTypes() {
    BasicSymbolsMill.initializePrimitives();
    this.initializeBasicOOTypes();
  }

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

  @Override
  public Options addStandardOptions(@NotNull Options options) {
    Preconditions.checkNotNull(options);
    options.addOption(org.apache.commons.cli.Option.builder("mp")
      .longOpt("modelpath")
      .hasArgs()
      .desc("Sets the artifact path for the input component models, space separated.")
      .build());

    //help
    options.addOption(org.apache.commons.cli.Option.builder("h")
      .longOpt("help")
      .desc("Prints this help dialog.")
      .build());

    //version
    options.addOption(org.apache.commons.cli.Option.builder("v")
      .longOpt("version")
      .desc("Prints version information.")
      .build());

    // pretty print
    options.addOption(org.apache.commons.cli.Option.builder("pp")
      .longOpt("prettyprint")
      .argName("file")
      .optionalArg(true)
      .numberOfArgs(1)
      .desc("Prints the AST to stdout or the specified file (optional).")
      .build());

    // store symbol table
    options.addOption(org.apache.commons.cli.Option.builder("s")
      .longOpt("symboltable")
      .argName("file")
      .hasArg()
      .desc("Serialized the Symbol table of the given artifact.")
      .build());

    // symbol paths
    options.addOption(org.apache.commons.cli.Option.builder("path")
      .hasArgs()
      .desc("Sets the artifact path for imported symbols, space separated.")
      .build());

    return options;
  }

  protected void add2Scope(@NotNull IOOSymbolsScope scope, @NotNull OOTypeSymbol... symbols) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(symbols);
    Arrays.stream(symbols).forEach(symbol -> {
      symbol.setEnclosingScope(scope);
      scope.add(symbol);
    });
  }
}
