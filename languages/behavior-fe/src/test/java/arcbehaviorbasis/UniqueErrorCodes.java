/* (c) https://github.com/MontiCore/monticore */
package arcbehaviorbasis;

import arcbasis.util.ArcError;
import montiarc.util.Error;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Disabled(value = "Error-codes for behavior-errors not assigned yet")
public class UniqueErrorCodes {

  private static Stream<Arguments> errors() {
    return Stream.of(
        Arguments.of("ArcCore-Errors", Arrays.stream(ArcError.values())),
        Arguments.of("Behavior-Errors", Arrays.stream(BehaviorError.values())),
        Arguments.of("all Errors", Stream.of(BehaviorError.values(), ArcError.values()).flatMap(Arrays::stream))
    );
  }

  @ParameterizedTest
  @MethodSource(value = "errors")
  public void checkForUniqueness(String occasion, Stream<? extends Error> errors){
    Map<String, String> codes = new HashMap<>();

    for(Error error:errors.toArray(Error[]::new)){
      String code = error.getErrorCode();
      if(codes.containsKey(code)){
        Assertions.fail("The error code " + code + " is used multiple times for "+occasion+": Once for " + codes.get(code) + ", Once for " + name(error));
      } else {
        codes.put(code, name(error));
      }
    }
  }

  /**
   * names an error for better differentiating
   * @param error error to be named
   * @return string representing the error
   */
  private static String name(Error error){
    return error instanceof Enum ? ((Enum<?>) error).name():error.printErrorMessage();
  }

}