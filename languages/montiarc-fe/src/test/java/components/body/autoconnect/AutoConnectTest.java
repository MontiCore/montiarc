package components.body.autoconnect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.monticore.types.types._ast.ASTQualifiedName;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTConnector;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentSymbol;
import montiarc.cocos.PortUsage;
import montiarc.cocos.SubComponentsConnected;

public class AutoConnectTest extends AbstractCoCoTest {
  
  private static final String PACKAGE = "components.body.autoconnect";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  /* This test tests whether the "autoconnect port" statement is working as intended. */
  @Test
  public void testAutoconnectPort() {
    Scope symTab = loadDefaultSymbolTable();
    Log.getFindings().clear();
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        PACKAGE + "." + "AutoConnectPorts", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    assertEquals(3, Log.getFindings().size());
    // 3 autoconnections failed cause of missing partners
    // 5 unused ports remaining
  
    assertTrue(comp.getAstNode().isPresent() && comp.getAstNode().get() instanceof ASTComponent);
    ASTComponent astComp = (ASTComponent) comp.getAstNode().get();
    
    MontiArcCoCoChecker coCoChecker = new MontiArcCoCoChecker().addCoCo(new PortUsage());
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "AutoConnectPorts");
    
    checkInvalid(coCoChecker, node, new ExpectedErrorInfo(1, "xMA058"));
    coCoChecker = new MontiArcCoCoChecker().addCoCo(new SubComponentsConnected());
    checkInvalid(coCoChecker, node, new ExpectedErrorInfo(4, "xMA059", "xMA060"));
    
    Collection<ASTConnector> connectors = astComp.getConnectors();
    List<String> connectorNames = connectors.stream().map(ASTConnector::toString).collect(Collectors.toList());
    
    assertEquals(7, connectors.size());
    assertTrue(connectorNames.contains("strIn -> a.strIn"));
    assertTrue(connectorNames.contains("intIn -> c.intIn"));
    assertTrue(connectorNames.contains("intIn -> b.intIn"));
    
