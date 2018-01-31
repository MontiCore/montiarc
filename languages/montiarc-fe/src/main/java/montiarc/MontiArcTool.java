/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;

import de.monticore.ModelingLanguageFamily;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._parser.MontiArcParser;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.MontiArcLanguageFamily;
import montiarc.cocos.MontiArcCoCos;
import montiarc.helper.JavaHelper;

/**
 * MontiArcTool
 *
 * @author Pfeiffer, Wortmann
 * @version $Revision$, $Date$
 */
public class MontiArcTool {
  
  protected ModelingLanguageFamily family;
  
  private MontiArcCoCoChecker checker;
  
  private boolean isSymTabInitialized;
  
  /**
   * Constructor for montiarc.MontiArcTool
   */
  public MontiArcTool() {
    family = new MontiArcLanguageFamily();
    checker = MontiArcCoCos.createChecker();
    isSymTabInitialized = false;
  }
  
  /**
   * Constructor for montiarc.MontiArcTool
   */
  public MontiArcTool(ModelingLanguageFamily fam) {
    this.family = fam;
    checker = MontiArcCoCos.createChecker();
    isSymTabInitialized = false;
  }
  
  /**
   * Constructor for montiarc.MontiArcTool
   */
  public MontiArcTool(ModelingLanguageFamily fam, MontiArcCoCoChecker checker) {
    this.family = fam;
    this.checker = checker;
    isSymTabInitialized = false;
  }
  
  public Optional<ASTMACompilationUnit> parse(String filename) {
    MontiArcParser p = new MontiArcParser();
    Optional<ASTMACompilationUnit> compUnit;
    try {
      compUnit = p.parse(filename);
      return compUnit;
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return Optional.empty();
    
  }
  
  /**
   * Executes CoCos on MontiArcNode
   * 
   * @param node
   * @return true if no errors occurred
   */
  public boolean checkCoCos(ASTMontiArcNode node) {
    if (!isSymTabInitialized) {
      Log.error("Symtab has to be initialized before checking CoCos");
      return false;
    }
    if (!node.getSymbol().isPresent()) {
      Log.error(
          "Symtab is not linked with passed node! Call getSymbol() or getASTNode() for getting the ast.");
    }
    
    checker.checkAll(node);
    if (Log.getErrorCount() != 0) {
      return false;
    }
    return true;
  }
  
  public Optional<ComponentSymbol> loadComponentSymbolWithoutCocos(String componentName,
      File... modelPaths) {
    Scope s = initSymbolTable(modelPaths);
    return s.<ComponentSymbol> resolve(componentName, ComponentSymbol.KIND);
  }
  
  public Optional<ComponentSymbol> loadComponentSymbolWithCocos(String componentName,
      File... modelPaths) {
    Optional<ComponentSymbol> compSym = loadComponentSymbolWithoutCocos(componentName, modelPaths);
    
    if (compSym.isPresent()) {
      checkCoCos((ASTMontiArcNode) compSym.get().getAstNode().get());
    }
    
    return compSym;
  }
  
  public ASTMontiArcNode getAstNode(String modelPath, String model) {
    // ensure an empty log
    Log.getFindings().clear();
    Optional<ComponentSymbol> comp = loadComponentSymbolWithoutCocos(model,
        Paths.get(modelPath).toFile());
    
    if (!comp.isPresent()) {
      Log.error("Model could not be resolved!");
      return null;
    }
    
    return (ASTMontiArcNode) comp.get().getAstNode().get();
  }
  
  /**
   * Initializes the Symboltable by introducing scopes for the passed
   * modelpaths. It does not create the symbol table! Symbols for models within
   * the modelpaths are not added to the symboltable until resolve() is called.
   * 
   * @param modelPaths
   * @return
   */
  public Scope initSymbolTable(File... modelPaths) {
    Set<Path> p = Sets.newHashSet();
    for (File mP : modelPaths) {
      p.add(Paths.get(mP.getAbsolutePath()));
    }
    
    final ModelPath mp = new ModelPath(p);
    
    GlobalScope gs = new GlobalScope(mp, family);
    JavaHelper.addJavaPrimitiveTypes(gs);
    isSymTabInitialized = true;
    return gs;
  }
  
  /**
   * Create symbol table from modelpath as String
   * 
   * @param string
   * @return
   */
  public Scope initSymbolTable(String modelpath) {
    return initSymbolTable(Paths.get(modelpath).toFile());
  }
  
}
