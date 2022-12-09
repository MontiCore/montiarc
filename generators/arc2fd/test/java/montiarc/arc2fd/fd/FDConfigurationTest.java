/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.fd;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FDConfigurationTest {
  FDConfiguration config = new FDConfiguration();

  /**
   * Method under test
   * {@link FDConfiguration#mapTypeToDisplayedName(Operation)} ()}
   */
  @Test
  void mapTypeToDisplayedName() {
    Assertions.assertThrows(NullPointerException.class,
        () -> config.mapTypeToDisplayedName(null));
    Assertions.assertEquals(FDConfiguration.MAP_TYPE_MAPPING.get(Operation.OPTIONALS),
        config.mapTypeToDisplayedName(Operation.OPTIONALS));
  }

  /**
   * Method under test {@link FDConfiguration#getOptionalsEnum()} ()}
   */
  @Test
  void getOptionalsEnum() {
    Assertions.assertEquals(Operation.OPTIONALS,
        config.getOptionalsEnum());
    Assertions.assertNotEquals(Operation.SIMPLE_OR,
        config.getOptionalsEnum());
  }

  /**
   * Method under test {@link FDConfiguration#getSimpleOrEnum()} ()}
   */
  @Test
  void getSimpleOrEnum() {
    Assertions.assertEquals(Operation.SIMPLE_OR,
        config.getSimpleOrEnum());
    Assertions.assertNotEquals(Operation.OPTIONALS,
        config.getSimpleOrEnum());
  }

  /**
   * Method under test {@link FDConfiguration#getXOREnum()} ()}
   */
  @Test
  void getXOREnum() {
    Assertions.assertEquals(Operation.XOR, config.getXOREnum());
    Assertions.assertNotEquals(Operation.OPTIONALS,
        config.getXOREnum());
  }

  /**
   * Method under test {@link FDConfiguration#getRemainingConjunctionsEnum()}
   * ()}
   */
  @Test
  void getRemainingConjunctionsEnum() {
    Assertions.assertEquals(Operation.REMAINING_CONJUNCTIONS,
        config.getRemainingConjunctionsEnum());
    Assertions.assertNotEquals(Operation.OPTIONALS,
        config.getRemainingConjunctionsEnum());
  }

  /**
   * Method under test {@link FDConfiguration#getRequiresEnum()} ()}
   */
  @Test
  void getRequiresEnum() {
    Assertions.assertEquals(Operation.REQUIRES,
        config.getRequiresEnum());
    Assertions.assertNotEquals(Operation.OPTIONALS,
        config.getRequiresEnum());
  }

  /**
   * Method under test {@link FDConfiguration#getExcludesEnum()} ()}
   */
  @Test
  void getExcludesEnum() {
    Assertions.assertEquals(Operation.EXCLUDES,
        config.getExcludesEnum());
    Assertions.assertNotEquals(Operation.OPTIONALS,
        config.getExcludesEnum());
  }
}
