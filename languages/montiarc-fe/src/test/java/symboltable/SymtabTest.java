/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.cocos.helper.Assert;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ComponentSymbolReference;
import montiarc._symboltable.ConnectorSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.ValueSymbol;
import montiarc.helper.SymbolPrinter;

/**
 * Tests for symbol table of MontiArc.
 *
 * @author Robert Heim
 */
public class SymtabTest {
  
  private static MontiArcTool tool;
  
  @BeforeClass
  public static void setUp() {
    // ensure an empty log
    Log.getFindings().clear();
    Log.enableFailQuick(false);
    tool = new MontiArcTool();
  }
  
  @Test
  public void testResolveJavaDefaultTypes() {
    Scope symTab = tool.createSymbolTable(Paths.get("src/test/resources/arc/symtab").toFile(), Paths.get("src/main/resources/defaultTypes").toFile());
    
    Optional<JTypeSymbol> javaType = symTab.resolve("String", JTypeSymbol.KIND);
    assertFalse(
        "java.lang types may not be resolvable without qualification in general (e.g., global scope).",
        javaType.isPresent());
        
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        "a.ComponentWithNamedInnerComponent", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    
    // java.lang.*
    javaType = comp.getSpannedScope().resolve("String", JTypeSymbol.KIND);
    assertTrue("java.lang types must be resolvable without qualification within components.",
        javaType.isPresent());

    // java.util.*
    javaType = comp.getSpannedScope().resolve("Set", JTypeSymbol.KIND);
    assertTrue("java.util types must be resolvable without qualification within components.",
        javaType.isPresent());
    
  }
  
  @Test
  public void testComponentWithNamedInnerComponent() {
    Scope symTab = tool.createSymbolTable("src/test/resources/arc/symtab");
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        "a.ComponentWithNamedInnerComponent", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    assertEquals("a", comp.getPackageName());
    assertEquals("ComponentWithNamedInnerComponent", comp.getName());
    assertEquals("a.ComponentWithNamedInnerComponent", comp.getFullName());
    assertFalse(comp.isInnerComponent());
    assertEquals(0, comp.getConfigParameters().size());
    assertEquals(1, comp.getAllIncomingPorts().size());
    assertEquals(1, comp.getAllOutgoingPorts().size());
    
    // ensures that inner component definitions can be loaded with the model loader, so we can
    // resolve references to them of sub components, see ModelNameCalculator.
    assertEquals(1, comp.getSubComponents().size());
    ComponentInstanceSymbol subComp = comp.getSubComponents().iterator().next();
    assertEquals("a.ComponentWithNamedInnerComponent.instance", subComp.getFullName());
    assertEquals("instance", subComp.getName());
    
    assertEquals(1, comp.getInnerComponents().size());
    
    ComponentSymbol inner = comp.getInnerComponent("NamedInnerComponent").orElse(null);
    assertNotNull(inner);
    ComponentSymbolReference compRefToInner = subComp.getComponentType();
    assertTrue(compRefToInner.getReferencedComponent().isPresent());
    assertTrue(inner == compRefToInner.getReferencedComponent().get());
    assertEquals("NamedInnerComponent", inner.getName());
    assertEquals("NamedInnerComponent", compRefToInner.getName());
    assertEquals("a.ComponentWithNamedInnerComponent.NamedInnerComponent", inner.getFullName());
    assertEquals("a.ComponentWithNamedInnerComponent.NamedInnerComponent",
        compRefToInner.getFullName());
    assertTrue(inner.isInnerComponent());
    assertTrue(compRefToInner.isInnerComponent());
    assertEquals(1, inner.getAllIncomingPorts().size());
    assertEquals(1, compRefToInner.getAllIncomingPorts().size());
    assertEquals(1, inner.getAllOutgoingPorts().size());
    assertEquals(1, compRefToInner.getAllOutgoingPorts().size());
    
    assertEquals(2, comp.getConnectors().size());
    ConnectorSymbol conn = comp.getConnector("instance.sIn").orElse(null);
    assertNotNull(conn);
    assertEquals("sIn", conn.getSource());
    assertEquals("instance.sIn", conn.getTarget());
    
    conn = comp.getConnector("sOut").orElse(null);
    assertNotNull(conn);
    assertEquals("instance.sOut", conn.getSource());
    assertEquals("sOut", conn.getTarget());
    assertEquals(
        "Connectors should not be added to both, the connector-defining-component AND the target-component, but only to the source",
        0, inner.getConnectors().size());
        
    ComponentSymbol innerComp = symTab.<ComponentSymbol> resolve(
        "a.ComponentWithNamedInnerComponent.NamedInnerComponent", ComponentSymbol.KIND)
        .orElse(null);
    assertNotNull(innerComp);
  }
  
  @Test
  public void testCompWithCfgArgs() {
    Scope symTab = tool.createSymbolTable("src/test/resources/arc/symtab");
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        "a.CompWithCfgArgs", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    assertEquals("a", comp.getPackageName());
    assertEquals("CompWithCfgArgs", comp.getName());
    assertEquals("a.CompWithCfgArgs", comp.getFullName());
    assertFalse(comp.isInnerComponent());
    assertEquals(1, comp.getConfigParameters().size());
    assertEquals(0, comp.getFormalTypeParameters().size());
  }
  
