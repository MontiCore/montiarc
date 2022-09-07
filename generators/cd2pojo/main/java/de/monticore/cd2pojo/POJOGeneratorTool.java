/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import com.google.common.base.Preconditions;
import de.monticore.cd2pojo.cocos.CDAssociationNamesUnique;
import de.monticore.cd2pojo.cocos.CDEllipsisParametersOnlyInLastPlace;
import de.monticore.cd2pojo.cocos.CDRoleNamesUnique;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.*;
import de.monticore.cd4code.cocos.CD4CodeCoCos;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.generating.GeneratorSetup;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class POJOGeneratorTool {
  
  protected POJOGenerator generator;

  /** Whether the generator should terminate before the generation process in case of errors in the models. */
  boolean failBeforeGenerationIfModelErrors;
  
  public POJOGeneratorTool(@NotNull POJOGenerator generator, boolean failBeforeGenerationIfModelErrors) {
    Preconditions.checkNotNull(generator);
    this.generator = generator;
    this.failBeforeGenerationIfModelErrors = failBeforeGenerationIfModelErrors;
  }
  
  public POJOGeneratorTool(@NotNull GeneratorSetup setup) {
    this(setup, true);
  }

  public POJOGeneratorTool(@NotNull GeneratorSetup setup, boolean failBeforeGenerationIfModelErrors) {
    this(new POJOGenerator(setup), failBeforeGenerationIfModelErrors);
  }
  
  public POJOGeneratorTool(@NotNull Path targetDir, @NotNull Path hwcPath) {
    this(new POJOGenerator(targetDir, hwcPath), true);
  }

  public POJOGeneratorTool(@NotNull Path targetDir, @NotNull Path hwcPath, boolean failBeforeGenerationIfModelErrors) {
    this(new POJOGenerator(targetDir, hwcPath), failBeforeGenerationIfModelErrors);
  }

  public void generateCDTypesInPaths(@NotNull Collection<Path> modelPaths) {
    Preconditions.checkNotNull(modelPaths);
    List<CDTypeSymbol> symbolsToGenerate = new ArrayList<>();
    ICD4CodeGlobalScope globalScope = getLoadedICD4CodeGlobalScope(modelPaths);
    for (ICD4CodeScope artifactScope : globalScope.getSubScopes()) {
      for(ICD4CodeScope sub : artifactScope.getSubScopes()) {
        symbolsToGenerate.addAll(sub.getLocalCDTypeSymbols());
      }
    }

    terminateOnModelErrorsIfEnabled();

    for (ICD4CodeScope scope: globalScope.getSubScopes()) {
      if (scope instanceof ICD4CodeArtifactScope) {
        printCDSymTab2Json((ICD4CodeArtifactScope) scope, this.generator.outputDirectory.toPath());
      }
    }
    generateAll(symbolsToGenerate);
  }

  protected void printCDSymTab2Json(@NotNull ICD4CodeArtifactScope artifactScope, @NotNull Path targetPath) {
    Preconditions.checkNotNull(artifactScope);
    Preconditions.checkNotNull(targetPath);
    if (artifactScope.getLocalDiagramSymbols().isEmpty()) return;
    if (artifactScope.getLocalCDPackageSymbols().isEmpty()) return;

    String serializedCDSymTab = new CD4CodeSymbols2Json().serialize(artifactScope);

    Path outputFilePath = Paths.get(targetPath.toAbsolutePath().toString(),
      artifactScope.getLocalCDPackageSymbols().get(0).getName().replaceAll("\\.", "/"),
      artifactScope.getLocalDiagramSymbols().get(0).getName() + ".sym");

    FileReaderWriter.storeInFile(outputFilePath, serializedCDSymTab);
  }
  
  public void generateCDTypesInPath(@NotNull Path modelPath) {
    Preconditions.checkNotNull(modelPath);
    generateCDTypesInPaths(Collections.singleton(modelPath));
  }
  
  public static ICD4CodeGlobalScope getLoadedICD4CodeGlobalScope(@NotNull Collection<Path> modelPaths) {
    Preconditions.checkNotNull(modelPaths);
    CD4CodeCoCoChecker checker = new CD4CodeCoCos().getCheckerForAllCoCos();
    checker.addCoCo(new CDAssociationNamesUnique());
    checker.addCoCo(new CDRoleNamesUnique());
    checker.addCoCo(new CDEllipsisParametersOnlyInLastPlace());
    return loadCD4CModelsFromPaths(modelPaths, checker);
  }
  
  /**
   * prepares the types for generation and then serializes symboltables and generates classes
   *
   * @param symbols collection to process
   */
  protected void generateAll(@NotNull Collection<CDTypeSymbol> symbols) {
    Preconditions.checkNotNull(symbols);
    symbols.forEach(this::doGenerate);
  }
  
  protected void doGenerate(@NotNull CDTypeSymbol typeSymbol) {
    Preconditions.checkNotNull(typeSymbol);
    Log.info("Now generating CD type symbol " + typeSymbol.getName(), "POJO Generator Tool");
    generator.generate(typeSymbol);
  }
  
  protected static String cdFileExtension = "cd";
  
  public static ICD4CodeGlobalScope loadCD4CModelsFromPaths(@NotNull Collection<Path> paths, @NotNull CD4CodeCoCoChecker cdChecker) {
    Preconditions.checkNotNull(paths);
    Preconditions.checkNotNull(cdChecker);
    ICD4CodeGlobalScope cd4CGlobalScope = CD4CodeMill.globalScope();
    for (Path path: paths) {
      cd4CGlobalScope.getSymbolPath().addEntry(path);
    }
    cd4CGlobalScope.setFileExt(cdFileExtension);
    BasicSymbolsMill.initializePrimitives();
    processModels(cd4CGlobalScope, cdChecker);
    return cd4CGlobalScope;
  }
  
  public static void processModels(@NotNull ICD4CodeGlobalScope scope, @NotNull CD4CodeCoCoChecker cdChecker) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(cdChecker);
    for (ASTCDCompilationUnit compUnit : createSymbolTable(scope)) {
      compUnit.accept(cdChecker.getTraverser());
    }
  }
  
  public static Collection<ASTCDCompilationUnit> createSymbolTable(@NotNull ICD4CodeGlobalScope scope) {
    Preconditions.checkNotNull(scope);
    Set<ASTCDCompilationUnit> set = new HashSet<>();
    for (ASTCDCompilationUnit ast : parseAndTransformModels(scope)) {
      CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
      set.add(ast);
    }
    for (ASTCDCompilationUnit ast : set) {
      CD4CodeSymbolTableCompleter completer = new CD4CodeSymbolTableCompleter(ast);
      ast.accept(completer.getTraverser());
    }
    return set;
  }
  
  public static Collection<ASTCDCompilationUnit> parseAndTransformModels(@NotNull ICD4CodeGlobalScope scope) {
    Preconditions.checkNotNull(scope);
    return scope.getSymbolPath().getEntries().stream()
      .flatMap(path -> parseAndTransformCD(path).stream())
      .collect(Collectors.toList());
  }
  
  public static Collection<ASTCDCompilationUnit> parseAndTransformCD(@NotNull Path path) {
    Preconditions.checkNotNull(path);
    try {
      return Files.walk(path).filter(Files::isRegularFile)
        .filter(f -> f.getFileName().toString().endsWith(cdFileExtension))
        .map(f -> parseAndTransformCD(f.toString()))
        .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    } catch (IOException e) {
      Log.error("Could not access " + path.toString() + ", there were I/O exceptions.");
    }
    return new ArrayList<>();
  }
  
  public static Optional<ASTCDCompilationUnit> parseAndTransformCD(@NotNull String filename) {
    Preconditions.checkNotNull(filename);
    return parseCD(filename).map((ASTCDCompilationUnit node) -> {
      new CD4CodeAfterParseTrafo().transform(node);
      return node;
    });
  }
  
  public static Optional<ASTCDCompilationUnit> parseCD(@NotNull String filename) {
    Preconditions.checkNotNull(filename);
    CD4CodeParser p = CD4CodeMill.parser();
    ASTCDCompilationUnit cd = null;
    try {
      cd = p.parse(filename).orElse(null);
    } catch (IOException e) {
      Log.error("Could not access " + filename + ", there were I/O exceptions.");
    }
    return Optional.ofNullable(cd);
  }

  /**
   * If attribute {@link this#failBeforeGenerationIfModelErrors} is set, this method will send the logger the signal to
   * terminate this process in case there are errors in the log. The state of fail quick is not relevant for this
   * termination process. It will not be affected by this method, either.
   */
  private void terminateOnModelErrorsIfEnabled() {
    // The following lines ensure that errors in the models will terminate this GeneratorTool before starting the
    // generation process
    if(failBeforeGenerationIfModelErrors) {
      Log.info("The cd2java generation process will be aborted here if some class diagrams models contain errors.",
        "PojoGeneratorTool");
      boolean wasFailQuickEnabled = Log.isFailQuickEnabled();
      Log.enableFailQuick(true);
      Log.enableFailQuick(wasFailQuickEnabled);
    }
  }
}