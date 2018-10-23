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
import static org.junit.Assert.fail;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import montiarc.cocos.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.cocos.helper.Assert;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JFieldSymbol;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ComponentSymbolReference;
import montiarc._symboltable.ConnectorSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc.helper.SymbolPrinter;

/**
 * This class checks all context conditions related to the definition of
 * subcomponents
 *
 * @author Andreas Wortmann
 */
public class SubComponentTest extends AbstractCoCoTest {
  
  private static final String PACKAGE = "components.body.subcomponents";
  
  public class Student extends Person{
    
  }
  
  public class Person {
    
  }
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testSubcomponentWithJavaCfgArgs() {
    checkValid(PACKAGE + "." + "SubcomponentsWithJavaCfgArg");
  }
  
  @Test
  public void testSubcomponentParametersOfWrongTypeWithCD() {
    final String modelName = PACKAGE + "." + "SubcomponentParametersOfWrongType";
    ASTMontiArcNode node = loadComponentAST(modelName);
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new SubcomponentParametersCorrectlyAssigned()),
        node, new ExpectedErrorInfo(3, "xMA064"));
  }
  
  @Test
  public void testInnerComponentExtendsDefiningComponent() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "InnerComponentExtendsDefiningComponent");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new InnerComponentNotExtendsDefiningComponent()),
        node, new ExpectedErrorInfo(1, "xMA083"));
  }
  
  /**
   * Test for ticket #21.
   */
  @Test
  public void testGenericCompWithInnerGenericComp() {
    ComponentSymbol comp = this.loadComponentSymbol(PACKAGE, "GenericCompWithInnerGenericComp");
    assertNotNull(comp);
  }
  
  @Test
  public void testSubcomponentReferenceCycles() {
    String qualifiedModelName = PACKAGE + "." + "SubcomponentReferenceCycleA";
    ExpectedErrorInfo errors
        = new ExpectedErrorInfo(1, "xMA086");
    final MontiArcCoCoChecker cocos
        = new MontiArcCoCoChecker().addCoCo(new SubcomponentReferenceCycle());
    checkInvalid(cocos, loadComponentAST(qualifiedModelName), errors);

    errors = new ExpectedErrorInfo(1, "xMA086");
    qualifiedModelName = PACKAGE + "." + "SubcomponentReferenceCycleB";
    checkInvalid(cocos, loadComponentAST(qualifiedModelName), errors);
  }
  
  @Test
  public void testConfigurableComponentWithInnerCfgComp() {
    final String name = PACKAGE + "." + "ConfigurableComponentWithInnerCfgComp";
    checkValid(name);
  }
  
  @Test
  public void testComponentInstanceNamesAmbiguous() {
    final String modelName = PACKAGE + "." + "ComponentInstanceNamesAmbiguous";
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(4, "xMA061");
    final MontiArcCoCoChecker cocos
        = new MontiArcCoCoChecker().addCoCo(new IdentifiersAreUnique());
    checkInvalid(cocos, loadComponentAST(modelName), errors);
  }
  
  @Test
  public void testConfigurableArchitectureComponent() {
    final String modelName = PACKAGE + "." + "ConfigurableArchitectureComponent";
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(3, "xMA061");
    final MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new IdentifiersAreUnique());
    checkInvalid(cocos, loadComponentAST(modelName), errors);
  }
  
  @Test
  /*
   * TODO Add Error codes for CV7
   */
  public void testAmbiguousImplicitAndExplicitSubcomponentNames() {
    final String modelName = PACKAGE + "." + "AmbiguousImplicitAndExplicitSubcomponentNames";
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(6, "xMA061");
    final MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new IdentifiersAreUnique());
    checkInvalid(cocos, loadComponentAST(modelName), errors);
  }
  
  @Test
  public void testComponentWithTypeParametersLacksInstance() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "ComponentWithTypeParametersLacksInstance");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ComponentWithTypeParametersHasInstance()),
        node, new ExpectedErrorInfo(1, "xMA009"));
  }
  
  @Test
  public void testInnerViolatesComponentNaming() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "InnerViolatesComponentNaming");
    checkInvalid(
        new MontiArcCoCoChecker()
            .addCoCo((MontiArcASTComponentCoCo) new NamesCorrectlyCapitalized()),
        node, new ExpectedErrorInfo(1, "xMA055"));
  }
  
  @Test
  public void testReferencedSubComponentsExists() {
    checkValid(PACKAGE + "." + "ReferencedSubComponentsExists");
  }
  
  @Test
  public void testCompThatUsesCompWithInterfaceParam() {
    checkValid(PACKAGE + "." + "CompThatUsesCompWithInterfaceParam");
  }
  
  @Test
  public void testReferencedSubComponentsNotExists() {
    final String qualifiedModelName = PACKAGE + "." + "ReferencedSubComponentsNotExists";
    final MontiArcCoCoChecker cocos
        = new MontiArcCoCoChecker().addCoCo(new SubComponentsConnected());
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(3, "xMA004");
    checkInvalid(cocos, loadComponentAST(qualifiedModelName), errors);
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
  @Ignore
  public void testInexistingSubComponent() {
    Log.getFindings().clear();
    loadComponentAST(PACKAGE + "." + "InexistingSubComponent");
    ExpectedErrorInfo.setERROR_CODE_PATTERN(Pattern.compile("x[0-9A-F]{5}"));
    ExpectedErrorInfo errors = new ExpectedErrorInfo(2, "xA1038");
    errors.checkExpectedPresent(Log.getFindings(), "No errors found!");
    ExpectedErrorInfo.reset();
  }
  
  @Test
  public void testWrongSubComponentArgument() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "WrongSubComponentArgument");
    checkInvalid(MontiArcCoCos.createChecker(), node, new ExpectedErrorInfo(1, "xMA064"));
  }
  
  @Test
  /* TODO: Adjust after implementing CoCo
   * [Hab16] CV7 */
  public void testUniquenessReferences() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "UniquenessReferences");
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(4, "xMA061");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new IdentifiersAreUnique()), node, errors);
  }
  
  @Test
  public void testComponentWithTypeParametersHasInstance() {
    checkValid(PACKAGE + "." + "ComponentWithTypeParametersHasInstance");
  }
  
  @Test
  public void testCompWithCfg() {
    checkValid(PACKAGE + "." + "CompWithCfg");
  }
  
  @Test
  public void testCompWithCfg2() {
    checkValid(PACKAGE + "." + "CompWithCfg2");
  }
  
  @Test
  public void testUniqueInnerCompDefinition() {
    try {
      checkValid(PACKAGE + "." + "UniqueInnerCompDefinition");
    }
    catch (de.monticore.symboltable.resolving.ResolvedSeveralEntriesException e) {
      assert e.toString().contains("xA4095");
      return;
    }
    fail();
  }
  
  @Test
  public void testValidAndInvalidSubcomponents() {
    final String qualifiedModelName = PACKAGE + "." + "ValidAndInvalidSubcomponents";
    final MontiArcCoCoChecker cocos
        = new MontiArcCoCoChecker().addCoCo(new SubComponentsConnected());
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(5, "xMA004", "xMA059", "xMA060");
    checkInvalid(cocos, loadComponentAST(qualifiedModelName), errors);
  }
  
  @Test
  public void testInvalidNestedComponentWithTypeParameterLacksInstance() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "NestedComponentWithTypeParameterLacksInstance");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ComponentWithTypeParametersHasInstance()),
        node, new ExpectedErrorInfo(1, "xMA009"));
  }
  
  @Test
  public void testComponentWithNamedInnerComponent() {
    String unqualifiedComponentName = "ComponentWithNamedInnerComponent";
    ComponentSymbol comp = this.loadComponentSymbol(PACKAGE, unqualifiedComponentName);
    
    assertFalse(comp.isInnerComponent());
    assertEquals(0, comp.getConfigParameters().size());
    assertEquals(1, comp.getAllIncomingPorts().size());
    assertEquals(1, comp.getAllOutgoingPorts().size());
    
    // ensures that inner component definitions can be loaded with the model
    // loader, so we can
    // resolve references to them of sub components, see ModelNameCalculator.
    assertEquals(1, comp.getSubComponents().size());
    ComponentInstanceSymbol subComp = comp.getSubComponents().iterator().next();
    assertEquals(PACKAGE + "." + "ComponentWithNamedInnerComponent.instance",
        subComp.getFullName());
    assertEquals("instance", subComp.getName());
    
    assertEquals(1, comp.getInnerComponents().size());
    
    ComponentSymbol inner = comp.getInnerComponent("NamedInnerComponent").orElse(null);
    assertNotNull(inner);
    ComponentSymbolReference compRefToInner = subComp.getComponentType();
    assertTrue(compRefToInner.getReferencedComponent().isPresent());
    assertTrue(inner == compRefToInner.getReferencedComponent().get());
    assertEquals("NamedInnerComponent", inner.getName());
    assertEquals("NamedInnerComponent", compRefToInner.getName());
    assertEquals(PACKAGE + "." + "ComponentWithNamedInnerComponent.NamedInnerComponent",
        inner.getFullName());
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
    
    Scope symTab = this.loadDefaultSymbolTable();
    ComponentSymbol innerComp = symTab.<ComponentSymbol> resolve(
        PACKAGE + "." + "ComponentWithNamedInnerComponent.NamedInnerComponent",
        ComponentSymbol.KIND)
        .orElse(null);
    assertNotNull(innerComp);
  }
  
  @Test
  public void testReferencingCompsWithCfg() {
    ComponentSymbol comp = MONTIARCTOOL.loadComponentSymbolWithCocos(
        PACKAGE + "." + "ReferencingCompsWithCfg",
        Paths.get(MODEL_PATH).toFile(), Paths.get(FAKE_JAVA_TYPES_PATH).toFile()).orElse(null);
    assertNotNull(comp);
    
    ComponentInstanceSymbol compWithArgsRef = comp.getSubComponent("cfg").orElse(null);
    assertNotNull(compWithArgsRef);
    JavaDSLPrettyPrinter prettyPrinter = new JavaDSLPrettyPrinter(new IndentPrinter());
    
    assertEquals(3, compWithArgsRef.getConfigArguments().size());
    ASTExpression arg1 = compWithArgsRef.getConfigArguments().get(0);
    assertEquals("1", prettyPrinter.prettyprint(arg1));
    // TODO proper setting of Kind? currently everything is an expression as we
    // extend JavaDSL
    
    ASTExpression arg2 = compWithArgsRef.getConfigArguments().get(1);
    assertEquals("\"Hallo\"", prettyPrinter.prettyprint(arg2));
    // TODO proper setting of Kind? currently everything is an expression as we
    // extend JavaDSL
    // instead of CommonValues
    // assertEquals(ValueEntry.Kind.Value, arg2.getKind());
    
    // TODO proper setting of Kind? currently everything is an expression as we
    // extend JavaDSL
    // instead of CommonValues
    // assertEquals(ValueEntry.Kind.ConstructorCall, arg3.getKind());
    
    ComponentSymbol compWithArgsType = compWithArgsRef.getComponentType().getReferencedComponent()
        .orElse(null);
    assertNotNull(compWithArgsType);
    // check that configuration parameters reference the correct paramter
    // types/names in
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
    ComponentSymbol comp = this.loadComponentSymbol(PACKAGE, "ReferencingCompsWithExpression");
    ComponentInstanceSymbol compWithArgsRef = comp.getSubComponent("cfg").orElse(null);
    assertNotNull(compWithArgsRef);
    
    assertEquals(2, compWithArgsRef.getConfigArguments().size());
    ASTExpression arg1 = compWithArgsRef.getConfigArguments().get(0);
    // expressions
    JavaDSLPrettyPrinter prettyPrinter = new JavaDSLPrettyPrinter(new IndentPrinter());
    assertEquals("2*1*5+1", prettyPrinter.prettyprint(arg1));
    // internal representation of expressions
    // assertEquals(4, arg1.getConstructorArguments().size());
    // assertEquals("2", arg1.getConstructorArguments().get(0).getValue());
    // assertEquals("1", arg1.getConstructorArguments().get(1).getValue());
    // assertEquals("5", arg1.getConstructorArguments().get(2).getValue());
    // assertEquals("1", arg1.getConstructorArguments().get(3).getValue());
    
    ASTExpression arg2 = compWithArgsRef.getConfigArguments().get(1);
    assertEquals("new Integer(2)*5", prettyPrinter.prettyprint(arg2));
    // assertEquals(2, arg2.getConstructorArguments().size());
    // assertEquals("new Integer(2)",
    // arg2.getConstructorArguments().get(0).getValue());
    // assertEquals(ValueEntry.Kind.ConstructorCall,
    // arg2.getConstructorArguments().get(0).getKind());
    // assertEquals("5", arg2.getConstructorArguments().get(1).getValue());
    // assertEquals(ValueEntry.Kind.Value,
    // arg2.getConstructorArguments().get(1).getKind());
  }
  
  @Test
  public void testImportedReferences() {
    ComponentSymbol comp = this.loadComponentSymbol(PACKAGE, "ComplexComponent");
    
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
    checkValid(PACKAGE+".ComponentWithInnerComponent");
    ComponentSymbol comp = this.loadComponentSymbol(PACKAGE, "ComponentWithInnerComponent");
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
    assertEquals(PACKAGE + "." + "ComponentWithInnerComponent.innerComponent.strIn",
        connector.getFullName());
    
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
    assertEquals(PACKAGE + "." + "ComponentWithInnerComponent.InnerComponent.strIn",
        inPort.getFullName());
    
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
    assertEquals(
        PACKAGE + "." + "ComponentWithInnerComponent.InnerComponent.InnerInnerComponent.strIn",
        inPort.getFullName());
    // connectors
    assertEquals(2, inner.getConnectors().size());
    
  }
  
  @Test
  public void testInnerComponents2() {
    ComponentSymbol comp = this.loadComponentSymbol(PACKAGE, "InnerComponents");
    
    assertEquals("6 subcomponents expected", 5, comp
        .getSubComponents().size());
    assertEquals(7, comp.getInnerComponents().size());
    
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
    assertEquals("2 named subcomponents", 2, inner.getSubComponents().size());
    
    // usage of external component type as sub component in same package in
    // inner component
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
    
    // usage of external component type as sub component in same package in
    // inner inner component
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
  
  @Test
  public void testUsingSCWithParams() {
    Scope symTab = this.loadDefaultSymbolTable();
    final String modelNameFq = PACKAGE + "." + "UsingSCWithParams";
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        modelNameFq, ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);

    final MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new SubComponentsConnected());
    final ASTMontiArcNode node = loadComponentAST(modelNameFq);
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(1, "xMA059");
    checkInvalid(cocos, node, errors);

    ComponentInstanceSymbol delay = (ComponentInstanceSymbol) comp.getSpannedScope()
        .resolve("deleteTempFile", ComponentInstanceSymbol.KIND).orElse(null);
    assertNotNull(delay);
    assertEquals("deleteTempFile", delay.getName());
    
    IndentPrinter printer = new IndentPrinter();
    JavaDSLPrettyPrinter prettyPrinter = new JavaDSLPrettyPrinter(printer);
    delay.getConfigArguments().get(0).accept(prettyPrinter);
    
    
    assertEquals(1, delay.getConfigArguments().size());
    assertEquals("1", printer.getContent());
  }
  
  @Test
  public void testUsingComplexParams() {
    Scope symTab = this.loadDefaultSymbolTable();
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        PACKAGE + "." + "UsingComplexParams", ComponentSymbol.KIND).orElse(null);
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
    // assertEquals("1",
    // delay.getConfigArguments().get(0).getConstructorArguments().get(0).getValue());
    // assertEquals("2",
    // delay.getConfigArguments().get(0).getConstructorArguments().get(1).getValue());
    // assertEquals("3",
    // delay.getConfigArguments().get(0).getConstructorArguments().get(2).getValue());
    // assertEquals("new HashMap<List<String>, List<Integer>>()",
    // delay.getConfigArguments().get(1).getValue());
    // assertEquals(Kind.ConstructorCall,
    // delay.getConfigArguments().get(1).getKind());
    // JTypeReference<? extends JTypeSymbol> typeRef =
    // delay.getConfigArguments().get(1).getType();
    // assertEquals("java.util.List",
    // typeRef.getTypeParameters().get(0).getType().getName());
    // assertEquals("java.util.List",
    // typeRef.getTypeParameters().get(1).getType().getName());
    // assertEquals("java.lang.String",
    // typeRef.getTypeParameters().get(0).getTypeParameters().get(0).getType().getName());
    // assertEquals("java.lang.Integer",
    // typeRef.getTypeParameters().get(1).getTypeParameters().get(0).getType().getName());
  }
  
  @Test
  public void testUsingComplexParamsWithCoco() {
    String path = PACKAGE + "." + "UsingComplexParams";
    checkValid(path);
  }
  
  @Test
  public void testHasGenericInputAndOutputPort() {
    checkValid(PACKAGE + "." + "_subcomponents" + "." +
        "HasGenericInputAndOutputPort");
  }
  
  @Test
  public void testHasPortsOfHierarchicalCDTypes() {
    checkValid(PACKAGE + "." + "_subcomponents" + "." +
        "HasPortsOfHierarchicalCDTypes");
  }
  
  @Test
  public void testHasStringInputAndIntegerOutput() {
    checkValid(PACKAGE + "." + "_subcomponents" + "." +
        "HasStringInputAndIntegerOutput");
  }
  
  @Test
  public void testHasStringInputAndOutput() {
    checkValid(PACKAGE + "." + "_subcomponents" + "." +
        "HasStringInputAndOutput");
  }
  
  @Test
  public void testHasIntegerInputAndOutput() {
    checkValid(PACKAGE + "." + "_subcomponents" + "." +
        "HasIntegerInputAndOutput");
  }
  
  @Test
  public void testHasThreeGenericInAndOneOutputPort() {
    checkValid(PACKAGE + "." + "_subcomponents" + "." +
        "HasThreeGenericInAndOneOutPort");
  }
  
  @Test
  public void testIntegerInputAndBooleanOutput() {
    checkValid(PACKAGE + "." + "_subcomponents" + "." +
        "IntegerInputAndBooleanOutput");
  }
  
  @Test
  public void testHasPortWithImportedType() {
    checkValid(PACKAGE + "." + "_subcomponents" + "." +
        "HasPortWithImportedType");
  }
  
  @Test
  public void testHasTwoStringInAndOneStrinOut() {
    checkValid(PACKAGE + "." + "_subcomponents" + "." +
        "HasTwoStringInAndOneStringOut");
  }
  
  @Test
  public void testGenericArchitectureComponent() {
    checkValid(PACKAGE + "." +
        "GenericArchitectureComponent");
  }
  
  @Test
  public void testSimpleGenericComponent() {
    checkValid(PACKAGE + "." + "_subcomponents" + "." +
        "SimpleGenericComponent");
  }
  
  @Test
  public void testGenericConfigurableComponent() {
    checkValid(PACKAGE + "." + "_subcomponents" + "." +
        "GenericConfigurableComponent");
  }
  
  @Test
  public void testAtomicComponent() {
    checkValid(PACKAGE + "." + "_subcomponents" + "." +
        "AtomicComponent");
  }
  
  @Test
  public void testGenericIfRequired() {
    checkValid(PACKAGE + "." + "_subcomponents" + "." +
        "GenericIfRequired");
  }
  
  @Test
  public void testMultipleInstancesWithSimpleConnector() {
    final ASTMontiArcNode astNode = loadComponentAST(
        PACKAGE + "." + "MultipleInstancesWithSimpleConnector");
    final ExpectedErrorInfo expectedErrorInfo = new ExpectedErrorInfo(5, "xMA059", "xMA060");
    checkInvalid(MontiArcCoCos.createChecker(), astNode, expectedErrorInfo);
  }
  
  @Test
  public void testSubcomponentsWithGenericTypeParams() {
    final ASTMontiArcNode astNode = loadComponentAST(
        PACKAGE + "." + "SubcomponentsWithGenericTypeParams");
    final ExpectedErrorInfo expectedErrorInfo = new ExpectedErrorInfo(3, "xMA085");
    checkInvalid(MontiArcCoCos.createChecker(), astNode, expectedErrorInfo);
  }
  
  @Test
  /* Checks that the CoCo InnerComponentNotExtendsDefiningComponent is working
   * as intended.
   * @Implements [Hab16] R12: An inner component type definition must not extend
   * the component type in which it is defined. (p. 68, lst. 3.47) */
  public void testOuterComponent() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "OuterComponent");
    final MontiArcCoCoChecker checker = new MontiArcCoCoChecker()
        .addCoCo(new InnerComponentNotExtendsDefiningComponent());
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(3, "xMA083");
    checkInvalid(checker, node, errors);
  }
  
  @Test
  public void testSubcomponentsWithWrongNumberOfCfgArgs() {
    final ASTMontiArcNode astNode = loadComponentAST(
        PACKAGE + "." + "SubcomponentsWithWrongNumberOfCfgArgs");
    final ExpectedErrorInfo expectedErrorInfo = new ExpectedErrorInfo(2, "xMA082");
    final MontiArcCoCoChecker checker = new MontiArcCoCoChecker()
        .addCoCo(new SubcomponentParametersCorrectlyAssigned());
    checkInvalid(checker, astNode, expectedErrorInfo);
  }

  @Test
  public void testWrongSubcomponentAndGenericParameters() {
    final ASTMontiArcNode astNode = loadComponentAST(
        PACKAGE + "." + "WrongSubcomponentAndGenericParameters");
    final ExpectedErrorInfo expectedErrorInfo
        = new ExpectedErrorInfo(3, "xMA064", "xMA085");
    final MontiArcCoCoChecker checker = new MontiArcCoCoChecker()
        .addCoCo(new SubcomponentParametersCorrectlyAssigned())
        .addCoCo(new SubcomponentGenericTypesCorrectlyAssigned());
    checkInvalid(checker, astNode, expectedErrorInfo);
  }

  @Test
  public void testWrongSubcomponentGenericsAssignment() {
    final ASTMontiArcNode node
        = loadComponentAST(PACKAGE + "." +
                               "WrongSubcomponentGenericsAssignment");
    final MontiArcCoCoChecker checker = new MontiArcCoCoChecker().addCoCo(new SubcomponentGenericTypesCorrectlyAssigned());
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(3, "xMA085", "xMA096");

    checkInvalid(checker, node, errors);
  }

  @Test
  public void testReferenceCycle() {
    final String qualifiedModelName = PACKAGE + "." + "ReferenceCycle";
    final MontiArcCoCoChecker checker
        = new MontiArcCoCoChecker().addCoCo(new SubcomponentReferenceCycle());
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(5, "xMA086");

    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }

  @Test
  public void testReferenceCycle2() {
    final String qualifiedModelName = PACKAGE + "." + "ReferenceCycle2";
    final MontiArcCoCoChecker checker
        = new MontiArcCoCoChecker().addCoCo(new SubcomponentReferenceCycle());
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(1, "xMA086");

    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }

  @Test
  // TODO Redundanzen zwischen CoCos prüfen
  public void testSubcomponentsWithCfgArgsAndTypeParams() {
    final String qualifiedModelName
        = PACKAGE + "." + "SubcomponentsWithCfgArgsAndTypeParams";
    final MontiArcCoCoChecker checker
        = MontiArcCoCos.createChecker();
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(9, "xMA082", "xMA085", "xMA096");

    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }

  @Test
  public void testHasConflictingSubcomponentNames() {
    checkValid(PACKAGE + "." + "HasConflictingSubcomponentNames");
  }


  @Test
  public void testSubCompWithNotExistingTypeAsTypeArg() {
    final String qualifiedModelName = PACKAGE + "." + "SubCompWithNotExistingTypeAsTypeArg";
    final MontiArcCoCoChecker checker =
        MontiArcCoCos.createChecker();
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(4, "xMA096", "xMA103", "xMA004");

    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }
}
