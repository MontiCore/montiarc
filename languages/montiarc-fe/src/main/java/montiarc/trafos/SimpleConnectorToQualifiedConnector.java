/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.trafos;

import de.monticore.ast.ASTNode;
import de.monticore.mcbasictypes1._ast.MCBasicTypes1Mill;
import de.monticore.mcexpressions._ast.ASTQualifiedNameExpression;
import de.monticore.mcexpressions._ast.ASTQualifiedNameExpressionBuilder;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.types.types._ast.ASTQualifiedNameBuilder;
import de.monticore.types.types._ast.TypesMill;
import montiarc._ast.*;
import montiarc._symboltable.ComponentSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Transforms simple connectors from component instances to connectors of the
 * embedding component. This is happening on the AST level and the
 * transformation removes the original simple connectors from the AST.
 *
 * @author (last commit) Michael Mutert
 * @version , 2018-04-19
 * @since TODO
 */
public class SimpleConnectorToQualifiedConnector {
  
  private ASTConnector createASTConnector(ASTSimpleConnector simpleConnector,
      ASTSubComponentInstance instanceComponent) {
    
    // Build the qualified name for the source of the connector
    final List<String> parts = new ArrayList<>();
    parts.add(instanceComponent.getName());
    parts.addAll(simpleConnector.getSource().getPartList());
    final ASTQualifiedName qualifiedSourceName = TypesMill.qualifiedNameBuilder().addAllParts(parts)
        .build();
    qualifiedSourceName.set_SourcePositionStart(
        simpleConnector.getSource().get_SourcePositionStart());
    qualifiedSourceName.set_SourcePositionEnd(
        simpleConnector.getSource().get_SourcePositionEnd());
    
    // Build a new Connector node for the Simple connector
    final ASTConnector connector = MontiArcMill.connectorBuilder()
        .addAllTargetss(simpleConnector.getTargetsList())
        .build();
    connector.setSource(qualifiedSourceName);
    connector.set_SourcePositionStart(
        simpleConnector.get_SourcePositionStart());
    connector.set_SourcePositionEnd(
        simpleConnector.get_SourcePositionEnd());
    
    return connector;
  }
  
  public void transform(ASTSubComponentInstance subComponentInstance,
      ComponentSymbol embeddingComponentSymbol) {
    final List<ASTSimpleConnector> connectors = subComponentInstance.getConnectorsList();
    for (ASTSimpleConnector simpleConnector : connectors) {
      // Create a new connector for each simple connector
      ASTConnector connector = createASTConnector(simpleConnector, subComponentInstance);
      
      // Add the new nodes to the AST
      final Optional<ASTNode> optionalAstNode = embeddingComponentSymbol.getAstNode();
      if (optionalAstNode.isPresent()) {
        final ASTComponent astNode = (ASTComponent) optionalAstNode.get();
        astNode.getBody().getElementList().add(connector);
      }
    }
    
    // Remove simple connector nodes from the AST
    subComponentInstance.setConnectorsList(new ArrayList<>());
  }
}
