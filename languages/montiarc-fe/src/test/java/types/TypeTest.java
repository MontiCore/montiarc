/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.ModelingLanguageFamily;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JMethodSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.CommonJTypeReference;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;

/**
 * This class checks all context conditions directly related to types (e.g., aggregation etc)
 *
 * @author Andreas Wortmann
 */
public class TypeTest extends AbstractCoCoTest {
  
  private static final String PACKAGE = "types";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testComponentWithCDType() {
    Scope symTab = this.loadDefaultSymbolTable();
    CDSymbol types = symTab.<CDSymbol> resolve(PACKAGE + "." + "Types", CDSymbol.KIND).orElse(null);
    assertNotNull(types);
    
    ComponentSymbol component = symTab
        .<ComponentSymbol> resolve(PACKAGE + "." + "ComponentWithCDType", ComponentSymbol.KIND)
        .orElse(null);
    assertNotNull(component);
  }
  
  @Test
  public void testFTPComponentWithCDType() {
    Scope symTab = this.loadDefaultSymbolTable();
    CDSymbol types = symTab.<CDSymbol> resolve(PACKAGE + "." + "Types", CDSymbol.KIND).orElse(null);
    assertNotNull(types);
    
    ComponentSymbol component = symTab
        .<ComponentSymbol> resolve(PACKAGE + "." + "FTPComponentWithCDType", ComponentSymbol.KIND)
        .orElse(null);
    assertNotNull(component);
  }
  
  @Test
  public void testCD() {
    final ModelPath modelPath = new ModelPath(Paths.get(MODEL_PATH));
    
    ModelingLanguageFamily family = new ModelingLanguageFamily();
    CD4AnalysisLanguage lang = new CD4AnalysisLanguage();
    family.addModelingLanguage(lang);
    GlobalScope scope = new GlobalScope(modelPath, family);
    
    Optional<CDSymbol> cd = scope.<CDSymbol> resolve(PACKAGE + "." + "Units",
        CDSymbol.KIND);
    assertTrue(cd.isPresent());
    Optional<CDTypeSymbol> quantity = scope.<CDTypeSymbol> resolve(PACKAGE + "." + "Units.Quantity",
        CDTypeSymbol.KIND);
    assertTrue(quantity.isPresent());
    Optional<JTypeSymbol> quantityAsJType = scope.<JTypeSymbol> resolve(
        PACKAGE + "." + "Units.Quantity",
        JTypeSymbol.KIND);
    assertTrue(quantityAsJType.isPresent());
  }
  
  @Test
  public void testCDusingMAWithCD() {
    Scope symTab = this.loadDefaultSymbolTable();
    Optional<CDSymbol> cd = symTab.<CDSymbol> resolve(PACKAGE + "." + "Units",
        CDSymbol.KIND);
    assertTrue(cd.isPresent());
    Optional<CDTypeSymbol> quantity = symTab.<CDTypeSymbol> resolve(
        PACKAGE + "." + "Units.Quantity",
        CDTypeSymbol.KIND);
    assertTrue(quantity.isPresent());
  }
  
  @Test
  /**
   * Tests whether SuperClasses get properly embedded in the symbol table, by checking the types of
   * the incoming and outgoing ports of the superclass.Simulation component.
   */
  public void testSuperClass() {
    Scope scope = this.loadDefaultSymbolTable();
    
    ComponentSymbol simulationComponent = scope
        .<ComponentSymbol> resolve(PACKAGE + "." + "Simulation", ComponentSymbol.KIND)
        .orElse(null);
    assertNotNull(simulationComponent);
    
    // Incoming Port
    ComponentSymbol errorComponent = simulationComponent
        .getInnerComponent("ErrorFilter")
        .orElse(null);
    assertNotNull(errorComponent);
    
    PortSymbol messagePort = errorComponent.getIncomingPort("msgs").orElse(null);
    assertNotNull(messagePort);
    
    JTypeReference<?> messageTypeRef = messagePort.getTypeReference();
    assertTrue(messageTypeRef.existsReferencedSymbol());
    assertTrue(messageTypeRef instanceof CommonJTypeReference);
    assertEquals("MyMessage", messageTypeRef.getName());
    JTypeSymbol messageType = messageTypeRef.getReferencedSymbol();
    // We will not check the type of the referenced symbol, since resolveMany()
    // doesn't care what gets returned
    assertEquals("MyMessage", messageType.getName());
    assertEquals(PACKAGE + "." + "SimulationData.MyMessage", messageType.getFullName());
    assertTrue(messageType.isAbstract());
    assertFalse(messageType.isInterface());
    assertFalse(messageType.isInnerType());
    
    // Outgoing Port
    PortSymbol errorPort = errorComponent
        .getOutgoingPort("errors")
        .orElse(null);
    assertNotNull(errorPort);
    
    JTypeReference<?> errorTypeRef = errorPort.getTypeReference();
    assertTrue(errorTypeRef.existsReferencedSymbol());
    assertTrue(errorTypeRef instanceof CommonJTypeReference);
    JTypeSymbol errorType = errorTypeRef.getReferencedSymbol();
    messageTypeRef = errorType.getSuperClass().orElse(null);
    assertEquals("MyMessage", messageTypeRef.getName());
    assertTrue(messageTypeRef.existsReferencedSymbol());
    messageType = messageTypeRef.getReferencedSymbol();
    assertEquals(PACKAGE + "." + "SimulationData.MyMessage", messageType.getFullName());
    assertEquals("MyMessage", messageType.getName());
    assertTrue(messageType.isAbstract());
    assertFalse(messageType.isInterface());
    assertFalse(messageType.isInnerType());
    
  }
  
