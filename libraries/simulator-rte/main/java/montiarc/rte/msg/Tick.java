/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.msg;

public final class Tick<T> extends Message<T> {

  /**
   * Centrally used tick object. Since class {@link Tick} is final
   * and does not contain any payload that depends on generic type
   * T, the concretely used tick object must not be strictly typed.
   */
  @SuppressWarnings("rawtypes")
  private static final Tick TICK = new Tick();

  private Tick() {
    super(null);
  }

  @Override
  public String toString() {
    return "Tk";
  }

  /**
   * Returns a Tick object with message data type <i>MT</i>.
   *
   * @return a Tick object
   * @param <MT> the data type
   */
  @SuppressWarnings("unchecked")
  public static <MT> Tick<MT> get() {
    // Type Tick does not use the type parameter T. Consequently an
    // untyped Tick singleton THE_TICK is sufficient.
    return TICK;
  }

}
