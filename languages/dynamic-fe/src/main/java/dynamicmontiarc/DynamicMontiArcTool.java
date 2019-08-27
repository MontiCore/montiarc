/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc;

import com.google.common.collect.Sets;
import de.monticore.ModelingLanguageFamily;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import dynamicmontiarc._cocos.DynamicMontiArcCoCoChecker;
import dynamicmontiarc._parser.DynamicMontiArcParser;
import dynamicmontiarc._symboltable.DynamicMontiArcLanguageFamily;
import dynamicmontiarc.cocos.DynamicMontiArcCoCos;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._ast.ASTMontiArcNode;
import montiarc._symboltable.ComponentSymbol;
import montiarc.helper.JavaDefaultTypesManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

/**
 * API for the DynamicMontiArc frontend. Holds methods for parsing a model,
 * building the symbol table, and checking context conditions.
 *
 * @see montiarc.MontiArcTool
 */
public class DynamicMontiArcTool {
  private ModelingLanguageFamily family;
  private DynamicMontiArcCoCoChecker checker;


  /**
   * Constructor for {@link DynamicMontiArcTool}.
   * Uses default modeling language {@link DynamicMontiArcLanguageFamily}
   * and context conditions {@link DynamicMontiArcCoCos}
   */
  public DynamicMontiArcTool() {
    this(new DynamicMontiArcLanguageFamily(),
        DynamicMontiArcCoCos.createChecker());
  }

  /**
   * Constructor for {@link DynamicMontiArcTool}.
   * Uses default context conditions {@link DynamicMontiArcCoCos}
   *
   * @param fam Modeling language family {@link ModelingLanguageFamily}
   */
  public DynamicMontiArcTool(ModelingLanguageFamily fam) {
    this(fam, DynamicMontiArcCoCos.createChecker());
  }

  /**
   * Constructor for {@link DynamicMontiArcTool}.
   *
   * @param fam     Modeling language family
   * @param checker Context condition checker
   */
  public DynamicMontiArcTool(ModelingLanguageFamily fam,
                             DynamicMontiArcCoCoChecker checker) {
    this.family = fam;
    this.checker = checker;
  }

    /**
     * Parser for Dynamic MontiArc models.
     *
     * @param filename full qualified name of model
     * @return Returns the AST of the model if parsable and else Optional EMPTY
     */
  public Optional<ASTMACompilationUnit> parse(String filename) {
    DynamicMontiArcParser p = new DynamicMontiArcParser();
    Optional<ASTMACompilationUnit> compUnit;
    try {
      compUnit = p.parse(filename);
      return compUnit;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Optional.empty();

  }

  /**
   * Checks context conditions on MontiArc AST.
   *
   * @param node AST for which to check context conditions
   * @return true if no errors occurred
   */
  public boolean checkCoCos(ASTMontiArcNode node) {
    if (!node.getSymbolOpt().isPresent()
        && !node.getSpannedScopeOpt().isPresent()) {
      Log.error("Symbol table has to be initialized before checking " +
          "context conditions.");
      return false;
    }
    checker.checkAll(node);
    if (Log.getErrorCount() != 0) {
      Log.debug("Found " + Log.getErrorCount() + " errors in node " + node
          + ".", "XX");
      return false;
    }
    return true;
  }

  /**
   * Loads a component symbol for the referenced component without checking
   * context conditions.
   *
   * @param componentName Full qualified name of the component model
   * @param modelPaths    Paths relative to the project path containing the
   *                      component models
   * @return Return component symbol if resolvable and else Optional EMPTY
   */
  public Optional<ComponentSymbol> loadComponentSymbolWithoutCocos(String componentName,
                                                                   File... modelPaths) {
    Scope s = initSymbolTable(modelPaths);
    return s.<ComponentSymbol>resolve(componentName, ComponentSymbol.KIND);
  }

  /**
   * Loads a component symbol for the referenced component and checks context
   * conditions.
   *
   * @param componentName Full qualified name of the component model
   * @param modelPaths    Paths relative to the project path containing the
   *                      component models
   * @return Return component symbol if resolvable and else Optional EMPTY
   */
  public Optional<ComponentSymbol> loadComponentSymbolWithCocos(String componentName,
                                                                File... modelPaths) {
    Optional<ComponentSymbol> componentSymbol =
        loadComponentSymbolWithoutCocos(componentName, modelPaths);
    if (componentSymbol.isPresent()) {
      checkCoCos((ASTMontiArcNode) componentSymbol.get().getAstNode().get());
    }
    return componentSymbol;
  }

  /**
   * Loads the AST for the referenced component model.
   *
   * @param model     The fully qualified model name
   * @param modelPath Path relative to the project path containing the model
   * @return Returns the AST of the model if parsable and else Optional EMPTY.
   */
  public Optional<? extends ASTMontiArcNode> getAstNode(String model,
                                                        String modelPath) {
    // ensure an empty log
    Log.getFindings().clear();
    Optional<ComponentSymbol> component =
        loadComponentSymbolWithoutCocos(model, Paths.get(modelPath).toFile());

    if (!component.isPresent()) {
      Log.error("Model could not be resolved!");
      return Optional.empty();
    }

    if (!component.get().getAstNode().isPresent()) {
      return Optional.empty();
    }
    return Optional.of((ASTMontiArcNode) component.get().getAstNode().get());
  }

  /**
   * Initializes the symbol table by introducing scopes for the passed model
   * paths. It does not create the symbol table! Symbols for models within the
   * model paths are not added to the symbol table until resolve() is called.
   *
   * @param modelPaths Paths relative to the project path containing the
   *                   component models
   * @return The initialized symbol table
   */
  public Scope initSymbolTable(File... modelPaths) {
    Set<Path> p = Sets.newHashSet();
    for (File mP : modelPaths) {
      p.add(Paths.get(mP.getAbsolutePath()));
    }
    final ModelPath modelPath = new ModelPath(p);
    GlobalScope globalScope = new GlobalScope(modelPath, family);
    JavaDefaultTypesManager.addJavaPrimitiveTypes(globalScope);
    return globalScope;
  }

  /**
   * Initializes the symbol table by introducing scopes for the passed model
   * paths. It does not create the symbol table! Symbols for models within the
   * model paths are not added to the symbol table until resolve() is called.
   *
   * @param modelPath Paths relative to the project path containing the
   *                  component models
   * @return The initialized symbol table
   */
  public Scope initSymbolTable(String modelPath) {
    return initSymbolTable(Paths.get(modelPath).toFile());
  }
}
