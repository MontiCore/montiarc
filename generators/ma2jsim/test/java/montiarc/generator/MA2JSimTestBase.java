/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import montiarc.ATestBase;
import montiarc.MontiArcMill;
import montiarc.check.MontiArcTypeCheck;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

/**
 * The base of every ma2jsim test. Handles initialization and reset of static
 * stuff (log, mill, tc delegate, basic symbols).
 */
public class MA2JSimTestBase extends ATestBase {

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
  }
}
