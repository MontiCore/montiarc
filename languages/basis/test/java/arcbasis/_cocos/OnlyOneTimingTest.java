/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.se_rwth.commons.logging.Log;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link OnlyOneTiming} is the class and context-condition under test.
 */
public class OnlyOneTimingTest extends ArcBasisAbstractTest {

  /**
   * If the port has no stereo values,
   * then the context-condition should not report any findings.
   */
  @Test
  public void shouldNotReportNoStereoValues() {
    // Given
    final ASTStereotype st = ArcBasisMill.stereotypeBuilder()
      .setValuesList(Collections.emptyList()).build();
    final OnlyOneTiming coco = new OnlyOneTiming();

    // When
    coco.check(st);

    // Then
    assertThat(Log.getFindingsCount()).isEqualTo(0);
  }

  /**
   * If the port has no timing stereo value,
   * then the context-condition should not report any findings.
   */
  @Test
  public void shouldNotReportNoTimingStereoValues() {
    // Given
    final ASTStereoValue sv = ArcBasisMill.stereoValueBuilder()
      .setName("some-value").setContent("").build();
    final ASTStereotype st = ArcBasisMill.stereotypeBuilder()
      .setValuesList(Collections.singletonList(sv)).build();
    final OnlyOneTiming coco = new OnlyOneTiming();

    Preconditions.checkState(!Timing.contains(sv.getName()));

    // When
    coco.check(st);

    // Then
    assertThat(Log.getFindingsCount()).isEqualTo(0);
  }

  /**
   * If the port has a single timing stereo value,
   * then the context-condition should not report any findings.
   */
  @ParameterizedTest
  @EnumSource(Timing.class)
  public void shouldNotReportSingleTimingStereoValue(@NotNull Timing timing) {
    Preconditions.checkNotNull(timing);

    // Given
    final ASTStereoValue sv = ArcBasisMill.stereoValueBuilder()
      .setName(timing.getName()).setContent("").build();
    final ASTStereotype st = ArcBasisMill.stereotypeBuilder()
      .setValuesList(Collections.singletonList(sv)).build();
    final OnlyOneTiming coco = new OnlyOneTiming();

    // When
    coco.check(st);

    // Then
    assertThat(Log.getFindingsCount()).isEqualTo(0);
  }

  /**
   * If the port has a single timing stereo value (in first position) and additional non-timing stereo values,
   * then the context-condition should not report any findings.
   */
  @ParameterizedTest
  @EnumSource(Timing.class)
  public void shouldNotReportTimingAsFirstStereoValue(@NotNull Timing timing) {
    Preconditions.checkNotNull(timing);

    // Given
    final ASTStereoValue sv1 = ArcBasisMill.stereoValueBuilder()
      .setName(timing.getName()).setContent("").build();
    final ASTStereoValue sv2 = ArcBasisMill.stereoValueBuilder()
      .setName("some-value").setContent("").build();
    final ASTStereotype st = ArcBasisMill.stereotypeBuilder()
      .setValuesList(ImmutableList.of(sv1, sv2)).build();
    final OnlyOneTiming coco = new OnlyOneTiming();

    Preconditions.checkState(!Timing.contains(sv2.getName()));

    // When
    coco.check(st);

    // Then
    assertThat(Log.getFindingsCount()).isEqualTo(0);
  }

  /**
   * If the port has a single timing stereo value (in second position) and additional non-timing stereo values,
   * then the context-condition should not report any findings.
   */
  @ParameterizedTest
  @EnumSource(Timing.class)
  public void shouldNotReportTimingAsSecondStereoValue(@NotNull Timing timing) {
    Preconditions.checkNotNull(timing);

    // Given
    final ASTStereoValue sv1 = ArcBasisMill.stereoValueBuilder()
      .setName("some-value").setContent("").build();
    final ASTStereoValue sv2 = ArcBasisMill.stereoValueBuilder()
      .setName(timing.getName()).setContent("").build();
    final ASTStereotype st = ArcBasisMill.stereotypeBuilder()
      .setValuesList(ImmutableList.of(sv1, sv2)).build();
    final OnlyOneTiming coco = new OnlyOneTiming();

    Preconditions.checkState(!Timing.contains(sv1.getName()));

    // When
    coco.check(st);

    // Then
    assertThat(Log.getFindingsCount()).isEqualTo(0);
  }

