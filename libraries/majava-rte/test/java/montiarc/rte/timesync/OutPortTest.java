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
 * Class under test {@link OutPort}
 */
public class OutPortTest {

  @ParameterizedTest
  @Order(1)
  @ValueSource(strings = { "", "i", "o", "port" })
  public void testConstructor(String name) {
    // When
    OutPort<Object> port = new OutPort<>(name);

    // Then
    assertAll(
      () -> assertThat(port.name).isEqualTo(name),
      () -> assertThat(port.observers).isNotNull(),
      () -> assertThat(port.value).isNull()
    );
    assertThat(port.observers).isEmpty();
  }

  @Test
  @Order(2)
  public void testConstructor() {
    // When
    OutPort<Object> port = new OutPort<>();

    // Then
    assertAll(
      () -> assertThat(port.name).isEqualTo(""),
      () -> assertThat(port.observers).isNotNull(),
      () -> assertThat(port.value).isNull()
    );
    assertThat(port.observers).isEmpty();
  }

  @Test
  @Order(3)
  public void testConnect1() {
    // Given
    OutPort<Object> out = new OutPort<>();
    InPort<Object> in = new InPort<>();

    // When
    out.connect(in);

    // Then
    assertThat(out.observers).isNotEmpty();
    assertThat(out.observers).containsExactlyInAnyOrder(in);
  }

  @Test
  @Order(4)
  public void testConnect2() {
    // Given
    OutPort<Object> out = new OutPort<>();
    InPort<Object> in1 = new InPort<>();
    InPort<Object> in2 = new InPort<>();

    // When
    out.connect(in1);
    out.connect(in2);

    // Then
    assertThat(out.observers.size()).isEqualTo(2);
    assertThat(out.observers).containsExactlyInAnyOrder(in1, in2);
  }

  @Test
  @Order(5)
  public void testConnectWitDupe() {
    // Given
    OutPort<Object> out = new OutPort<>();
    InPort<Object> in = new InPort<>();

    // When
    out.connect(in);
    out.connect(in);

    // Then
    assertThat(out.observers.size()).isEqualTo(1);
    assertThat(out.observers).containsExactlyInAnyOrder(in);
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
    OutPort<Object> port = new OutPort<>();
    port.name = name;
    port.value = value;

    // When
    port.tick();

    // Then
    assertAll(
      () -> assertThat(port.name).isEqualTo(name),
      () -> assertThat(port.value).isNull()
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
    OutPort<Object> out = new OutPort<>();
    out.value = objOut;
    InPort<Object> in = new InPort<>();
    in.synced = synced;
    in.value = objIn;
    out.connect(in);

    // When
    out.tick();

    // Then
    assertAll(
      () -> assertThat(out.value).isNull(),
      () -> assertThat(in.value).isNull(),
      () -> assertThat(in.synced).isFalse()
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
    OutPort<Object> out = new OutPort<>();
    out.value = objOut;
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
      () -> assertThat(out.value).isNull(),
      () -> assertThat(in1.value).isNull(),
      () -> assertThat(in1.synced).isFalse(),
      () -> assertThat(in2.value).isNull(),
      () -> assertThat(in2.synced).isFalse()
    );
  }

  @Test
  @Order(9)
  public void testSetValue() {
    // Given
    OutPort<Object> port = new OutPort<>();
    port.value = null;
    Object object = new Object();

    // When
    port.setValue(object);

    // Then
    assertThat(port.value).isEqualTo(object);
  }

  @Test
  @Order(10)
  public void testSetValue1() {
    // Given
    OutPort<Object> out = new OutPort<>();
    out.value = null;
    InPort<Object> in = new InPort<>();
    in.value = null;
    out.connect(in);
    Object object = new Object();

    // When
    out.setValue(object);

    // Then
    assertAll(
      () -> assertThat(out.value).isEqualTo(object),
      () -> assertThat(in.value).isEqualTo(object),
      () -> assertThat(in.synced).isTrue()
    );
  }

  @Test
  @Order(11)
  public void testSetValue2() {
    // Given
    OutPort<Object> out = new OutPort<>();
    out.value = null;
    InPort<Object> in1 = new InPort<>();
    in1.value = null;
    InPort<Object> in2 = new InPort<>();
    in2.value = null;
    out.connect(in1);
    out.connect(in2);
    Object object = new Object();

    // When
    out.setValue(object);

    // Then
    assertAll(
      () -> assertThat(out.value).isEqualTo(object),
      () -> assertThat(in1.value).isEqualTo(object),
      () -> assertThat(in1.synced).isTrue(),
      () -> assertThat(in2.value).isEqualTo(object),
      () -> assertThat(in2.synced).isTrue()
    );
  }
}