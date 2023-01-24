/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Arrays;
import java.util.Optional;

public enum Timing {
  UNTIMED("untimed"),
  TIMED("timed"),
  TIMED_SYNC("sync");

  final private String name;

  public String getName() {
    return this.name;
  }

  Timing(@NotNull String name) {
    Preconditions.checkNotNull(name);
    Preconditions.checkArgument(!name.isBlank());
    this.name = name;
  }

  public static Timing DEFAULT = UNTIMED;

  public static boolean contains(String name) {
    for (Timing t : values()) {
      if (t.name.equals(name)) return true;
    }
    return false;
  }

  public static Optional<Timing> of(String name) {
    return Arrays.stream(Timing.values()).filter(t -> t.getName().equals(name)).findFirst();
  }

  public boolean matches(Timing t) {
    return this.equals(t);
  }
}
