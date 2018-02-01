package components.body.ports;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc.cocos.InPortUniqueSender;
import montiarc.cocos.MontiArcCoCos;
import montiarc.cocos.PortNameIsLowerCase;
import montiarc.cocos.PortUsage;
import montiarc.cocos.SubComponentsConnected;

/**
 * This class checks all context conditions directly related to port definitions
 *
 * @author Andreas Wortmann
 */
public class PortTests extends AbstractCoCoTest {

  private static final String PACKAGE = "components.body.ports";

  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testInexistingPortType() {
    // TODO: Star imports?
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "InexistingPortType");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(1, "xMA076"));

    checkValid(PACKAGE + "." + "BumpControl");
  }

  @Test
  public void testBumpControl() {
    checkValid(PACKAGE + "." + "BumpControl");
  }

  @Test
  public void testNonUniquePortNames() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "NonUniquePortNames");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(3, "xMA053"));
  }

  @Test
  public void testPortTypeResolving() {
    MontiArcTool tool = new MontiArcTool();
    ComponentSymbol motorSymbol = tool
        .loadComponentSymbolWithCocos("components.body.ports.PortTypeResolving",
            Paths.get("src/test/resources").toFile(),
            Paths.get(FAKE_JAVA_TYPES_PATH).toFile())
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
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "InPortAmbiguousSender");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new InPortUniqueSender()),
        node, new ExpectedErrorInfo(2, "xMA005"));
  }

  @Test
  public void testInPortUniqueSender() {
    checkValid(PACKAGE + "." + "InPortUniqueSender");
  }

  @Test
  /* Checks whether all port names in the port definition start with a lower case letter */
  public void testPortWithUpperCaseName() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "PortWithUpperCaseName");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new PortNameIsLowerCase());
    checkInvalid(cocos, node, new ExpectedErrorInfo(1, "xMA077"));
  }

  @Test
  public void testUnconnectedPorts() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "UnconnectedPorts");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new PortUsage());
    checkInvalid(cocos, node, new ExpectedErrorInfo(3, "xMA057", "xMA058"));

    cocos = new MontiArcCoCoChecker().addCoCo(new SubComponentsConnected());
    checkInvalid(cocos, node, new ExpectedErrorInfo(4, "xMA059", "xMA060"));
  }

  @Test
  public void testCompWithGenericPorts() {
    ComponentSymbol comp = MONTIARCTOOL.loadComponentSymbolWithoutCocos(PACKAGE + "." + "CompWithGenericPorts",
        Paths.get("src/test/resources").toFile(),
        Paths.get(FAKE_JAVA_TYPES_PATH).toFile()).orElse(null);

    assertNotNull(comp);
    assertEquals(3, comp.getFormalTypeParameters().size());

    JTypeSymbol typeSymbol = comp.getFormalTypeParameters().get(0);
    assertEquals("K", typeSymbol.getName());
    assertTrue(typeSymbol.isFormalTypeParameter());
    assertEquals(1, typeSymbol.getSuperTypes().size());

    typeSymbol = comp.getFormalTypeParameters().get(1);
    assertEquals("V", typeSymbol.getName());
    assertTrue(typeSymbol.isFormalTypeParameter());
    assertEquals(1, typeSymbol.getSuperTypes().size());

    typeSymbol = comp.getFormalTypeParameters().get(2);
    assertEquals("W", typeSymbol.getName());
    assertTrue(typeSymbol.isFormalTypeParameter());
    assertEquals(0, typeSymbol.getSuperTypes().size());

    PortSymbol myKInput = comp.getIncomingPort("myKInput").orElse(null);
    assertNotNull(myKInput);
    assertEquals("K", myKInput.getTypeReference().getName());
    PortSymbol myWInput = comp.getIncomingPort("myWInput").orElse(null);
    assertNotNull(myWInput);
    assertEquals("W", myWInput.getTypeReference().getName());
    PortSymbol myVInput = comp.getOutgoingPort("myVOutput").orElse(null);
    assertNotNull(myVInput);
    assertEquals("V", myVInput.getTypeReference().getName());

  }
  
  @Test
  public void testPortsWithStereotypes() {
    Scope symTab = this.loadDefaultSymbolTable();
    PortSymbol port = symTab.<PortSymbol> resolve(PACKAGE + "." + "PortsWithStereotypes.integerIn", PortSymbol.KIND).orElse(null);
    assertNotNull(port);

    assertEquals(3, port.getStereotype().size());
    assertEquals("held", port.getStereotype().get("disabled").get());
    assertEquals("1", port.getStereotype().get("initialOutput").get());
    assertFalse(port.getStereotype().get("ignoreWarning").isPresent());
  }

}
