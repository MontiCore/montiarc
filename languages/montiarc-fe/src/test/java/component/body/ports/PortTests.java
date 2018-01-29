package component.body.ports;

import static org.junit.Assert.assertNotNull;

import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;

import contextconditions.AbstractCoCoTest;
import contextconditions.AbstractCoCoTestExpectedErrorInfo;
import de.monticore.symboltable.types.JTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc.cocos.InPortUniqueSender;
import montiarc.cocos.MontiArcCoCos;

/**
 * This class checks all context conditions directly related to port definitions
 *
 * @author Andreas Wortmann
 */
public class PortTests extends AbstractCoCoTest {
  
  private static final String MP = "";
  
  private static final String PACKAGE = "component.body.ports";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testInexistingPortType() {
    // TODO: Star imports?
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "InexistingPortType");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new AbstractCoCoTestExpectedErrorInfo(1, "xMA076"));
    
    checkValid("", "contextconditions.valid.BumpControl");
  }
  
  @Test
  public void testBumpControl() {
    checkValid(MP, PACKAGE + "." + "BumpControl");
  }
  
  @Test
  public void testNonUniquePortNames() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "NonUniquePortNames");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new AbstractCoCoTestExpectedErrorInfo(3, "xMA053"));
  }
  
  @Test
  public void testPortTypeResolving() {
    MontiArcTool tool = new MontiArcTool();
    ComponentSymbol motorSymbol = tool
        .getComponentSymbol("component.body.ports.PortTypeResolving",
            Paths.get("src/test/resources").toFile(),
            Paths.get("src/main/resources/defaultTypes").toFile())
        .orElse(null);
    
    assertNotNull(motorSymbol);
    
    PortSymbol commandPort = motorSymbol.getIncomingPort("command").orElse(null);
    
    assertNotNull(commandPort);
    
    JTypeSymbol typeSymbol = commandPort
        .getTypeReference()
        .getReferencedSymbol();
    
    assertNotNull(typeSymbol);
  }
  
  @Test
  public void testInPortAmbiguousSender() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "InPortAmbiguousSender");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new InPortUniqueSender()),
        node, new AbstractCoCoTestExpectedErrorInfo(2, "xMA005"));
  }
  
  @Test
  public void testInPortUniqueSender() {
    checkValid(MP, PACKAGE + "." + "InPortUniqueSender");
  }
  
}
