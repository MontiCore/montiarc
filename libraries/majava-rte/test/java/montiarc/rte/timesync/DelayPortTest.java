/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * Class under test {@link DelayPort}
 */
public class DelayPortTest {

  @ParameterizedTest
  @Order(1)
  @ValueSource(strings = { "", "i", "o", "port" })
  public void testConstructor(String name) {
    // When
    DelayPort<Object> port = new DelayPort<>(name);

    // Then
    assertAll(
      () -> assertThat(port.name).isEqualTo(name),
      () -> assertThat(port.observers).isNotNull(),
      () -> assertThat(port.value).isNull(),
      () -> assertThat(port.nextValue).isNull()
    );
    assertThat(port.observers).isEmpty();
  }

  @Test
  @Order(2)
  public void testConstructor() {
    // When
    DelayPort<Object> port = new DelayPort<>();

    // Then
    assertAll(
      () -> assertThat(port.name).isEqualTo(""),
      () -> assertThat(port.observers).isNotNull(),
      () -> assertThat(port.value).isNull(),
      () -> assertThat(port.nextValue).isNull()
    );
    assertThat(port.observers).isEmpty();
  }

  @ParameterizedTest
  @Order(6)
  @CsvSource({
    "\"\", null",
    "port, null",
    "port, new Object()"
  })
  public void testTick(String name, Object value) {
    // Given
    DelayPort<Object> port = new DelayPort<>();
    port.name = name;
    port.value = null;
    port.nextValue = value;

    // When
    port.tick();

    // Then
    assertAll(
      () -> assertThat(port.name).isEqualTo(name),
      () -> assertThat(port.value).isEqualTo(value),
      () -> assertThat(port.nextValue).isNull()
    );
  }

  @ParameterizedTest
  @Order(7)
  @CsvSource({
    "null, false, null",
    "null, true, null",
    "new Object(), false, null",
    "new Object(), true, null",
    "null, false, new Object()",
    "null, true, new Object()",
    "new Object(), false, new Object()",
    "new Object(), true, new Object()"
  })
  public void testTick(Object objOut, boolean synced, Object objIn) {
    // Given
    DelayPort<Object> out = new DelayPort<>();
    out.value = null;
    out.nextValue = objOut;
    InPort<Object> in = new InPort<>();
    in.synced = synced;
    in.value = objIn;
    out.connect(in);

    // When
    out.tick();

    // Then
    assertAll(
      () -> assertThat(out.value).isEqualTo(objOut),
      () -> assertThat(out.nextValue).isNull(),
      () -> assertThat(in.value).isEqualTo(objOut),
      () -> assertThat(in.synced).isTrue()
    );
  }

  @ParameterizedTest
  @Order(8)
  @CsvSource({
    "null, false, null, false, null",
    "null, true, null, false, null",
    "null, false, null, true, null",
    "null, true, null, true, null",
    "new Object(), false, null, false, null",
    "new Object(), true, null, false, null",
    "new Object(), false, null, true, null",
    "new Object(), true, null, true, null",
    "null, false, new Object(), false, null",
    "null, true, new Object(), false, null",
    "null, false, new Object(), true, null",
    "null, true, new Object(), true, null",
    "null, false, null, false, new Object()",
    "null, true, null, false, new Object()",
    "null, false, null, true, new Object()",
    "null, true, null, true, new Object()",
    "new Object(), false, new Object(), false, new Object()",
    "new Object(), true, new Object(), false, new Object()",
    "new Object(), false, new Object(), true, new Object()",
    "new Object(), true, new Object(), true, new Object()",
  })
  public void testTick2(Object objOut,
                        boolean synced1,
                        Object objIn1,
                        boolean synced2,
                        Object objIn2) {
    // Given
    DelayPort<Object> out = new DelayPort<>();
    out.value = null;
    out.nextValue = objOut;
    InPort<Object> in1 = new InPort<>();
    in1.synced = synced1;
    in1.value = objIn1;
    InPort<Object> in2 = new InPort<>();
    in2.synced = synced2;
    in2.value = objIn2;
    out.connect(in1);
    out.connect(in2);

    // When
    out.tick();

    // Then
    assertAll(
      () -> assertThat(out.value).isEqualTo(objOut),
      () -> assertThat(out.nextValue).isNull(),
      () -> assertThat(in1.value).isEqualTo(objOut),
      () -> assertThat(in1.synced).isTrue(),
      () -> assertThat(in2.value).isEqualTo(objOut),
      () -> assertThat(in2.synced).isTrue()
    );
  }

  @ParameterizedTest
  @Order(10)
  @CsvSource({
    "null, null, null",
    "new Object(), null, null",
    "null, new Object(), null",
    "null, null, new Object()",
    "new Object(), new Object(), null",
    "new Object(), null, new Object()",
    "null, new Object(), new Object()",
    "new Object(), new Object(), new Object()",
  })
  public void testSetValue(Object objOut, Object objNext, Object objIn) {
    // Given
    DelayPort<Object> out = new DelayPort<>();
    out.value = objOut;
    out.nextValue = null;
    InPort<Object> in = new InPort<>();
    in.value = objIn;
    out.connect(in);

    // When
    out.setValue(objNext);

    // Then
    assertAll(
      () -> assertThat(out.value).isEqualTo(objOut),
      () -> assertThat(in.value).isEqualTo(objIn)
    );
  }

  @ParameterizedTest
  @Order(11)
  @CsvSource({
    "null, null, null, null",
    "new Object(), null, null, null",
    "null, new Object(), null, null",
    "null, null, new Object(), null",
    "null, null, null, new Object()",
    "new Object(), new Object(), null, null",
    "new Object(), null, new Object(), null",
    "new Object(), null, null, new Object()",
    "null, new Object(), new Object(), null",
    "null, new Object(), null, new Object()",
    "new Object(), new Object(), new Object(), null",
    "new Object(), new Object(), null,  new Object()",
    "new Object(), null, new Object(),  new Object()",
    "null, new Object(), new Object(),  new Object()",
    "new Object(), new Object(), new Object(),  new Object()",
  })
  public void testSetValue(Object objOut, Object objNext, Object objIn1, Object objIn2) {
    // Given
    DelayPort<Object> out = new DelayPort<>();
    out.value = objOut;
    out.nextValue = null;
    InPort<Object> in1 = new InPort<>();
    in1.value = objIn1;
    InPort<Object> in2 = new InPort<>();
    in2.value = objIn2;
    out.connect(in1);
    out.connect(in2);

    // When
    out.setValue(objNext);

    // Then
    assertAll(
      () -> assertThat(out.value).isEqualTo(objOut),
      () -> assertThat(out.nextValue).isEqualTo(objNext),
      () -> assertThat(in1.value).isEqualTo(objIn1),
      () -> assertThat(in2.value).isEqualTo(objIn2)
    );
  }
}