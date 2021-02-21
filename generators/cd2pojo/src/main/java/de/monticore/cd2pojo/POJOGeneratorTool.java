/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import com.google.common.base.Preconditions;
import de.monticore.cd2pojo.cocos.CDAssociationNamesUnique;
import de.monticore.cd2pojo.cocos.CDEllipsisParametersOnlyInLastPlace;
import de.monticore.cd2pojo.cocos.CDRoleNamesUnique;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.ICD4CodeScope;
import de.monticore.cd4code.cocos.CD4CodeCoCos;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.generating.GeneratorSetup;
import de.monticore.io.paths.ModelPath;
import de.monticore.types.check.DefsTypeBasic;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class POJOGeneratorTool {
  
  protected POJOGenerator generator;
  
  public POJOGeneratorTool(@NotNull POJOGenerator generator) {
    Preconditions.checkNotNull(generator);
    this.generator = generator;
    initMill();
  }
  
  public POJOGeneratorTool(@NotNull GeneratorSetup setup) {
    this(new POJOGenerator(setup));
  }
  
  public POJOGeneratorTool(@NotNull Path targetDir, @NotNull Path hwcPath) {
    this(new POJOGenerator(targetDir, hwcPath));
  }
  
  protected void initMill() {
    CD4CodeMill.init();
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
    generateAll(symbolsToGenerate);

    //TODO Serialize
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
    Log.debug("Now generating CD type symbol " + typeSymbol.getName(), "POJO Generator Tool");
    generator.generate(typeSymbol);
  }
  
  protected static String cdFileExtension = "cd";
  
  public static ICD4CodeGlobalScope loadCD4CModelsFromPaths(@NotNull Collection<Path> paths, @NotNull CD4CodeCoCoChecker cdChecker) {
    Preconditions.checkNotNull(paths);
    Preconditions.checkNotNull(cdChecker);
    ModelPath mp = new ModelPath(paths);
    ICD4CodeGlobalScope cd4CGlobalScope = CD4CodeMill.globalScope();
    cd4CGlobalScope.clear();
    cd4CGlobalScope.setModelPath(mp);
    cd4CGlobalScope.setFileExt(cdFileExtension);
    processModels(cd4CGlobalScope, cdChecker);
    addBasicTypes(cd4CGlobalScope);
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
    CD4CodeMill.init();
    Set<ASTCDCompilationUnit> set = new HashSet<>();
    for (ASTCDCompilationUnit astcdCompilationUnit : parseAndTransformModels(scope)) {
      CD4CodeMill.scopesGenitorDelegator().createFromAST(astcdCompilationUnit);
      set.add(astcdCompilationUnit);
    }
    return set;
  }
  
  public static Collection<ASTCDCompilationUnit> parseAndTransformModels(@NotNull ICD4CodeGlobalScope scope) {
    Preconditions.checkNotNull(scope);
    return scope.getModelPath().getFullPathOfEntries().stream()
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
  
  protected static void addBasicTypes(@NotNull ICD4CodeGlobalScope scope) {
    Preconditions.checkNotNull(scope);
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
}