package de.monticore.lang.montiarc.montiarcautomaton;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.lang.montiarc.montiarc._ast.ASTMontiArcNode;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.symboltable.Scope;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;

public class SymTabTest extends AbstractSymtabTest {
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
    
  @Test
  public void testParseBumperbot() {
    ASTMontiArcNode node = getAstNode("src/test/resources/", "valid.bumperbot.BumpControl");
    assertNotNull(node);
  }
  
  @Test
  public void testCDType2JavaType() {
    Scope symTab = createSymTab("src/test/resources/");
    CDTypeSymbol type1 = symTab.<CDTypeSymbol> resolve("valid.bumperbot.datatypes.MotorCommand", CDTypeSymbol.KIND).orElse(null);
    assertNotNull(type1);
    JavaTypeSymbol type2 = symTab.<JavaTypeSymbol> resolve("valid.bumperbot.datatypes.MotorCommand", JavaTypeSymbol.KIND).orElse(null);
    assertNotNull(type2);
  }
  
  @Test
  public void testCDField2JavaField() {
    Scope symTab = createSymTab("src/test/resources/");
    CDFieldSymbol field1 = symTab.<CDFieldSymbol> resolve("valid.bumperbot.datatypes.MotorCommand.STOP", CDFieldSymbol.KIND).orElse(null);
    assertNotNull(field1);
    JavaFieldSymbol field2 = symTab.<JavaFieldSymbol> resolve("valid.bumperbot.datatypes.MotorCommand.STOP", JavaFieldSymbol.KIND).orElse(null);
    assertNotNull(field2);
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
