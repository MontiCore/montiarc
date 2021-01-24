/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Holds test for the handwritten methods of {@link ASTComponentInstantiation}.
 */
public class ComponentInstantiationTest extends AbstractTest {

  ASTComponentInstantiation astCompInstantiation;

  @BeforeEach
  public void setUpComponentInstantiation() {
    astCompInstantiation = ArcBasisMill.componentInstantiationBuilder()
      .setMCType(Mockito.mock(ASTMCObjectType.class))
      .setComponentInstanceList("comp1", "comp2", "comp3").build();
  }

  @ParameterizedTest
  @MethodSource("indexAndInstanceProvider")
  public void shouldReturnExpectedInstance(int index, String instance) {
    Assertions.assertEquals(instance, astCompInstantiation.getInstanceName(index));
  }

  @Test
  public void shouldReturnExpectedInstances() {
    List<String> expectedTargets = Arrays.asList("comp1", "comp2", "comp3");
    List<String> actualTargets = astCompInstantiation.getInstancesNames();
    Assertions.assertTrue(expectedTargets.containsAll(actualTargets));
    Assertions.assertTrue(actualTargets.containsAll(expectedTargets));
  }

  static Stream<Arguments> indexAndInstanceProvider() {
    return Stream.of(Arguments.of(0, "comp1"), Arguments.of(1, "comp2"), Arguments.of(2, "comp3"));
  }
}