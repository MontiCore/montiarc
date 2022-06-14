/* (c) https://github.com/MontiCore/monticore */
package arcbasis.timing;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import de.monticore.umlstereotype._ast.ASTStereotype;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TimingCollectorTest extends AbstractTest {

  protected static Stream<Arguments> stereotypeAndTimingsProvider() {
    return Stream.of(
        Arguments.of(
            ArcBasisMill.stereotypeBuilder()
                .addValues(
                    ArcBasisMill.stereoValueBuilder()
                        .setContent("").setName("untimed").build()
                ).build(),
            Collections.singletonList("untimed")
        ),
        Arguments.of(
            ArcBasisMill.stereotypeBuilder()
                .addAllValues(
                    Arrays.asList(
                        ArcBasisMill.stereoValueBuilder()
                            .setContent("").setName("other").build(),
                        ArcBasisMill.stereoValueBuilder()
                            .setContent("").setName("untimed").build()
                    )
                ).build(),
            Collections.singletonList("untimed")
        ),
        Arguments.of(
            ArcBasisMill.stereotypeBuilder()
                .addAllValues(
                    Arrays.asList(
                        ArcBasisMill.stereoValueBuilder()
                            .setContent("").setName("sync").build(),
                        ArcBasisMill.stereoValueBuilder()
                            .setContent("").setName("untimed").build()
                    )
                ).build(),
            Arrays.asList("sync", "untimed")
        ));
  }

  @Test
  public void defaultAvailableTimings() {
    // Given
    List<String> defaulltTimings = Arrays.asList(
        "untimed", "instant", "delayed", "sync", "causaulsyn");

    // When
    TimingCollector collector = new TimingCollector();
    List<String> actual = collector.getAvailableTimings()
        .stream().map(Timing::getName).collect(Collectors.toList());

    // Then
    Assertions.assertIterableEquals(defaulltTimings, actual);
  }

  @Test
  public void customAvailableTimings() {
    // Given
    List<String> timings = Arrays.asList(
        "untimed", "instant");

    // When
    TimingCollector collector = new TimingCollector("untimed", "instant");
    List<String> actual = collector.getAvailableTimings()
        .stream().map(Timing::getName).collect(Collectors.toList());

    // Then
    Assertions.assertIterableEquals(timings, actual);
  }

  @ParameterizedTest
  @MethodSource("stereotypeAndTimingsProvider")
  public void getCorrectTimings(ASTStereotype stereotype, List<String> containedTimings) {
    // When
    List<String> actual = TimingCollector.getTimings(stereotype)
        .stream().map(Timing::getName).collect(Collectors.toList());

    // Then
    Assertions.assertIterableEquals(containedTimings, actual);
  }
}
