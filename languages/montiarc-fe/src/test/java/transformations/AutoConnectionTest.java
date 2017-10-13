package transformations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.PortUsage;
import org.junit.Test;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ConnectorSymbol;
import symboltable.AbstractSymboltableTest;

public class AutoConnectionTest extends AbstractSymboltableTest {

  @Test
  public void testAutoconnectPort() {
    Scope symTab = createSymTab("src/test/resources/arc/transformations");
    Log.getFindings().clear();
    ComponentSymbol comp = symTab.<ComponentSymbol>resolve(
        "a.AutoConnectPorts", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    // 3 autoconnections failed cause of missing partners
    // 5 unused ports remaining
    // TODO implement PortUsage Coco
    // assertEquals(8, Log.getFindings().size());
    assertEquals(3, Log.getFindings().size());

    Collection<ConnectorSymbol> connectors = comp.getConnectors();
    List<String> connectorNames = new ArrayList<String>();
    for (ConnectorSymbol con : connectors) {
      connectorNames.add(con.toString());
    }

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
    Scope symTab = createSymTab("src/test/resources/arc/transformations");
    Log.getFindings().clear();
    ComponentSymbol comp = symTab.<ComponentSymbol>resolve(
        "a.AutoconnectPortPartiallyConnected", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);

    Collection<ConnectorSymbol> connectors = comp.getConnectors();
    List<String> connectorNames = new ArrayList<String>();
    for (ConnectorSymbol con : connectors) {
      connectorNames.add(con.toString());

    }

    assertEquals(3, connectors.size());
    assertTrue(connectorNames.contains("sIn -> e1.sIn"));
    assertTrue(connectorNames.contains("e1.sOut -> e2.sIn"));
    assertTrue(connectorNames.contains("e2.sOut -> sOut"));

  }

  @Test
  public void testAutoconnectType1() {
    Scope symTab = createSymTab("src/test/resources/arc/transformations");
    Log.getFindings().clear();
    ComponentSymbol comp = symTab.<ComponentSymbol>resolve(
        "a.AutoConnectType", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);

    // 5 duplicate autoconnectios matches
    // 8 still unused ports
    // TODO implement PortUsage coco
    assertEquals(5, Log.getFindings().size());
    // assertEquals(13, Log.getFindings().size());

    Collection<ConnectorSymbol> connectors = comp.getConnectors();
    List<String> connectorNames = new ArrayList<String>();
    for (ConnectorSymbol con : connectors) {
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
  public void testAutoconnectType2() {
    Scope symTab = createSymTab("src/test/resources/arc/transformations");
    Log.getFindings().clear();
    ComponentSymbol comp = symTab.<ComponentSymbol>resolve(
        "a.AutoConnectType2", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);

    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new PortUsage());

    // 1 duplicate autoconnection matches
    // 3 unused ports due to failed autoconnection
    // TODO implement PortUsage coco
    assertEquals(1, Log.getFindings().size());
    // assertEquals(4, Log.getFindings().size());

    Collection<ConnectorSymbol> connectors = comp.getConnectors();
    List<String> connectorNames = new ArrayList<String>();
    for (ConnectorSymbol con : connectors) {
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
    Scope symTab = createSymTab("src/test/resources/arc/transformations");
    Log.getFindings().clear();
    ComponentSymbol comp = symTab.<ComponentSymbol>resolve(
        "a.AutoConnectGenericUsage", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);

    Collection<ConnectorSymbol> connectors = comp.getConnectors();
    List<String> connectorNames = new ArrayList<String>();
    for (ConnectorSymbol con : connectors) {
      connectorNames.add(con.toString());
    }

    assertEquals(2, connectors.size());
    assertTrue(connectorNames.contains("strIn -> myGeneric.myStrIn"));
    assertTrue(connectorNames.contains("myGeneric.myStrOut -> strOut"));
  }

  @Test
  public void testAutoconnectGenericInnerComponentType() {
    Scope symTab = createSymTab("src/test/resources/arc/transformations");
    Log.getFindings().clear();
    ComponentSymbol comp = symTab.<ComponentSymbol>resolve(
        "a.AutoConnectGenericInnerComponent", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);

    Collection<ConnectorSymbol> connectors = comp.getConnectors();
    List<String> connectorNames = new ArrayList<String>();
    for (ConnectorSymbol con : connectors) {
      connectorNames.add(con.toString());
    }

    assertEquals(4, connectors.size());
    assertTrue(connectorNames.contains("strIn -> myGeneric.myStrIn"));
    assertTrue(connectorNames.contains("myGeneric.myStrOut -> strOut"));
    assertTrue(connectorNames.contains("intIn -> a.myStrIn"));
    assertTrue(connectorNames.contains("a.myStrOut -> intOut"));
  }

  @Test
  public void testAutoconnectGenericTypeInHierarchie() {
    Scope symTab = createSymTab("src/test/resources/arc/transformations");
    Log.getFindings().clear();
    ComponentSymbol comp = symTab.<ComponentSymbol>resolve(
        "a.AutoConnectGenericUsageHierarchie", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);

    Collection<ConnectorSymbol> connectors = comp.getConnectors();
    List<String> connectorNames = new ArrayList<String>();
    for (ConnectorSymbol con : connectors) {
      connectorNames.add(con.toString());
    }

    assertEquals(2, connectors.size());
    assertTrue(connectorNames.contains("strIn -> sInner.tIn"));
    assertTrue(connectorNames.contains("sInner.tOut -> objOut"));
  }

  @Test
  public void testAutoconnectGenericPorts() {
    Scope symTab = createSymTab("src/test/resources/arc/transformations");
    Log.getFindings().clear();
    ComponentSymbol comp = symTab.<ComponentSymbol>resolve(
        "a.AutoConnectGenericPorts", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);

    Collection<ConnectorSymbol> connectors = comp.getConnectors();
    List<String> connectorNames = new ArrayList<String>();
    for (ConnectorSymbol con : connectors) {
      connectorNames.add(con.toString());
    }

    // TODO implement unused ports coco
    // 3 warnings (1x unable to autoconnect, 2x unused ports)
    // assertEquals(3, Log.getFindings().stream().filter(f -> f.isWarning()).count());
    assertEquals(1, Log.getFindings().stream().filter(f -> f.isWarning()).count());

    assertEquals(2, connectors.size());
    assertTrue(connectorNames.contains("strIn -> myGenericStr.myStrIn"));
    assertTrue(connectorNames.contains("myGenericStr.myStrOut -> strOut"));
    assertFalse(connectorNames.contains("strIn -> myGenericInt.myStrIn"));
    assertFalse(connectorNames.contains("myGenericInt.myStrOut -> strOut"));
  }

  @Test
  public void testAutoconnectArrayTypes() {
    Scope symTab = createSymTab("src/test/resources/arc/transformations");
    Log.getFindings().clear();
    ComponentSymbol comp = symTab.<ComponentSymbol>resolve(
        "a.AutoConnectArrayTypes", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);

    Collection<ConnectorSymbol> connectors = comp.getConnectors();
    List<String> connectorNames = new ArrayList<String>();
    for (ConnectorSymbol con : connectors) {
      connectorNames.add(con.toString());
    }

    // TODO implement unused ports coco
    // 3 warnings (1x unable to autoconnect, 2x unused ports)
    // assertEquals(3, Log.getFindings().stream().filter(f -> f.isWarning()).count());
    assertEquals(1, Log.getFindings().stream().filter(f -> f.isWarning()).count());

    assertEquals(2, connectors.size());
    assertTrue(connectorNames.contains("strIn -> ref.strIn1"));
    assertTrue(connectorNames.contains("ref.strOut1 -> strOut"));
    assertFalse(connectorNames.contains("strIn -> ref.strIn2"));
    assertFalse(connectorNames.contains("ref.strOut2 -> strOut"));
  }

  @Test
  public void testAutoconnectPortAndType() {
    Scope symTab = createSymTab("src/test/resources/arc/transformations");
    Log.getFindings().clear();
    ComponentSymbol comp = symTab.<ComponentSymbol>resolve(
        "a.AutoConnectPortAndType", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);

    Collection<ConnectorSymbol> connectors = comp.getConnectors();
    List<String> connectorNames = new ArrayList<String>();
    for (ConnectorSymbol con : connectors) {
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
}
