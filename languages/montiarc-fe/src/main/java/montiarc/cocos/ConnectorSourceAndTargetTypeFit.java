/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTConnector;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ConnectorSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc.helper.TypeCompatibilityChecker;

/**
 * Checks whether source and target type of connected ports match.
 * 
 * @implements [Hab16] R8: The target port in a connection has to be compatible
 * to the source port, i.e., the type of the target port is identical or a
 * supertype of the source port type. (p. 66, lst. 3.43)
 * @author Jerome Pfeiffer
 * @version $Revision$, $Date$
 */
public class ConnectorSourceAndTargetTypeFit implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    ComponentSymbol compSym = (ComponentSymbol) node.getSymbol().get();
    
    for (ConnectorSymbol connector : compSym.getConnectors()) {
      Optional<PortSymbol> sourcePort = null;
      Optional<PortSymbol> targetPort = null;
      try {
        sourcePort = connector.getSourcePort();
        targetPort = connector.getTargetPort();
      } catch (ResolvedSeveralEntriesException e) {
        break;
      }

      if (sourcePort.isPresent() && targetPort.isPresent()) {
        PortSymbol source = sourcePort.get();
        PortSymbol target = targetPort.get();
        
        Collection<ComponentInstanceSymbol> subComps = compSym.getSubComponents();
        
        JTypeReference<? extends JTypeSymbol> sourceType = source.getTypeReference();
        JTypeReference<? extends JTypeSymbol> targetType = target.getTypeReference();
        
        List<JTypeSymbol> sourceTypeFormalParams = sourceType.getReferencedSymbol()
            .getFormalTypeParameters().stream()
            .map(p -> (JTypeSymbol) p).collect(Collectors.toList());
        List<JTypeSymbol> targetTypeFormalParams = targetType.getReferencedSymbol()
            .getFormalTypeParameters().stream()
            .map(p -> (JTypeSymbol) p).collect(Collectors.toList());
        
        List<ActualTypeArgument> sourceParams = sourceType.getActualTypeArguments();
        List<ActualTypeArgument> targetParams = targetType.getActualTypeArguments();
        
        // We have to load the binding of the formal type parameters to check
        // the types
        if (sourceType.getReferencedSymbol().isFormalTypeParameter()) {
          ASTConnector c = (ASTConnector) connector.getAstNode().get();
          sourceParams = getActualTypeArgumentsFromPortOfConnector(c, compSym, c.getSource());
        }
        
        if (targetType.getReferencedSymbol().isFormalTypeParameter()) {
          ASTConnector c = (ASTConnector) connector.getAstNode().get();
          ASTQualifiedName targetFQN = c.getTargets().stream()
              .filter(n -> n.toString().equals(connector.getTarget()))
              .findFirst().get();
          targetParams = getActualTypeArgumentsFromPortOfConnector(c, compSym, targetFQN);
        }
        
        if (!TypeCompatibilityChecker.doTypesMatch(sourceType, sourceTypeFormalParams,
            sourceParams.stream()
                .map(a -> (JTypeReference<?>) a.getType()).collect(Collectors.toList()),
            targetType, targetTypeFormalParams, targetParams.stream()
                .map(a -> (JTypeReference<?>) a.getType()).collect(Collectors.toList())))
          Log.error(
              "0xMA084 Source and target type of connector " + connector.getName()
                  + " do not match.",
              connector.getAstNode().get().get_SourcePositionStart());
      }
    } 
  }
  
  private List<ActualTypeArgument> getActualTypeArgumentsFromPortOfConnector(
      ASTConnector connector, ComponentSymbol definingComponent,
      ASTQualifiedName nameOfConnectorEndpoint) {
    List<ActualTypeArgument> params = new ArrayList<>();
    if (nameOfConnectorEndpoint.getParts().size() > 1) {
      String compName = nameOfConnectorEndpoint.getParts().get(0);
      params = definingComponent.getSpannedScope()
          .<ComponentInstanceSymbol> resolve(compName, ComponentInstanceSymbol.KIND).get()
          .getComponentType().getActualTypeArguments();
    }
    return params;
    
  }
  
}
