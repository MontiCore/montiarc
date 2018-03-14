/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc._ast;

import de.monticore.ast.ASTNode;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
/**
 * Interface for all AST nodes of the MontiArc language.
 */
public interface ASTMontiArcNode extends ASTMontiArcNodeTOP {

  @Override
  public default void remove_Child(ASTNode arg0) {
    // TODO ???
    
  }

}