  /**
   * If the port has multiple timing stereo values,
   * then the context-condition should report an error.
   */
  @ParameterizedTest
  @MethodSource("provideTwoTimingStereoValues")
  public void shouldReportMultipleTimingStereoValues(@NotNull Timing t1, @NotNull Timing t2) {
    Preconditions.checkNotNull(t1);
    Preconditions.checkNotNull(t2);

    // Given
    final ASTStereoValue sv1 = ArcBasisMill.stereoValueBuilder()
      .setName(t1.getName()).setContent("").build();
    final ASTStereoValue sv2 = ArcBasisMill.stereoValueBuilder()
      .setName(t2.getName()).setContent("").build();
    final ASTStereotype st = ArcBasisMill.stereotypeBuilder()
      .setValuesList(ImmutableList.of(sv1, sv2)).build();
    final OnlyOneTiming coco = new OnlyOneTiming();

    // When
    coco.check(st);

    // Then
    checkOnlyExpectedErrorsPresent(ArcError.MULTIPLE_TIMING_ANNOTATIONS);
  }

  public static Stream<Arguments> provideTwoTimingStereoValues() {
    return Stream.of(
      Arguments.of(Timing.UNTIMED, Timing.UNTIMED),
      Arguments.of(Timing.UNTIMED, Timing.TIMED),
      Arguments.of(Timing.UNTIMED, Timing.TIMED_SYNC),
      Arguments.of(Timing.TIMED, Timing.TIMED),
      Arguments.of(Timing.TIMED, Timing.TIMED_SYNC),
      Arguments.of(Timing.TIMED_SYNC, Timing.TIMED_SYNC)
    );
  }

  /**
   * If the port has multiple timing stereo values,
   * then the context-condition should report an error.
   */
  @ParameterizedTest
  @MethodSource("provideThreeTimingStereoValues")
  public void shouldReportMultipleTimingStereoValues(@NotNull String s1,
                                                 @NotNull String s2,
                                                 @NotNull String s3) {
    Preconditions.checkNotNull(s1);
    Preconditions.checkNotNull(s2);
    Preconditions.checkNotNull(s3);

    // Given
    final ASTStereoValue sv1 = ArcBasisMill.stereoValueBuilder()
      .setName(s1).setContent("").build();
    final ASTStereoValue sv2 = ArcBasisMill.stereoValueBuilder()
      .setName(s2).setContent("").build();
    final ASTStereoValue sv3 = ArcBasisMill.stereoValueBuilder()
      .setName(s3).setContent("").build();
    final ASTStereotype st = ArcBasisMill.stereotypeBuilder()
      .setValuesList(ImmutableList.of(sv1, sv2, sv3)).build();
    final OnlyOneTiming coco = new OnlyOneTiming();

    // When
    coco.check(st);

    // Then
    checkOnlyExpectedErrorsPresent(ArcError.MULTIPLE_TIMING_ANNOTATIONS);
  }

  public static Stream<Arguments> provideThreeTimingStereoValues() {
    final String UNTIMED = Timing.UNTIMED.getName();
    final String TIMED = Timing.TIMED.getName();
    final String TIME_SYNC = Timing.TIMED_SYNC.getName();
    final String SOME = "some-value";
    return Stream.of(
      Arguments.of(UNTIMED, TIMED, TIME_SYNC),
      Arguments.of(UNTIMED, UNTIMED, UNTIMED),
      Arguments.of(TIMED, TIMED, TIMED),
      Arguments.of(TIME_SYNC, TIME_SYNC, TIME_SYNC),
      Arguments.of(UNTIMED, UNTIMED, SOME),
      Arguments.of(UNTIMED, SOME, UNTIMED),
      Arguments.of(SOME, UNTIMED, UNTIMED),
      Arguments.of(TIMED, TIMED, SOME),
      Arguments.of(TIMED, SOME, TIMED),
      Arguments.of(SOME, TIMED, TIMED),
      Arguments.of(TIME_SYNC, TIME_SYNC, SOME),
      Arguments.of(TIME_SYNC, SOME, TIME_SYNC),
      Arguments.of(SOME, TIME_SYNC, TIME_SYNC),
      Arguments.of(UNTIMED, TIMED, SOME),
      Arguments.of(TIMED, UNTIMED, SOME),
      Arguments.of(UNTIMED, TIME_SYNC, SOME),
      Arguments.of(TIME_SYNC, UNTIMED, SOME),
      Arguments.of(TIMED, TIME_SYNC, SOME),
      Arguments.of(TIME_SYNC, TIMED, SOME)
    );
  }
}
