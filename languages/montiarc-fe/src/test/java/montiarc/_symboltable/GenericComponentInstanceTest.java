/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ComponentTypeSymbolSurrogate;
import montiarc.AbstractTest;
import montiarc.MontiArcCLI;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public class GenericComponentInstanceTest extends AbstractTest {

  protected String Test_PATH = "montiarc/_symboltable";

  @Test
  public void shouldReturnGenericType() {
    //Given
    MontiArcCLI cli = new MontiArcCLI();
    Path path = Paths.get(RELATIVE_MODEL_PATH, Test_PATH, "example1");

    cli.loadSymbols(MontiArcMill.globalScope().getFileExt(), path);
    Collection<ASTMACompilationUnit> asts = cli.parse(".arc", path);
    cli.runDefaultTasks(asts);
    ComponentTypeSymbol compC = MontiArcMill.globalScope().resolveComponentType("B").get();

    //When
    ComponentTypeSymbol genericType = compC.getSubComponents().get(0).getGenericType();
    ComponentTypeSymbol type = compC.getSubComponents().get(0).getType();

    //Then
    Assertions.assertNotEquals(genericType, type);
    Assertions.assertTrue(genericType instanceof ComponentTypeSymbolSurrogate);
    Assertions.assertEquals("T", genericType.getName());
    Assertions.assertEquals("A", type.getName());
  }
}