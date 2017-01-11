package de.monticore.lang.montiarc.ajava;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.automaton.ioautomaton.ScopeHelper;
import de.monticore.automaton.ioautomaton._symboltable.AutomatonSymbol;
import de.monticore.java.symboltable.JavaMethodSymbol;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.lang.montiarc.ajava._symboltable.AJavaDefinitionKind;
import de.monticore.lang.montiarc.ajava._symboltable.AJavaDefinitionSymbol;
import de.monticore.lang.montiarc.montiarc._ast.ASTMontiArcNode;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
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
    Scope symtab = createSymTab(MODELPATH);
    Optional<ComponentSymbol> oBControl = symtab
        .<ComponentSymbol> resolve("valid.bumperbot.BumpControl", ComponentSymbol.KIND);
    assertTrue(oBControl.isPresent());
    ComponentSymbol bControl = oBControl.get();
    Collection<AutomatonSymbol> automatons = ScopeHelper.resolveManyDown(bControl.getSpannedScope(),
        AutomatonSymbol.KIND);
    assertTrue(automatons.size() == 1);
  }
  
  @Test
  public void testAJavaEmbedding() {
    ASTMontiArcNode node = getAstNode(MODELPATH, "valid.Foo");
    assertNotNull(node);
    Scope symtab = createSymTab(MODELPATH);
    Optional<ComponentSymbol> oFoo = symtab.<ComponentSymbol> resolve("valid.Foo",
        ComponentSymbol.KIND);
    assertTrue(oFoo.isPresent());
    ComponentSymbol foo = oFoo.get();
    Optional<AJavaDefinitionSymbol> aJavaDef = foo.getSpannedScope()
        .<AJavaDefinitionSymbol> resolve("increaseHulu", AJavaDefinitionSymbol.KIND);
    assertTrue(aJavaDef.isPresent());
//    Optional<JavaTypeSymbol> jSymbol = aJavaDef.get().getSpannedScope().<JavaTypeSymbol>resolve("hulu", JavaTypeSymbol.KIND);
//    assertTrue(jSymbol.isPresent());
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
