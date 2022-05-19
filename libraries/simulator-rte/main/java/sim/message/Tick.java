/* (c) https://github.com/MontiCore/monticore */
package sim.message;

/**
 * Represents a tick. To ticks are the borders from one time unit.
 *
 * @param <T> Type of the port the tick is used in
 */
public final class Tick<T> extends TickedMessage<T> {

  /**
   * Hash code of a tick.
   */
  private static final int HASH_CODE = 42;

  private static final long serialVersionUID = -1554360226603665938L;

  /**
   * Centrally used tick object. Since class {@link Tick} is final and does not contain any payload that depends on
   * generic type T, the concretely used tick object must not be strictly typed.
   */
  @SuppressWarnings("rawtypes")
  private static final Tick THE_TICK = new Tick();

  /**
   * Creates a new Tick.
   *
   * @deprecated use {@link Tick#get()} instead. Will be set to private after 2.5.0 release.
   */

  @Override
  public boolean equals(Object o) {
    // null check not needed, since null instanceof Tick is false.
    return o instanceof Tick;
  }

  @Override
  public int hashCode() {
    return HASH_CODE;
  }

  @Override
  public String toString() {
    return "Tk";
  }

  /**
   * @see TickedMessage#isTick()
   */
  @Override
  public boolean isTick() {
    return true;
  }

  /**
   * Returns a Tick object that can be sent by ports with type <i>MT</i>.
   *
   * @return a Tick object
   */
  @SuppressWarnings("unchecked")
  public static <MT> Tick<MT> get() {
    // Type Tick does not use the type parameter T. Consequently an
    // untyped Tick singleton THE_TICK is sufficient.
    return THE_TICK;
  }
}
