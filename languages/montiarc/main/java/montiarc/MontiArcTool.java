/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCos;
import montiarc._symboltable.IMontiArcArtifactScope;
import montiarc.check.MontiArcTypeCheck;
import montiarc.logging.MontiArcLog;
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
import org.apache.commons.io.IOUtils;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MontiArcTool extends MontiArcToolTOP {

  public static final String SYMBOLS_REPORT_DIR = "symbols-inc-data";
  public static final String INC_CHECK_VERSION_PATH = "buildInfo.properties";

  private Supplier<String> versionSupplier =
    new VersionFileDeserializer(INC_CHECK_VERSION_PATH)::loadVersion;

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
  public void init() {
    MontiArcLog.init();
    MontiArcMill.init();
    MontiArcTypeCheck.init();
    SymTypeRelations.init();
    MCCollectionSymTypeRelations.init();
  }

  @Override
  public void run(@NotNull String[] args) {
    Preconditions.checkNotNull(args);

    MontiArcMill.globalScope().clear();
    MontiArcMill.globalScope().init();

    try {
      // parse input options from the command line
      CommandLineParser cliParser = new DefaultParser();

      if (args.length > 0 && args[0].equals("create")) {
        if (args.length == 1) {
          printHelp();
          return;
        }
        Options options = this.initCreateOptions();
        CommandLine cl = cliParser.parse(options, args);
        runCreate(args[1], cl);
      } else {
        Options options = this.initOptions();
        CommandLine cl = cliParser.parse(options, args);

        // if --h: print help and stop
        if (cl.hasOption("h")) {
          this.printHelp();
          return;
        }

        // if --version: print version and stop
        if (cl.hasOption("v")) {
          this.printVersion();
          return;
        }

        // if --input is missing: print help and stop
        if (!cl.hasOption("i")) {
          this.printHelp();
          return;
        }

        // if --d or --t: enable verbose logging
        if (cl.hasOption("d")) {
          MontiArcLog.initDEBUG();
        } else if (cl.hasOption("t")) {
          MontiArcLog.initTRACE();
        }

        run(cl);
      }
    } catch (ParseException e) {
      Log.error(String.format(MontiArcError.TOOL_PARSE_IOEXCEPTION.toString(), e.getMessage()));
    }
  }

  protected void run(@NotNull CommandLine cl) {
    Preconditions.checkNotNull(cl);
    Preconditions.checkArgument(!cl.hasOption("h"));
    Preconditions.checkArgument(!cl.hasOption("v"));
    Preconditions.checkArgument(cl.hasOption("i"));

    String[] i = cl.hasOption("i") ? splitPathEntries(cl.getOptionValues("i")) : new String[0];

    String[] p = cl.hasOption("path") ? splitPathEntries(cl.getOptionValues("path")) : new String[0];

    String pp = cl.hasOption("pp") ? Optional.of(cl.getOptionValue("pp")).orElse("") : null;

    String s = cl.getOptionValue("s");

    String r = cl.getOptionValue("r");

    boolean c2mc = cl.hasOption("c2mc");

    boolean novar = cl.hasOption("novar");

    this.run(i, p, pp, s, r, c2mc, novar);
  }

  protected void run(@NotNull String[] i,
                     @NotNull String[] p,
                     @Nullable String pp,
                     @Nullable String s,
                     @Nullable String r,
                     boolean c2mc,
                     boolean novar) {
    Preconditions.checkNotNull(i);
    Preconditions.checkNotNull(p);
    Preconditions.checkArgument(i.length > 0);

    MontiArcMill.globalScope().clear();
    MontiArcMill.globalScope().init();
    this.initBuildInSymbols(c2mc);
    this.initGlobalScope(p);
    this.compile(i, pp, s, r, c2mc, novar);
  }

  /**
   * Parses all MontiArc component models found in the specified input files
   * and directories and checks context-condition. Optionally pretty-prints
   * the models, serializes their symbol table, and generates reports.
   * <p>
   * Class2MC (c2mc) can be enabled to import symbols from the Java runtime
   * environment (Java RTE). Context-condition checking for variable components
   * can be skipped (novar) to improve performance.
   *
   * @param i     Array of file and directory paths that form the modelpath,
   *              i.e, the paths that contain the MontiArc models to be compiled.
   *              At least one path must be provided.
   * @param pp    Path to the directory where pretty-printed models should be stored.
   *              If {@code null}, pretty-printing is disabled.
   *              If an empty string is provided, models are printed to standard output.
   * @param s     Path to the directory where the symbol table should be serialized.
   *              If {@code null}, symbol table serialization is disabled.
   * @param r     Path to the directory where reports should be stored.
   *              If {@code null}, report generation is disabled.
   * @param c2mc  Enables importing of Java symbols (via Class2MC).
   * @param novar Disables context-condition checking for variable components to improve performance.
   * @return A collection of the abstract syntay trees (ASTs) of the parsed
   * input models found in the given model paths.
   */
  public Set<ASTMACompilationUnit> compile(@NotNull String[] i,
                                           @Nullable String pp,
                                           @Nullable String s,
                                           @Nullable String r,
                                           boolean c2mc,
                                           boolean novar) {
    Preconditions.checkNotNull(i);
    Preconditions.checkArgument(i.length > 0);

    Log.info(() -> "Parse the input models", "MontiArcTool");
    Set<ASTMACompilationUnit> asts = this.parse(i);

    this.runAfterParserCoCos(asts);

    Log.enableFailQuick(true);

    this.defaultImportTrafo(asts, c2mc);

    Log.enableFailQuick(false);

    Log.info(() -> "Run post parsing transformations", "MontiArcTool");
    this.runAfterParsingTrafos(asts);

    Log.info(() -> "Run symbol-table creation phase 1", "MontiArcTool");
    Collection<IMontiArcArtifactScope> scopes = this.createSymbolTable(asts);

    Log.info(() -> "Run symbol-table creation phase 2", "MontiArcTool");
    this.runSymbolTablePhase2(asts);

    Log.info(() -> "Run post symbol-table creation transformations", "MontiArcTool");
    this.runAfterSymbolTablePhase2Trafos(asts);

    Log.info(() -> "Run symbol-table creation phase 3", "MontiArcTool");
    this.runSymbolTablePhase3(asts);

    Log.info(() -> "Perform initial context-condition checks", "MontiArcTool");
    this.runDefaultCoCos(asts);

    Log.info(() -> "Perform remaining context-condition checks", "MontiArcTool");
    this.runAdditionalCoCos(asts, !novar);

    Log.enableFailQuick(true);

    if (pp != null) {
      Log.info(() -> "Pretty print models", "MontiArcTool");
      this.prettyPrint(asts, pp);
    }

    if (s != null) {
      Log.info(() -> "Print symbol table", "MontiArcTool");
      this.storeSymbols(scopes, i, s, r);
    }

    return asts;
  }

  protected void runCreate(String name, CommandLine cl) {
    String templateName = "montiarc-templates-main/";
    if (cl.hasOption("t")) {
      templateName += cl.getOptionValue("t").toLowerCase();
    } else {
      templateName += "empty";
    }
    // download and unzip
    try {
      Path zip = Files.createTempFile("MontiArcTemplateProject", ".zip");
      String gitTag = this.versionSupplier.get().contains("SNAPSHOT") ? "heads/main" : ("tags/" + this.versionSupplier.get().substring(0, 5));
      Log.info(() -> "Downloading template...", "MontiArcTool");
      FileUtils.copyURLToFile(new URI("https://github.com/MontiCore/montiarc-templates/archive/refs/" + gitTag + ".zip").toURL(), zip.toFile());
      Log.info(() -> "Creating Project " + name, "MontiArcTool");
      boolean foundTemplate = false;
      try (java.util.zip.ZipFile zipFile = new ZipFile(zip.toFile())) {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
          ZipEntry entry = entries.nextElement();
          if (!entry.getName().toLowerCase().startsWith(templateName)) continue;
          if (!foundTemplate) foundTemplate = true;
          File entryDestination = new File(name, entry.getName().substring(templateName.length()));
          if (entry.isDirectory()) {
            entryDestination.mkdirs();
          } else {
            entryDestination.getParentFile().mkdirs();
            try (InputStream in = zipFile.getInputStream(entry);
                 OutputStream out = new FileOutputStream(entryDestination)) {
              IOUtils.copy(in, out);
            }
          }
        }
      }
      if (!foundTemplate) Log.error(MontiArcError.TOOL_CREATE_TEMPLATE_NOT_EXIST.format(cl.getOptionValue("t")));
    } catch (IOException | SecurityException e) {
      Log.error(e.getMessage());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public void defaultImportTrafo(@NotNull Collection<ASTMACompilationUnit> asts, boolean c2mc) {
    Preconditions.checkNotNull(asts);
    asts.forEach(ast -> defaultImportTrafo(ast, c2mc));
  }

  public void defaultImportTrafo(@NotNull ASTMACompilationUnit ast, boolean c2mc) {
    Preconditions.checkNotNull(ast);
    if (c2mc) {
      ast.addImportStatement(MontiArcMill.mCImportStatementBuilder()
        .setMCQualifiedName(MontiArcMill.mCQualifiedNameBuilder()
          .setPartsList(List.of("java", "lang"))
          .build())
        .setStar(true)
        .build());
    }
  }

  public Set<ASTMACompilationUnit> parse(@NotNull String[] paths) {
    Preconditions.checkNotNull(paths);
    Preconditions.checkArgument(paths.length > 0);

    List<Path> pathList = List.copyOf(new MCPath(paths).getEntries());

    for (int i = 0; i < pathList.size(); i++) {
      for (int j = i + 1; j < pathList.size(); j++) {
        if (pathList.get(i).startsWith(pathList.get(j).toString() + File.separator)) {
          Log.error(MontiArcError.SUPERIMPOSED_MODELPATH.format(pathList.get(j).toString(), pathList.get(i).toString()));
        } else if (pathList.get(j).startsWith(pathList.get(i).toString() + File.separator)) {
          Log.error(MontiArcError.SUPERIMPOSED_MODELPATH.format(pathList.get(i).toString(), pathList.get(j).toString()));
        }
      }
    }

    Log.enableFailQuick(true);
    Log.enableFailQuick(false);

    return this.parse("arc", pathList);
  }

  public Set<ASTMACompilationUnit> parse(@NotNull String fileExt,
                                         @NotNull Collection<Path> paths) {
    Preconditions.checkNotNull(fileExt);
    Preconditions.checkNotNull(paths);
    Preconditions.checkArgument(!fileExt.isEmpty());

    Set<ASTMACompilationUnit> asts = new HashSet<>();
    for (Path path : paths) {
      asts.addAll(this.parse(fileExt, path));
    }
    return Collections.unmodifiableSet(asts);
  }

  public Set<ASTMACompilationUnit> parse(@NotNull String fileExt,
                                         @NotNull Path path) {
    Preconditions.checkNotNull(fileExt);
    Preconditions.checkNotNull(path);
    Preconditions.checkArgument(!fileExt.isEmpty());

    File filepath = path.toFile();

    if (!filepath.exists()) {
      Log.warn("Directory does not exist: " + path);
      return Collections.emptySet();
    }

    Set<ASTMACompilationUnit> asts = new HashSet<>();
    if (filepath.isFile()) {
      this.parse(filepath, filepath).ifPresent(asts::add);
    } else if (filepath.isDirectory()) {
      for (File file : FileUtils.listFiles(filepath, new String[]{fileExt}, true)) {
        this.parse(filepath, file).ifPresent(asts::add);
      }
    }

    return Collections.unmodifiableSet(asts);
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
              ast.get().getArcComponentType().get_SourcePositionStart(),
            ast.get().isPresentPackage() ?
              ast.get().getPackage().get_SourcePositionEnd() :
              ast.get().getArcComponentType().get_SourcePositionEnd()
          );
        }
      } else if (root.isFile()) {
        final String rfp = Names.getPackageFromPath(root.toPath().getParent().toString());
        if (!rfp.endsWith(pkg)) {
          // If the root is the file itself than the package should be a suffix of the file path
          Log.error(String.format(MontiArcError.PACKAGE_AND_FILE_PATH_DIFFER.toString(), pkg, rfp),
            ast.get().isPresentPackage() ?
              ast.get().getPackage().get_SourcePositionStart() :
              ast.get().getArcComponentType().get_SourcePositionStart(),
            ast.get().isPresentPackage() ?
              ast.get().getPackage().get_SourcePositionEnd() :
              ast.get().getArcComponentType().get_SourcePositionEnd()
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

  public void runAfterSymbolTablePhase2Trafos(@NotNull Collection<ASTMACompilationUnit> asts) {
    Preconditions.checkNotNull(asts);
    asts.forEach(this::runAfterSymbolTablePhase2Trafos);
  }

  public void runAfterSymbolTablePhase2Trafos(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);
    MontiArcTrafos.afterSymTabP2().applyAll(ast);
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
    MontiArcCoCos.afterSymTab1().checkAll(ast);
  }

  public void runAdditionalCoCos(@NotNull Collection<ASTMACompilationUnit> asts) {
    Preconditions.checkNotNull(asts);
    asts.forEach(this::runAdditionalCoCos);
  }

  @Override
  public void runAdditionalCoCos(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);
    MontiArcCoCos.afterSymTab2().checkAll(ast);
  }

  public void runAdditionalCoCos(@NotNull Collection<ASTMACompilationUnit> asts, boolean checkVariability) {
    Preconditions.checkNotNull(asts);
    asts.forEach(a -> this.runAdditionalCoCos(a, checkVariability));
  }

  public void runAdditionalCoCos(@NotNull ASTMACompilationUnit ast, boolean checkVariability) {
    Preconditions.checkNotNull(ast);
    MontiArcCoCos.afterSymTab2(checkVariability).checkAll(ast);
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
      String pkgPath = "";
      if (ast.isPresentPackage()) {
        pkgPath = Names.getPathFromQualifiedName(ast.getPackage().getQName());
      }
      file = Paths.get(file, pkgPath, ast.getArcComponentType().getName() + ".arc").toString();
    }
    this.print(MontiArcMill.prettyPrint(ast, true), file);
  }

  @Override
  public void print(@NotNull String content, @NotNull String path) {
    Preconditions.checkNotNull(content);
    Preconditions.checkNotNull(path);
    super.print(content, path);
  }

  public void storeSymbols(@NotNull Collection<IMontiArcArtifactScope> scopes,
                           @NotNull String[] input,
                           @NotNull String symboltableDir,
                           @Nullable String reports) {
    Preconditions.checkNotNull(scopes);
    Preconditions.checkNotNull(symboltableDir);

    Optional<String> reportDir = Optional.ofNullable(reports);
    Collection<IMontiArcArtifactScope> scopes4NewSerialization;
    Map<IMontiArcArtifactScope, ASTMACompilationUnit> scopeToAst;

    boolean writeReports = reportDir.isPresent() && !(input.length == 0);
    if (writeReports) {
      IncCheckUtil.Config incCheckConfig = new IncCheckUtil.Config(
        Arrays.asList(input), symboltableDir, reportDir.get(), SYMBOLS_REPORT_DIR, Collections.emptyList(), versionSupplier.get()
      );
      IncCheckUtil.configureIncCheckReporting(incCheckConfig);

      scopeToAst = extractCompUnitsFrom(scopes);

      Map<String, ASTMACompilationUnit> astByQName = IncCheckUtil.resolveAstByQName(scopeToAst.values());
      UpToDateResults upToDateInfo = IncCheckUtil.calcUpToDateData(astByQName, incCheckConfig);

      IncCheckUtil.removeOutdatedGenerationResults(upToDateInfo, incCheckConfig);
      scopes4NewSerialization =
        IncCheckUtil.calcReportedModelsForNewGeneration(upToDateInfo, astByQName)
          .stream()
          .map(a -> a.getArcComponentType().getEnclosingScope())
          .map(s -> (IMontiArcArtifactScope) s)
          .collect(Collectors.toList());
    } else {
      scopes4NewSerialization = scopes;
      scopeToAst = new HashMap<>(0);
    }

    MCPath modelPaths = new MCPath(input);

    for (IMontiArcArtifactScope scope : scopes4NewSerialization) {
      Optional<ASTMACompilationUnit> ast = Optional.ofNullable(scopeToAst.get(scope));

      Optional<Path> modelLocation = ast.flatMap(a -> IncCheckUtil.findModelLocation(modelPaths, a));
      boolean writeReport4ThisModel = writeReports && modelLocation.isPresent();  // implies ast.isPresent

      // Init reporting for current ast
      if (writeReport4ThisModel) {
        IncCheckUtil.setIncCheckReportingOn(ast.orElseThrow(), modelLocation.get());
      }

      // In all cases
      this.storeSymbols(scope, symboltableDir);

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
    entries.forEach(entry -> MontiArcMill.globalScope().getSymbolPath().addEntry(entry));
  }

  public void initializeStreams() {
    URL streamURL = MontiArcTool.class.getClassLoader().getResource("Stream.symtabdefinitionsym");
    if (streamURL == null) return;
    try {
      JarURLConnection urlConnection = (JarURLConnection) streamURL.openConnection();
      JarFile jar = urlConnection.getJarFile();
      Path jarPath = Path.of(jar.getName());
      MontiArcMill.globalScope().getSymbolPath().addEntry(jarPath);
    } catch (IOException ignored) {}
  }


  public void initializeClass2MC() {
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
  }

  protected void initBuildInSymbols(boolean c2mc) {
    BasicSymbolsMill.initializePrimitives();
    initializeStreams();
    if (c2mc) {
      this.initializeClass2MC();
    } else {
      BasicSymbolsMill.initializeObject();
      BasicSymbolsMill.initializeString();
    }
  }

  @Override
  public Options addStandardOptions(@NotNull Options options) {
    Preconditions.checkNotNull(options);
    super.addStandardOptions(options);

    // allow multiple inputs
    Option i = options.getOption("i");
    i.setDescription("Parses alls MontiArc models from the specified files or directories (recursively)");
    i.setArgs(Option.UNLIMITED_VALUES);
    i.setArgName("paths");

    // allow path to be used as long option also
    Option p = options.getOption("path");
    p.setLongOpt("path");
    p.setArgName("dir");

    // print to a directory instead of a file
    Option pp = options.getOption("pp");
    pp.setDescription("Prints the models to stdout or the specified directory (optional)");
    pp.setArgName("dir");

    // serialize to a directory instead of a file
    Option s = options.getOption("s");
    s.setDescription("Serializes the symbol table of the given artifacts to the specified directory");
    s.setArgName("dir");

    options.addOption(Option.builder("c2mc")
      .longOpt("class2mc")
      .desc("Enables importing java symbols from the java runtime environment")
      .build());

    options.addOption(Option.builder("novar")
      .longOpt("no-variability-checks")
      .desc("Disable the analysis of variable components for better performance")
      .build());

    options.addOption(Option.builder("d")
      .longOpt("debug")
      .desc("Enables verbose logging with debug-level information")
      .build());

    options.addOption(Option.builder("t")
      .longOpt("trace")
      .desc("Enables verbose logging with trace-level information")
      .build());

    return options;
  }

  protected Options initCreateOptions() {
    Options options = new Options();
    options.addOption(Option.builder("t")
      .longOpt("template")
      .optionalArg(true)
      .numberOfArgs(1)
      .argName("templateName")
      .desc("The project template that should be used for the new project.")
      .build());
    return options;
  }

  protected void printHelp() {
    org.apache.commons.cli.HelpFormatter formatter = new org.apache.commons.cli.HelpFormatter();
    formatter.setWidth(80);
    formatter.printHelp("MontiArcTool [build]", " The main MontiArc build command.", initOptions(), "", true);
    formatter.printHelp("MontiArcTool create <name>", " Create a new MontiArc project with the given name in the current folder.", initCreateOptions(), "", true);
  }

  /**
   * For scopes whose astNode is an instance of {@link ASTMACompilationUnit}, returns a map of the scope to its astNode.
   * Other scopes are ignored and not included in the result.
   */
  protected Map<IMontiArcArtifactScope, ASTMACompilationUnit> extractCompUnitsFrom(
    @NotNull Collection<IMontiArcArtifactScope> scopes) throws IllegalStateException {
    Preconditions.checkNotNull(scopes);

    if (scopes.stream().map(IMontiArcArtifactScope::getAstNode).anyMatch(a -> !(a instanceof ASTMACompilationUnit))) {
      Log.debug(() ->
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
   * Splits composedPath on their {@link File#pathSeparator},
   * e.g. {@code some/path:another/path} on Unix
   * would return {@code {some/path, another/path}}
   * and {@code some\path;other\path} on Windows
   * would return {@code {some\path, other\path}}
   */
  protected final @NotNull String[] splitPathEntries(@NotNull String paths) {
    Preconditions.checkNotNull(paths);

    return paths.split(Pattern.quote(File.pathSeparator));
  }

  /**
   * {@link this#splitPathEntries(String)} on every entry of <i>composedPath</i>.
   */
  protected final @NotNull String[] splitPathEntries(@NotNull String[] paths) {
    Preconditions.checkNotNull(paths);
    return Arrays.stream(paths)
      .map(this::splitPathEntries)
      .flatMap(Arrays::stream)
      .toArray(String[]::new);
  }
}
