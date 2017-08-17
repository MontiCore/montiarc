package symboltable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import de.monticore.symboltable.Scope;
import montiarc._symboltable.ComponentSymbol;

public class LoaderAndCalculatorTest extends AbstractSymboltableTest {
  
  private final String MODEL_PATH = "src/test/resources/symboltable/modelloader";
  
  private static Scope getGlobalScope(final Scope scope) {
    Scope s = scope;
    while (s.getEnclosingScope().isPresent()) {
      s = s.getEnclosingScope().get();
    }
    return s;
  }
  
  @Test
  public void testModelLoader() {
    Scope scope = createSymTab(MODEL_PATH);
    Optional<ComponentSymbol> sym = scope
        .<ComponentSymbol> resolve("industry.PIController", ComponentSymbol.KIND);
    assertTrue(sym.isPresent());
    
    Optional<ComponentSymbol> sym2 = scope
        .<ComponentSymbol> resolve("industry2.PIController", ComponentSymbol.KIND);
    assertTrue(sym2.isPresent());
    
    assertTrue(sym.get() != sym2.get());
  }
  
  // resolves also to the same ComponentSymbol even both are in different packages
  @Test
  public void testSymbolTableResolve() {
    Scope scope = createSymTab(MODEL_PATH);
    
    Optional<ComponentSymbol> sym3 = getGlobalScope(scope)
        .<ComponentSymbol> resolveDown("industry.PIController", ComponentSymbol.KIND);
    assertFalse("resolveDown may not load any model from ModelLoader", sym3.isPresent());
    
    Optional<ComponentSymbol> sym = scope
        .<ComponentSymbol> resolve("industry.PIController", ComponentSymbol.KIND);
    assertTrue(sym.isPresent());
    
    Optional<ComponentSymbol> sym2 = getGlobalScope(scope)
        .<ComponentSymbol> resolveDown("industry2.PIController", ComponentSymbol.KIND);
    assertFalse(sym2.isPresent());
    
  }
}