  @Test
  /**
   * Tests whether Fields are properly embedded in the symbol table.
   */
  public void testFields() {
    Scope scope = this.loadDefaultSymbolTable();
    
    ComponentSymbol simulationSymbol = scope
        .<ComponentSymbol> resolve(PACKAGE + "." + "Simulation", ComponentSymbol.KIND)
        .orElse(null);
    assertNotNull(simulationSymbol);
    
    ComponentSymbol errorFilterSymbol = simulationSymbol
        .getInnerComponent("ErrorFilter")
        .orElse(null);
    assertNotNull(errorFilterSymbol);
    
    PortSymbol msgsPort = errorFilterSymbol.getIncomingPort("msgs").orElse(null);
    assertNotNull(msgsPort);
    
    JTypeReference<?> msgsTypeRef = msgsPort.getTypeReference();
    assertTrue(msgsTypeRef.existsReferencedSymbol());
    
    JTypeSymbol msgsType = msgsTypeRef.getReferencedSymbol();
    List<?> fields = msgsType.getFields();
    assertEquals(2, fields.size());
    
    assertTrue(msgsType.getField("content").isPresent());
    assertTrue(msgsType.getField("timestamp").isPresent());
  }
  
  @Test
  /**
   * Tests whether we can get methods from the symbol table
   */
  public void testMethod() {
    Scope symTab = this.loadDefaultSymbolTable();
    
    ComponentSymbol simulationSymbol = symTab
        .<ComponentSymbol> resolve(PACKAGE + "." + "Simulation", ComponentSymbol.KIND)
        .orElse(null);
    assertNotNull(simulationSymbol);
    
    PortSymbol msgsPort = simulationSymbol.getIncomingPort("msgs").orElse(null);
    assertNotNull(msgsPort);
    
    JTypeReference<?> msgTypeRef = msgsPort.getTypeReference();
    assertTrue(msgTypeRef.existsReferencedSymbol());
    
    JTypeSymbol msgType = msgTypeRef.getReferencedSymbol();
    List<? extends JMethodSymbol> methods = msgType.getMethods();
    assertFalse(methods.isEmpty());
    
    assertEquals("toString", methods.get(0).getName());
  }
  
  @Test
  /**
   * Tests whether we can get Interfaces from the symbol table.
   */
  public void testInterface() {
    Scope scope = this.loadDefaultSymbolTable();
    
    ComponentSymbol simulationSymbol = scope
        .<ComponentSymbol> resolve(PACKAGE + "." + "Simulation", ComponentSymbol.KIND)
        .orElse(null);
    assertNotNull(simulationSymbol);
    
    PortSymbol msgsPort = simulationSymbol.getIncomingPort("msgs").orElse(null);
    assertNotNull(msgsPort);
    
    JTypeReference<?> msgsTypeRef = msgsPort.getTypeReference();
    assertTrue(msgsTypeRef.existsReferencedSymbol());
    
    JTypeSymbol msgsType = msgsTypeRef.getReferencedSymbol();
    assertNotNull(msgsType);
    
    assertFalse(msgsType.getInterfaces().isEmpty());
    JTypeReference<?> firstInterfaceRef = msgsType.getInterfaces().get(0);
    assertTrue(firstInterfaceRef.existsReferencedSymbol());
    
    assertEquals("Traceable", firstInterfaceRef.getName());
  }
  
  /**
   * Tests whether referenced symbols have the correct KIND.
   */
  @Test
  public void testKind() {
    Scope scope = this.loadDefaultSymbolTable();
    
    ComponentSymbol simulationSymbol = scope
        .<ComponentSymbol> resolve(PACKAGE + "." + "Simulation", ComponentSymbol.KIND)
        .orElse(null);
    assertNotNull(simulationSymbol);
    
    PortSymbol msgsPort = simulationSymbol.getIncomingPort("msgs").orElse(null);
    assertNotNull(msgsPort);
    
    JTypeSymbol msgsType = msgsPort.getTypeReference().getReferencedSymbol();
    
    assertTrue(msgsType.isKindOf(JTypeSymbol.KIND));
    assertTrue(msgsType.isKindOf(CDTypeSymbol.KIND));
  }
  
}
