package generation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import de.montiarcautomaton.generator.helper.ComponentHelper;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cocos.mcg2ebnf.AssociationNoStereotypesCoCo;
import infrastructure.AbstractCoCoTest;
import montiarc.MontiArcTool;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTPort;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;

public class ComponentHelperTest extends AbstractCoCoTest {

  private static final String PACKAGE = "components.head.parameters";
  
  @Test
  public void getPortTypeName() {
    ComponentSymbol comp = this.loadComponentSymbol(PACKAGE, "ComponentWithGenerics");
    ComponentHelper helper = new ComponentHelper(comp);

    Optional<PortSymbol> portSymbol = comp.getSpannedScope().resolve("inT", PortSymbol.KIND);
    assertTrue(portSymbol.isPresent());

    String portTypeName = helper.printPortTypeName(portSymbol.get());
    assertEquals("T", portTypeName);

    portTypeName = helper.printPortTypeName(portSymbol.get());
    assertEquals("T", portTypeName);

    portSymbol = comp.getSpannedScope().resolve("outK", PortSymbol.KIND);
    assertTrue(portSymbol.isPresent());

    portTypeName = helper.printPortTypeName(portSymbol.get());
    assertEquals("K", portTypeName);

    portTypeName = helper.printPortTypeName(portSymbol.get());
    assertEquals("K", portTypeName);    
  }
  
  @Test
  public void testAutoboxing() {
    String datatype = "Map<List<int>[],Set<double[]>>";
    String result = ComponentHelper.autobox(datatype);
    assertEquals("Map<List<Integer>[],Set<Double[]>>", result);
  }

  @Test
  public void getPortGenerics() {
    ComponentSymbol comp = this.loadComponentSymbol("components.head.generics", "Car");
    ComponentHelper helper = new ComponentHelper(comp);
    
    Optional<PortSymbol> portSymbol = comp.getSpannedScope().resolve("motor", PortSymbol.KIND);
    assertTrue(portSymbol.isPresent());

    String portTypeName = helper.printPortTypeName(portSymbol.get());
    assertEquals("java.util.HashMap<Double[],List<String>>[]", portTypeName);

    portSymbol = comp.getSpannedScope().resolve("wheels", PortSymbol.KIND);
    assertTrue(portSymbol.isPresent());

    portTypeName = helper.printPortTypeName(portSymbol.get());
    assertEquals("List<java.util.HashMap<Boolean,Double>>", portTypeName);
  }
  
  @Test
  public void testVariableTypeName() {
    ComponentSymbol comp = this.loadComponentSymbol(PACKAGE, "ComponentWithGenericVariables");
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
    ComponentSymbol comp = tool.loadComponentSymbolWithCocos("components.head.parameters.ComponentWithGenericParameters", Paths.get("src/test/resources").toFile(), Paths.get("src/main/resources/defaultTypes").toFile()).orElse(null);
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
}