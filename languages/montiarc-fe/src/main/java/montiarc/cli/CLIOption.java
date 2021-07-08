/* (c) https://github.com/MontiCore/monticore */
package montiarc.cli;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

public enum CLIOption implements ICLIOption {
  HELP("h", "help"),
  FAIL_QUICK("f", "fail-quick"),
  INPUT("i", "input"),
  STDIN("stdin", "stdin"),
  BUILD_IN_TYPES("t", "build-in-types"),
  SYMBOL_TABLE("s", "symbol-table"),
  PRETTY_PRINT("pp", "pretty-print"),
  PATH("path", "path");

  protected final String name;
  protected final String longName;

  /**
   * @param name The name of this cli option.
   * @param longName The alternative long name of this cli option.
   */
  CLIOption(@NotNull String name, @NotNull String longName) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(longName);
    this.name = name;
    this.longName = longName;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String getLongName() {
    return this.longName;
  }

  @Override
  public String printOption() {
    return "-" + this.getName();
  }

  @Override
  public String printLongOption() {
    return "--" + this.getLongName();
  }
}