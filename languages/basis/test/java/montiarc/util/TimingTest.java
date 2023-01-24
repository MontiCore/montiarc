/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

import com.google.common.base.Preconditions;
import montiarc.Timing;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

public class TimingTest {

  @ParameterizedTest
  @EnumSource(Timing.class)
  public void shouldContain(@NotNull Timing timing) {
    Preconditions.checkNotNull(timing);

    // When && Then
    assertThat(Timing.contains(timing.getName())).isTrue();
  }

  @Test
  public void shouldNotContainEmpty() {
    // Given
    final String empty = "";

    // When && Then
    assertThat(Timing.contains(empty)).isFalse();
  }

  @Test
  public void shouldNotContainBlank() {
    // Given
    final String blank = "  ";

    // When && Then
    assertThat(Timing.contains(blank)).isFalse();
  }

  @Test
  public void shouldNotContainNull() {
    // When && Then
    assertThat(Timing.contains(null)).isFalse();
  }
}
