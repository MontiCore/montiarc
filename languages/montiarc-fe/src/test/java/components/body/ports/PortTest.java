/* (c) https://github.com/MontiCore/monticore */
package components.body.ports;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc.cocos.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class checks all context conditions directly related to port definitions
 *
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
        new ExpectedErrorInfo(3, "xMA101","xMA076"));
    
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
    final String qualifiedModelName = PACKAGE + "." + "KeywordAsPortName";
    final MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(4, "xMA028");
    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }
  
  @Test
  public void testNonUniquePortNames() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "NonUniquePortNames");
    checkInvalid(MontiArcCoCos.createChecker(), node,
        new ExpectedErrorInfo(6, "xMA053"));
  }
  
  @Test
  public void testFormalTypeParametersNotExist() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "PortTypesNotExist");
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(2, "xMA101"));
  }
  
  @Test
  public void testPortNameAmbiguous() {
    final String modelName = PACKAGE + "." + "PortNameAmbiguous";
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(3, "xMA053", "xMA061", "xMA069");
    final MontiArcCoCoChecker cocos
        = new MontiArcCoCoChecker().addCoCo(new IdentifiersAreUnique());
    checkInvalid(cocos, loadComponentAST(modelName), errors);
  }
  
  @Test
  /**
   * TODO: CV7 Error Codes
   */
  public void testImplicitAndExplicitPortNaming() {
    final String modelName = PACKAGE + "." + "ImplicitAndExplicitPortNaming";
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(6, "xMA053");
    final MontiArcCoCoChecker cocos
        = new MontiArcCoCoChecker().addCoCo(new IdentifiersAreUnique());
    checkInvalid(cocos, loadComponentAST(modelName), errors);
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
  public void testUniquenessConnectors() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "UniquenessConnectors");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new InPortUniqueSender()),
        node, new ExpectedErrorInfo(2, "xMA005"));
  }
  
  @Test
  public void testExistingPortInConnector() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "ExistingPortInConnector");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ConnectorSourceAndTargetExistAndFit()),
        node, new ExpectedErrorInfo(3, "xMA066", "xMA067"));
  }
  
  @Test
  public void testPortCompatibilityWithGenerics() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "PortCompatibilityWithGenerics");
    final MontiArcCoCoChecker checker = new MontiArcCoCoChecker()
        .addCoCo(new ConnectorSourceAndTargetExistAndFit());
    final ExpectedErrorInfo expectedErrors = new ExpectedErrorInfo(11, "xMA033");
    checkInvalid(checker, node, expectedErrors);
  }
  
  @Test
  public void testPortCompatibilityWithGenerics2() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "PortCompatibilityWithGenerics2");
    final MontiArcCoCoChecker checker = new MontiArcCoCoChecker()
        .addCoCo(new ConnectorSourceAndTargetExistAndFit());
    final ExpectedErrorInfo expectedErrors = new ExpectedErrorInfo(4, "xMA033");
    checkInvalid(checker, node, expectedErrors);
  }
  
  @Test
  /*
    Usually 1 error, but 3 due to issue #241, #243
   */
  public void testPortCompatibilityWithGenerics3() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "PortCompatibilityWithGenerics3");
    final MontiArcCoCoChecker checker = new MontiArcCoCoChecker()
        .addCoCo(new ConnectorSourceAndTargetExistAndFit());
    final ExpectedErrorInfo expectedErrors = new ExpectedErrorInfo(3, "xMA033");
    checkInvalid(checker, node, expectedErrors);
  }
  
  
  @Test
  public void testInPortUniqueSender() {
    checkValid(PACKAGE + "." + "InPortUniqueSender");
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
  // TODO Fix test. Model uses types like PrintStream and System which are not imported. Test is not executing any CoCos
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
    checkValid(PACKAGE + ".ComponentWithJavaTypedPorts");
  }
  
  @Test
  public void testPorts() {
    checkValid(PACKAGE + ".Ports");
  }
  
  @Test
  public void testCompWithArrays() {
    checkValid(PACKAGE + "." + "CompWithArrays");
  }
  
  @Test
  public void testJavaTypesAsPortTypes() {
    checkValid(PACKAGE + "." + "JavaTypesAsPortTypes");
  }
  
  @Test
  public void testComponentForCDTypesTest() {
    checkValid(PACKAGE + "." + "ComponentForCDTypesTest");
  }
  
  @Test
  public void testUndefinedPortTypes() {
    MontiArcCoCoChecker cocos
        = new MontiArcCoCoChecker().addCoCo(new UsedTypesExist());
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(6, "xMA101");
    final String qualifiedModelName = PACKAGE + "." + "UndefinedPortTypes";
    checkInvalid(cocos, loadComponentAST(qualifiedModelName), errors);
  }

  @Test
  public void testReferencingComp() {
    final ASTMontiArcNode node
        = loadComponentAST(PACKAGE + "." + "ReferencingComp");
    MontiArcCoCoChecker cocos
        = MontiArcCoCos.createChecker();
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(1, "xMA033");
    checkInvalid(cocos, node, errors);
  }

  @Test
  public void testHasProhibitedPortName() {
    final String modelName = PACKAGE + "." + "HasProhibitedPortName";
    final MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    final ExpectedErrorInfo expectedErrorInfo
        = new ExpectedErrorInfo(4, "xMA046");
    checkInvalid(checker, loadComponentAST(modelName), expectedErrorInfo);
  }

  @Test
  public void testCompWithGenericArraysAsPorts() {
    final String modelName = PACKAGE + "." + "CompWithGenericArraysAsPorts";
    final MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    final ExpectedErrorInfo expectedErrorInfo
        = new ExpectedErrorInfo(5, "xMA048");
    checkInvalid(checker, loadComponentAST(modelName), expectedErrorInfo);
  }

  @Test
  public void testNameConflictsWithInheritance() {
    final String modelName = PACKAGE + "." + "NameConflictsWithInheritance";
    final MontiArcCoCoChecker checker
        = new MontiArcCoCoChecker().addCoCo(new IdentifiersAreUnique());
    final ExpectedErrorInfo expectedErrorInfo
        = new ExpectedErrorInfo(2, "xMA053", "xMA035");
    checkInvalid(checker, loadComponentAST(modelName), expectedErrorInfo);
  }

  @Test
  public void testHasConflictingInPortNames() {
    checkValid(PACKAGE + "." + "HasConflictingInPortNames");
  }

  @Test
  public void testHasConflictingOutPortNames() {
    checkValid(PACKAGE + "." + "HasConflictingOutPortNames");
  }

  @Test
  @Ignore("ConnectorSourceAndTargetExistAndFit currently does not find the" +
              "incompatible types from the first connector")
  public void testPortCompatibilityWithGenerics4() {
    final String fqModelName = PACKAGE + "." + "PortCompatibilityWithGenerics4";
    final MontiArcCoCoChecker checker =
        new MontiArcCoCoChecker()
            .addCoCo(new ConnectorSourceAndTargetExistAndFit());
    final ExpectedErrorInfo expectedErrors = new ExpectedErrorInfo(1, "");
    checkInvalid(checker, loadComponentAST(fqModelName), expectedErrors);
  }
}
