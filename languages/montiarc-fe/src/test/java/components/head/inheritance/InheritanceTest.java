/* (c) https://github.com/MontiCore/monticore */
package components.head.inheritance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Optional;

import ch.qos.logback.core.pattern.parser.OptionTokenizer;
import de.monticore.symboltable.Scope;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTConnector;
import montiarc.cocos.SubcomponentReferenceCycle;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._parser.MontiArcParser;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc.cocos.CircularInheritance;
import montiarc.cocos.ConfigurationParametersCorrectlyInherited;
import montiarc.cocos.MontiArcCoCos;

/**
 * This class checks all context conditions related to component inheritance
 *
 * @author Andreas Wortmann
 */
public class InheritanceTest extends AbstractCoCoTest {

  private static final String PACKAGE = "components.head.inheritance";

  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testSuperComponents() {
    ComponentSymbol subB = this.loadComponentSymbol(PACKAGE, "SubB");
    assertTrue(subB.getIncomingPort("anotherIn").isPresent());
    assertTrue(subB.getOutgoingPort("anotherOut").isPresent());
    assertTrue(subB.getOutgoingPort("anotherOut2").isPresent());

    // inherited
    assertTrue(subB.getIncomingPort("myInput",true).isPresent());
    assertEquals(2, subB.getAllIncomingPorts().size());

    assertTrue(subB.getOutgoingPort("myOutput", true).isPresent());
    assertTrue(subB.getOutgoingPort("myOutput519", true).isPresent());
    assertEquals(4, subB.getAllOutgoingPorts().size());
  }

  @Test
  public void testPortInheritance() {
    ComponentSymbol comp = this.loadComponentSymbol(PACKAGE, "ExtendsSuperComponent");
    assertTrue(comp.getIncomingPort("inputInteger").isPresent()); // Locally
                                                                  // defined
                                                                  // port
    assertTrue(comp.getIncomingPort("inputString", true).isPresent()); // port inherited from
                                                   // SuperComponent
    checkValid(PACKAGE + "." + "ExtendsSuperComponent");
  }
  
  @Test
  public void testConnectingInheritedPorts() {
    ComponentSymbol cmp = this.loadComponentSymbol(PACKAGE, "ComposedComponentUsingInheritedPorts");
    assertTrue(cmp.getAstNode().isPresent());
    assertTrue(cmp.getAstNode().get() instanceof ASTComponent);
    ASTComponent astComp = (ASTComponent) cmp.getAstNode().get();
    
    Iterator<ASTConnector> iterator = astComp.getConnectors().iterator();
    ASTConnector conn0 = iterator.next();
    Optional<PortSymbol> source = cmp.getPort(conn0.getSource().toString(),true);
    Optional<PortSymbol> target = cmp.getSubComponent(conn0.getTargets(0).getPart(0)).get().getComponentType().getPort(conn0.getTargets(0).getPart(1),true);
    assertTrue(source.isPresent());
    assertTrue(target.isPresent());
    assertEquals("inputIntegerA", source.get().getName());
    assertEquals("inputInteger", target.get().getName());
    assertTrue(target.get().getComponent().isPresent());
    assertEquals("ExtendsSuperComponent", target.get().getComponent().get().getName());
    
    ASTConnector conn1 = iterator.next();
    source = cmp.getPort(conn1.getSource().toString(),true);
    target = cmp.getSubComponent(conn1.getTargets(0).getPart(0)).get().getComponentType().getPort(conn1.getTargets(0).getPart(1),true);
    assertTrue(source.isPresent());
    assertTrue(target.isPresent());
    assertEquals("inputStringA", source.get().getName());
    assertEquals("inputString", target.get().getName());
    assertTrue(target.get().getComponent().isPresent());
    assertEquals("SuperComponent", target.get().getComponent().get().getName());
  }
  
  @Test
  public void testContainer() {
    ComponentSymbol cont = this.loadComponentSymbol(PACKAGE, "Container");
    assertEquals(2, cont.getInnerComponents().size());
    ComponentSymbol inner2 = cont.getInnerComponents().stream()
        .filter(i -> i.getName().equals("InnerComponent2")).findFirst().get();
    assertEquals(inner2.getSuperComponent().get().getName(), "InnerComponent1");
  }
  
