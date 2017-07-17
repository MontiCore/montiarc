package de.monticore.lang.montiarc.javap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.lang.montiarc.montiarc._ast.ASTMontiArcNode;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.JavaBehaviorSymbol;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;

public class SymTabTest extends AbstractSymtabTest {
  
  public static final String MODELPATH = "src/test/resources";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  @Ignore // This test operates a no longer existing symbol
  public void testAutomatonEmbedding() {
    ASTMontiArcNode node = getAstNode(MODELPATH, "javap.valid.bumperbot.BumpControl");
    assertNotNull(node);
    Scope symtab = createSymTab(MODELPATH);
    Optional<ComponentSymbol> oBControl = symtab
        .<ComponentSymbol> resolve("javap.valid.bumperbot.BumpControl", ComponentSymbol.KIND);
    assertTrue(oBControl.isPresent());
    ComponentSymbol bControl = oBControl.get();
//    Collection<AutomatonSymbol> automatons = ScopeHelper.resolveManyDown(bControl.getSpannedScope(),
//        AutomatonSymbol.KIND);
//    assertTrue(automatons.size() == 1);
  }
  
  @Test
  public void testAJavaEmbedding() {
    ASTMontiArcNode node = getAstNode(MODELPATH, "javap.valid.Foo");
    assertNotNull(node);
    Scope symtab = createSymTab(MODELPATH);
    Optional<ComponentSymbol> oFoo = symtab.<ComponentSymbol> resolve("javap.valid.Foo",
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
