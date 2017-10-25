/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ConnectorSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.ValueSymbol;

/**
 * @author Robert Heim
 */
public class SymtabArcdTest extends AbstractSymboltableTest {

  private final String MODEL_PATH = "src/test/resources/symboltable";

  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testCompWithGenericPorts() {
    Scope symTab = createSymTab(MODEL_PATH + "/genericPorts");
    ComponentSymbol comp = symTab.<ComponentSymbol>resolve(
        "a.CompWithGenericPorts", ComponentSymbol.KIND).orElse(null);
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
  public void testCompWithGenericsAndInnerGenericComponent() {
    Scope symTab = createSymTab(MODEL_PATH + "/genericPorts");
    ComponentSymbol comp = symTab.<ComponentSymbol>resolve(
        "a.GenericCompWithInnerGenericComp", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
  }

  @Test
  public void testCompWithCfgsAndInnerCfgComponent() {
    Scope symTab = createSymTab(MODEL_PATH + "/configs");
    ComponentSymbol comp = symTab.<ComponentSymbol>resolve(
        "a.ConfigurableComponentWithInnerCfgComp", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
  }

  @Test
  public void testReferencingCompsWithCfg() {
    Scope symTab = createSymTab(MODEL_PATH + "/configs");
    ComponentSymbol comp = symTab.<ComponentSymbol>resolve(
        "a.ReferencingCompsWithCfg", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);

    ComponentInstanceSymbol compWithArgsRef = comp.getSubComponent("cfg").orElse(null);
    assertNotNull(compWithArgsRef);

    assertEquals(3, compWithArgsRef.getConfigArguments().size());
    ValueSymbol<?> arg1 = compWithArgsRef.getConfigArguments().get(0);
    assertEquals("1", arg1.getValue());
    // TODO proper setting of Kind? currently everything is an expression as we extend JavaDSL
    // instead of CommonValues
    // assertEquals(ValueSymbol.Kind.Value, arg1.getKind());

    ValueSymbol<?> arg2 = compWithArgsRef.getConfigArguments().get(1);
    assertEquals("\"Hallo\"", arg2.getValue());
    // TODO proper setting of Kind? currently everything is an expression as we extend JavaDSL
    // instead of CommonValues
    // assertEquals(ValueEntry.Kind.Value, arg2.getKind());
    
    String spacelessArg3 = "new Integer[]{1, 2, 3}".replace(" ", "");
    ValueSymbol<?> arg3 = compWithArgsRef.getConfigArguments().get(2);
    assertEquals(spacelessArg3, arg3.getValue().replace(" ", ""));
    // TODO proper setting of Kind? currently everything is an expression as we extend JavaDSL
    // instead of CommonValues
//     assertEquals(ValueEntry.Kind.ConstructorCall, arg3.getKind());

    ComponentSymbol compWithArgsType = compWithArgsRef.getComponentType().getReferencedComponent()
        .orElse(null);
    assertNotNull(compWithArgsType);
    // check that configuration parameters reference the correct paramter types/names in
    // the referenced component type
    assertEquals(3, compWithArgsType.getConfigParameters().size());
    JFieldSymbol cfgField1 = compWithArgsType.getConfigParameters().get(0);
    assertEquals("a", cfgField1.getName());
    assertEquals("int", cfgField1.getType().getName());
    assertEquals(0, cfgField1.getType().getReferencedSymbol().getFormalTypeParameters().size());
    assertEquals(0, cfgField1.getType().getDimension());

    JFieldSymbol cfgField2 = compWithArgsType.getConfigParameters().get(1);
    assertEquals("foo", cfgField2.getName());
    assertEquals("String", cfgField2.getType().getName());
    assertEquals("java.lang.String", cfgField2.getType().getReferencedSymbol().getFullName());
    assertEquals(0, cfgField2.getType().getReferencedSymbol().getFormalTypeParameters().size());
    assertEquals(0, cfgField2.getType().getDimension());

    JFieldSymbol cfgField3 = compWithArgsType.getConfigParameters().get(2);
    assertEquals("iArray", cfgField3.getName());
    assertEquals("int", cfgField3.getType().getName());
    assertEquals(0, cfgField3.getType().getReferencedSymbol().getFormalTypeParameters().size());
    assertEquals(1, cfgField3.getType().getDimension());
  }

  @Test
  public void testReferencingCompsWithExpression() {
    Scope symTab = createSymTab(MODEL_PATH + "/configs");
    ComponentSymbol comp = symTab.<ComponentSymbol>resolve(
        "a.ReferencingCompsWithExpression", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    ComponentInstanceSymbol compWithArgsRef = comp.getSubComponent("cfg").orElse(null);
    assertNotNull(compWithArgsRef);

    assertEquals(2, compWithArgsRef.getConfigArguments().size());
    ValueSymbol<?> arg1 = compWithArgsRef.getConfigArguments().get(0);
    // expressions
    assertEquals("2*1*5+1", arg1.getValue());
    // internal representation of expressions
    assertEquals(ValueSymbol.Kind.Expression, arg1.getKind());
//     assertEquals(4, arg1.getConstructorArguments().size());
    // assertEquals("2", arg1.getConstructorArguments().get(0).getValue());
//     assertEquals(ValueSymbol.Kind.Value, arg1.getConstructorArguments().get(0).getKind());
    // assertEquals("1", arg1.getConstructorArguments().get(1).getValue());
    // assertEquals(ValueSymbol.Kind.Value, arg1.getConstructorArguments().get(1).getKind());
    // assertEquals("5", arg1.getConstructorArguments().get(2).getValue());
    // assertEquals(ValueSymbol.Kind.Value, arg1.getConstructorArguments().get(2).getKind());
    // assertEquals("1", arg1.getConstructorArguments().get(3).getValue());
    // assertEquals(ValueSymbol.Kind.Value, arg1.getConstructorArguments().get(3).getKind());

    ValueSymbol<?> arg2 = compWithArgsRef.getConfigArguments().get(1);
    assertEquals("new Integer(2)*5", arg2.getValue());
    assertEquals(ValueSymbol.Kind.Expression, arg2.getKind());
    // assertEquals(2, arg2.getConstructorArguments().size());
    // assertEquals("new Integer(2)", arg2.getConstructorArguments().get(0).getValue());
    // assertEquals(ValueEntry.Kind.ConstructorCall,
    // arg2.getConstructorArguments().get(0).getKind());
    // assertEquals("5", arg2.getConstructorArguments().get(1).getValue());
    // assertEquals(ValueEntry.Kind.Value, arg2.getConstructorArguments().get(1).getKind());
  }

  //
  @Test
  public void testImportedReferences() {
    Scope symTab = createSymTab(MODEL_PATH + "/importedReferences");
    ComponentSymbol comp = symTab.<ComponentSymbol>resolve(
        "a.SimpleComponent", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);

    assertEquals("6 instances (3 named and 3 auto-instances) should be present!", 6,
        comp.getSubComponents().size());

    ComponentInstanceSymbol ref = comp.getSubComponent("src").orElse(null);
    assertNotNull(ref);

    ComponentInstanceSymbol b2 = comp.getSubComponent("b2").orElse(null);
    assertNotNull(b2);

    ComponentInstanceSymbol myC = comp.getSubComponent("myC").orElse(null);
    assertNotNull(myC);
    assertEquals(1, myC.getComponentType().getActualTypeArguments().size());
    assertEquals("String",
    myC.getComponentType().getActualTypeArguments().get(0).getType().getName());

    ComponentSymbol myCType = myC.getComponentType().getReferencedComponent().orElse(null);
    assertNotNull(myCType);
    assertEquals("C", myCType.getName());
    assertEquals("c.C", myCType.getFullName());

    ComponentInstanceSymbol c2Auto = comp.getSubComponent("c2").orElse(null);
    assertNotNull(c2Auto);

    ComponentInstanceSymbol qfc1Auto = comp.getSubComponent("qFComponent1").orElse(null);
    assertNotNull(qfc1Auto);
    ComponentSymbol qfc1Type = qfc1Auto.getComponentType().getReferencedComponent().orElse(null);
    assertNotNull("Full-qualified usage of a component instead of importing it must be possible.",
        qfc1Type);
    assertEquals("QFComponent1", qfc1Type.getName());
    assertEquals("d.QFComponent1", qfc1Type.getFullName());

    ComponentInstanceSymbol qfc2 = comp.getSubComponent("qfc2").orElse(null);
    assertNotNull(qfc2);
    ComponentSymbol qfc2Type = qfc2.getComponentType().getReferencedComponent().orElse(null);
    assertNotNull("Full-qualified usage of a component instead of importing it must be possible.",
        qfc2Type);
    assertEquals("QFComponent2", qfc2Type.getName());
    assertEquals("d.QFComponent2", qfc2Type.getFullName());
  }

  @Test
  public void testSuperComponents() {
    Scope symTab = createSymTab(MODEL_PATH + "/superComponents");
    ComponentSymbol subB = symTab.<ComponentSymbol>resolve(
        "a.SubB", ComponentSymbol.KIND).orElse(null);
    assertNotNull(subB);

    assertNotNull(subB);
    assertTrue(subB.getIncomingPort("anotherIn").isPresent());
    assertTrue(subB.getOutgoingPort("anotherOut").isPresent());
    assertTrue(subB.getOutgoingPort("anotherOut2").isPresent());

    // inherited
    assertNotNull(subB.getSpannedScope().resolve("myInput", PortSymbol.KIND));
    assertEquals(2, subB.getAllIncomingPorts().size());

    assertNotNull(subB.getSpannedScope().resolve("myOutput", PortSymbol.KIND));
    assertNotNull(subB.getSpannedScope().resolve("myOutput519", PortSymbol.KIND));
    assertEquals(4, subB.getAllOutgoingPorts().size());
  }

  @Test
  public void testInnerComponents() {
    Scope symTab = createSymTab(MODEL_PATH + "/innerComps");
    ComponentSymbol comp = symTab.<ComponentSymbol>resolve(
        "a.ComponentWithInnerComponent", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    assertEquals("1 auto-instance and 1 named subcomponent", 2, comp
        .getSubComponents().size());
    assertEquals(1, comp.getInnerComponents().size());

    // ports
    assertEquals(2, comp.getPorts().size());
    assertEquals(1, comp.getIncomingPorts().size());
    assertEquals(1, comp.getOutgoingPorts().size());
    PortSymbol inPort = comp.getIncomingPort("strIn").orElse(null);
    assertNotNull(inPort);
    assertEquals("strIn", inPort.getName());
    assertEquals("a.ComponentWithInnerComponent.strIn", inPort.getFullName());

    // connectors
    assertEquals(2, comp.getConnectors().size());
    ConnectorSymbol connector = comp.getConnector("innerComponent.strIn").orElse(null);
    assertEquals("innerComponent.strIn", connector.getName());
    assertEquals("a.ComponentWithInnerComponent.innerComponent.strIn", connector.getFullName());

    // inner
    ComponentSymbol inner = comp.getInnerComponent("InnerComponent").orElse(null);
    assertNotNull(inner);
    assertEquals("InnerComponent", inner.getName());
    assertEquals("a.ComponentWithInnerComponent.InnerComponent", inner.getFullName());
    assertEquals("1 auto-instance and 1 named subcomponent", 2, inner.getSubComponents().size());

    // ports
    assertEquals(2, inner.getPorts().size());
    assertEquals(1, inner.getIncomingPorts().size());
    assertEquals(1, inner.getOutgoingPorts().size());
    inPort = inner.getIncomingPort("strIn").orElse(null);
    assertNotNull(inPort);
    assertEquals("strIn", inPort.getName());
    assertEquals("a.ComponentWithInnerComponent.InnerComponent.strIn", inPort.getFullName());

    // connectors
    assertEquals(2, inner.getConnectors().size());

    // inner inner
    ComponentSymbol innerInner = inner.getInnerComponent("InnerInnerComponent").orElse(null);
    assertNotNull(innerInner);
    assertEquals("InnerInnerComponent", innerInner.getName());
    assertEquals("a.ComponentWithInnerComponent.InnerComponent.InnerInnerComponent",
        innerInner.getFullName());
    assertEquals("0 auto-instances with same name and 1 named subcomponent", 1,
        innerInner.getSubComponents().size());

    // ports
    assertEquals(2, innerInner.getPorts().size());
    assertEquals(1, innerInner.getIncomingPorts().size());
    assertEquals(1, innerInner.getOutgoingPorts().size());
    inPort = innerInner.getIncomingPort("strIn").orElse(null);
    assertNotNull(inPort);
    assertEquals("strIn", inPort.getName());
    assertEquals("a.ComponentWithInnerComponent.InnerComponent.InnerInnerComponent.strIn",
        inPort.getFullName());
    // connectors
    assertEquals(2, inner.getConnectors().size());

  }

  @Test
  public void testInnerComponents2() {
    Scope symTab = createSymTab(MODEL_PATH + "/innerComps");
    ComponentSymbol comp = symTab.<ComponentSymbol>resolve(
        "b.InnerComponents", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);

    assertEquals("3 named subcomponents and 1 auto-instance", 4, comp
        .getSubComponents().size());
    assertEquals(1, comp.getInnerComponents().size());

    // usage of external component type as sub component in same package
    ComponentInstanceSymbol instance = comp.getSubComponent("ref").orElse(null);
    assertNotNull("usage of external component type as sub component in same package", instance);
    assertEquals("ref", instance.getName());
    assertEquals("b.InnerComponents.ref", instance.getFullName());
    ComponentSymbol refType = instance.getComponentType().getReferencedComponent().orElse(null);
    assertNotNull(refType);
    assertEquals("SimpleComponentWithAutomaton", refType.getName());
    assertEquals("b.SimpleComponentWithAutomaton", refType.getFullName());

    // inner
    ComponentSymbol inner = comp.getInnerComponent("Inner").orElse(null);
    assertNotNull(inner);
    assertEquals("Inner", inner.getName());
    assertEquals("b.InnerComponents.Inner", inner.getFullName());
    assertEquals("1 auto-instance and 2 named subcomponents", 3, inner.getSubComponents().size());

    // usage of external component type as sub component in same package in inner component
    instance = inner.getSubComponent("ref").orElse(null);
    assertNotNull(
        "usage of external component type as sub component in same package in inner component",
        instance);
    assertEquals("ref", instance.getName());
    assertEquals("b.InnerComponents.Inner.ref", instance.getFullName());
    refType = instance.getComponentType().getReferencedComponent().orElse(null);
    assertNotNull(refType);
    assertEquals("SimpleComponentWithAutomaton", refType.getName());
    assertEquals("b.SimpleComponentWithAutomaton", refType.getFullName());

    // inner inner
    ComponentSymbol innerInner = inner.getInnerComponent("InnerInner").orElse(null);
    assertNotNull(innerInner);
    assertEquals("InnerInner", innerInner.getName());
    assertEquals("b.InnerComponents.Inner.InnerInner",
        innerInner.getFullName());
    assertEquals("1 named subcomponent and 0 auto-instances", 1,
        innerInner.getSubComponents().size());

    // usage of external component type as sub component in same package in inner inner component
    instance = innerInner.getSubComponent("ref").orElse(null);
    assertNotNull(
        "usage of external component type as sub component in same package in inner inner component",
        instance);
    assertEquals("ref", instance.getName());
    assertEquals("b.InnerComponents.Inner.InnerInner.ref", instance.getFullName());
    refType = instance.getComponentType().getReferencedComponent().orElse(null);
    assertNotNull(refType);
    assertEquals("SimpleComponentWithAutomaton", refType.getName());
    assertEquals("b.SimpleComponentWithAutomaton", refType.getFullName());

  }
  //
  // @Test
  // public void testNamedInnerComponent() {
  // ArcdTestTool tool = createTestToolWithoutJava(new String[] {
  // INPUT_TEST_FOLDER + "/arcd/symtab/innerComps/a/ComponentWithNamedInnerComponent.arcd" },
  // INPUT_TEST_FOLDER + "/arcd/symtab/innerComps");
  // assertTrue(tool.run());
  // assertEquals(0, handler.getErrors().size());
  // assertEquals(0, handler.getWarnings().size());
  //
  // ArchitectureDiagramRoot root = (ArchitectureDiagramRoot) initSymtabForRoot(tool,
  // "a.ComponentWithNamedInnerComponent");
  // try {
  // NameSpace ns = getNameSpaceFor(root.getAst().getType());
  // ComponentReferenceEntry ref = (ComponentReferenceEntry) resolver.resolve("instance",
  // ComponentReferenceEntry.KIND, ns);
  // assertTrue(ref != null);
  // assertEquals("instance", ref.getName());
  // assertEquals("a.ComponentWithNamedInnerComponent.NamedInnerComponent",
  // ref.getComponentType().getName());
  // }
  // catch (AmbigousException e) {
  // fail(e.getMessage());
  // }
  //
  //
  // }
  //
  // @Test
  // public void testLoadManually() {
  // ArcdTestTool tool = createTestToolWithoutJava(new String[] {INPUT_TEST_FOLDER +
  // "/arcd/symtab/innerComps/a/ComponentWithInnerComponent.arcd" },
  // INPUT_TEST_FOLDER + "/arcd/symtab/innerComps");
  // assertTrue(tool.run());
  // assertEquals(0, handler.getErrors().size());
  // assertEquals(0, handler.getWarnings().size());
  //
  // initSymtabForRoot(tool, "a.ComponentWithInnerComponent");
  //
//   Set<ISTEntry> manuallyLoaded = InterfaceLoader.loadExported(modelLoader,
  // "a.ComponentWithInnerComponent",
  // ArcdConstants.ST_KIND_PROTECTED, deserializers, ComponentEntry.KIND);
  // assertEquals(1, manuallyLoaded.size());
  // ComponentEntry protectedEntry = (ComponentEntry) manuallyLoaded.iterator().next();
  //
  // // check, if protected information is available
  // assertEquals(2, protectedEntry.getSubComponents().size());
  // assertEquals(1, protectedEntry.getInnerComponents().size());
  // assertEquals("a.ComponentWithInnerComponent.InnerComponent",
  // protectedEntry.getInnerComponents().get(0).getName());
  // assertEquals(2, protectedEntry.getConnectors().size());
  //
  // }
}
