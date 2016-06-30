/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.monticore.lang.montiarc.montiarc._symboltable.ExpandedComponentInstanceSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol;
import de.monticore.symboltable.Scope;
import org.junit.Test;

/**
 * Tests for toString methods of MontiArc symbols.
 *
 * @author Michael von Wenckstern
 */
public class ExpandedComponentInstanceTest extends AbstractSymtabTest {

  @Test
  public void testComponentSub2() throws Exception {
    Scope symTab = createSymTab("src/test/resources/arc/symtab");
    ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
        "a.sub2", ExpandedComponentInstanceSymbol.KIND).orElse(null);
    assertNotNull(inst);
    System.out.println(inst);

    assertEquals(inst.getPorts().size(), 3);
    assertTrue(inst.getPort("string").isPresent()); // from a.Sub2
    assertTrue(inst.getPort("integer").isPresent()); // from a.Sub2
    assertTrue(inst.getPort("input").isPresent()); // from b.SuperSamePackage
  }

  @Test
  public void testComponentSub1() throws Exception {
    Scope symTab = createSymTab("src/test/resources/arc/symtab");
    ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
        "a.sub1", ExpandedComponentInstanceSymbol.KIND).orElse(null);
    assertNotNull(inst);
    System.out.println(inst);

    assertEquals(inst.getPorts().size(), 8); // the port "in String stringIn" of super component "a.SuperSamePackage"
    // will be hidden by the port "in String stringIn" of the own component "a.Sub1"
    assertEquals(inst.getSubComponents().size(), 6);
  }

  @Test
  public void testComponentSub1cComp() throws Exception {
    // test for model loader to load instance without loading component
    // definition before
    Scope symTab = createSymTab("src/test/resources/arc/symtab");
    ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
        "a.sub1.cComp", ExpandedComponentInstanceSymbol.KIND).orElse(null);
    assertNotNull(inst);
    System.out.println(inst);
  }

  @Test
  public void testSuperComp1() throws Exception {
    // test for recursive inheritance
    Scope symTab = createSymTab("src/test/resources/arc/symtab");
    ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
        "instance.superComp1", ExpandedComponentInstanceSymbol.KIND).orElse(null);
    assertNotNull(inst);
    System.out.println(inst);

    assertEquals(6, inst.getConnectors().size());
    assertEquals(3, inst.getSubComponent("a1").get().getConnectors().size());
    assertEquals(12, inst.getPorts().size()); // 2x in SuperComp1; 3x in D; 4x in C; 3x in A
    assertEquals(5, inst.getSubComponents().size()); // 3x SuperComp1; 1x in D; 0x in C;
    // 1x in A (A::b1 is overlapped by same name in SuperComp1)
    assertEquals(inst.getSubComponent("a1").get().getSubComponents().size(), 2); // b1 and b2
  }

  @Test
  public void testSubGenericInstance() throws Exception {
    Scope symTab = createSymTab("src/test/resources/arc/symtab");
    ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
        "generics.subGenericInstance", ExpandedComponentInstanceSymbol.KIND).orElse(null);

    assertNotNull(inst);
    System.out.println(inst);
    // test whether T is replaced by Integer
    inst.getPorts().stream().forEachOrdered(p -> assertEquals(p.getTypeReference().getName(), "Integer"));

    ExpandedComponentInstanceSymbol inst2 = symTab.<ExpandedComponentInstanceSymbol>resolve(
        "generics.superGenericCompInstance", ExpandedComponentInstanceSymbol.KIND).orElse(null);

    assertNotNull(inst2);
    System.out.println(inst2);
    // test whether T is replaced by Integer
    assertEquals(inst2.getSubComponent("sgc").get().getPort("tIn").get().getTypeReference().getName(), "Double");
    assertEquals(inst2.getSubComponent("sgc").get().getPort("tOut").get().getTypeReference().getName(), "Integer");

    assertEquals(inst2.getSubComponent("sgc2").get().getPort("tIn").get().getTypeReference().getName(), "Boolean");
    assertEquals(inst2.getSubComponent("sgc2").get().getPort("tOut").get().getTypeReference().getName(), "String");
  }

  @Test
  public void testGenericInstance() throws Exception {
    Scope symTab = createSymTab("src/test/resources/arc/symtab");
    ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
        "generics.genericInstance", ExpandedComponentInstanceSymbol.KIND).orElse(null);

    assertNotNull(inst);
    System.out.println(inst);

    //<editor-fold desc="How to derive types through generics">
    /*
    component GenericInstance {
      component Generic<T extends Number> {
        ports in T in1,
              in T in2,
              out T out1;

        component SuperGenericComparableComp2<String, T> sc1;
        component SuperGenericComparableComp2<Integer, T> sc2;
      }

      component Generic<Double> gDouble;
      component Generic<Integer> gInteger;
    }

    component SuperGenericComparableComp2<K, T extends Comparable<T>> {

        port
            in T tIn,
            out K tOut;
    }

    ==>
    component GenericInstance {
      component Generic<T=Double> gDouble {
        ports in Double in1,
              in Double in2,
              out Double out1;

        component SuperGenericComparableComp2<K=String, T=Double> sc1 {
          port
            in Double tIn,
            out String tOut;
        }

        component SuperGenericComparableComp2<K=Integer, T=Double> sc2 {
          port
            in Double tIn,
            out Integer tOut;
        }
      }

      component Generic<T=Integer> gInteger {
        ports in Integer in1,
              in Integer in2,
              out Integer out1;

        component SuperGenericComparableComp2<K=String, T=Integer> sc1 {
          port
            in Integer tIn,
            out String tOut;
        }

        component SuperGenericComparableComp2<K=Integer, T=Integer> sc2 {
          port
            in Integer tIn,
            out Integer tOut;
        }
      }
     */
    //</editor-fold>
    assertEquals(inst.getSubComponent("gDouble").get().getSubComponent("sc1")
        .get().getPort("tIn").get().getTypeReference().getName(), "Double");
    assertEquals(inst.getSubComponent("gDouble").get().getSubComponent("sc1")
        .get().getPort("tOut").get().getTypeReference().getName(), "String");

    assertEquals(inst.getSubComponent("gDouble").get().getSubComponent("sc2")
        .get().getPort("tIn").get().getTypeReference().getName(), "Double");
    assertEquals(inst.getSubComponent("gDouble").get().getSubComponent("sc2")
        .get().getPort("tOut").get().getTypeReference().getName(), "Integer");

    assertEquals(inst.getSubComponent("gInteger").get().getSubComponent("sc1")
        .get().getPort("tIn").get().getTypeReference().getName(), "Integer");
    assertEquals(inst.getSubComponent("gInteger").get().getSubComponent("sc1")
        .get().getPort("tOut").get().getTypeReference().getName(), "String");

    assertEquals(inst.getSubComponent("gInteger").get().getSubComponent("sc2")
        .get().getPort("tIn").get().getTypeReference().getName(), "Integer");
    assertEquals(inst.getSubComponent("gInteger").get().getSubComponent("sc2")
        .get().getPort("tOut").get().getTypeReference().getName(), "Integer");
  }

  @Test
  public void testGenericExtension() throws Exception {
    Scope symTab = createSymTab("src/test/resources/arc/symtab");
    ExpandedComponentInstanceSymbol inst = symTab.<ExpandedComponentInstanceSymbol>resolve(
        "generics.baseClassGenerics", ExpandedComponentInstanceSymbol.KIND).orElse(null);

    assertNotNull(inst);
    System.out.println(inst);

    assertEquals(inst.getPort("boolIn").get().getTypeReference().getName(), "Boolean");
    assertEquals(inst.getPort("intOut").get().getTypeReference().getName(), "Integer");
    assertEquals(inst.getPort("sIn1").get().getTypeReference().getName(), "String"); // test if T is replaced by String
    assertEquals(inst.getPort("sIn2").get().getTypeReference().getName(), "String");
    assertEquals(inst.getPort("sOut").get().getTypeReference().getName(), "String");
  }

  @Test
  public void testLoadingInstancePort() throws Exception {
    Scope symTab = createSymTab("src/test/resources/arc/symtab");
    PortSymbol port = symTab.<PortSymbol>resolve(
        "a.sub1.cComp.in1", PortSymbol.KIND).orElse(null);
    assertNotNull(port);
    System.out.println(port);
  }
}
