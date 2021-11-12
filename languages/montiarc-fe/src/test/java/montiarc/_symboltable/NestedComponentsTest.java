/* (c) https://github.com/MontiCore/monticore */

package montiarc._symboltable;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import montiarc.AbstractTest;
import montiarc.MontiArcCLI;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class NestedComponentsTest extends AbstractTest {

  protected String Test_PATH = "symboltable/nestedComponents";

  @Test
  public void shouldCreateNestedTypes () {
    // Given
    MontiArcCLI cli = new MontiArcCLI();
    Path path = Paths.get(RELATIVE_MODEL_PATH, Test_PATH);
    cli.loadSymbols(MontiArcMill.globalScope().getFileExt(), path);
    Collection<ASTMACompilationUnit> asts = cli.parse(".arc", path);
    cli.runDefaultTasks(asts);

    ComponentTypeSymbol rootComp = MontiArcMill.globalScope().resolveComponentType("WithInnerComponents").get();

    // When
    List<ComponentTypeSymbol> innerTypes = rootComp.getInnerComponents();
    ComponentTypeSymbol innerType1 = findCompTypeIn(innerTypes, "Inner1");
    ComponentTypeSymbol innerType2 = findCompTypeIn(innerTypes, "Inner2");
    ComponentTypeSymbol nestedType = findCompTypeIn(innerTypes, "Nested");

    List<ComponentTypeSymbol> mostNestedTypes = nestedType.getInnerComponents();
    ComponentTypeSymbol nextNested1 = findCompTypeIn(mostNestedTypes, "NextNested1");
    ComponentTypeSymbol nextNested2 = findCompTypeIn(mostNestedTypes, "NextNested2");

    List<ComponentInstanceSymbol> instancesInRootComp = rootComp.getSubComponents();
    ComponentInstanceSymbol inr1 = findCompInstIn(instancesInRootComp, "inr1");
    ComponentInstanceSymbol inr2 = findCompInstIn(instancesInRootComp, "inr2");
    ComponentInstanceSymbol inr22 = findCompInstIn(instancesInRootComp, "inr22");
    ComponentInstanceSymbol nest1 = findCompInstIn(instancesInRootComp, "nest1");

    List<ComponentInstanceSymbol> instancesInNestedComp = nestedType.getSubComponents();
    ComponentInstanceSymbol nn1 = findCompInstIn(instancesInNestedComp, "nn1");
    ComponentInstanceSymbol nn2 = findCompInstIn(instancesInNestedComp, "nn2");

    // Then
    Assertions.assertEquals(innerType1, inr1.getType());
    Assertions.assertEquals(innerType2, inr2.getType());
    Assertions.assertEquals(innerType2, inr22.getType());
    Assertions.assertEquals(nestedType, nest1.getType());
    Assertions.assertEquals(nextNested1, nn1.getType());
    Assertions.assertEquals(nextNested2, nn2.getType());
  }

  protected ComponentTypeSymbol findCompTypeIn(
    @NotNull Collection<ComponentTypeSymbol> typeCollection,
    @NotNull String compName
  ) {
    Preconditions.checkArgument(typeCollection != null);
    Preconditions.checkArgument(compName != null);

    Optional<ComponentTypeSymbol> result =
      typeCollection
      .stream()
      .filter(t -> t.getName().equals(compName))
      .findFirst();

    Assertions.assertTrue(
      result.isPresent(),
      "Could not find ComponentTypeSymbol " + compName
    );
    return result.get();
  }

  protected ComponentInstanceSymbol findCompInstIn(
    @NotNull Collection<ComponentInstanceSymbol> instanceCollection,
    @NotNull String compName
  ) {
    Preconditions.checkArgument(instanceCollection != null);
    Preconditions.checkArgument(compName != null);

    Optional<ComponentInstanceSymbol> result =
      instanceCollection
        .stream()
        .filter(i -> i.getName().equals(compName))
        .findFirst();

    Assertions.assertTrue(
      result.isPresent(),
      "Could not find ComponentInstanceSymbol " + compName
    );
    return result.get();
  }
}
