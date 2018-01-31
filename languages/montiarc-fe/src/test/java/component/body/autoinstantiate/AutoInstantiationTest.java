/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package component.body.autoinstantiate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;

public class AutoInstantiationTest {
  
  private final String MODEL_PATH = "src/test/resources/symboltable";
  
  private final String MP = "src/test/resources/";
  
  private static final String PACKAGE = "component.body.autoinstantiate";
  
  private static MontiArcTool tool;
  
  @BeforeClass
  public static void setUp() {
    // ensure an empty log
    Log.getFindings().clear();
    Log.enableFailQuick(false);
    tool = new MontiArcTool();
  }
  
  private ComponentSymbol loadComponent(String unqualifiedComponentName) {
    Scope symTab = tool.createSymbolTable(MP);
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        PACKAGE + "." + unqualifiedComponentName, ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    return comp;
  }
  
  /**
   * There is only one Subcomponent instance symbol not 2 like there was in MontiArc 3.<br>
   * 
   * <pre>
   * comp B {
   *   comp A x;
   * }
   * </pre>
   * 
   * MontiArc3 subInstances: {a, x} <br>
   * MontiArc4 subInstances: {x}
   */
  @Test
  public void testBWithSubAWithInstanceName() {
    ComponentSymbol comp = this.loadComponent("BWithSubAWithInstanceName");
    assertEquals(PACKAGE, comp.getPackageName());
    assertEquals("BWithSubAWithInstanceName", comp.getName());
    assertEquals(PACKAGE + "." + "BWithSubAWithInstanceName", comp.getFullName());
    Collection<ComponentInstanceSymbol> subcomps = comp.getSubComponents();
    assertEquals(1, subcomps.size());
    for (ComponentInstanceSymbol s : subcomps) {
      assertEquals("y", s.getName());
    }
    
  }
  
  @Test
  public void testBWithSubAWithoutInstanceName() {
    ComponentSymbol comp = this.loadComponent("BWithSubAWithoutInstanceName");
    assertEquals(PACKAGE, comp.getPackageName());
    assertEquals("BWithSubAWithoutInstanceName", comp.getName());
    assertEquals(PACKAGE + "." + "BWithSubAWithoutInstanceName", comp.getFullName());
    Collection<ComponentInstanceSymbol> subcomps = comp.getSubComponents();
    assertEquals(1, subcomps.size());
    for (ComponentInstanceSymbol s : subcomps) {
      assertEquals("a", s.getName());
    }
  }
  
  @Test
  public void testComponentWithPortName() {
    ComponentSymbol comp = this.loadComponent("ComponentWithPortName");
    assertEquals(PACKAGE, comp.getPackageName());
    assertEquals("ComponentWithPortName", comp.getName());
    assertEquals(PACKAGE + "." + "ComponentWithPortName", comp.getFullName());
    Collection<PortSymbol> ports = comp.getPorts();
    assertEquals(1, ports.size());
    for (PortSymbol p : ports) {
      assertEquals("x", p.getName());
    }
  }
  
  @Test
  public void testComponentWithSimplifiedPortDef() {
    ComponentSymbol comp = this.loadComponent("ComponentWithSimplifiedPortDef");
    assertEquals(6, comp.getPorts().size());
    assertEquals(5, comp.getAllIncomingPorts().size());
    assertEquals(1, comp.getAllOutgoingPorts().size());
  }
  
  @Test
  public void testComponentWithSimplifiedPortDef2() {
    ComponentSymbol comp = this.loadComponent("ComponentWithSimplifiedPortDef2");
    assertEquals(6, comp.getPorts().size());
    assertEquals(5, comp.getAllIncomingPorts().size());
    assertEquals(1, comp.getAllOutgoingPorts().size());
  }
  
  @Test
  public void testComponentWithoutPortName() {
    ComponentSymbol comp = this.loadComponent("ComponentWithoutPortName");
    assertEquals(PACKAGE, comp.getPackageName());
    assertEquals("ComponentWithoutPortName", comp.getName());
    assertEquals(PACKAGE + "." + "ComponentWithoutPortName", comp.getFullName());
    Collection<PortSymbol> ports = comp.getPorts();
    assertEquals(1, ports.size());
    for (PortSymbol p : ports) {
      assertEquals("string", StringTransformations.uncapitalize(p.getTypeReference().getName()));
    }
  }
  
  @Test
  public void testComponentWithSimplifiedVariableDef() {
    ComponentSymbol comp = this.loadComponent("ComponentWithSimplifiedVariableDef");
    assertEquals(6, comp.getVariables().size());
  }
  
  @Test
  public void testComponentWithSimplifiedVariableDef2() {
    ComponentSymbol comp = this.loadComponent("ComponentWithSimplifiedVariableDef2");
    assertEquals(6, comp.getVariables().size());
  }
  
  /**
   * Assure that an inner component with formal type parameters is not auto-instantiated
   */
  @Test
  public void test() {
    ComponentSymbol comp = this.loadComponent("InnerComponentWithFormalTypeParameters");
    assertEquals(0, comp.getSubComponents().size());
  }
}
