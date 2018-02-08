package symboltable;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.symboltable.Scope;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;
import infrastructure.AutomatonBaseTest;
import montiarc._ast.ASTMontiArcNode;
import montiarc._symboltable.ComponentSymbol;

public class AutomatonSymbolTableTest extends AutomatonBaseTest {
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testParseBumperbot() {
    ASTMontiArcNode node = getAstNode("src/test/resources/", "contextconditions.valid.BumpControl");
    assertNotNull(node);
  }
  
  @Test
  public void testCDType2JavaType() {
    Scope symTab = createSymTab("src/test/resources/");
    CDTypeSymbol cdType = symTab
        .<CDTypeSymbol> resolve("contextconditions.valid.Datatypes.MotorCommand", CDTypeSymbol.KIND)
        .orElse(null);
    assertNotNull(cdType);
    JavaTypeSymbol javaType = symTab
        .<JavaTypeSymbol> resolve("contextconditions.valid.Datatypes.MotorCommand", JavaTypeSymbol.KIND)
        .orElse(null);
    assertNotNull(javaType);
  }
  
  @Test
  public void testCDField2JavaField() {
    Scope symTab = createSymTab("src/test/resources/");
    CDFieldSymbol cdField = symTab
        .<CDFieldSymbol> resolve("contextconditions.valid.Datatypes.MotorCommand.STOP", CDFieldSymbol.KIND)
        .orElse(null);
    assertNotNull(cdField);
    JavaFieldSymbol javaField = symTab.<JavaFieldSymbol> resolve(
        "contextconditions.valid.Datatypes.MotorCommand.STOP", JavaFieldSymbol.KIND).orElse(null);
    assertNotNull(javaField);
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
