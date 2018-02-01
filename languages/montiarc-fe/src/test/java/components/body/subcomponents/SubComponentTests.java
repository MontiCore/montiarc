/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package components.body.subcomponents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JFieldSymbol;
import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ComponentSymbolReference;
import montiarc._symboltable.ConnectorSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.ValueSymbol;
import montiarc.cocos.ComponentInstanceNamesAreUnique;
import montiarc.cocos.ComponentWithTypeParametersHasInstance;
import montiarc.cocos.MontiArcCoCos;
import montiarc.cocos.SubcomponentParametersCorrectlyAssigned;

/**
 * This class checks all context conditions related to the definition of
 * subcomponents
 *
 * @author Andreas Wortmann
 */
public class SubComponentTests extends AbstractCoCoTest {

  private static final String MP = "";

  private static final String PACKAGE = "components.body.subcomponents";

  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testSubcomponentParametersOfWrongType() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "SubcomponentParametersOfWrongType");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new SubcomponentParametersCorrectlyAssigned()),
        node, new ExpectedErrorInfo(1, "xMA064"));
  }

  /**
   * Test for ticket #21.
   */
  @Test
  public void testGenericCompWithInnerGenericComp() {
    ComponentSymbol comp = this.loadComponent(MP, PACKAGE, "GenericCompWithInnerGenericComp");
    assertNotNull(comp);
  }

  @Test
  public void testConfigurableComponentWithInnerCfgComp() {
    ComponentSymbol comp = this.loadComponent(MP, PACKAGE, "ConfigurableComponentWithInnerCfgComp");
    assertNotNull(comp);
  }

  @Test
  public void testComponentInstanceNamesAmbiguous() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "ComponentInstanceNamesAmbiguous");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ComponentInstanceNamesAreUnique()),
        node,
        new ExpectedErrorInfo(2, "xMA061"));
  }

  @Test
  public void testComponentWithTypeParametersLacksInstance() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "ComponentWithTypeParametersLacksInstance");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ComponentWithTypeParametersHasInstance()),
        node, new ExpectedErrorInfo(1, "xMA009"));
  }

  @Test
  public void testReferencedSubComponentsExists() {
    checkValid(MP, PACKAGE + "." + "ReferencedSubComponentsExists");
  }

  @Test
  /**
   * Symbol table already throws an exception, therefore the coco is never
   * checked. A fix" + would be to stop the symbol table from throwing the
   * exception, in order to have a" + better error message. For now we just
   * check that we give out the rudimentary error xA1038, which tells us that
   * the non-existent component could not be loaded, but doesn't provide more
   * detail.
   */
  public void testInexistingSubComponent() {
    Log.getFindings().clear();
    getAstNode(MP, PACKAGE + "." + "InexistingSubComponent");
    ExpectedErrorInfo.setERROR_CODE_PATTERN(Pattern.compile("x[0-9A-F]{5}"));
    ExpectedErrorInfo errors = new ExpectedErrorInfo(2, "xA1038");
    errors.checkExpectedPresent(Log.getFindings(), "No errors found!");
    ExpectedErrorInfo.reset();
  }

  @Test
  public void testWrongSubComponentArgument() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "WrongSubComponentArgument");
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(1, "xMA064"));
  }

  @Test
  public void testComponentWithTypeParametersHasInstance() {
    checkValid(MP, PACKAGE + "." + "ComponentWithTypeParametersHasInstance");
  }

  @Test
  public void testInvalidNestedComponentWithTypeParameterLacksInstance() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "NestedComponentWithTypeParameterLacksInstance");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ComponentWithTypeParametersHasInstance()),
        node, new ExpectedErrorInfo(1, "xMA009"));
  }

  @Test
  public void testComponentWithNamedInnerComponent() {
    String unqualifiedComponentName = "ComponentWithNamedInnerComponent";
    ComponentSymbol comp = this.loadComponent(MP, PACKAGE, unqualifiedComponentName);

    assertFalse(comp.isInnerComponent());
    assertEquals(0, comp.getConfigParameters().size());
    assertEquals(1, comp.getAllIncomingPorts().size());
    assertEquals(1, comp.getAllOutgoingPorts().size());

    // ensures that inner component definitions can be loaded with the model loader, so we can
    // resolve references to them of sub components, see ModelNameCalculator.
    assertEquals(1, comp.getSubComponents().size());
    ComponentInstanceSymbol subComp = comp.getSubComponents().iterator().next();
    assertEquals(PACKAGE + "." + "ComponentWithNamedInnerComponent.instance", subComp.getFullName());
    assertEquals("instance", subComp.getName());

    assertEquals(1, comp.getInnerComponents().size());

    ComponentSymbol inner = comp.getInnerComponent("NamedInnerComponent").orElse(null);
    assertNotNull(inner);
    ComponentSymbolReference compRefToInner = subComp.getComponentType();
    assertTrue(compRefToInner.getReferencedComponent().isPresent());
    assertTrue(inner == compRefToInner.getReferencedComponent().get());
    assertEquals("NamedInnerComponent", inner.getName());
    assertEquals("NamedInnerComponent", compRefToInner.getName());
    assertEquals(PACKAGE + "." + "ComponentWithNamedInnerComponent.NamedInnerComponent", inner.getFullName());
    assertEquals(PACKAGE + "." + "ComponentWithNamedInnerComponent.NamedInnerComponent",
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

    Scope symTab = new MontiArcTool().initSymbolTable("src/test/resources/");
    ComponentSymbol innerComp = symTab.<ComponentSymbol> resolve(
        PACKAGE + "." + "ComponentWithNamedInnerComponent.NamedInnerComponent", ComponentSymbol.KIND)
        .orElse(null);
    assertNotNull(innerComp);
  }


  @Test
  public void testReferencingCompsWithCfg() {
    ComponentSymbol comp = new MontiArcTool().loadComponentSymbolWithCocos(
        PACKAGE + "." + "ReferencingCompsWithCfg",
        Paths.get("src/test/resources/").toFile(), Paths.get("src/main/resources/defaultTypes").toFile()).orElse(null);
    assertNotNull(comp);


    ComponentInstanceSymbol compWithArgsRef = comp.getSubComponent("cfg").orElse(null);
    assertNotNull(compWithArgsRef);
    JavaDSLPrettyPrinter prettyPrinter = new JavaDSLPrettyPrinter(new IndentPrinter());

    assertEquals(3, compWithArgsRef.getConfigArguments().size());
    ValueSymbol<?> arg1 = compWithArgsRef.getConfigArguments().get(0);
    assertEquals("1", prettyPrinter.prettyprint(arg1.getValue()));
    // TODO proper setting of Kind? currently everything is an expression as we extend JavaDSL
    // instead of CommonValues
    // assertEquals(ValueSymbol.Kind.Value, arg1.getKind());

    ValueSymbol<?> arg2 = compWithArgsRef.getConfigArguments().get(1);
    assertEquals("\"Hallo\"", prettyPrinter.prettyprint(arg2.getValue()));
    // TODO proper setting of Kind? currently everything is an expression as we extend JavaDSL
    // instead of CommonValues
    // assertEquals(ValueEntry.Kind.Value, arg2.getKind());

//    String spacelessArg3 = "new Integer[]{1, 2, 3}".replace(" ", "");
//    ValueSymbol<?> arg3 = compWithArgsRef.getConfigArguments().get(2);
//    assertEquals(spacelessArg3, arg3.getValue().replace(" ", ""));
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
    ComponentSymbol comp = new MontiArcTool().loadComponentSymbolWithoutCocos(
        PACKAGE + "." + "ReferencingCompsWithExpression", Paths.get("src/test/resources/").toFile()).orElse(null);
    assertNotNull(comp);
    ComponentInstanceSymbol compWithArgsRef = comp.getSubComponent("cfg").orElse(null);
    assertNotNull(compWithArgsRef);

    assertEquals(2, compWithArgsRef.getConfigArguments().size());
    ValueSymbol<?> arg1 = compWithArgsRef.getConfigArguments().get(0);
    // expressions
    JavaDSLPrettyPrinter prettyPrinter = new JavaDSLPrettyPrinter(new IndentPrinter());
    assertEquals("2*1*5+1", prettyPrinter.prettyprint(arg1.getValue()));
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
    assertEquals("new Integer(2)*5", prettyPrinter.prettyprint(arg2.getValue()));
    assertEquals(ValueSymbol.Kind.Expression, arg2.getKind());
    // assertEquals(2, arg2.getConstructorArguments().size());
    // assertEquals("new Integer(2)", arg2.getConstructorArguments().get(0).getValue());
    // assertEquals(ValueEntry.Kind.ConstructorCall,
    // arg2.getConstructorArguments().get(0).getKind());
    // assertEquals("5", arg2.getConstructorArguments().get(1).getValue());
    // assertEquals(ValueEntry.Kind.Value, arg2.getConstructorArguments().get(1).getKind());
  }

  @Test
  public void testImportedReferences() {
    ComponentSymbol comp = new MontiArcTool().loadComponentSymbolWithoutCocos(
        PACKAGE + "." + "ComplexComponent", Paths.get("src/test/resources/").toFile()).orElse(null);
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
    assertEquals("Sub2", myCType.getName());
    assertEquals("components.body.subcomponents.Sub2", myCType.getFullName());

    ComponentInstanceSymbol c2Auto = comp.getSubComponent("c2").orElse(null);
    assertNotNull(c2Auto);

    ComponentInstanceSymbol qfc1Auto = comp.getSubComponent("qFComponent1").orElse(null);
    assertNotNull(qfc1Auto);
    ComponentSymbol qfc1Type = qfc1Auto.getComponentType().getReferencedComponent().orElse(null);
    assertNotNull("Full-qualified usage of a component instead of importing it must be possible.",
        qfc1Type);
    assertEquals("Sub4", qfc1Type.getName());
    assertEquals(PACKAGE + "._subcomponents." + "Sub4", qfc1Type.getFullName());

    ComponentInstanceSymbol qfc2 = comp.getSubComponent("qfc2").orElse(null);
    assertNotNull(qfc2);
    ComponentSymbol qfc2Type = qfc2.getComponentType().getReferencedComponent().orElse(null);
    assertNotNull("Full-qualified usage of a component instead of importing it must be possible.",
        qfc2Type);
    assertEquals("Sub5", qfc2Type.getName());
    assertEquals(PACKAGE + "._subcomponents." + "Sub5", qfc2Type.getFullName());
  }

  @Test
  public void testInnerComponents() {
    ComponentSymbol comp = new MontiArcTool().loadComponentSymbolWithoutCocos(
        PACKAGE + "." + "ComponentWithInnerComponent", Paths.get("src/test/resources/").toFile()).orElse(null);
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
    assertEquals(PACKAGE + "." + "ComponentWithInnerComponent.strIn", inPort.getFullName());

    // connectors
    assertEquals(2, comp.getConnectors().size());
    ConnectorSymbol connector = comp.getConnector("innerComponent.strIn").orElse(null);
    assertEquals("innerComponent.strIn", connector.getName());
    assertEquals(PACKAGE + "." + "ComponentWithInnerComponent.innerComponent.strIn", connector.getFullName());

    // inner
    ComponentSymbol inner = comp.getInnerComponent("InnerComponent").orElse(null);
    assertNotNull(inner);
    assertEquals("InnerComponent", inner.getName());
    assertEquals(PACKAGE + "." + "ComponentWithInnerComponent.InnerComponent", inner.getFullName());
    assertEquals("1 auto-instance and 1 named subcomponent", 2, inner.getSubComponents().size());

    // ports
    assertEquals(2, inner.getPorts().size());
    assertEquals(1, inner.getIncomingPorts().size());
    assertEquals(1, inner.getOutgoingPorts().size());
    inPort = inner.getIncomingPort("strIn").orElse(null);
    assertNotNull(inPort);
    assertEquals("strIn", inPort.getName());
    assertEquals(PACKAGE + "." + "ComponentWithInnerComponent.InnerComponent.strIn", inPort.getFullName());

    // connectors
    assertEquals(2, inner.getConnectors().size());

    // inner inner
    ComponentSymbol innerInner = inner.getInnerComponent("InnerInnerComponent").orElse(null);
    assertNotNull(innerInner);
    assertEquals("InnerInnerComponent", innerInner.getName());
    assertEquals(PACKAGE + "." + "ComponentWithInnerComponent.InnerComponent.InnerInnerComponent",
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
    assertEquals(PACKAGE + "." + "ComponentWithInnerComponent.InnerComponent.InnerInnerComponent.strIn",
        inPort.getFullName());
    // connectors
    assertEquals(2, inner.getConnectors().size());

  }

  @Test
  public void testInnerComponents2() {
    ComponentSymbol comp = new MontiArcTool().loadComponentSymbolWithoutCocos(
        PACKAGE + "." + "InnerComponents", Paths.get("src/test/resources/").toFile()).orElse(null);
    assertNotNull(comp);

    assertEquals("3 named subcomponents and 1 auto-instance", 4, comp
        .getSubComponents().size());
    assertEquals(1, comp.getInnerComponents().size());

    // usage of external component type as sub component in same package
    ComponentInstanceSymbol instance = comp.getSubComponent("ref").orElse(null);
    assertNotNull("usage of external component type as sub component in same package", instance);
    assertEquals("ref", instance.getName());
    assertEquals(PACKAGE + "." + "InnerComponents.ref", instance.getFullName());
    ComponentSymbol refType = instance.getComponentType().getReferencedComponent().orElse(null);
    assertNotNull(refType);
    assertEquals("SimpleComponentWithAutomaton", refType.getName());
    assertEquals(PACKAGE + "." + "SimpleComponentWithAutomaton", refType.getFullName());

    // inner
    ComponentSymbol inner = comp.getInnerComponent("Inner").orElse(null);
    assertNotNull(inner);
    assertEquals("Inner", inner.getName());
    assertEquals(PACKAGE + "." + "InnerComponents.Inner", inner.getFullName());
    assertEquals("1 auto-instance and 2 named subcomponents", 3, inner.getSubComponents().size());

    // usage of external component type as sub component in same package in inner component
    instance = inner.getSubComponent("ref").orElse(null);
    assertNotNull(
        "usage of external component type as sub component in same package in inner component",
        instance);
    assertEquals("ref", instance.getName());
    assertEquals(PACKAGE + "." + "InnerComponents.Inner.ref", instance.getFullName());
    refType = instance.getComponentType().getReferencedComponent().orElse(null);
    assertNotNull(refType);
    assertEquals("SimpleComponentWithAutomaton", refType.getName());
    assertEquals(PACKAGE + "." + "SimpleComponentWithAutomaton", refType.getFullName());

    // inner inner
    ComponentSymbol innerInner = inner.getInnerComponent("InnerInner").orElse(null);
    assertNotNull(innerInner);
    assertEquals("InnerInner", innerInner.getName());
    assertEquals(PACKAGE + "." + "InnerComponents.Inner.InnerInner",
        innerInner.getFullName());
    assertEquals("1 named subcomponent and 0 auto-instances", 1,
        innerInner.getSubComponents().size());

    // usage of external component type as sub component in same package in inner inner component
    instance = innerInner.getSubComponent("ref").orElse(null);
    assertNotNull(
        "usage of external component type as sub component in same package in inner inner component",
        instance);
    assertEquals("ref", instance.getName());
    assertEquals(PACKAGE + "." + "InnerComponents.Inner.InnerInner.ref", instance.getFullName());
    refType = instance.getComponentType().getReferencedComponent().orElse(null);
    assertNotNull(refType);
    assertEquals("SimpleComponentWithAutomaton", refType.getName());
    assertEquals(PACKAGE + "." + "SimpleComponentWithAutomaton", refType.getFullName());

  }
}
