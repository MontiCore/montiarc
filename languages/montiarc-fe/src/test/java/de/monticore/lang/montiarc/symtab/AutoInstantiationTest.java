/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.symtab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.lang.montiarc.AbstractSymtabTest;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentInstanceSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

public class AutoInstantiationTest extends AbstractSymtabTest {

  @BeforeClass
  public static void setUp() {
    // ensure an empty log
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }

  /**
   * There is only one Subcomponent instance symbol not 2 like there was in MontiArc 3.<br>
   * <pre>
   * comp B {
   *   comp A x;
   * }
   * </pre>
   * MontiArc3 subInstances: {a, x} <br>
   * MontiArc4 subInstances: {x}
   */
  @Test
  public void testSubcomponentWithInstanceName() {
    Scope symTab = createSymTab("src/test/resources/symboltable");
    ComponentSymbol comp = symTab.<ComponentSymbol>resolve(
        "instantiation.BWithSubAWithInstanceName", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    assertEquals("instantiation", comp.getPackageName());
    assertEquals("BWithSubAWithInstanceName", comp.getName());
    assertEquals("instantiation.BWithSubAWithInstanceName", comp.getFullName());
    Collection<ComponentInstanceSymbol> subcomps = comp.getSubComponents();
    assertEquals(1, subcomps.size());
    for (ComponentInstanceSymbol s : subcomps) {
      assertEquals("y", s.getName());
    }

  }

  @Test
  public void testSubcomponentWithoutInstanceName() {
    Scope symTab = createSymTab("src/test/resources/symboltable");
    ComponentSymbol comp = symTab.<ComponentSymbol>resolve(
        "instantiation.BWithSubAWithoutInstanceName", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    assertEquals("instantiation", comp.getPackageName());
    assertEquals("BWithSubAWithoutInstanceName", comp.getName());
    assertEquals("instantiation.BWithSubAWithoutInstanceName", comp.getFullName());
    Collection<ComponentInstanceSymbol> subcomps = comp.getSubComponents();
    assertEquals(1, subcomps.size());
    for (ComponentInstanceSymbol s : subcomps) {
      assertEquals("a", s.getName());
    }
  }

  @Test
  public void testPortWithName() {
    Scope symTab = createSymTab("src/test/resources/symboltable");
    ComponentSymbol comp = symTab.<ComponentSymbol>resolve(
        "instantiation.ComponentWithPortName", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    assertEquals("instantiation", comp.getPackageName());
    assertEquals("ComponentWithPortName", comp.getName());
    assertEquals("instantiation.ComponentWithPortName", comp.getFullName());
    Collection<PortSymbol> ports = comp.getPorts();
    assertEquals(1, ports.size());
    for (PortSymbol p : ports) {
      assertEquals("x", p.getName());
    }
  }

  @Test
  public void testPortWithoutName() {
    Scope symTab = createSymTab("src/test/resources/symboltable");
    ComponentSymbol comp = symTab.<ComponentSymbol>resolve(
        "instantiation.ComponentWithoutPortName", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    assertEquals("instantiation", comp.getPackageName());
    assertEquals("ComponentWithoutPortName", comp.getName());
    assertEquals("instantiation.ComponentWithoutPortName", comp.getFullName());
    Collection<PortSymbol> ports = comp.getPorts();
    assertEquals(1, ports.size());
    for (PortSymbol p : ports) {
      assertEquals("string", StringTransformations.uncapitalize(p.getTypeReference().getName()));
    }
  }

  /**
   * Assure that an inner component with formal type parameters is not auto-instantiated
   */
  @Test
  public void testInnerComponentWithFormalTypeParameters() {
    Scope symTab = createSymTab("src/test/resources/symboltable");
    ComponentSymbol component = symTab
        .<ComponentSymbol>resolve(
            "instantiation.InnerComponentWithFormalTypeParameters", ComponentSymbol.KIND)
        .orElse(null);

    assertNotNull(component);
    assertEquals(0, component.getSubComponents().size());
  }
}