    assertTrue(connectorNames.contains("b.myInt -> d.myInt"));
    assertTrue(connectorNames.contains("c.bb -> d.bool"));
    assertTrue(connectorNames.contains("d.intOut -> intOut"));
    assertTrue(connectorNames.contains("d.strOut -> strOut"));
    
  }
  
  
  @Test
  public void testAutoconnectPortPartiallyConnected() {
    Scope symTab = loadDefaultSymbolTable();
    Log.getFindings().clear();
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        PACKAGE + "." + "AutoconnectPortPartiallyConnected", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    assertTrue(comp.getAstNode().isPresent() && comp.getAstNode().get() instanceof ASTComponent);
    ASTComponent astComp = (ASTComponent) comp.getAstNode().get();
    
    Collection<ASTConnector> connectors = astComp.getConnectors();
    List<String> connectorNames = new ArrayList<String>();
    for (ASTConnector con : connectors) {
      connectorNames.add(con.toString());
      
    }
    
    assertEquals(3, connectors.size());
    assertTrue(connectorNames.contains("sIn -> e1.sIn"));
    assertTrue(connectorNames.contains("e1.sOut -> e2.sIn"));
    assertTrue(connectorNames.contains("e2.sOut -> sOut"));
    
  }
  
  @Test
  public void testAutoconnectType() {
    Scope symTab = loadDefaultSymbolTable();
    Log.getFindings().clear();
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        PACKAGE + "." + "AutoConnectType", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
  
    assertTrue(comp.getAstNode().isPresent() && comp.getAstNode().get() instanceof ASTComponent);
    ASTComponent astComp = (ASTComponent) comp.getAstNode().get();
    
    // 5 duplicate autoconnection matches
    assertEquals(5, Log.getFindings().size());
    
    // 8 still unused ports
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "AutoConnectType");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new PortUsage());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xMA058"));
    cocos = new MontiArcCoCoChecker().addCoCo(new SubComponentsConnected());
    checkInvalid(cocos, node, new ExpectedErrorInfo(6, "xMA059", "xMA060"));
    
    Collection<ASTConnector> connectors = astComp.getConnectors();
    List<String> connectorNames = new ArrayList<String>();
    for (ASTConnector con : connectors) {
      connectorNames.add(con.toString());
      
    }
    assertEquals(5, connectors.size());
    assertTrue(connectorNames.contains("strIn -> a.strIn"));
    assertTrue(connectorNames.contains("c.bb -> d.bool"));
    assertTrue(connectorNames.contains("d.intOut -> intOut"));
    assertTrue(connectorNames.contains("intIn -> b.intIn"));
    assertTrue(connectorNames.contains("a.data -> d.dataSthElse"));
  }

  @Test
  public void testDuplicateAutoconnectMatches() {
    Scope symTab = loadDefaultSymbolTable();
    Log.getFindings().clear();
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        PACKAGE + "." + "DuplicateAutoconnectMatches", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
  
    assertTrue(comp.getAstNode().isPresent() && comp.getAstNode().get() instanceof ASTComponent);
    ASTComponent astComp = (ASTComponent) comp.getAstNode().get();
    
    // 1 duplicate autoconnection matches
    assertEquals(1, Log.getFindings().size());
    
    // 3 unused ports due to failed autoconnection
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "DuplicateAutoconnectMatches");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new PortUsage());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xMA057"));
    cocos = new MontiArcCoCoChecker().addCoCo(new SubComponentsConnected());
    checkInvalid(cocos, node, new ExpectedErrorInfo(1, "xMA059"));
    
    Collection<ASTConnector> connectors = astComp.getConnectors();
    List<String> connectorNames = new ArrayList<String>();
    for (ASTConnector con : connectors) {
      connectorNames.add(con.toString());
    }
    
    assertEquals(4, connectors.size());
    assertTrue(connectorNames.contains("intIn -> inner.myInteger"));
    assertTrue(connectorNames.contains("inner.myBoolean -> boolOut1"));
    assertTrue(connectorNames.contains("inner.myBoolean -> boolOut2"));
    assertTrue(connectorNames.contains("inner.myDouble -> doubleOut"));
  }
  
  @Test
  public void testAutoconnectGenericType() {
    Scope symTab = loadDefaultSymbolTable();
    Log.getFindings().clear();
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        PACKAGE + "." + "AutoConnectGenericUsage", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
  
    assertTrue(comp.getAstNode().isPresent() && comp.getAstNode().get() instanceof ASTComponent);
    ASTComponent astComp = (ASTComponent) comp.getAstNode().get();
    
    Collection<ASTConnector> connectors = astComp.getConnectors();
    List<String> connectorNames = new ArrayList<String>();
    for (ASTConnector con : connectors) {
      connectorNames.add(con.toString());
    }
    
    assertEquals(2, connectors.size());
    assertTrue(connectorNames.contains("strIn -> myGeneric.inT"));
    assertTrue(connectorNames.contains("myGeneric.outT -> strOut"));
  }
  
  @Test
  public void testAutoconnectGenericInnerComponentType() {
    Scope symTab = loadDefaultSymbolTable();
    Log.getFindings().clear();
    final String modelName
        = PACKAGE + "." + "AutoConnectGenericInnerComponent";
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        modelName, ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
 
    assertTrue(comp.getAstNode().isPresent() && comp.getAstNode().get() instanceof ASTComponent);
    ASTComponent astComp = (ASTComponent) comp.getAstNode().get();
    
    Collection<ASTConnector> connectors = astComp.getConnectors();

    List<String> connectorNames = new ArrayList<String>();
    for (ASTConnector con : connectors) {
      connectorNames.add(con.toString());
    }
    
    assertEquals(4, connectors.size());
    assertTrue(connectorNames.contains("dbIn -> myGeneric.inT"));
    assertTrue(connectorNames.contains("myGeneric.outT -> dbOut"));
    assertTrue(connectorNames.contains("intIn -> a.inT"));
    assertTrue(connectorNames.contains("a.outT -> intOut"));
  }

  @Ignore("This test creates only one of the two connectors because Ports can only be connected to ports of the exact same Type. Type inheritance is ignored at the moment")
  @Test
  public void testAutoconnectGenericTypeInHierarchy() {
    Scope symTab = loadDefaultSymbolTable();
    Log.getFindings().clear();
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        PACKAGE + "." + "AutoConnectGenericUsageHierarchy", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
  
    assertTrue(comp.getAstNode().isPresent() && comp.getAstNode().get() instanceof ASTComponent);
    ASTComponent astComp = (ASTComponent) comp.getAstNode().get();
    
    Collection<ASTConnector> connectors = astComp.getConnectors();
    List<String> connectorNames = new ArrayList<String>();
    for (ASTConnector con : connectors) {
      connectorNames.add(con.toString());
    }
    
    assertEquals(2, connectors.size());
    assertTrue(connectorNames.contains("strIn -> sInner.tIn"));
    assertTrue(connectorNames.contains("sInner.tOut -> objOut"));
  }

  @Test
  public void testAutoconnectGenericPorts() {
    Scope symTab = loadDefaultSymbolTable();
    Log.getFindings().clear();
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        PACKAGE + "." + "AutoConnectGenericPorts", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
  
    assertTrue(comp.getAstNode().isPresent() && comp.getAstNode().get() instanceof ASTComponent);
    ASTComponent astComp = (ASTComponent) comp.getAstNode().get();
    
    Collection<ASTConnector> connectors = astComp.getConnectors();
    List<String> connectorNames = new ArrayList<String>();
    for (ASTConnector con : connectors) {
      connectorNames.add(con.toString());
    }
    
    // 3 warnings (1x unable to autoconnect, 2x unused ports)
    assertEquals(1, Log.getFindings().stream().filter(f -> f.isWarning()).count());
    
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "AutoConnectGenericPorts");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new SubComponentsConnected());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xMA059", "xMA060"));
    
    assertEquals(2, connectors.size());
    assertTrue(connectorNames.contains("strIn -> myGenericStr.inT"));
    assertTrue(connectorNames.contains("myGenericStr.outT -> strOut"));
    assertFalse(connectorNames.contains("strIn -> myGenericInt.inT"));
    assertFalse(connectorNames.contains("myGenericInt.outT -> strOut"));
  }
  @Test
  public void testAutoconnectArrayTypes() {
    Scope symTab = loadDefaultSymbolTable();
    Log.getFindings().clear();
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        PACKAGE + "." + "AutoConnectArrayTypes", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
  
    assertTrue(comp.getAstNode().isPresent() && comp.getAstNode().get() instanceof ASTComponent);
    ASTComponent astComp = (ASTComponent) comp.getAstNode().get();
    
    Collection<ASTConnector> connectors = astComp.getConnectors();
    List<String> connectorNames = new ArrayList<String>();
    for (ASTConnector con : connectors) {
      connectorNames.add(con.toString());
    }
    
    // 3 warnings (1x unable to autoconnect, 2x unused ports)
    assertEquals(1, Log.getFindings().stream().filter(f -> f.isWarning()).count());
    
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "AutoConnectArrayTypes");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new SubComponentsConnected());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xMA059", "xMA060"));
    
    assertEquals(2, connectors.size());
    assertTrue(connectorNames.contains("strIn -> ref.strIn1"));
    assertTrue(connectorNames.contains("ref.strOut1 -> strOut"));
    assertFalse(connectorNames.contains("strIn -> ref.strIn2"));
    assertFalse(connectorNames.contains("ref.strOut2 -> strOut"));
  }

  @Test
  public void testAutoconnectPortAndType() {
    Scope symTab = loadDefaultSymbolTable();
    Log.getFindings().clear();
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        PACKAGE + "." + "AutoConnectPortAndType", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
  
    assertTrue(comp.getAstNode().isPresent() && comp.getAstNode().get() instanceof ASTComponent);
    ASTComponent astComp = (ASTComponent) comp.getAstNode().get();
    
    Collection<ASTConnector> connectors = astComp.getConnectors();
    List<String> connectorNames = new ArrayList<String>();
    for (ASTConnector con : connectors) {
      connectorNames.add(con.toString());
    }
    
    // 1 warning because of failed autoconnection of port y when processing 'autoconnect port'
    assertEquals(1, Log.getFindings().stream().filter(f -> f.isWarning()).count());
    
    assertEquals(4, connectors.size());
    assertTrue(connectorNames.contains("a -> ref.a"));
    assertTrue(connectorNames.contains("b -> ref.b"));
    assertTrue(connectorNames.contains("c -> ref.c"));
    assertTrue(connectorNames.contains("ref.x -> y"));
  }

  @Test
  public void testReferencedPortAndType() {
    checkValid(PACKAGE + "." + "ReferencedPortAndType");
  }

  @Test
  public void testDummyComponent1() {
    checkValid(PACKAGE + ".dummycomponents." + "DummyComponent1");
  }

  @Test
  public void testDummyComponent2() {
    checkValid(PACKAGE + ".dummycomponents." + "DummyComponent2");
  }

  @Test
  public void testDummyComponent3() {
    checkValid(PACKAGE + ".dummycomponents." + "DummyComponent3");
  }

  @Test
  public void testDummyComponent4() {
    checkValid(PACKAGE + ".dummycomponents." + "DummyComponent4");
  }

  @Test
  public void testDummyComponent5() {
    checkValid(PACKAGE + ".dummycomponents." + "DummyComponent5");
  }
}
