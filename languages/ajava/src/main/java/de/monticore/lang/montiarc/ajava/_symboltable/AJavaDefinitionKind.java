/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.ajava._symboltable;

import de.monticore.symboltable.SymbolKind;

/**
 * Symbol Kind of AJavaDefinitions.
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class AJavaDefinitionKind implements SymbolKind{
  private static final String NAME = "de.monticore.lang.montiarc.ajava._symboltable.AJavaDefinitionKind";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isKindOf(SymbolKind kind) {
    return NAME.equals(kind.getName()) || SymbolKind.super.isKindOf(kind);
  }
}