  /**
   * Test for ticket #21.
   */
  @Test
  public void testCompWithGenericsAndInnerGenericComponent() {
    Scope symTab = tool.createSymbolTable("src/test/resources/arc/symtab");
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        "a.GenericCompWithInnerGenericComp", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
  }
  
  @Test
  public void testNamedInnerComponent() {
    Scope symTab = tool.createSymbolTable("src/test/resources/arc/symtab");
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        "a.ComponentWithNamedInnerComponent", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    ComponentInstanceSymbol instance = (ComponentInstanceSymbol) comp.getSpannedScope()
        .resolve("instance", ComponentInstanceSymbol.KIND).orElse(null);
    assertNotNull(instance);
    assertEquals("instance", instance.getName());
    assertEquals("NamedInnerComponent",
        instance.getComponentType().getName());
    assertEquals("a.ComponentWithNamedInnerComponent.NamedInnerComponent",
        instance.getComponentType().getFullName());
  }
  
  @Ignore("ValueSymbol?!")
  @Test
  public void testParametersSymtab() {
    Scope symTab = tool.createSymbolTable("src/test/resources/arc/symtab");
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        "params.UsingSCWithParams", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    
    //assertEquals(0, Log.getErrorCount());
    // TODO portusage coco
    // assertEquals(1, Log.getFindings().stream().filter(f -> f.isWarning()).count());
    assertEquals(0, Log.getFindings().stream().filter(f -> f.isWarning()).count());
    
    ComponentInstanceSymbol delay = (ComponentInstanceSymbol) comp.getSpannedScope()
        .resolve("deleteTempFile", ComponentInstanceSymbol.KIND).orElse(null);
    assertNotNull(delay);
    assertEquals("deleteTempFile", delay.getName());
    
    assertEquals(1, delay.getConfigArguments().size());
    assertEquals("1", delay.getConfigArguments().get(0).getValue());

    //Is an expression since there is no value symbol.
    assertEquals(ValueSymbol.Kind.Value,
        delay.getConfigArguments().get(0).getKind());
  }
  
  /**
   * TODO: ValueSymbol?!
   */
  @Test
  public void testComplexParametersSymtab() {
    Scope symTab = tool.createSymbolTable("src/test/resources/arc/symtab");
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        "params.UsingComplexParams", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    
    assertEquals(0, Log.getErrorCount());
    Assert.assertEqualErrorCounts(new ArrayList<Finding>(),
        Log.getFindings().stream().filter(f -> f.isWarning()).collect(Collectors.toList()));
        
    ComponentInstanceSymbol delay = (ComponentInstanceSymbol) comp.getSpannedScope()
        .resolve("cp", ComponentInstanceSymbol.KIND).orElse(null);
    assertNotNull(delay);
    assertEquals("cp", delay.getName());
    
    assertEquals(2, delay.getConfigArguments().size());
    assertEquals("new int[] {1, 2, 3}",
        SymbolPrinter.printConfigArgument(delay.getConfigArguments().get(0)));
    // TODO value symbol
    // assertEquals(ValueSymbol.Kind.ConstructorCall, delay.getConfigArguments().get(0).getKind());
    // assertEquals("1",
    // delay.getConfigArguments().get(0).getConstructorArguments().get(0).getValue());
    // assertEquals("2",
    // delay.getConfigArguments().get(0).getConstructorArguments().get(1).getValue());
    // assertEquals("3",
    // delay.getConfigArguments().get(0).getConstructorArguments().get(2).getValue());
    // assertEquals("new HashMap<List<String>, List<Integer>>()",
    // delay.getConfigArguments().get(1).getValue());
    // assertEquals(Kind.ConstructorCall, delay.getConfigArguments().get(1).getKind());
    // JTypeReference<? extends JTypeSymbol> typeRef = delay.getConfigArguments().get(1).getType();
    // assertEquals("java.util.List", typeRef.getTypeParameters().get(0).getType().getName());
    // assertEquals("java.util.List", typeRef.getTypeParameters().get(1).getType().getName());
    // assertEquals("java.lang.String",
    // typeRef.getTypeParameters().get(0).getTypeParameters().get(0).getType().getName());
    // assertEquals("java.lang.Integer",
    // typeRef.getTypeParameters().get(1).getTypeParameters().get(0).getType().getName());
  }
  
