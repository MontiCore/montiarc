/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.montiarc._symboltable;

import de.monticore.symboltable.CommonSymbol;

/**
 * TODO Do we really need this?
 *
 * @author Robert Heim
 */
public abstract class AComponentImplementationSymbol extends CommonSymbol {

  public static final AComponentImplementationKind KIND = AComponentImplementationKind.INSTANCE;

  /**
   * Constructor for de.monticore.lang.montiarc.montiarc._symboltable.AComponentImplementationSymbol
   *
   * @param name
   */
  public AComponentImplementationSymbol(String name) {
    super(name, KIND);
  }

}
