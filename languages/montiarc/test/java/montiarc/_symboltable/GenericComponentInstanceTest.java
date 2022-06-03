/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.CompTypeExpression;
import genericarc.check.TypeExprOfGenericComponent;
import montiarc.AbstractTest;
import montiarc.MontiArcTool;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public class GenericComponentInstanceTest extends AbstractTest {

  protected String Test_PATH = "montiarc/_symboltable";

  @Disabled(value = "Because we currently do not support generic component types.")
  @Test
  public void shouldReturnGenericType() {
    //Given
    MontiArcTool cli = new MontiArcTool();
    Path path = Paths.get(RELATIVE_MODEL_PATH, Test_PATH, "example1");

    cli.loadSymbols(MontiArcMill.globalScope().getFileExt(), path);
    Collection<ASTMACompilationUnit> asts = cli.parse(".arc", path);
    cli.runDefaultTasks(asts);
    ComponentTypeSymbol compC = MontiArcMill.globalScope().resolveComponentType("B").get();

    //When
    CompTypeExpression genericType = compC.getSubComponents().get(0).getType();

    //Then
    Assertions.assertTrue(genericType instanceof TypeExprOfGenericComponent);
    Assertions.assertEquals("T", genericType.getTypeInfo().getTypeParameters().get(0).getName());
  }
}