/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTConnector;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.*;
import montiarc.helper.InheritanceTypeHelper;
import montiarc.helper.TypeCompatibilityChecker;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Checks whether source and target type of connected ports match. It also
 * checks whether the source and target ports of the connectors actually exist.
 *
 * @author Jerome Pfeiffer, Michael Mutert
 * @implements [Hab16] CO3: Unqualified sources or targets in connectors either
 * refer to a port or a subcomponent in the same namespace. (p.61 Lst. 3.35)
 * @implements [Hab16] R5: The first part of a qualified connector’s source
 * respectively target must correspond to a subcomponent declared in the current
 * component definition. (p.64 Lst. 3.40)
 * @implements [Hab16] R6: The second part of a qualified connector’s source
 * respectively target must correspond to a port name of the referenced
 * subcomponent determined by the first part. (p.64, Lst. 3.41)
 * @implements [Hab16] R7: The source port of a simple connector must exist in
 * the subcomponents type. (p.65 Lst. 3.42)
 * @implements [Hab16] R8: The target port in a connection has to be compatible
 * to the source port, i.e., the type of the target port is identical or a
 * supertype of the source port type. (p. 66, lst. 3.43)
 */
public class ConnectorSourceAndTargetExistAndFit implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    if (!node.getSymbolOpt().isPresent()) {
      Log.error(
          String.format("0xMA010 ASTComponent node \"%s\" has no " +
                  "symbol. Did you forget to run the " +
                  "SymbolTableCreator before checking cocos?",
              node.getName()));
      return;
    }
    ComponentSymbol compSym = (ComponentSymbol) node.getSymbolOpt().get();
    
    node.getConnectors().forEach(connector -> {
      final Optional<PortSymbol> sourcePort;
      final List<Optional<PortSymbol>> targetPorts = new ArrayList<>();
      final List<ASTQualifiedName> targets = new ArrayList<>();
      try {
        if(connector.getSource().sizeParts() <= 1){
           sourcePort= compSym.getPort(connector.getSource().toString(), true);
        }else{
          Optional<ComponentInstanceSymbol> componentInstanceSymbol = compSym.getSubComponent(connector.getSource().getPart(0));
          if(componentInstanceSymbol.isPresent()){
            sourcePort=componentInstanceSymbol.get().getComponentType().getPort(connector.getSource().getPart(1), true);
          }else{
            sourcePort=Optional.empty();
          }
        }
        if (!sourcePort.isPresent()) {
          Log.error(
              String.format("0xMA066 source port %s of connector %s does not " +
                      "exist.",
                  connector.getSource(),
                  connector.getSource() + " ->" + connector.getTargetsList().stream()
                      .map(ASTQualifiedName::toString).reduce(" ", String::concat),
                  connector.get_SourcePositionStart()));
        }
  
        connector.streamTargetss().forEach(target -> {
          Optional<PortSymbol> targetPort;
          if(target.sizeParts() <= 1){
            targetPort = compSym.getPort(target.toString(), true);
          }else{
            Optional<ComponentInstanceSymbol> componentInstanceSymbol = compSym.getSubComponent(target.getPart(0));
            if(componentInstanceSymbol.isPresent()){
              targetPort=componentInstanceSymbol.get().getComponentType().getPort(target.getPart(1),true);
            }else{
              targetPort=Optional.empty();
            }
          }
          if (!targetPort.isPresent()) {
            Log.error(
                String.format("0xMA067 target port %s of connector %s does not " +
                        "exist.",
                    target,
                    connector.getSource() + " ->" + connector.getTargetsList().stream()
                        .map(ASTQualifiedName::toString).reduce(" ", String::concat),
                    connector.get_SourcePositionStart()));
          }
          targetPorts.add(targetPort);
          targets.add(target);
        });
      }
      catch (ResolvedSeveralEntriesException e) {
        return;
      }
  
      if (!sourcePort.isPresent()) {
        return;
      }
      PortSymbol source = sourcePort.get();
      for (int i = 0; i<targetPorts.size() ; i++) {
  
        if (!targetPorts.get(i).isPresent()) {
          return;
        }
        PortSymbol target = targetPorts.get(i).get();
  
        JTypeReference<? extends JTypeSymbol> sourceType = source.getTypeReference();
        JTypeReference<? extends JTypeSymbol> targetType = target.getTypeReference();
  
        // The checks are only applicable if the types of both ports exist
        if (!sourceType.existsReferencedSymbol() || !targetType.existsReferencedSymbol()) {
          return;
        }
  
        // Status: Both ports exist and the types of both ports exist
        // Next step is to determine the component in which the ports are defined.
        // In this process the real type arguments of the defining components have
        // to be determined.
        // Also, for the defining component the formal type parameters have to
        // be determined.
  
        List<JTypeSymbol> sourceTypeFormalParams
            = sourceType.getReferencedSymbol()
            .getFormalTypeParameters().stream()
            .map(p -> (JTypeSymbol) p).collect(Collectors.toList());
        List<JTypeSymbol> targetTypeFormalParams
            = targetType.getReferencedSymbol()
            .getFormalTypeParameters().stream()
            .map(p -> (JTypeSymbol) p).collect(Collectors.toList());
  
        List<JTypeReference<? extends JTypeSymbol>> sourceTypeArgumentTypes
            = sourceType.getActualTypeArguments()
            .stream()
            .map(arg -> (JTypeReference<? extends JTypeSymbol>) arg.getType())
            .collect(Collectors.toList());
        List<JTypeReference<? extends JTypeSymbol>> targetTypeArgumentTypes
            = targetType.getActualTypeArguments()
            .stream()
            .map(arg -> (JTypeReference<? extends JTypeSymbol>) arg.getType())
            .collect(Collectors.toList());
  
        // We have to load the binding of the formal type parameters to check
        // the types
        if (TypeCompatibilityChecker.hasNestedGenerics(sourceType)) {
          ASTQualifiedName sourceNameFQ = connector.getSource();
    
          final List<ActualTypeArgument> initialTypeArgs
              = InheritanceTypeHelper.getInitialTypeArguments(compSym, sourceNameFQ);
    
          // Determine the formal type arguments and type parameters of the defining component
          final Map.Entry<List<? extends JTypeSymbol>, List<ActualTypeArgument>>
              sourceTypeParamsAndRealTypeArgs
              = InheritanceTypeHelper.getTypeParamsAndRealTypeArgsForDefiningComponent(
              compSym, source, sourceNameFQ, initialTypeArgs);
    
          sourceTypeFormalParams = sourceTypeParamsAndRealTypeArgs.getKey().stream()
              .map(p -> (JTypeSymbol) p)
              .collect(Collectors.toList());
          sourceTypeArgumentTypes
              = sourceTypeParamsAndRealTypeArgs.getValue().stream()
              .map(a -> (JTypeReference<?>) a.getType())
              .collect(Collectors.toList());
        }
  
        if (TypeCompatibilityChecker.hasNestedGenerics(targetType)) {
          int finalI = i;
          ASTQualifiedName targetFQN
              = connector.getTargetsList().stream()
              .filter(n -> n.toString().equals(targets.get(finalI).toString()))
              .findFirst().get();
    
          final List<ActualTypeArgument> initialTypeArgs
              = InheritanceTypeHelper.getInitialTypeArguments(compSym, targetFQN);
    
          // Determine the formal type arguments and type parameters of the defining component
          final Map.Entry<List<? extends JTypeSymbol>, List<ActualTypeArgument>> targetTypeParamsAndRealTypeArgs
              = InheritanceTypeHelper.getTypeParamsAndRealTypeArgsForDefiningComponent(
              compSym, target, targetFQN, initialTypeArgs);
    
          targetTypeFormalParams = targetTypeParamsAndRealTypeArgs.getKey().stream()
              .map(p -> (JTypeSymbol) p)
              .collect(Collectors.toList());
          targetTypeArgumentTypes
              = targetTypeParamsAndRealTypeArgs.getValue()
              .stream()
              .map(a -> (JTypeReference<?>) a.getType())
              .collect(Collectors.toList());
        }
  
        if (!TypeCompatibilityChecker.areTypesEqual(
            sourceType,             // Type reference to the type of the source port
            sourceTypeFormalParams, // Formal parameters occurring in the source type
            sourceTypeArgumentTypes,// The actual types of the source type params
            targetType,             // Type reference to the type of the target port
            targetTypeFormalParams, // Formal parameters occurring in the target type
            targetTypeArgumentTypes)// The actual types of the target type params
        ) {
          String realSourceType
              = InheritanceTypeHelper.determineRealType(
              sourceType, sourceTypeFormalParams, sourceTypeArgumentTypes);
          String realTargetType
              = InheritanceTypeHelper.determineRealType(
              targetType, targetTypeFormalParams, targetTypeArgumentTypes);
          Log.error(
              String.format("0xMA033 Source type '%s' and target type '%s' of " +
                      "connector %s->%s do not match.",
                  realSourceType, realTargetType,
                  connector.getSource(), targets.get(i)),
              connector.get_SourcePositionStart());
        }
      }
    });
  }
}
