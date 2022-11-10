/* (c) https://github.com/MontiCore/monticore */
package elevator;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SplitterTest {

  @Test
  @Order(1)
  public void testSetUp() {
    // Given
    Splitter splitter = new Splitter();

    // When
    splitter.setUp();

    // Then
    assertAll(
      () -> assertThat(splitter.getI()).isNotNull(),
      () -> assertThat(splitter.getO1()).isNotNull(),
      () -> assertThat(splitter.getO2()).isNotNull(),
      () -> assertThat(splitter.getO3()).isNotNull(),
      () -> assertThat(splitter.getO4()).isNotNull()
    );
  }

  @Test
  @Order(2)
  public void testInit() {
    // Given
    Splitter splitter = new Splitter();
    splitter.setUp();

    // When
    splitter.init();

    // Then
    assertAll(
      () -> assertThat(splitter.getI().getValue()).isNull(),
      () -> assertThat(splitter.getO1().getValue()).isNull(),
      () -> assertThat(splitter.getO2().getValue()).isNull(),
      () -> assertThat(splitter.getO3().getValue()).isNull(),
      () -> assertThat(splitter.getO4().getValue()).isNull()
    );
  }

  @Order(3)
  @ParameterizedTest
  @MethodSource("relations")
  public void testCompute(@Nullable Integer i,
                          @NotNull boolean o1,
                          @NotNull boolean o2,
                          @NotNull boolean o3,
                          @NotNull boolean o4) {
    // Given
    Splitter splitter = new Splitter();
    splitter.setUp();
    splitter.init();

    splitter.getI().update(i);

    // When
    splitter.compute();

    // Then
    assertAll(
      () -> assertThat(splitter.getI().getValue()).isEqualTo(i),
      () -> assertThat(splitter.getO1().getValue()).isEqualTo(o1),
      () -> assertThat(splitter.getO2().getValue()).isEqualTo(o2),
      () -> assertThat(splitter.getO3().getValue()).isEqualTo(o3),
      () -> assertThat(splitter.getO4().getValue()).isEqualTo(o4)
    );
  }

  public static Stream<Arguments> relations() {
    return Stream.of(
      Arguments.of(null, false, false, false, false),
      Arguments.of(-1, false, false, false, false),
      Arguments.of(0, false, false, false, false),
      Arguments.of(1, true, false, false, false),
      Arguments.of(2, false, true, false, false),
      Arguments.of(3, false, false, true, false),
      Arguments.of(4, false, false, false, true)
    );
  }

  @Test
  @Order(4)
  public void testTick() {
    // Given
    Splitter splitter = new Splitter();
    splitter.setUp();

    int i = 0;
    splitter.getI().update(i);
    splitter.getO1().setValue(true);
    splitter.getO1().setValue(true);
    splitter.getO1().setValue(true);
    splitter.getO1().setValue(true);

    // When
    splitter.tick();

    // Then
    assertAll(
      () -> assertThat(splitter.getI().getValue()).isNotNull().isEqualTo(i),
      () -> assertThat(splitter.getO1().getValue()).isNull(),
      () -> assertThat(splitter.getO2().getValue()).isNull(),
      () -> assertThat(splitter.getO3().getValue()).isNull(),
      () -> assertThat(splitter.getO4().getValue()).isNull()
    );
  }


  @Order(5)
  @ParameterizedTest
  @MethodSource("histories")
  public void testCompute(@NotNull Integer[] i,
                          @NotNull boolean[] o1,
                          @NotNull boolean[] o2,
                          @NotNull boolean[] o3,
                          @NotNull boolean[] o4) {
    Preconditions.checkNotNull(i);
    Preconditions.checkArgument(i.length > 0);
    Preconditions.checkArgument(o1.length == i.length);
    Preconditions.checkArgument(o2.length == i.length);
    Preconditions.checkArgument(o3.length == i.length);
    Preconditions.checkArgument(o4.length == i.length);

    // Given
    Splitter splitter = new Splitter();
    splitter.setUp();

    boolean[] actO1 = new boolean[o1.length];
    boolean[] actO2 = new boolean[o2.length];
    boolean[] actO3 = new boolean[o3.length];
    boolean[] actO4 = new boolean[o4.length];

    // When
    splitter.init();

    for (int j = 0; j < i.length; j++) {
      splitter.getI().update(i[j]);
      splitter.compute();

      actO1[j] = splitter.getO1().getValue();
      actO2[j] = splitter.getO2().getValue();
      actO3[j] = splitter.getO3().getValue();
      actO4[j] = splitter.getO4().getValue();
    }

    // Then
    assertAll(
      () -> assertThat(actO1).containsExactly(o1),
      () -> assertThat(actO2).containsExactly(o2),
      () -> assertThat(actO3).containsExactly(o3),
      () -> assertThat(actO4).containsExactly(o4)
    );
  }

  public static Stream<Arguments> histories() {
    return Stream.of(
      // 1
      Arguments.of(
        new Integer[]{null, null},
        new boolean[]{false, false},
        new boolean[]{false, false},
        new boolean[]{false, false},
        new boolean[]{false, false}
      ),
      // 2
      Arguments.of(
        new Integer[]{0, 0},
        new boolean[]{false, false},
        new boolean[]{false, false},
        new boolean[]{false, false},
        new boolean[]{false, false}
      ),
      // 3
      Arguments.of(
        new Integer[]{1, 0},
        new boolean[]{true, false},
        new boolean[]{false, false},
        new boolean[]{false, false},
        new boolean[]{false, false}
      ),
      // 4
      Arguments.of(
        new Integer[]{0, 1},
        new boolean[]{false, true},
        new boolean[]{false, false},
        new boolean[]{false, false},
        new boolean[]{false, false}
      ),
      // 5
      Arguments.of(
        new Integer[]{0, 1, 0},
        new boolean[]{false, true, false},
        new boolean[]{false, false, false},
        new boolean[]{false, false, false},
        new boolean[]{false, false, false}
      ),
      // 6
      Arguments.of(
        new Integer[]{1, 0, 1},
        new boolean[]{true, false, true},
        new boolean[]{false, false, false},
        new boolean[]{false, false, false},
        new boolean[]{false, false, false}
      ),
      // 7
      Arguments.of(
        new Integer[]{1, 1, 1},
        new boolean[]{true, true, true},
        new boolean[]{false, false, false},
        new boolean[]{false, false, false},
        new boolean[]{false, false, false}
      ),
      // 8
      Arguments.of(
        new Integer[]{0, 1, 2, 3, 4, 0},
        new boolean[]{false, true, false, false, false, false},
        new boolean[]{false, false, true, false, false, false},
        new boolean[]{false, false, false, true, false, false},
        new boolean[]{false, false, false, false, true, false}
      ),
      // 9
      Arguments.of(
        new Integer[]{0, 4, 3, 2, 1, 0},
        new boolean[]{false, false, false, false, true, false},
        new boolean[]{false, false, false, true, false, false},
        new boolean[]{false, false, true, false, false, false},
        new boolean[]{false, true, false, false, false, false}
      )
    );
  }
}
