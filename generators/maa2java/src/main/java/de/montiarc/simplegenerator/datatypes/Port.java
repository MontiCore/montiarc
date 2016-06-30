/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.simplegenerator.datatypes;


/**
 * A port is a {@code DataSource}.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class Port<T> extends DataSource<T> {
  
  public Port() {
    super();
  }
  
  public Port(T initialValue) {
    super(initialValue);
  }
}
