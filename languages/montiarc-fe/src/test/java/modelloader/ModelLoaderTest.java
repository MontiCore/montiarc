/* (c) https://github.com/MontiCore/monticore */
package modelloader;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import montiarc._symboltable.ComponentSymbol;

public class ModelLoaderTest extends AbstractCoCoTest {
  
  private static final String PACKAGE = "modelloader";
  
  public static final boolean ENABLE_FAIL_QUICK = true;
  
  @Before
  public void setup() {
    Log.enableFailQuick(ENABLE_FAIL_QUICK);
  }
  
  private static Scope getGlobalScope(final Scope scope) {
    Scope s = scope;
    while (s.getEnclosingScope().isPresent()) {
      s = s.getEnclosingScope().get();
    }
    return s;
  }
  
  @Test
  public void testModelLoader() {
    Optional<ComponentSymbol> sym = MONTIARCTOOL.loadComponentSymbolWithoutCocos(
        PACKAGE + "." + "industry.PIController", Paths.get(MODEL_PATH).toFile());
    assertTrue(sym.isPresent());
    
    Optional<ComponentSymbol> sym2 = MONTIARCTOOL.loadComponentSymbolWithoutCocos(
        PACKAGE + "." + "industry2.PIController", Paths.get(MODEL_PATH).toFile());
    assertTrue(sym2.isPresent());
    
    assertTrue(sym.get() != sym2.get());
  }
  
  // resolves also to the same ComponentSymbol even both are in different packages
  @Test
  public void testSymbolTableResolve() {
    Scope scope = MONTIARCTOOL.initSymbolTable(MODEL_PATH);
    
    Optional<ComponentSymbol> sym3 = getGlobalScope(scope)
        .<ComponentSymbol> resolveDown(PACKAGE + "." + "industry.PIController",
            ComponentSymbol.KIND);
    assertFalse("resolveDown may not load any model from ModelLoader", sym3.isPresent());
    
    Optional<ComponentSymbol> sym = scope
        .<ComponentSymbol> resolve(PACKAGE + "." + "industry.PIController", ComponentSymbol.KIND);
    assertTrue(sym.isPresent());
    
    Optional<ComponentSymbol> sym2 = getGlobalScope(scope)
        .<ComponentSymbol> resolveDown(PACKAGE + "." + "industry2.PIController",
            ComponentSymbol.KIND);
    assertFalse(sym2.isPresent());
    
  }
}
