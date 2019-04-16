package generation;

import de.montiarcautomaton.generator.MontiArcGeneratorTool;
import de.montiarcautomaton.generator.helper.ComponentHelper;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import infrastructure.AbstractCoCoTest;
import montiarc.MontiArcTool;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class ComponentHelperTest extends AbstractCoCoTest {

  private static final String PACKAGE = "components.head.parameters";
  protected static final String TARGET_TEST_MODELS = "target/test-models/";

  
  @Test
  @Ignore("TODO Fix Java Types in the test symbol table. They appear to be missing or wrongly added. Then uncomment all lines")
  public void determinePortTypeName() {

    ComponentSymbol comp;
    Optional<PortSymbol> portSymbolOpt;
    String printedPortType;

    comp = this.loadComponentSymbol(
        "components.head.inheritance",
        "SubSubNestedGenericPortType",
        "target/test-models/");
    ComponentHelper helper = new ComponentHelper(comp);
    portSymbolOpt = comp.getIncomingPort("nestedGenericInPort", true);
    assertTrue("Could not find expected port", portSymbolOpt.isPresent());
    printedPortType = ComponentHelper.getRealPortTypeString(comp, portSymbolOpt.get());
    assertEquals("List<Map<String, String>>", printedPortType);

    ComponentSymbol subComponentSymbol = this.loadComponentSymbol(
        "components.head.inheritance",
        "SubNestedGenericPortType",
        "target/test-models/");

    portSymbolOpt = comp.getIncomingPort("nestedGenericInPort", true);
    assertTrue("Could not find expected port", portSymbolOpt.isPresent());
    printedPortType = ComponentHelper.getRealPortTypeString(comp, portSymbolOpt.get());
    assertEquals("List<Map<String, K>>", printedPortType);

    comp = this.loadComponentSymbol(
        "components.head.generics",
        "SubCompExtendsGenericCompValid",
        TARGET_TEST_MODELS);

    portSymbolOpt = comp.getIncomingPort("tIn", true);
    assertTrue("Could not find expected port", portSymbolOpt.isPresent());
    printedPortType = ComponentHelper.getRealPortTypeString(comp, portSymbolOpt.get());
    assertEquals("String", printedPortType);
    portSymbolOpt = comp.getOutgoingPort("kOut", true);
    assertTrue("Could not find expected port", portSymbolOpt.isPresent());
    printedPortType = ComponentHelper.getRealPortTypeString(comp, portSymbolOpt.get());
    assertEquals("Integer", printedPortType);
  }

  @Test
  public void getPortTypeName() {
    ComponentSymbol comp = this.loadComponentSymbol(PACKAGE, "ComponentWithGenerics", TARGET_TEST_MODELS);
    ComponentHelper helper = new ComponentHelper(comp);

    Optional<PortSymbol> portSymbol = comp.getSpannedScope().resolve("inT", PortSymbol.KIND);
    assertTrue(portSymbol.isPresent());

    String portTypeName = helper.printPortType(portSymbol.get());
    assertEquals("T", portTypeName);

    portTypeName = helper.printPortType(portSymbol.get());
    assertEquals("T", portTypeName);

    portSymbol = comp.getSpannedScope().resolve("outK", PortSymbol.KIND);
    assertTrue(portSymbol.isPresent());

    portTypeName = helper.printPortType(portSymbol.get());
    assertEquals("K", portTypeName);

    portTypeName = helper.printPortType(portSymbol.get());
    assertEquals("K", portTypeName);

//    comp = loadComponentSymbol("components.body.connectors", "GenericSourceTypeIsSubtypeOfTargetType", TARGET_TEST_MODELS);
//    helper = new ComponentHelper(comp);
//
//    final Optional<PortSymbol> inT = comp.getPort("inT");
//    assertTrue(inT.isPresent());
//    final String inTType = helper.printPortType(inT.get());
//    assertEquals("T", inTType);
  }

  @Test
  public void autobox() {
    String datatype = "Map<List<int>[],Set<double[]>>";
    String result = ComponentHelper.autobox(datatype);
    assertEquals("Map<List<Integer>[],Set<Double[]>>", result);

    datatype = "new HashMap<List<int>[],Set<double[]>>()";
    result = ComponentHelper.autobox(datatype);
    assertEquals("new HashMap<List<Integer>[],Set<Double[]>>()", result);
  }

  @Test
  public void getPortGenerics() {
    ComponentSymbol comp = this.loadComponentSymbol("components.head.generics", "Car", TARGET_TEST_MODELS);
    ComponentHelper helper = new ComponentHelper(comp);
    
    Optional<PortSymbol> portSymbol = comp.getSpannedScope().resolve("motor", PortSymbol.KIND);
    assertTrue(portSymbol.isPresent());

    String portTypeName = helper.printPortType(portSymbol.get());
    assertEquals("java.util.HashMap<Double[],List<String>>", portTypeName);

    portSymbol = comp.getSpannedScope().resolve("wheels", PortSymbol.KIND);
    assertTrue(portSymbol.isPresent());

    portTypeName = helper.printPortType(portSymbol.get());
    assertEquals("List<java.util.HashMap<Boolean,Double>>", portTypeName);
  }
  
  @Test
  public void testVariableTypeName() {
    ComponentSymbol comp = this.loadComponentSymbol(PACKAGE, "ComponentWithGenericVariables", TARGET_TEST_MODELS);
    ComponentHelper helper = new ComponentHelper(comp);

    Optional<VariableSymbol> variableSymbol = comp.getSpannedScope().resolve("varWithTypeT", VariableSymbol.KIND);
    assertTrue(variableSymbol.isPresent());
    String variableTypName = helper.printVariableTypeName(variableSymbol.get());
    assertEquals("T", variableTypName);

    variableSymbol = comp.getSpannedScope().resolve("varWithTypeKextendsNumber", VariableSymbol.KIND);
    assertTrue(variableSymbol.isPresent());
    variableTypName = helper.printVariableTypeName(variableSymbol.get());
    assertEquals("K", variableTypName);
  }

  @Test
  public void getParamTypeName() {
    
    MontiArcTool tool = new MontiArcTool();
//    ComponentSymbol comp = tool.loadComponentSymbolWithCocos("components.head.parameters.ComponentWithGenericParameters", Paths.get("src/test/resources").toFile(), Paths.get("src/main/resources/defaultTypes").toFile()).orElse(null);
    ComponentSymbol comp = this.loadComponentSymbol("components.head.parameters", "ComponentWithGenericParameters", TARGET_TEST_MODELS);
    assertNotNull(comp);
    ComponentHelper helper = new ComponentHelper(comp);

    final List<JFieldSymbol> configParameters = comp.getConfigParameters();

    JFieldSymbol parameter = configParameters.get(0);
    String parameterType = helper.getParamTypeName(parameter);
    assertEquals("T", parameterType);

    parameter = configParameters.get(1);
    parameterType = helper.getParamTypeName(parameter);
    assertEquals("K", parameterType);
  }

  @Test
  @Ignore
  public void getGenericTypeParametersWithInterfaces() {
    final ComponentSymbol componentSymbol = loadComponentSymbol("components.body.connectors", "GenericSourceTypeIsSubtypeOfTargetType", TARGET_TEST_MODELS);
    ComponentHelper helper = new ComponentHelper(componentSymbol);
    final List<JTypeSymbol> formalTypeParameters = componentSymbol.getFormalTypeParameters();
    assertFalse(formalTypeParameters.isEmpty());

    final List<String> genericParametersWithBounds = helper.getGenericTypeParametersWithInterfaces();
    assertTrue(!genericParametersWithBounds.isEmpty());

    assertEquals("T extends java.lang.Number".replace(" ", ""),
        genericParametersWithBounds.get(0).replace(" ", ""));
  }

  @Test
  public void isSuperComponentGeneric(){
    ComponentSymbol componentSymbol
        = loadComponentSymbol("components.body.connectors", "ConnectsCompatibleInheritedPorts2", TARGET_TEST_MODELS);
    ComponentHelper helper = new ComponentHelper(componentSymbol);
    assertFalse(helper.isSuperComponentGeneric());

    componentSymbol = loadComponentSymbol("components.body.subcomponents._subcomponents", "InheritsOutgoingStringPort", TARGET_TEST_MODELS);
    helper = new ComponentHelper(componentSymbol);
    assertTrue(helper.isSuperComponentGeneric());
  }

  @Test
  public void hasSuperComponent() {
    ComponentSymbol componentSymbol
        = loadComponentSymbol("components.body.connectors", "ConnectsCompatibleInheritedPorts2", TARGET_TEST_MODELS);
    ComponentHelper helper = new ComponentHelper(componentSymbol);
    assertTrue(helper.hasSuperComp());

    componentSymbol = loadComponentSymbol("components.body.subcomponents._subcomponents", "HasGenericOutput", TARGET_TEST_MODELS);
    helper = new ComponentHelper(componentSymbol);
    assertFalse(helper.hasSuperComp());
  }

  @Test
  public void getSuperComponentActualTypeArguments() {
    ComponentSymbol componentSymbol = loadComponentSymbol(
        "components.body.subcomponents._subcomponents", "InheritsOutgoingStringPort", TARGET_TEST_MODELS);
    ComponentHelper helper = new ComponentHelper(componentSymbol);
    List<String> superCompActualTypeArguments = helper.getSuperCompActualTypeArguments();
    assertTrue(!superCompActualTypeArguments.isEmpty());
    assertEquals("java.lang.String", superCompActualTypeArguments.get(0));


    // Currently disabled due to issues #241, #243
//    componentSymbol = loadComponentSymbol("components.head.generics", "SubCompExtendsGenericComparableCompValid", TARGET_TEST_MODELS);
//    helper = new ComponentHelper(componentSymbol);
//    superCompActualTypeArguments = helper.getSuperCompActualTypeArguments();
//    assertTrue(!superCompActualTypeArguments.isEmpty());
//    assertEquals("K", superCompActualTypeArguments.get(0));

  }
}