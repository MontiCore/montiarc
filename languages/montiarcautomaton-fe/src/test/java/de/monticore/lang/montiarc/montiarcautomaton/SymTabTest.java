package de.monticore.lang.montiarc.montiarcautomaton;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol;
import de.monticore.lang.montiarc.montiarc._ast.ASTMACompilationUnit;
import de.monticore.lang.montiarc.montiarc._ast.ASTMontiArcNode;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol;
import de.monticore.lang.montiarc.montiarcautomaton._parser.MontiArcAutomatonParser;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;

public class SymTabTest extends AbstractSymtabTest {
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void test() throws IOException {
    MontiArcAutomatonParser a = new MontiArcAutomatonParser();
    Optional<ASTMACompilationUnit> model= a.parse("src/test/resources/valid/BumpControlT1.maa");
    assertNotNull(model.orElse(null));
    
    ASTMontiArcNode node = getAstNode("src/test/resources/", "valid.BumpControlT1");
  }
  
  @Test
  public void testBumperbot() {
    ASTMontiArcNode node = getAstNode("src/test/resources/", "valid.bumperbot.BumpControl");
    node = null;
  }
  
  protected static ASTMontiArcNode getAstNode(String modelPath, String model) {
    // ensure an empty log
    Log.getFindings().clear();
    
    Scope symTab = createSymTab(modelPath);
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(model, ComponentSymbol.KIND).orElse(null);
    assertNotNull("Could not resolve model " + model, comp);
    
    return (ASTMontiArcNode) comp.getAstNode().get();
  }
}
