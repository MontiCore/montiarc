/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.ajava.generator.codegen;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import _templates.de.montiarcautomaton.ajava.AJavaMain;
import de.montiarcautomaton.automatongenerator.helper.AJavaHelper;
import de.monticore.ModelingLanguageFamily;
import de.monticore.ast.ASTNode;
import de.monticore.automaton.ioautomaton.JavaHelper;
import de.monticore.io.paths.ModelPath;
import de.monticore.java.javadsl._ast.ASTBlockStatement;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.lang.montiarc.ajava._ast.ASTAJavaDefinition;
import de.monticore.lang.montiarc.ajava._ast.ASTComponentInitialization;
import de.monticore.lang.montiarc.ajava._ast.ASTVariableInitialization;
import de.monticore.lang.montiarc.ajava._symboltable.AJavaDefinitionSymbol;
import de.monticore.lang.montiarc.ajava._symboltable.AJavaLanguageFamily;
import de.monticore.lang.montiarc.montiarc._ast.ASTComponent;
import de.monticore.lang.montiarc.montiarc._ast.ASTElement;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentVariableSymbol;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.Names;

/**
 * Generates an implementation class for ajava.
 *
 * @author  Jerome Pfeiffer
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class AJavaGenerator {
  
  protected static Scope createSymTab(String modelPath) {
    ModelingLanguageFamily fam = new AJavaLanguageFamily();
    final ModelPath mp = new ModelPath(Paths.get(modelPath), Paths.get("src/main/resources/defaultTypes"));
    GlobalScope scope = new GlobalScope(mp, fam);
    JavaHelper.addJavaPrimitiveTypes(scope);
    return scope;
  }
  
  public static void generateModel(String simpleName, String packageName, String modelPath, String fqnModelName, String targetPath) {
    Scope symtab = createSymTab(modelPath);
    String model = packageName + "." + simpleName;
    ComponentSymbol comp = symtab.<ComponentSymbol> resolve(model, ComponentSymbol.KIND).get();
    
    doGenerate(getPath(targetPath, packageName, comp.getName()), comp.getAstNode().get(), comp);    
  }
  
  public static void doGenerate(Path filepath, ASTNode node, CommonSymbol symbol) {
    if (symbol.getKind().isKindOf(ComponentSymbol.KIND)) {
      ComponentSymbol comp = (ComponentSymbol) symbol;
      Collection<AJavaDefinitionSymbol> ajava = comp.getSpannedScope()
          .<AJavaDefinitionSymbol> resolveLocally(AJavaDefinitionSymbol.KIND);
      AJavaDefinitionSymbol ajavaDef = ajava.iterator().next();
      
      String inputName = comp.getName() + "Input";
      String resultName = comp.getName() + "Result";
      String implName = comp.getName() + "Impl";
      
      Optional<ASTComponentInitialization> init = getComponentInitialization(comp);
      List<ASTVariableInitialization> varInits = new ArrayList<>();
      
      if(init.isPresent()){
        varInits = init.get().getVariableInitializations();
      }
      
      
      JavaDSLPrettyPrinter printer = new JavaDSLPrettyPrinter(new IndentPrinter());
      ASTAJavaDefinition ajavaNode = (ASTAJavaDefinition) ajavaDef.getAstNode().get();
      
      StringBuilder sb = new StringBuilder();
      for (ASTBlockStatement s : ajavaNode.getBlockStatements()) {
        sb.append(printer.prettyprint(s));
      }
      
      AJavaHelper helper = new AJavaHelper(comp);
      
      Collection<ComponentVariableSymbol> vars = comp.getComponentVariables();
      
      AJavaMain.generate(filepath, node, helper, comp.getPackageName(), comp.getImports(),
          ajavaDef.getName(), resultName, inputName,
          implName, comp.getIncomingPorts(), comp.getOutgoingPorts(), comp.getConfigParameters(),comp.getComponentVariables(), sb.toString(), varInits);
      
    }
  }
  
  private static Optional<ASTComponentInitialization> getComponentInitialization(ComponentSymbol comp) {
    Optional<ASTComponentInitialization> ret = Optional.empty();
    Optional<ASTNode> ast = comp.getAstNode();
    if(ast.isPresent()) {
      ASTComponent compAST = (ASTComponent) ast.get();
      for(ASTElement e : compAST.getBody().getElements()) {
        if(e instanceof ASTComponentInitialization) {
          ret = Optional.of((ASTComponentInitialization) e);
          
        }
      }
    }    
    return ret;
  }
  
  
  
  /**
   * Computes the target path of the generated java file.
   * @param targetPath the path of the target folder
   * @param packageName the package name of the model
   * @param name the model name
   * @return
   */
  private static Path getPath(String targetPath, String packageName, String name) {
    return Paths.get(targetPath, Names.getPathFromPackage(packageName), name + ".java");
  }
}
