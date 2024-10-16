/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import arcautomaton.ArcAutomatonMill;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCos;
import montiarc._symboltable.IMontiArcArtifactScope;
import montiarc.report.IncCheckUtil;
import montiarc.report.UpToDateResults;
import montiarc.report.VersionFileDeserializer;
import montiarc.trafo.MontiArcTrafos;
import montiarc.util.MontiArcError;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MontiArcTool extends MontiArcToolTOP {

  public static final String SYMBOLS_REPORT_DIR = "symbols-inc-data";
  public static final String MONITARC_INC_CHECK_VERSION_PATH = "montiarc/MontiArcToolVersion.txt";

  private Supplier<String> versionSupplier =
    new VersionFileDeserializer(MONITARC_INC_CHECK_VERSION_PATH)::loadVersion;

  public void setMa2JavaVersionSupplier(@NotNull Supplier<String> versionSupplier) {
    this.versionSupplier = Preconditions.checkNotNull(versionSupplier);
  }

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

      // if --input is missing: print help
      if (!cl.hasOption("i")) {
        this.printHelp(options);
        return;
      }

      this.initGlobalScope(cl);
      this.initializeBasicTypes();
      this.initializeTickEvent();
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

    List<Path> paths = List.copyOf(this.createModelPath(cl).getEntries());

    for (int i = 0; i < paths.size(); i++) {
      for (int j = i + 1; j < paths.size(); j++) {
        if (paths.get(i).startsWith(paths.get(j).toString() + File.separator)) {
          Log.error(MontiArcError.SUPERIMPOSED_MODELPATH.format(paths.get(j).toString(), paths.get(i).toString()));
        } else if (paths.get(j).startsWith(paths.get(i).toString() + File.separator)) {
          Log.error(MontiArcError.SUPERIMPOSED_MODELPATH.format(paths.get(i).toString(), paths.get(j).toString()));
        }
      }
    }

    Log.enableFailQuick(true);
    Log.enableFailQuick(false);

    Collection<ASTMACompilationUnit> asts = this.parse("arc", paths);

    this.runAfterParserCoCos(asts);
    Log.enableFailQuick(true);

    if (cl.hasOption("c2mc")) {
      this.defaultImportTrafo(asts);
    }

    this.runTasks(asts, cl);
  }

  protected MCPath createModelPath(@NotNull CommandLine cl) {
    Preconditions.checkNotNull(cl);

    if (cl.hasOption("i")) {
      return new MCPath(this.getAllModelDirsFrom(cl).toArray(new String[0]));
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
        .setPartsList(List.of("java", "lang"))
        .build())
      .setStar(true)
      .build());
  }

  public void runTasks(@NotNull Collection<ASTMACompilationUnit> asts, @NotNull CommandLine cl) {
    Preconditions.checkNotNull(asts);
    Preconditions.checkNotNull(cl);

    Log.enableFailQuick(false);

    Log.info("Run post parsing transformations", "MontiArcTool");
    this.runAfterParsingTrafos(asts);

    Log.info("Run symbol-table creation phase 1", "MontiArcTool");
    Collection<IMontiArcArtifactScope> scopes = this.createSymbolTable(asts);

    Log.info("Run symbol-table creation phase 2", "MontiArcTool");
    this.runSymbolTablePhase2(asts);

    Log.info("Run symbol-table creation phase 3", "MontiArcTool");
    this.runSymbolTablePhase3(asts);

    Log.info("Run post symbol-table creation transformations", "MontiArcTool");
    this.runAfterSymbolTablePhase3Trafos(asts);

    Log.info("Perform initial context-condition checks", "MontiArcTool");
    this.runDefaultCoCos(asts);

    Log.info("Perform remaining context-condition checks", "MontiArcTool");
    this.runAdditionalCoCos(asts);
    Log.enableFailQuick(true);

    if (cl.hasOption("pp")) {
      Log.info("Pretty print models", "MontiArcTool");
      this.prettyPrint(asts, Optional.ofNullable(cl.getOptionValue("pp")).orElse(""));
    }

    this.runAdditionalTasks(scopes, cl);
  }

  public void runAdditionalTasks(@NotNull Collection<IMontiArcArtifactScope> scopes, @NotNull CommandLine cl) {
    Preconditions.checkNotNull(scopes);
    Preconditions.checkNotNull(cl);

    if (cl.hasOption("symboltable")) {
      Log.info("Print symbol table", "MontiArcTool");
      this.storeSymbols(scopes, cl);
    }
  }

  public Collection<ASTMACompilationUnit> parse(@NotNull String fileExt,
                                                @NotNull Collection<Path> paths) {
    Preconditions.checkNotNull(fileExt);
    Preconditions.checkNotNull(paths);
    Preconditions.checkArgument(!fileExt.isEmpty());

    List<ASTMACompilationUnit> asts = new ArrayList<>();
    for (Path path : paths) {
      asts.addAll(this.parse(fileExt, path));
    }
    return Collections.unmodifiableList(asts);
  }

  public Collection<ASTMACompilationUnit> parse(@NotNull String fileExt,
                                                @NotNull Path path) {
    Preconditions.checkNotNull(fileExt);
    Preconditions.checkNotNull(path);
    Preconditions.checkArgument(!fileExt.isEmpty());

    File filepath = path.toFile();

    if (!filepath.exists()) {
      Log.warn("Directory does not exist: " + path);
      return Collections.emptyList();
    }

    Collection<ASTMACompilationUnit> asts = new ArrayList<>();
    if (filepath.isFile()) {
      this.parse(filepath, filepath).ifPresent(asts::add);
    } else if (filepath.isDirectory()) {
      for (File file : FileUtils.listFiles(filepath, new String[]{fileExt}, true)) {
        this.parse(filepath, file).ifPresent(asts::add);
      }
    }

    return Collections.unmodifiableCollection(asts);
  }

  /**
   * Parses the file as a montiarc component model and checks its name and
   * package against the filename and relative filepath.
   *
   * @param root the file root used to determine the relative filepath
   * @param file the file to parse
   * @return an {@code Optional} of the file's AST if the file parsed without errors
   */
  Optional<ASTMACompilationUnit> parse(@NotNull File root,
                                       @NotNull File file) {

    Optional<ASTMACompilationUnit> ast = this.parse(file.toPath());

    if (ast.isPresent()) {
      final String pkg = ast.get().isPresentPackage() ? ast.get().getPackage().getQName() : "";
      if (root.isDirectory()) {
        final String rfp = Names.getPackageFromPath(root.toPath().relativize(file.toPath().getParent()).toString());
        if (!pkg.equals(rfp)) {
          // If the root is a directory the package should match the relative file path
          Log.error(String.format(MontiArcError.PACKAGE_AND_FILE_PATH_DIFFER.toString(), pkg, rfp),
            ast.get().isPresentPackage() ?
              ast.get().getPackage().get_SourcePositionStart() :
              ast.get().getComponentType().get_SourcePositionStart()
          );
        }
      } else if (root.isFile()) {
        final String rfp = Names.getPackageFromPath(root.toPath().getParent().toString());
        if (!rfp.endsWith(pkg)) {
          // If the root is the file itself than the package should be a suffix of the file path
          Log.error(String.format(MontiArcError.PACKAGE_AND_FILE_PATH_DIFFER.toString(), pkg, rfp),
            ast.get().isPresentPackage() ?
              ast.get().getPackage().get_SourcePositionStart() :
              ast.get().getComponentType().get_SourcePositionStart()
          );
        }
      }
    }
    return ast;
  }

  public Optional<ASTMACompilationUnit> parse(@NotNull Path file) {
    Preconditions.checkNotNull(file);
    Preconditions.checkArgument(file.toFile().exists());
    Preconditions.checkArgument(file.toFile().isFile());
    try {
      return MontiArcMill.parser().parse(file.toString());
    } catch (IOException e) {
      Log.error(String.format(MontiArcError.TOOL_PARSE_IOEXCEPTION.toString(), file), e);
    }
    return Optional.empty();
  }

  @Override
  public IMontiArcArtifactScope createSymbolTable(@NotNull ASTMACompilationUnit node) {
    Preconditions.checkNotNull(node);
    return MontiArcMill.scopesGenitorDelegator().createFromAST(node);
  }

  public Collection<IMontiArcArtifactScope> createSymbolTable(@NotNull Collection<ASTMACompilationUnit> nodes) {
    Preconditions.checkNotNull(nodes);
    return MontiArcMill.scopesGenitorDelegator().createFromAST(nodes);
  }

  public void runSymbolTablePhase2(@NotNull ASTMACompilationUnit node) {
    Preconditions.checkNotNull(node);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(node);
  }

  public void runSymbolTablePhase2(@NotNull Collection<ASTMACompilationUnit> nodes) {
    Preconditions.checkNotNull(nodes);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(nodes);
  }

  public void runSymbolTablePhase3(@NotNull ASTMACompilationUnit node) {
    Preconditions.checkNotNull(node);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(node);
  }

  public void runSymbolTablePhase3(@NotNull Collection<ASTMACompilationUnit> nodes) {
    Preconditions.checkNotNull(nodes);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(nodes);
  }

  public void runAfterParsingTrafos(@NotNull Collection<ASTMACompilationUnit> asts) {
    Preconditions.checkNotNull(asts);
    asts.forEach(this::runAfterParsingTrafos);
  }

  public void runAfterParsingTrafos(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);
    MontiArcTrafos.afterParsing().applyAll(ast);
  }

  public void runAfterSymbolTablePhase3Trafos(@NotNull Collection<ASTMACompilationUnit> asts) {
    Preconditions.checkNotNull(asts);
    asts.forEach(this::runAfterSymbolTablePhase3Trafos);
  }

  public void runAfterSymbolTablePhase3Trafos(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);
    MontiArcTrafos.afterSymTab().applyAll(ast);
  }

  public void runAfterParserCoCos(@NotNull Collection<ASTMACompilationUnit> asts) {
    Preconditions.checkNotNull(asts);
    asts.forEach(this::runAfterParserCoCos);
  }

  public void runAfterParserCoCos(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);
    MontiArcCoCos.afterParser().checkAll(ast);
  }

  public void runDefaultCoCos(@NotNull Collection<ASTMACompilationUnit> asts) {
    Preconditions.checkNotNull(asts);
    asts.forEach(this::runDefaultCoCos);
  }

  @Override
  public void runDefaultCoCos(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);
    MontiArcCoCos.afterSymTab().checkAll(ast);
  }

  public void runAdditionalCoCos(@NotNull Collection<ASTMACompilationUnit> asts) {
    Preconditions.checkNotNull(asts);
    asts.forEach(this::runAdditionalCoCos);
  }

  @Override
  public void runAdditionalCoCos(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);
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
    this.print(MontiArcMill.prettyPrint(ast, true), file);
  }

  @Override
  public void print(@NotNull String content, @NotNull String path) {
    Preconditions.checkNotNull(content);
    Preconditions.checkNotNull(path);
    super.print(content, path);
  }

  /**
   * @param cl At least the option "symboltable" must be set.
   */
  public void storeSymbols(@NotNull Collection<IMontiArcArtifactScope> scopes, @NotNull CommandLine cl) {
    // The majority of this method deals about the reporting of the tooling execution.
    Preconditions.checkNotNull(scopes);
    Preconditions.checkNotNull(cl);
    Preconditions.checkArgument(cl.hasOption("symboltable"));

    String symbolTargetDir = cl.getOptionValue("symboltable");
    Optional<String> reportDir = Optional.ofNullable(cl.getOptionValue("report"));
    List<String> modelpaths = this.getAllModelDirsFrom(cl);
    Collection<IMontiArcArtifactScope> scopes4NewSerialization;
    Map<IMontiArcArtifactScope, ASTMACompilationUnit> scopeToAst;

    boolean writeReports = reportDir.isPresent() && !modelpaths.isEmpty();
    if (writeReports) {
      IncCheckUtil.Config incCheckConfig = new IncCheckUtil.Config(
        modelpaths, symbolTargetDir, reportDir.get(), SYMBOLS_REPORT_DIR, Collections.emptyList(), versionSupplier.get()
      );
      IncCheckUtil.configureIncCheckReporting(incCheckConfig);

      scopeToAst = extractCompUnitsFrom(scopes);

      Map<String, ASTMACompilationUnit> astByQName = IncCheckUtil.resolveAstByQName(scopeToAst.values());
      UpToDateResults upToDateInfo = IncCheckUtil.calcUpToDateData(astByQName, incCheckConfig);

      IncCheckUtil.removeOutdatedGenerationResults(upToDateInfo, incCheckConfig);
      scopes4NewSerialization =
        IncCheckUtil.calcReportedModelsForNewGeneration(upToDateInfo, astByQName)
          .stream()
          .map(a -> a.getComponentType().getEnclosingScope())
          .map(s -> (IMontiArcArtifactScope) s)
          .collect(Collectors.toList());
    } else {
      scopes4NewSerialization = scopes;
      scopeToAst = new HashMap<>(0);
    }

    MCPath modelPaths = new MCPath(modelpaths.toArray(new String[0]));

    for (IMontiArcArtifactScope scope : scopes4NewSerialization) {
      Optional<ASTMACompilationUnit> ast = Optional.ofNullable(scopeToAst.get(scope));

      Optional<Path> modelLocation = ast.flatMap(a -> IncCheckUtil.findModelLocation(modelPaths, a));
      boolean writeReport4ThisModel = writeReports && modelLocation.isPresent();  // implies ast.isPresent

      // Init reporting for current ast
      if (writeReport4ThisModel) {
        IncCheckUtil.setIncCheckReportingOn(ast.orElseThrow(), modelLocation.get());
      }

      // In all cases
      this.storeSymbols(scope, symbolTargetDir);

      if (writeReport4ThisModel) {
        Reporting.flush(ast.orElseThrow());
      }
    }
  }

  @Override
  public void storeSymbols(@NotNull IMontiArcArtifactScope scope, @NotNull String path) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(path);
    Preconditions.checkArgument(!path.isEmpty());

    super.storeSymbols(scope, path + "/" + Names.getPathFromPackage(scope.getFullName()) + ".arcsym");
  }

  protected void initGlobalScope(@NotNull CommandLine cl) {
    Preconditions.checkNotNull(cl);
    if (cl.hasOption("path")) {
      this.initGlobalScope(getAllSymbolImportDirsFrom(cl));
    } else {
      this.initGlobalScope();
    }
  }

  public void initGlobalScope() {
    MontiArcMill.globalScope().clear();
    MontiArcMill.globalScope().init();
    MCCollectionSymTypeRelations.init();
  }

  public void initGlobalScope(@NotNull String... entries) {
    Preconditions.checkNotNull(entries);

    // `new MCPath(String...)` fails if *one* of the Paths that we pass is composed of multiple paths with a path
    // separator in between, e.g.: foo/bar:goo/rar on Linux. Therefore, we manually separate these paths first.
    String[] paths = splitPathEntries(entries);
    for (int i = 0; i < paths.length; i++) {
      for (int j = i + 1; j < paths.length; j++) {
        if (paths[i].startsWith(paths[j] + File.separator)) {
          Log.error(MontiArcError.SUPERIMPOSED_SYMPATH.format(paths[j], paths[i]));
        } else if (paths[j].startsWith(paths[i] + File.separator)) {
          Log.error(MontiArcError.SUPERIMPOSED_SYMPATH.format(paths[i], paths[j]));
        }
      }
    }
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

  public void initializeTickEvent() {
    ArcAutomatonMill.initializeTick();
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

  @Override
  public Options addStandardOptions(@NotNull Options options) {
    Preconditions.checkNotNull(options);
    options = super.addStandardOptions(options);

    // We accept multiple inputs
    Option i = options.getOption("i");
    i.setArgName("files");
    i.setArgs(Option.UNLIMITED_VALUES);
    i.setDescription("Reads the source files (mandatory) and parses their contents");

    // class2mc
    options.addOption(Option.builder("c2mc")
      .longOpt("class2mc")
      .desc("Enables to resolve java classes in the model path")
      .build());

    return options;
  }

  /**
   * For scopes whose astNode is an instance of {@link ASTMACompilationUnit}, returns a map of the scope to its astNode.
   * Other scopes are ignored and not included in the result.
   */
  protected Map<IMontiArcArtifactScope, ASTMACompilationUnit> extractCompUnitsFrom(
    @NotNull Collection<IMontiArcArtifactScope> scopes) throws IllegalStateException {
    Preconditions.checkNotNull(scopes);

    if (scopes.stream().map(IMontiArcArtifactScope::getAstNode).anyMatch(a -> !(a instanceof ASTMACompilationUnit))) {
      Log.debug(
        String.format("MontiArcTool only works with ASTMACompilationUnit instances, but also found asts of type %s.",
          scopes.stream()
            .map(IMontiArcArtifactScope::getAstNode)
            .filter(a -> !(a instanceof ASTMACompilationUnit))
            .map(Object::getClass)
            .map(Class::getSimpleName)
            .collect(Collectors.joining(", "))
        ),
        "MontiArcTool"
      );
    }

    return scopes.stream()
      .filter(s -> s.getAstNode() instanceof ASTMACompilationUnit)
      .collect(Collectors.toMap(
        Function.identity(),
        s -> (ASTMACompilationUnit) s.getAstNode()
      ));
  }

  /**
   * Extracts all model paths as separate strings from the given command line.
   * <br>
   * E.g. the model path argument may be the Array {"\first\path", "\second\path;third\path"} on Windows or
   * {"first/path", "second/path:third/path"} on Unix.
   * This method will decompose composed paths and return them as separate strings. The other paths are unaffected.
   * Thus, this method would yield for the above example:
   * {"\first\path", "\second\path", "third\path"} on Windows and {"first/path", "second/path", "third/path"} on Unix.
   */
  protected List<String> getAllModelDirsFrom(@NotNull CommandLine cl) {
    Preconditions.checkNotNull(cl);
    return cl.hasOption("i") ?
      splitPathEntriesToList(cl.getOptionValues("i")) :
      Collections.emptyList();
  }

  /**
   * Extracts all hwc paths as separate strings from the given command line.
   * <br>
   * E.g. the hwc path argument may be the Array {"\first\path", "\second\path;third\path"} on Windows or
   * {"first/path", "second/path:third/path"} on Unix.
   * This method will decompose composed paths and return them as separate strings. The other paths are unaffected.
   * Thus, this method would yield for the above example:
   * {"\first\path", "\second\path", "third\path"} on Windows and {"first/path", "second/path", "third/path"} on Unix.
   */
  protected List<String> getAllHwcDirsFrom(@NotNull CommandLine cl) {
    Preconditions.checkNotNull(cl);
    return cl.hasOption("handwritten-code") ?
      splitPathEntriesToList(cl.getOptionValues("handwritten-code")) :
      Collections.emptyList();
  }

  /**
   * Extracts all symbol import dir paths as separate strings from the given command line.
   * <br>
   * E.g. the symbol import path argument may be the Array {"\first\path", "\second\path;third\path"} on Windows or
   * {"first/path", "second/path:third/path"} on Unix.
   * This method will decompose composed paths and return them as separate strings. The other paths are unaffected.
   * Thus, this method would yield for the above example:
   * {"\first\path", "\second\path", "third\path"} on Windows and {"first/path", "second/path", "third/path"} on Unix.
   */
  protected String[] getAllSymbolImportDirsFrom(@NotNull CommandLine cl) {
    Preconditions.checkNotNull(cl);
    return cl.hasOption("path") ?
      splitPathEntries(cl.getOptionValues("path")) :
      new String[0];
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


  /**
   * Like {@link #splitPathEntries(String[])}, but returns a {@code List<String>} instead.
   */
  protected final @NotNull List<String> splitPathEntriesToList(@NotNull String[] composedPath) {
    return Arrays.asList(splitPathEntries(composedPath));
  }
}
