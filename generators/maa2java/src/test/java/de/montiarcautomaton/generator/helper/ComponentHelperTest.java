package de.montiarcautomaton.generator.helper;

import de.monticore.symboltable.Scope;
import montiarc.MontiArcTool;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

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
}