  /**
   * TODO: ValueSymbol!?
   */
  @Test
  public void testGenericParametersSymtab3() {
    Scope symTab = tool.createSymbolTable("src/test/resources/arc/symtab");
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        "params.UsingComplexGenericParams", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    
    assertEquals(0, Log.getErrorCount());
    assertEquals(0, Log.getFindings().stream().filter(f -> f.isWarning()).count());
    
    ComponentInstanceSymbol delay = (ComponentInstanceSymbol) comp.getSpannedScope()
        .resolve("cp", ComponentInstanceSymbol.KIND).orElse(null);
    assertNotNull(delay);
    assertEquals("cp", delay.getName());
    
    assertEquals(2, delay.getConfigArguments().size());
    assertEquals("new int[] {1, 2, 3}", SymbolPrinter.printConfigArgument(delay.getConfigArguments().get(0)));
    // TODO value symbol
    // assertEquals(Kind.ConstructorCall, delay.getConfigArguments().get(0).getKind());
    // assertEquals("1",
    // delay.getConfigArguments().get(0).getConstructorArguments().get(0).getValue());
    // assertEquals("2",
    // delay.getConfigArguments().get(0).getConstructorArguments().get(1).getValue());
    // assertEquals("3",
    // delay.getConfigArguments().get(0).getConstructorArguments().get(2).getValue());
    
    assertEquals("new HashMap<List<K>, List<V>>()", SymbolPrinter.printConfigArgument(delay.getConfigArguments().get(1)));
    // TODO value symbol
    // assertEquals(Kind.ConstructorCall, delay.getConfigArguments().get(1).getKind());
    // ArcdTypeReferenceEntry typeRef = delay.getConfigArguments().get(1).getType();
    // assertEquals("java.util.List", typeRef.getTypeParameters().get(0).getType().getName());
    // assertEquals("java.util.List", typeRef.getTypeParameters().get(1).getType().getName());
    // assertEquals("K",
    // typeRef.getTypeParameters().get(0).getTypeParameters().get(0).getType().getName());
    // assertEquals("V",
    // typeRef.getTypeParameters().get(1).getTypeParameters().get(0).getType().getName());
    
  }
  
  @Test
  public void testPortStereoType() {
    Scope symTab = tool.createSymbolTable("src/test/resources/arc/symtab");
    PortSymbol port = symTab.<PortSymbol> resolve("a.Sub1.integerIn", PortSymbol.KIND).orElse(null);
    assertNotNull(port);
    
    assertEquals(3, port.getStereotype().size());
    assertEquals("held", port.getStereotype().get("disabled").get());
    assertEquals("1", port.getStereotype().get("initialOutput").get());
    assertFalse(port.getStereotype().get("ignoreWarning").isPresent());
  }
  
  @Test
  public void testConnectorStereoType() {
    Scope symTab = tool.createSymbolTable("src/test/resources/arc/symtab");
    ConnectorSymbol connector = symTab
        .<ConnectorSymbol> resolve("a.Sub1.stringOut", ConnectorSymbol.KIND).orElse(null);
    assertNotNull(connector);
    
    assertEquals(1, connector.getStereotype().size());
    assertFalse(connector.getStereotype().get("conStereo").isPresent());
  }
  
  @Test
  public void testComponentEntryIsDelayed() {
    Scope symTab = tool.createSymbolTable("src/test/resources/arc/symtab");
    ComponentSymbol parent = symTab.<ComponentSymbol> resolve(
        "timing.Timing", ComponentSymbol.KIND).orElse(null);
    assertNotNull(parent);
    
    assertEquals(0, Log.getErrorCount());
    assertEquals(0, Log.getFindings().stream().filter(f -> f.isWarning()).count());
    
    ComponentSymbol child = parent.getInnerComponent("TimedInner").orElse(null);
    assertNotNull(child);
    assertFalse(child.hasDelay());
    
    child = parent.getInnerComponent("TimedDelayingInner").orElse(null);
    assertNotNull(child);
    assertTrue(child.hasDelay());
    
    child = parent.getInnerComponent("TimeSyncInner").orElse(null);
    assertNotNull(child);
    assertFalse(child.hasDelay());
    
    child = parent.getInnerComponent("TimeCausalSyncInner").orElse(null);
    assertNotNull(child);
    assertTrue(child.hasDelay());
    
    child = parent.getInnerComponent("UntimedInner").orElse(null);
    assertNotNull(child);
    assertFalse(child.hasDelay());
  }
  
  @Ignore("TODO ocl invariants?")
  @Test
  public void testAdaptOCLFieldToPort() {
    Scope symTab = tool.createSymbolTable("src/test/resources/arc/symtab");
    ComponentSymbol parent = symTab.<ComponentSymbol> resolve(
        "ocl.OCLFieldToPort", ComponentSymbol.KIND).orElse(null);
    assertNotNull(parent);
    
    assertEquals(0, Log.getErrorCount());
    assertEquals(0, Log.getFindings().stream().filter(f -> f.isWarning()).count());
  }
  
  @Ignore("TODO ocl invariants?")
  @Test
  public void testAdaptOCLFieldToArcdField() {
    Scope symTab = tool.createSymbolTable("src/test/resources/arc/symtab");
    ComponentSymbol parent = symTab.<ComponentSymbol> resolve(
        "ocl.OCLFieldToArcField", ComponentSymbol.KIND).orElse(null);
    assertNotNull(parent);
    
    assertEquals(0, Log.getErrorCount());
    assertEquals(0, Log.getFindings().stream().filter(f -> f.isWarning()).count());

  }
}
