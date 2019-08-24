/* (c) https://github.com/MontiCore/monticore */
package montiarc.helper;

import static com.google.common.base.Preconditions.checkNotNull;

import montiarc.MontiArcConstants;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTElement;
import montiarc._ast.ASTMontiArcTiming;

/**
 * Contains available time paradigms. 
 *
 */
public enum Timing {
  
  /**
   * Component is not aware of time progress.
   */
  UNTIMED("untimed"),
  
  /**
   * Component is aware of time progress and might react to time events.
   */
  INSTANT("instant"),
  
  /**
   * Timed component that produces a delay (= needs computation time).
   *
   * @since 2.5.0
   */
  DELAYED("delayed"),
  
  /**
   * Component is aware of time progress and might react to time events. At max one message per
   * time-slice allowed.
   */
  TIME_SYNCHRONOUS("synchronous"),
  
  /**
   * Time synchronous component with causal behavior, i.e., at least one time frame delayed.
   */
  CAUSAL_SYNCHRONOUS("causal synchronous");
  
  private final String toString;
  
  private Timing(String str) {
    this.toString = str;
  }
  
  /**
   * @param component ast node of the component
   * @return {@link Timing} of the given component, or
   * {@link MontiArcConstants.DEFAULT_TIME_PARADIGM}, if no explicit time paradigm exists.
   */
  public static Timing getBehaviorKind(ASTComponent component) {
    Timing result = MontiArcConstants.DEFAULT_TIME_PARADIGM;
    for (ASTElement elem : component.getBody().getElementList()) {
      if (elem instanceof ASTMontiArcTiming) {
        ASTMontiArcTiming casted = (ASTMontiArcTiming) elem;
        if (casted.isDelayed()) {
          result = Timing.DELAYED;
        }
        else if (casted.isInstant()) {
          result = Timing.INSTANT;
        }
        else if (casted.isSync()) {
          result = Timing.TIME_SYNCHRONOUS;
        }
        else if (casted.isCausalsync()) {
          result = Timing.CAUSAL_SYNCHRONOUS;
        }
        else if (casted.isUntimed()) {
          result = Timing.UNTIMED;
        }
        break;
      }
    }
    return result;
  }
  
  /**
   * @param paradigm String representation of a {@link Timing}. Must not be null.
   * @return {@link Timing} created from the given string.
   * @throws NullPointerException if {@code paradigm == null}
   */
  public static Timing createBehaviorKind(String paradigm) {
    checkNotNull(paradigm);
    Timing timeParadigm = MontiArcConstants.DEFAULT_TIME_PARADIGM;
    
    if (paradigm.equals(TIME_SYNCHRONOUS.toString())) {
      timeParadigm = TIME_SYNCHRONOUS;
    }
    else if (paradigm.equals(CAUSAL_SYNCHRONOUS.toString())) {
      timeParadigm = CAUSAL_SYNCHRONOUS;
    }
    else if (paradigm.equals(INSTANT.toString())) {
      timeParadigm = INSTANT;
    }
    else if (paradigm.equals(UNTIMED.toString())) {
      timeParadigm = UNTIMED;
    }
    else if (paradigm.equals(DELAYED.toString())) {
      timeParadigm = DELAYED;
    }
    return timeParadigm;
  }
  
  @Override
  public String toString() {
    return toString;
  }
  
  /**
   * @return true, if this paradigm is timed or time-synchronous, else false.
   */
  public boolean isTimed() {
    if (this.equals(UNTIMED)) {
      return false;
    }
    else {
      return true;
    }
  }
  
  /**
   * @return true, if the component produces an initial delay.
   */
  public boolean isDelaying() {
    return this == DELAYED || this == CAUSAL_SYNCHRONOUS;
  }
  
  public boolean isTimeSynchronous() {
    return this == TIME_SYNCHRONOUS || this == CAUSAL_SYNCHRONOUS;
  }
}
