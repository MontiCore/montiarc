/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package component.body.subcomponents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.symboltable.Scope;
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
  
  private static final String PACKAGE = "component.body.subcomponents";
  
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
    
    Scope symTab = new MontiArcTool().createSymbolTable("src/test/resources/");
    ComponentSymbol innerComp = symTab.<ComponentSymbol> resolve(
        PACKAGE + "." + "ComponentWithNamedInnerComponent.NamedInnerComponent", ComponentSymbol.KIND)
        .orElse(null);
    assertNotNull(innerComp);
  }
}
