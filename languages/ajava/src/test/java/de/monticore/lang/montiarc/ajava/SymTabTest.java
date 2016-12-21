package de.monticore.lang.montiarc.ajava;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.lang.montiarc.montiarc._ast.ASTMontiArcNode;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;

public class SymTabTest extends AbstractSymtabTest {
  
  public static final String MODELPATH = "src/test/resources";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testAutomatonEmbedding() {
    ASTMontiArcNode node = getAstNode(MODELPATH, "valid.bumperbot.BumpControl");
    assertNotNull(node);
  }
  
  @Test
   public void testAJavaEmbedding() {
     ASTMontiArcNode node = getAstNode(MODELPATH, "valid.Foo");
     assertNotNull(node);
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
