package tooling.modelloader;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._symboltable.ComponentSymbol;

public class ModelLoaderTests {
  
  private final String MP = "src/test/resources/";
  public static final boolean ENABLE_FAIL_QUICK = true;

  private static MontiArcTool tool;
  
  @Before
  public void setup() {
    Log.getFindings().clear();
    Log.enableFailQuick(ENABLE_FAIL_QUICK);
    tool = new MontiArcTool();
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
    Optional<ComponentSymbol> sym = tool.loadComponentSymbolWithoutCocos("tooling.modelloader.industry.PIController", Paths.get(MP).toFile());
    assertTrue(sym.isPresent());
    
    Optional<ComponentSymbol> sym2 = tool.loadComponentSymbolWithoutCocos("tooling.modelloader.industry2.PIController", Paths.get(MP).toFile());
    assertTrue(sym2.isPresent());
    
    assertTrue(sym.get() != sym2.get());
  }
  
  // resolves also to the same ComponentSymbol even both are in different packages
  @Test
  public void testSymbolTableResolve() {
    Scope scope = tool.initSymbolTable(MP);
    
    Optional<ComponentSymbol> sym3 = getGlobalScope(scope)
        .<ComponentSymbol> resolveDown("tooling.modelloader.industry.PIController", ComponentSymbol.KIND);
    assertFalse("resolveDown may not load any model from ModelLoader", sym3.isPresent());
    
    Optional<ComponentSymbol> sym = scope
        .<ComponentSymbol> resolve("tooling.modelloader.industry.PIController", ComponentSymbol.KIND);
    assertTrue(sym.isPresent());
    
    Optional<ComponentSymbol> sym2 = getGlobalScope(scope)
        .<ComponentSymbol> resolveDown("tooling.modelloader.industry2.PIController", ComponentSymbol.KIND);
    assertFalse(sym2.isPresent());
    
  }
}
