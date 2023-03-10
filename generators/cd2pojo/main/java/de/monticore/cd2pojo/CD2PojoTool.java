/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import com.google.common.base.Preconditions;
import de.monticore.CDGeneratorTool;
import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.codegen.TopDecorator;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd2pojo.cocos.CD2PojoCoCosDelegator;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code._symboltable.CD4CodeScopesGenitorDelegator;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDPackageSymbolDeSer;
import de.monticore.cdbasis.trafo.CDBasisDefaultPackageTrafo;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
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
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CD2PojoTool extends CDGeneratorTool {

  public static void main(String[] args) {
    CD2PojoTool tool = new CD2PojoTool();
    tool.run(args);
  }

  @Override
  public void run(String[] args) {

    this.init();

    Options options = initOptions();

    try {
      CommandLineParser cliParser = new DefaultParser();
      CommandLine cmd = cliParser.parse(options, args);

      if (!cmd.hasOption("i")) {
        printHelp(options);
        return;
      }

      if (cmd.hasOption("c2mc")) {
        initializeClass2MC();
      }

      Log.enableFailQuick(false);
      Collection<ASTCDCompilationUnit> asts = this.parse(".cd", this.createModelPath(cmd).getEntries());
      Log.enableFailQuick(true);

      asts = transform(asts);

      if (cmd.hasOption("path")) {
        String[] paths = splitPathEntries(cmd.getOptionValue("path"));
        CD4CodeMill.globalScope().setSymbolPath(new MCPath(paths));
      }

      Log.enableFailQuick(false);
      Collection<ICD4CodeArtifactScope> scopes = this.createSymbolTable(asts);
      this.completeSymbolTable(asts);
      Log.enableFailQuick(true);

      if (cmd.hasOption("c")) {
        Log.enableFailQuick(false);
        runCoCos(asts);
        Log.enableFailQuick(true);
      }

      if (cmd.hasOption("gen")) {
        GlobalExtensionManagement glex = new GlobalExtensionManagement();
        glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
        GeneratorSetup setup = new GeneratorSetup();

        // setup default package when generating
        CD4CodeTraverser t = CD4CodeMill.traverser();
        t.add4CDBasis(new CDBasisDefaultPackageTrafo());
        asts.forEach(ast -> ast.accept(t));

        if (cmd.hasOption("tp")) {
          setup.setAdditionalTemplatePaths(
            Arrays.stream(cmd.getOptionValues("tp"))
              .map(Paths::get)
              .map(Path::toFile)
              .collect(Collectors.toList()));
        }

        if (cmd.hasOption("hwc")) {
          setup.setHandcodedPath(new MCPath(Paths.get(cmd.getOptionValue("hwc"))));
          TopDecorator topDecorator = new TopDecorator(setup.getHandcodedPath());
          asts = asts.stream().map(topDecorator::decorate).collect(Collectors.toList());
        }

        setup.setGlex(glex);
        setup.setOutputDirectory(new File(cmd.getOptionValue("o")));

        CDGenerator generator = new CDGenerator(setup);
        String configTemplate = cmd.getOptionValue("ct", "cd2java.CD2Java");
        TemplateController tc = setup.getNewTemplateController(configTemplate);
        TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);
        List<Object> configTemplateArgs = Arrays.asList(glex, generator);

        asts.forEach(ast -> mapCD4CImports(CD4C.getInstance(), ast));

        asts.forEach(ast -> hpp.processValue(tc, ast, configTemplateArgs));
      }

      if (cmd.hasOption("s")) {
        this.storeSymTab(scopes, cmd.getOptionValue("s"));
      }

    } catch (
      ParseException e) {
      Log.error("0xA7105 Could not process parameters: " + e.getMessage());
    }
  }

  public Collection<ASTCDCompilationUnit> parse(@NotNull String fileExt, @NotNull Collection<Path> directories) {
    Preconditions.checkNotNull(fileExt);
    Preconditions.checkNotNull(directories);
    Preconditions.checkArgument(!fileExt.isEmpty());
    return directories.stream().flatMap(directory -> this.parse(fileExt, directory).stream()).collect(Collectors.toList());
  }

  public Collection<ASTCDCompilationUnit> parse(@NotNull String fileExt, @NotNull Path directory) {
    Preconditions.checkNotNull(fileExt);
    Preconditions.checkNotNull(directory);
    Preconditions.checkArgument(!fileExt.isEmpty());
    Preconditions.checkArgument(directory.toFile().exists(), "Directory does not exist: " + directory);
    Preconditions.checkArgument(directory.toFile().isDirectory(), "Directory is file: " + directory);
    try (Stream<Path> paths = Files.walk(directory)) {
      return paths.filter(Files::isRegularFile)
        .filter(file -> file.getFileName().toString().endsWith(fileExt)).map(Path::toString)
        .map(this::parse)
        .collect(Collectors.toSet());
    } catch (
      IOException e) {
      Log.error("0xA1063 Error while traversing the file structure `" + directory + "`.", e);
    }
    return Collections.emptySet();
  }

  protected Collection<ASTCDCompilationUnit> transform(Collection<ASTCDCompilationUnit> asts) {
    return asts.stream().map(this::transform).collect(Collectors.toList());
  }

  protected Collection<ICD4CodeArtifactScope> createSymbolTable(Collection<ASTCDCompilationUnit> asts) {
    return asts.stream().map(this::createSymbolTable).collect(Collectors.toList());
  }

  @Override
  public ICD4CodeArtifactScope createSymbolTable(ASTCDCompilationUnit ast) {
    CD4CodeScopesGenitorDelegator genitor = CD4CodeMill.scopesGenitorDelegator();
    ICD4CodeArtifactScope scope = genitor.createFromAST(ast);
    if (ast.isPresentMCPackageDeclaration()) {
      scope.setPackageName(ast.getMCPackageDeclaration().getMCQualifiedName().getQName());
    }
    return scope;
  }

  protected void completeSymbolTable(Collection<ASTCDCompilationUnit> asts) {
    asts.forEach(this::completeSymbolTable);
  }

  @Override
  public void completeSymbolTable(ASTCDCompilationUnit ast) {
    ast.accept(new CD4CodeSymbolTableCompleter(ast).getTraverser());
  }

  protected void runCoCos(Collection<ASTCDCompilationUnit> asts) {
    asts.forEach(this::runCoCos);
  }

  @Override
  protected void runCoCos(ASTCDCompilationUnit ast) {
    CD4CodeCoCoChecker checker = new CD2PojoCoCosDelegator().getCheckerForAllCoCos();
    checker.checkAll(ast);
  }

  protected MCPath createModelPath(@NotNull CommandLine cl) {
    Preconditions.checkNotNull(cl);

    if (cl.hasOption("i")) {
      return new MCPath(splitPathEntries(cl.getOptionValues("i")));
    } else {
      return new MCPath();
    }
  }

  protected final @NotNull String[] splitPathEntries(@NotNull String composedPath) {
    Preconditions.checkNotNull(composedPath);
    return composedPath.split(Pattern.quote(File.pathSeparator));
  }

  protected final @NotNull String[] splitPathEntries(@NotNull String[] composedPaths) {
    Preconditions.checkNotNull(composedPaths);
    return Arrays.stream(composedPaths)
      .map(this::splitPathEntries)
      .flatMap(Arrays::stream)
      .toArray(String[]::new);
  }

  protected void storeSymTab(Collection<ICD4CodeArtifactScope> scopes, String path) {
    SymTypeConvTrafo trafo = new SymTypeConvTrafo();
    for (ICD4CodeArtifactScope scope : scopes) {
      Collection<ICD4CodeArtifactScope> tfScopes = trafo.apply(scope);
      for (ICD4CodeArtifactScope tfScope : tfScopes) {
        String file = path + File.separator + Names.getPathFromPackage(tfScope.getFullName()) + ".sym";
        this.storeSymTab(tfScope, file);
      }
    }
  }

  @Override
  public void init() {
    CD4CodeMill.init();
    BasicSymbolsMill.initializePrimitives();
    CD4CodeMill.globalScope().getSymbolDeSers().clear();
    CD4CodeMill.globalScope().setDeSer(new CD2PojoDeSer());
    CD4CodeMill.globalScope().getSymbolDeSers().put("de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol", new de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbolDeSer());
    CD4CodeMill.globalScope().getSymbolDeSers().put("de.monticore.cdassociation._symboltable.CDAssociationSymbol", new de.monticore.cdassociation._symboltable.CDAssociationSymbolDeSer());
    CD4CodeMill.globalScope().getSymbolDeSers().put("de.monticore.cdassociation._symboltable.CDRoleSymbol", new de.monticore.cdassociation._symboltable.CDRoleSymbolDeSer());
    CD4CodeMill.globalScope().getSymbolDeSers().put("de.monticore.cdbasis._symboltable.CDPackageSymbol", new CDPackageSymbolDeSer());
    CD4CodeMill.globalScope().getSymbolDeSers().put("de.monticore.cdbasis._symboltable.CDTypeSymbol", new CD2PojoTypeSymbolDeSer());
    CD4CodeMill.globalScope().getSymbolDeSers().put("de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol", new de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolDeSer());
    CD4CodeMill.globalScope().getSymbolDeSers().put("de.monticore.symbols.oosymbols._symboltable.FieldSymbol", new de.monticore.symbols.oosymbols._symboltable.FieldSymbolDeSer());
    CD4CodeMill.globalScope().getSymbolDeSers().put("de.monticore.symbols.oosymbols._symboltable.MethodSymbol", new de.monticore.symbols.oosymbols._symboltable.MethodSymbolDeSer());
    CD4CodeMill.globalScope().getSymbolDeSers().put("de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol", new de.monticore.symbols.basicsymbols._symboltable.DiagramSymbolDeSer());
    CD4CodeMill.globalScope().getSymbolDeSers().put("de.monticore.symbols.basicsymbols._symboltable.TypeSymbol", new de.monticore.symbols.basicsymbols._symboltable.TypeSymbolDeSer());
    CD4CodeMill.globalScope().getSymbolDeSers().put("de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol", new de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbolDeSer());
    CD4CodeMill.globalScope().getSymbolDeSers().put("de.monticore.symbols.basicsymbols._symboltable.VariableSymbol", new de.monticore.symbols.basicsymbols._symboltable.VariableSymbolDeSer());
    CD4CodeMill.globalScope().getSymbolDeSers().put("de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol", new de.monticore.symbols.basicsymbols._symboltable.FunctionSymbolDeSer());
  }
}
