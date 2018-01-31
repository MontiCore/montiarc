package de.montiarcautomaton.generator.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JFieldSymbol;
import montiarc.MontiArcTool;
import montiarc._ast.ASTComponent;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;

public class ComponentHelperTest {

  @Test
  public void getParamValues() {
    MontiArcTool tool = new MontiArcTool();
    final Scope symbolTable = tool.createSymbolTable("src/test/resources/components/helper/ComposedTestComponent.arc");
    ComponentSymbol comp = tool.getComponentSymbol("components.helper.ComposedTestComponent", Paths.get("src/test/resources").toFile(), Paths.get("src/main/resources/defaultTypes").toFile()).orElse(null);
    assertNotNull(comp);


    ComponentHelper helper = new ComponentHelper(comp);

    Optional<ComponentInstanceSymbol> subComponent = comp.getSubComponent("first");
    assertTrue(subComponent.isPresent());
    List<String> paramValues = (List<String>) helper.getParamValues(subComponent.get());
    assertEquals("\"1st\"", paramValues.get(0));
    assertEquals("5", paramValues.get(1));
    assertEquals("new Object()", paramValues.get(2));

    subComponent = comp.getSubComponent("second");
    assertTrue(subComponent.isPresent());
    paramValues = (List<String>) helper.getParamValues(subComponent.get());
    assertEquals("\"2nd\"", paramValues.get(0));
    assertEquals("42", paramValues.get(1));
    assertEquals("new Object()", paramValues.get(2));

    subComponent = comp.getSubComponent("third");
    assertTrue(subComponent.isPresent());
    paramValues = (List<String>) helper.getParamValues(subComponent.get());
    assertEquals("\"3rd\"", paramValues.get(0));
    assertEquals("3", paramValues.get(1));
    assertEquals("new Integer(7)", paramValues.get(2));
  }

  @Test
  public void getPortTypeName() {
    MontiArcTool tool = new MontiArcTool();
    final Scope symbolTable = tool.createSymbolTable("src/test/resources/components/helper/ComponentWithGenerics.arc");
    ComponentSymbol comp = tool.getComponentSymbol("components.helper.ComponentWithGenerics", Paths.get("src/test/resources").toFile(), Paths.get("src/main/resources/defaultTypes").toFile()).orElse(null);
    assertNotNull(comp);

    ComponentHelper helper = new ComponentHelper(comp);
    ASTComponent compNode = (ASTComponent) comp.getAstNode().get();

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
  public void getPortGenerics() {
    MontiArcTool tool = new MontiArcTool();
    final Scope symbolTable = tool.createSymbolTable("src/test/resources/components/head/generics/Car.arc");
    ComponentSymbol comp = tool.getComponentSymbol("components.head.generics.Car", Paths.get("src/test/resources").toFile(), Paths.get("src/main/resources/defaultTypes").toFile()).orElse(null);
    assertNotNull(comp);

    ComponentHelper helper = new ComponentHelper(comp);
    ASTComponent compNode = (ASTComponent) comp.getAstNode().get();
    
    Optional<PortSymbol> portSymbol = comp.getSpannedScope().resolve("motor", PortSymbol.KIND);
    assertTrue(portSymbol.isPresent());

    String portTypeName = helper.printPortTypeName(portSymbol.get());
    assertEquals("HashBasedTable<Boolean,Double[],List<String>>[]", portTypeName);

    portTypeName = helper.printPortTypeName(portSymbol.get());
    assertEquals("HashBasedTable<Boolean,Double[],List<String>>[]", portTypeName);

    portSymbol = comp.getSpannedScope().resolve("wheels", PortSymbol.KIND);
    assertTrue(portSymbol.isPresent());

    portTypeName = helper.printPortTypeName(portSymbol.get());
    assertEquals("List<com.google.common.collect.ImmutableMap<Boolean,Double>[]>", portTypeName);

    portTypeName = helper.printPortTypeName(portSymbol.get());
    assertEquals("List<com.google.common.collect.ImmutableMap<Boolean,Double>[]>", portTypeName);
  }
  
  @Test
  public void testVariableTypeName() {
    MontiArcTool tool = new MontiArcTool();
    ComponentSymbol comp = tool.getComponentSymbol("components.helper.ComponentWithGenericVariables", Paths.get("src/test/resources").toFile(), Paths.get("src/main/resources/defaultTypes").toFile()).orElse(null);
    assertNotNull(comp);

    ComponentHelper helper = new ComponentHelper(comp);
    ASTComponent compNode = (ASTComponent) comp.getAstNode().get();

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
    ComponentSymbol comp = tool.getComponentSymbol("components.helper.ComponentWithGenericParameters", Paths.get("src/test/resources").toFile(), Paths.get("src/main/resources/defaultTypes").toFile()).orElse(null);
    assertNotNull(comp);

    ComponentHelper helper = new ComponentHelper(comp);
    ASTComponent compNode = (ASTComponent) comp.getAstNode().get();

    final List<JFieldSymbol> configParameters = comp.getConfigParameters();

    JFieldSymbol parameter = configParameters.get(0);
    String parameterType = helper.getParamTypeName(parameter);
    assertEquals("T", parameterType);

    parameter = configParameters.get(1);
    parameterType = helper.getParamTypeName(parameter);
    assertEquals("K", parameterType);
  }
}