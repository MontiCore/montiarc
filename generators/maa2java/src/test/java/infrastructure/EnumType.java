/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package infrastructure;

import java.util.Set;

/**
 * TODO
 *
 * @author (last commit)
 * @version ,
 * @since TODO
 */
public class EnumType {

  private final String name;
  private final Set<String> values;

  public EnumType(String name, Set<String> values) {
    this.name = name;
    this.values = values;
  }

  public String getName() {
    return name;
  }

  public Set<String> getValues() {
    return values;
  }
}
