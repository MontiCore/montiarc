package symboltable;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import infrastructure.AJavaBaseTest;
import montiarc._ast.ASTMontiArcNode;
import montiarc._symboltable.AutomatonSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.JavaBehaviorSymbol;

public class AJavaSymbolTableTest extends AJavaBaseTest {
  
  public static final String MODELPATH = "src/test/resources";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testAutomatonEmbeddingInSymTab() {
    ASTMontiArcNode node = getAstNode(MODELPATH, "contextconditions.valid.BumpControl");
    assertNotNull(node);
    Scope symtab = createSymTab(MODELPATH);
    Optional<ComponentSymbol> oBControl = symtab
        .<ComponentSymbol> resolve("contextconditions.valid.BumpControl", ComponentSymbol.KIND);
    assertTrue(oBControl.isPresent());
    ComponentSymbol bControl = oBControl.get();
    Optional<AutomatonSymbol>  autSymbol = bControl.getSpannedScope().<AutomatonSymbol>
            resolve("BumpControl", AutomatonSymbol.KIND);
    assertTrue(autSymbol.isPresent());
  }

  @Test
  public void testAJavaFunctionEmbeddingInSymTab() {
    ASTMontiArcNode node = getAstNode(MODELPATH, "contextconditions.valid.DistanceLogger");
    assertNotNull(node);
    Scope symtab = createSymTab(MODELPATH);
    Optional<ComponentSymbol> oFoo = symtab.<ComponentSymbol> resolve("contextconditions.valid.DistanceLogger",
        ComponentSymbol.KIND);
    assertTrue(oFoo.isPresent());
    ComponentSymbol foo = oFoo.get();
    Optional<JavaBehaviorSymbol> aJavaDef = foo.getSpannedScope()
        .<JavaBehaviorSymbol> resolve("increaseHulu", JavaBehaviorSymbol.KIND);
    assertTrue(aJavaDef.isPresent());
  }
  
  protected static ASTMontiArcNode getAstNode(String modelPath, String model) {
    // ensure an empty log
    Log.getFindings().clear();
    Scope symTab = createSymTab(modelPath);
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(model, ComponentSymbol.KIND)
        .orElse(null);
    assertNotNull("Could not resolve model " + model, comp);
    
    return (ASTMontiArcNode) comp.getAstNode().get();
  }
}
