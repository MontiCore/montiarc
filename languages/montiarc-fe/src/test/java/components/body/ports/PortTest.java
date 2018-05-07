package components.body.ports;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import montiarc._cocos.MontiArcASTComponentCoCo;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc.cocos.InPortUniqueSender;
import montiarc.cocos.MontiArcCoCos;
import montiarc.cocos.NamesCorrectlyCapitalized;
import montiarc.cocos.PortUsage;
import montiarc.cocos.SubComponentsConnected;

/**
 * This class checks all context conditions directly related to port definitions
 *
 * @author Andreas Wortmann
 */
public class PortTest extends AbstractCoCoTest {
  
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
  public void testComponentWithArrayAsPortType() {
    checkValid(PACKAGE + "." + "ComponentWithArrayAsPortTypeParameter");
  }
  
  @Test
  public void testComponentWithArrayAsPortTypeUsage() {
    checkValid(PACKAGE + "." + "ComponentWithArrayAsPortTypeParameterUsage");
  }
  
  @Test
  public void testKeywordsAsPortName() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "KeywordAsPortName");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(4, "xMA087"));
  }
  
  @Test
  public void testNonUniquePortNames() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "NonUniquePortNames");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(6, "xMA053"));
  }
  
  @Test
  @Ignore("TODO: Fix UniqueIdentifiers.java CoCo")
  public void testPortNameAmbiguous() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "PortNameAmbiguous");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(1, "xMA053"));
  }
  
  @Test
  @Ignore("TODO: Fix UniqueIdentifiers.java CoCo")
  public void testImplicitAndExplicitPortNaming() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "ImplicitAndExplicitPortNaming");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(3, "xMA053"));
  }
  
  @Test
  public void testPortTypeResolving() {
    ComponentSymbol motorSymbol = this.loadComponentSymbol(PACKAGE, "PortTypeResolving");
    
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
  public void testPortsWithAmbiguousSenders() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "PortsWithAmbiguousSenders");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new InPortUniqueSender()),
        node, new ExpectedErrorInfo(4, "xMA005"));
  }
  
  @Test
  @Ignore("See UniquenessConnectors.arc_")
  public void testUniquenessConnectors() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "UniquenessConnectors");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new InPortUniqueSender()),
        node, new ExpectedErrorInfo(4, "xMA005"));
  }
  
  @Test
  @Ignore("TODO Adjust error and count after implementing CV7")
  public void testGenericPortsWithAndWithoutNames() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "GenericPortsWithAndWithoutNames");
    final ExpectedErrorInfo expectedErrors = new ExpectedErrorInfo();
    // TODO Adjust error and count after implementing CV7
    checkInvalid(MontiArcCoCos.createChecker(),
        node, expectedErrors);
  }
  
  @Test
  public void testInPortUniqueSender() {
    checkValid(PACKAGE + "." + "InPortUniqueSender");
  }
  
  @Test
  @Ignore("TODO: Currently no errors found even though it is an invalid model")
  public void testGenericPortsWithoutTypeParams() {
    checkValid(PACKAGE + "." + "GenericPortsWithoutTypeParams");
  }
  
  @Test
  /* Checks whether all port names in the port definition start with a lower
   * case letter */
  public void testPortWithUpperCaseName() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "PortWithUpperCaseName");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
        .addCoCo((MontiArcASTComponentCoCo) new NamesCorrectlyCapitalized());
    checkInvalid(cocos, node, new ExpectedErrorInfo(1, "xMA077"));
  }
  
  @Test
  /* Tests the CoCos CV5 and CV6 from the dissertation of Arne Haber. These are
   * the checks that all ports should be connected of components and
   * subcomponents. */
  public void testUnconnectedPorts() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "UnconnectedPorts");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new PortUsage());
    checkInvalid(cocos, node, new ExpectedErrorInfo(3, "xMA057", "xMA058"));
    
    cocos = new MontiArcCoCoChecker().addCoCo(new SubComponentsConnected());
    checkInvalid(cocos, node, new ExpectedErrorInfo(4, "xMA059", "xMA060"));
  }
  
  @Test
  /* Tests the CoCos CV5 and CV6 from the dissertation of Arne Haber. These are
   * the checks that all ports should be connected of components and
   * subcomponents. */
  public void testUnconnectedPorts2() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "UnconnectedPorts2");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new PortUsage());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xMA057", "xMA058"));
    
    cocos = new MontiArcCoCoChecker().addCoCo(new SubComponentsConnected());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xMA059", "xMA060"));
  }
  
  @Test
  public void testCompWithGenericPorts() {
    ComponentSymbol comp = this.loadComponentSymbol(PACKAGE, "CompWithGenericPorts");
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
    PortSymbol port = symTab
        .<PortSymbol> resolve(PACKAGE + "." + "PortsWithStereotypes.integerIn", PortSymbol.KIND)
        .orElse(null);
    assertNotNull(port);
    
    assertEquals(3, port.getStereotype().size());
    assertEquals("held", port.getStereotype().get("disabled").get());
    assertEquals("1", port.getStereotype().get("initialOutput").get());
    assertFalse(port.getStereotype().get("ignoreWarning").isPresent());
  }
  
  @Test
  public void testJavaTypedPorts() {
    checkValid("components.body.ports.ComponentWithJavaTypedPorts");
  }
}
