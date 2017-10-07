/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc._symboltable;

/**
 * @author Robert Heim
 */
public class AComponentImplementationKind
    implements de.monticore.symboltable.SymbolKind {

  public static final AComponentImplementationKind INSTANCE = new AComponentImplementationKind();

  private static final String NAME = AComponentImplementationKind.class.getName();

  @Override
  public String getName() {
    return NAME;
  }

  protected AComponentImplementationKind() {
  }

}
