/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc._symboltable;

import de.monticore.symboltable.CommonSymbol;

/**
 * @author Robert Heim
 */
public abstract class AComponentImplementationSymbol extends CommonSymbol {

  public static final AComponentImplementationKind KIND = new AComponentImplementationKind();

  /**
   * Constructor for de.monticore.lang.montiarc.montiarc._symboltable.AComponentImplementationSymbol
   *
   * @param name
   */
  public AComponentImplementationSymbol(String name) {
    super(name, KIND);
  }

}
