/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Class under test {@link InPort}
 */
public class InPortTest {

  @ParameterizedTest
  @Order(1)
  @ValueSource(strings = { "", "i", "o", "port" })
  public void testConstructor(String name) {
    // When
    InPort<Object> port = new InPort<>(name);

    // Then
    assertAll(
      () -> assertThat(port.name).isEqualTo(name),
      () -> assertThat(port.synced).isFalse(),
      () -> assertThat(port.value).isNull()
    );
  }

  @Test
  @Order(2)
  public void testConstructor() {
    // When
    InPort<Object> port = new InPort<>();

    // Then
    assertAll(
      () -> assertThat(port.name).isEmpty(),
      () -> assertThat(port.synced).isFalse(),
      () -> assertThat(port.value).isNull()
    );
  }

  @ParameterizedTest
  @Order(3)
  @CsvSource({
    "\"\", false, null",
    "port, false, null",
    "port, true, null",
    "port, true, new Object()",
    "port, true, new Object()"
  })
  public void testTick(String name, boolean synced, Object value) {
    // Given
    InPort<Object> port = new InPort<>();
    port.name = name;
    port.synced = synced;
    port.value = value;

    // When
    port.tick();

    // Then
    Assertions.assertAll(
      () -> assertThat(port.name).isEqualTo(name),
      () -> assertThat(port.synced).isFalse(),
      () -> assertThat(port.value).isNull()
    );
  }

  @Test
  @Order(4)
  public void testUpdate() {
    // Given
    Object object = new Object();
    InPort<Object> in = new InPort<>();
    in.value = new Object();

    // When
    in.update(object);

    // Then
    Assertions.assertEquals(object, in.value);
  }
}
