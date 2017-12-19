package symboltable;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMontiArcNode;
import montiarc._symboltable.AutomatonSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.JavaBehaviorSymbol;

public class AJavaSymbolTableTest {
  
  public static final String MODELPATH = "src/test/resources";
  
  private static MontiArcTool tool;
  
  @BeforeClass
  public static void setUp() {
    tool = new MontiArcTool();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testAutomatonEmbeddingInSymTab() {
    ASTMontiArcNode node = tool.getAstNode(MODELPATH, "contextconditions.valid.BumpControl");
    assertNotNull(node);
    Scope symtab = tool.createSymbolTable(MODELPATH);
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
    ASTMontiArcNode node = tool.getAstNode(MODELPATH, "contextconditions.valid.DistanceLogger");
    assertNotNull(node);
    Scope symtab = tool.createSymbolTable(MODELPATH);
    Optional<ComponentSymbol> oFoo = symtab.<ComponentSymbol> resolve("contextconditions.valid.DistanceLogger",
        ComponentSymbol.KIND);
    assertTrue(oFoo.isPresent());
    ComponentSymbol foo = oFoo.get();
    Optional<JavaBehaviorSymbol> aJavaDef = foo.getSpannedScope()
        .<JavaBehaviorSymbol> resolve("increaseHulu", JavaBehaviorSymbol.KIND);
    assertTrue(aJavaDef.isPresent());
  }
  
}
