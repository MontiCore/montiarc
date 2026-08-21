/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.check.MontiArcTypeCheck;
import montiarc.trafo.MontiArcTrafos;
import montiarc.util.SymbolPathLoader;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

/**
 * The base of every montiarc test. Handles initialization and reset of static
 * stuff (log, mill, tc delegate, basic symbols).
 */
public abstract class MontiArcTestBase extends ATestBase {

  /**
   * We initialize the mill before all tests. The mill does not need to be
   * re-initialized between tests. Tests should not modify the mill delegate.
   * We also clear the global scope just to make sure there aren't any symbols
   * remaining from other test classes.
   */
  @BeforeAll
  protected static void initMill() {
    MontiArcMill.init();
    MontiArcMill.globalScope().clear();
  }

  /**
   * We initialize the type-check and basis symbols before each test.
   * Don't assume that these are available before then, e.g., for the source
   * of a parameterized test. We (re-) initialize the type-check to ensure
   * cleanup of type-check caches between tests. The symbols are initialized
   * for each test anew as the global scope is cleared between tests.
   */
  @BeforeEach
  protected void init() {
    MontiArcTypeCheck.init();
    BasicSymbolsMill.initializePrimitives();
    BasicSymbolsMill.initializeString();

    try{
      SymbolPathLoader.addResource("Stream.symtabdefinitionsym");
    } catch (IOException ignore) {}
  }

  /**
   * Compiles the given montiarc diagram, creating its abstract syntax tree and
   * symbol-table, as well as apply after parsing and after symbol table
   * transformations.
   * @param model the model to parse as a string
   * @return the ast of the parsed montiarc diagram
   */
  public static ASTMACompilationUnit compile(@NotNull String model) {
    Preconditions.checkNotNull(model);
    try {
      ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model)
        .orElseThrow(() -> new IllegalStateException(Log.getFindings().toString()));
      MontiArcTrafos.afterParsing().applyAll(ast);
      MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
      MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
      MontiArcTrafos.afterSymTabP2().applyAll(ast);
      MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);
      return ast;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
