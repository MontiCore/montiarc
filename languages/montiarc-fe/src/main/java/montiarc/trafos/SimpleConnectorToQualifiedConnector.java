/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.trafos;

import de.monticore.ast.ASTNode;
import de.monticore.types.types._ast.ASTQualifiedName;
import montiarc._ast.*;
import montiarc._symboltable.ComponentSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Transforms simple connectors from component instances to connectors of
 * the embedding component. This is happening on the AST level and the
 * transformation removes the original simple connectors from the AST.
 *
 * @author (last commit) Michael Mutert
 * @version , 2018-04-19
 * @since TODO
 */
public class SimpleConnectorToQualifiedConnector {

  private ASTConnector createASTConnector(ASTSimpleConnector simpleConnector,
                                          ASTSubComponentInstance instanceComponent){

    // Build the qualified name for the source of the connector
    final List<String> parts = new ArrayList<>();
    parts.add(instanceComponent.getName());
    parts.addAll(simpleConnector.getSource().getParts());
    final ASTQualifiedName qualifiedSourceName = ASTQualifiedName
                                       .getBuilder()
                                       .parts(parts)
                                       .build();

    // Build a new Connector node for the Simple connector
    final ASTConnector connector = ASTConnector
                                   .getBuilder()
                                   .source(qualifiedSourceName)
                                   .targets(simpleConnector.getTargets())
                                   .build();
    connector.set_SourcePositionStart(simpleConnector.get_SourcePositionStart());
    connector.set_SourcePositionEnd(simpleConnector.get_SourcePositionEnd());

    return connector;
  }

  public void transform(ASTSubComponentInstance subComponentInstance,
                        ComponentSymbol embeddingComponentSymbol){
    final List<ASTSimpleConnector> connectors = subComponentInstance.getConnectors();
    for (ASTSimpleConnector simpleConnector : connectors) {
      // Create a new connector for each simple connector
      ASTConnector connector = createASTConnector(simpleConnector, subComponentInstance);

      // Add the new nodes to the AST
      final Optional<ASTNode> optionalAstNode = embeddingComponentSymbol.getAstNode();
      if(optionalAstNode.isPresent()){
        final ASTComponent astNode = (ASTComponent) optionalAstNode.get();
        astNode.getBody().getElements().add(connector);
      }
    }

    // Remove simple connector nodes from the AST
    subComponentInstance.setConnectors(new ArrayList<>());
  }

}