  @Test
  public void testCircularInheritance() {
    final String componentName = PACKAGE + "." + "CircularInheritanceA";
    ASTMontiArcNode node = loadComponentAST(componentName);
    final MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new CircularInheritance());
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(1, "xMA017");
    checkInvalid(cocos, node, errors);
  }
  
  @Test
  public void testTransitiveCircularInheritance() {
    final String componentName = PACKAGE + "." + "TransitiveCircularInheritanceA";
    ASTMontiArcNode node = loadComponentAST(componentName);
    final MontiArcCoCoChecker cocos
        = new MontiArcCoCoChecker().addCoCo(new CircularInheritance());
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(1, "xMA017");
    checkInvalid(cocos, node, errors);
  }

  @Test
  /*
   *  Checks that a component extending another component with configuration
   *  parameters provides at least as many parameters.
   *
   * @implements [Hab16] R14: Components that inherit from a parametrized
   *    component provide configuration parameters with
   *    the same types, but are allowed to provide more
   *    parameters. (p.69 Lst. 3.49)
   */
  public void testTooFewConfigurationParameters() {
    final ASTMontiArcNode node
        = loadComponentAST(PACKAGE + "." + "TooFewConfigurationParameters");
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(1, "xMA084");
    checkInvalid(MontiArcCoCos.createChecker(), node, errors);
  }

  @Test
  /*
   *  Checks that a component extending another component with configuration
   *  parameters provides at least as many parameters.
   *
   * @implements [Hab16] R14: Components that inherit from a parametrized
   *    component provide configuration parameters with
   *    the same types, but are allowed to provide more
   *    parameters. (p.69 Lst. 3.49)
   */
  public void testParameterTypesNotMatching() {
    final ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "ParameterTypesNotMatching");
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(2, "xMA084");
    checkInvalid(MontiArcCoCos.createChecker(), node, errors);
  }

  @Test
  /*
   *  Checks that a component extending another component with configuration
   *  parameters provides at least as many parameters.
   *
   * @implements [Hab16] R14: Components that inherit from a parametrized
   *    component provide configuration parameters with
   *    the same types, but are allowed to provide more
   *    parameters. (p.69 Lst. 3.49)
   */
  public void testMissingOptionalParameters() {
    final ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "MissingOptionalParameter");
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(1, "xMA084");
    checkInvalid(MontiArcCoCos.createChecker(), node, errors);
  }

  @Test
  public void testHasRequiredAndOptionalConfigParameters() {
    checkValid(PACKAGE + "." + "HasRequiredAndOptionalConfigParameters");
  }

  @Test
  @Ignore("Check validity: Inheritance of subcomponents?")
  public void testSubB() {
    checkValid(PACKAGE + "." + "SubB");
  }

  @Test
  public void testSubCompCorrect1() {
    checkValid(PACKAGE + "." + "SubCompCorrect1");
  }

  @Test
  public void testSubCompCorrect2() {
    checkValid(PACKAGE + "." + "SubCompCorrect2");
  }

  @Test
  public void testSubCompCorrect3() {
    checkValid(PACKAGE + "." + "SubCompCorrect3");
  }

  @Test
  public void testSubCompCorrect4() {
    checkValid(PACKAGE + "." + "SubCompCorrect4");
  }

  @Test
  public void testSubCompAndOptionalConfigParameters() {
    checkValid(PACKAGE + "." + "SubCompAndOptionalConfigParameters");
    ComponentSymbol comp = this.loadComponentSymbol(PACKAGE, "SubCompAndOptionalConfigParameters");
  }

  @Test
  public void testExtendsSubCompAndOptionalConfigParameters() {
    final ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "ExtendsSubCompAndOptionalConfigParameters");
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(2, "xMA084");
    final MontiArcCoCoChecker montiArcCoCoChecker = new MontiArcCoCoChecker().addCoCo(new ConfigurationParametersCorrectlyInherited());
    checkInvalid(montiArcCoCoChecker, node, errors);
  }

  @Test
  public void testComponent1InCycle() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "Component1InCycle");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new CircularInheritance()), node,
        new ExpectedErrorInfo(1, "xMA017"));
  }

  @Test
  public void testWrongParameterOrder() {
    ASTMontiArcNode node
        = loadComponentAST(PACKAGE + "." + "WrongParameterOrder");
    final MontiArcCoCoChecker cocos
        = new MontiArcCoCoChecker()
              .addCoCo(new ConfigurationParametersCorrectlyInherited());
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(3, "xMA084");
    checkInvalid(cocos, node, errors);
  }
  
  @Test
  public void testExtendGenericComponentWithGenericConfigArg() {
    checkValid(PACKAGE + "." + "ExtendGenericComponentWithGenericConfigArg"); 
  }

  @Test
  public void testSubSubNestedGenericPortType() {
    checkValid(PACKAGE + "." + "SubSubNestedGenericPortType");
  }

  @Test
  public void testSubNestedGenericPortType() {
    checkValid(PACKAGE + "." + "SubNestedGenericPortType");
  }

  @Test
  public void testNestedGenericPortType() {
    checkValid(PACKAGE + "." + "NestedGenericPortType");
  }
  
}
