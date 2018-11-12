/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.se_rwth.commons.logging.Log;
import javafx.util.Pair;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTConnector;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.*;
import montiarc.helper.SymbolPrinter;
import montiarc.helper.TypeCompatibilityChecker;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Checks whether source and target type of connected ports match. It also
 * checks whether the source and target ports of the connectors actually exist.
 *
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
 * @author Jerome Pfeiffer, Michael Mutert
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
    
    for (ConnectorSymbol connector : compSym.getConnectors()) {
      final Optional<PortSymbol> sourcePort;
      final Optional<PortSymbol> targetPort;
      try {
        sourcePort = connector.getSourcePort();
        if (!sourcePort.isPresent()) {
          Log.error(
              String.format("0xMA066 source port %s of connector %s does not " +
                  "exist.",
                  connector.getSource(),
                  connector.getFullName()),
              connector.getSourcePosition());
        }

        targetPort = connector.getTargetPort();
        if (!targetPort.isPresent()) {
          Log.error(
              String.format("0xMA067 target port %s of connector %s does not " +
                  "exist.",
                  connector.getTarget(),
                  connector.getFullName()),
              connector.getSourcePosition());
        }
      }
      catch (ResolvedSeveralEntriesException e) {
        continue;
      }

      // Only check if both ports actually exist
      if (!sourcePort.isPresent() || !targetPort.isPresent()) {
        continue;
      }

      PortSymbol source = sourcePort.get();
      PortSymbol target = targetPort.get();

      JTypeReference<? extends JTypeSymbol> sourceType = source.getTypeReference();
      JTypeReference<? extends JTypeSymbol> targetType = target.getTypeReference();


      // The checks are only applicable if the types of both ports exist
      if (!sourceType.existsReferencedSymbol() || !targetType.existsReferencedSymbol()) {
        continue;
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
                .map(arg -> (JTypeReference<? extends JTypeSymbol>)arg.getType())
                .collect(Collectors.toList());
      List<JTypeReference<? extends JTypeSymbol>> targetTypeArgumentTypes
          = targetType.getActualTypeArguments()
                .stream()
                .map(arg -> (JTypeReference<? extends JTypeSymbol>)arg.getType())
                .collect(Collectors.toList());

      // We have to load the binding of the formal type parameters to check
      // the types
      if (TypeCompatibilityChecker.hasNestedGenerics(sourceType)) {
        ASTConnector connectorNode = (ASTConnector) connector.getAstNode().get();
        ASTQualifiedName sourceNameFQ = connectorNode.getSource();

        final List<ActualTypeArgument> initialTypeArgs
            = getInitialTypeArguments(compSym, sourceNameFQ);

        // Determine the formal type arguments and type parameters of the defining component
        final Pair<List<? extends JTypeSymbol>, List<ActualTypeArgument>>
            sourceTypeParamsAndRealTypeArgs
            = getTypeParamsAndRealTypeArgsForDefiningComponent(compSym, source, sourceNameFQ, initialTypeArgs);

        sourceTypeFormalParams = sourceTypeParamsAndRealTypeArgs.getKey().stream()
              .map(p -> (JTypeSymbol) p)
              .collect(Collectors.toList());
        sourceTypeArgumentTypes
            = sourceTypeParamsAndRealTypeArgs.getValue().stream()
                  .map(a -> (JTypeReference<?>) a.getType())
                  .collect(Collectors.toList());
      }


      if (TypeCompatibilityChecker.hasNestedGenerics(targetType)) {
        ASTConnector astConnector = (ASTConnector) connector.getAstNode().get();
        ASTQualifiedName targetFQN
            = astConnector.getTargetsList().stream()
                  .filter(n -> n.toString().equals(connector.getTarget()))
                  .findFirst().get();

        final List<ActualTypeArgument> initialTypeArgs
            = getInitialTypeArguments(compSym, targetFQN);

        // Determine the formal type arguments and type parameters of the defining component
        final Pair<List<? extends JTypeSymbol>, List<ActualTypeArgument>> targetTypeParamsAndRealTypeArgs
            = getTypeParamsAndRealTypeArgsForDefiningComponent(compSym, target, targetFQN, initialTypeArgs);

        targetTypeFormalParams = targetTypeParamsAndRealTypeArgs.getKey().stream()
              .map(p -> (JTypeSymbol) p)
              .collect(Collectors.toList());
        targetTypeArgumentTypes
            = targetTypeParamsAndRealTypeArgs.getValue()
                  .stream()
                  .map(a -> (JTypeReference<?>) a.getType())
                  .collect(Collectors.toList());
      }

      if (!TypeCompatibilityChecker.doTypesMatch(
          sourceType,             // Type reference to the type of the source port
          sourceTypeFormalParams, // Formal parameters occurring in the source type
          sourceTypeArgumentTypes,// The actual types of the source type params
          targetType,             // Type reference to the type of the target port
          targetTypeFormalParams, // Formal parameters occurring in the target type
          targetTypeArgumentTypes)// The actual types of the target type params
          ) {
        String realSourceType
            = determineRealType(sourceType, sourceTypeFormalParams, sourceTypeArgumentTypes);
        String realTargetType
            = determineRealType(targetType, targetTypeFormalParams, targetTypeArgumentTypes);
        Log.error(
            String.format("0xMA033 Source type '%s' and target type '%s' of " +
                              "connector %s->%s do not match.",
                realSourceType, realTargetType,
                connector.getSource(), connector.getTarget()),
            connector.getAstNode().get().get_SourcePositionStart());
      }
    }
  }

  /**
   * Prints the real type of the given type reference. This means that all type
   * parameters are replaced by their real type arguments, which are given in
   * realTypeArguments.
   * @param type The type to print
   * @param formalTypeParameters the formal type parameters of the type to print
   * @param realTypeArguments The actual type arguments of the type parameters
   * @return The printed type with replaced type parameters.
   */
  private String determineRealType(
      JTypeReference<? extends JTypeSymbol> type,
      List<JTypeSymbol> formalTypeParameters,
      List<JTypeReference<? extends JTypeSymbol>> realTypeArguments) {

    if(type.getReferencedSymbol().isFormalTypeParameter()){
      // First case: The type is just a type parameter
      // Thus, return the printed real type argument of this type parameter
      String paramName = type.getReferencedSymbol().getName();
      final int typeParamIndex
          = formalTypeParameters.stream()
                .map(Symbol::getName)
                .collect(Collectors.toList())
                .indexOf(paramName);
      return SymbolPrinter.printTypeWithFormalTypeParameters(
          realTypeArguments.get(typeParamIndex).getReferencedSymbol());
    }

    // The type is not a type parameter.
    // Therefore it has a type and possibly type arguments.
    // Print the type and print
    String result = type.getReferencedSymbol().getName();
    // Print the type arguments with replaced type parameters
    if(type.getActualTypeArguments().size() > 0) {
      result += "<";
      final String args = type.getActualTypeArguments().stream()
                              .map(arg -> determineRealType(
                                  (JTypeReference<? extends JTypeSymbol>) arg.getType(),
                                  formalTypeParameters,
                                  realTypeArguments))
                              .collect(Collectors.joining(","));
      result += args + ">";
    }
    return result;

  }

  private List<ActualTypeArgument> getInitialTypeArguments(
      ComponentSymbol compSym,
      ASTQualifiedName endpointNameFQ) {
    final List<ActualTypeArgument> initialTypeArgs = new ArrayList<>();

    if(endpointNameFQ.getPartList().size() > 1) {
      // The source port is defined in the a subcomponent (or a parent thereof)
      // of the component defining the connector

      // Get the formal type arguments of the subcomponent
      final String subcomponentName = endpointNameFQ.getPart(0);
      final Optional<ComponentInstanceSymbol> subComponentOpt
          = compSym.getSubComponent(subcomponentName);
      if (subComponentOpt.isPresent()) {
        final ComponentSymbolReference subcompTypeRef
            = subComponentOpt.get().getComponentType();
        initialTypeArgs.addAll(subcompTypeRef.getActualTypeArguments());
      }

    } else if (endpointNameFQ.getPartList().size() == 1) {
      // The source port is defined in the same component as the connector
      // or inherited from a super component
      for (JTypeSymbol typeSymbol : compSym.getFormalTypeParameters()) {
        if (!typeSymbol.getInterfaces().isEmpty()){
          final ActualTypeArgument typeArgument
              = new ActualTypeArgument(false, true, typeSymbol.getInterfaces().get(0));
          initialTypeArgs.add(typeArgument);
        } else {
          final JavaTypeSymbolReference typeParamTypeRef
              = new JavaTypeSymbolReference(
              typeSymbol.getName(), compSym.getSpannedScope(), 0);
          final ActualTypeArgument typeArgument
              = new ActualTypeArgument(false, false, typeParamTypeRef);
          initialTypeArgs.add(typeArgument);
        }
      }
    }
    return initialTypeArgs;
  }

  /**
   * Determine the type parameters of the component defining the given port
   * and the 'real' type arguments of those type parameters.
   *
   * Starting in the startingComponent, the initial type arguments are mapped
   * to the type parameters of possible super components until the component
   * that actually defines the port is reached.
   *
   * <br/><br/>
   * Example 1: <br/>
   * {@code
   * component A<T> {
   *   port in T inT;
   * }
   * } <br/>
   * The call to this function with the component symbol of {@code A}, the port
   * symbol of inT, and initial type arguments ['String'] returns the pair
   * {@code ([T], ['String'])}, where the actual type of T is 'String'.
   *
   * <br/><br/>
   * Example 2: <br/>
   * {@code
   * component A<T> {
   *   port in T inT;
   * }} <br/>
   * {@code
   * component B<K,E> extends A<E> {}
   * } <br/>
   * The call to this function with the component symbol of {@code B}, the port
   * symbol of inT, and initial type arguments ['Integer','String'] returns the pair
   * {@code ([T], ['String'])}, where the actual type of T is 'String'.
   *
   * @param startingComponent The component that contains the port, but not
   *                          necessarily defines it. It is the starting point
   *                          for a search in the inheritance hierarchy.
   * @param portSymbol The port to search the defining components type
   *                   parameters and arguments for.
   * @param startingTypeArgs The list of type arguments of the startingComponent.
   *                         This should be empty if the starting component
   *                         defines the connector in which the port is used.
   * @return A pair consisting of
   * 1. A list of the type parameters of the defining component and
   * 2. The real type arguments for the defining component.
   */
  private Pair<List<? extends JTypeSymbol>, List<ActualTypeArgument>>
  getTypeParamsAndRealTypeArgsForDefiningComponent(
      ComponentSymbol startingComponent,
      PortSymbol portSymbol,
      ASTQualifiedName endpointName,
      List<ActualTypeArgument> startingTypeArgs) {

    List<JTypeSymbol> resultTypeParameters = new ArrayList<>();

    // Maps the names of the type parameters to the actual arguments
    Map<String, ActualTypeArgument> realTypeArguments = new HashMap<>();

    // Initialization
    ComponentSymbol currentComponent;
    if(endpointName.sizeParts() == 1) {
      currentComponent = startingComponent;
    } else {
      currentComponent = startingComponent.getSubComponent(endpointName.getPart(0)).get().getComponentType();
    }

    // The list of current real type arguments
    for (JTypeSymbol typeParam : currentComponent.getFormalTypeParameters()) {
      final int index = currentComponent.getFormalTypeParameters().indexOf(typeParam);
      realTypeArguments.put(typeParam.getName(), startingTypeArgs.get(index));
    }

    // Find the component that defines the port
    while(!currentComponent.getPort(portSymbol.getName(), false).isPresent()
           && currentComponent.getSuperComponent().isPresent()){

      Map<String, ActualTypeArgument> superCompTypeArgs = new HashMap<>();
      final ComponentSymbolReference superCompRef =
          currentComponent.getSuperComponent().get();
      final ComponentSymbol superCompSymbol =
          superCompRef.getReferencedSymbol();

      currentComponent = currentComponent.getSuperComponent().get();

      // Fill the map for the super component
      for (JTypeSymbol typeSymbol : superCompSymbol.getFormalTypeParameters()) {
        final int typeParamIndex
            = superCompSymbol.getFormalTypeParameters().indexOf(typeSymbol);
        final ActualTypeArgument actualTypeArg =
            superCompRef.getActualTypeArguments().get(typeParamIndex);

        // The real type argument of the formal type parameter is the type argument
        // where possible formal type parameters from the previous iteration
        // have been replaced by their real values.
        ActualTypeArgument realTypeArg
            = replaceTypeParamByRealArgument(actualTypeArg, realTypeArguments);
        superCompTypeArgs.put(typeSymbol.getName(), realTypeArg);
      }
      realTypeArguments = superCompTypeArgs;
    }

    // Here, the currentComponent is the component that actually defines the
    // port. Furthermore, the list of type arguments and the real type arguments
    // are those of the defining component

    if(!currentComponent.getPort(portSymbol.getName(), false).isPresent()) {
      // TODO ERROR: The port is not defined in the current component and there is no super component it could be inherited from
      return new Pair<>(new ArrayList<>(), new ArrayList<>());
    }

    // Determine the type parameters of the defining component
    resultTypeParameters = currentComponent.getFormalTypeParameters();
    List<ActualTypeArgument> resultTypeArguments = new ArrayList<>();
    for (JTypeSymbol resultTypeParameter : resultTypeParameters) {
      resultTypeArguments.add(realTypeArguments.get(resultTypeParameter.getName()));
    }
    return new Pair<>(resultTypeParameters, resultTypeArguments);
  }

  /**
   * Construct a new ActualTypeArgument such that all occurrences of formal type
   * parameters in the given actualTypeArg are replaced by their real values.
   *
   * @param actualTypeArg The type argument to check
   * @param realTypeArguments The real values for possibly occurring type parameters.
   * @return The type argument where type parameters have been replaced by their
   * actual values.
   */
  private ActualTypeArgument replaceTypeParamByRealArgument(
      ActualTypeArgument actualTypeArg,
      Map<String, ActualTypeArgument> realTypeArguments) {

    final JTypeReference<? extends JTypeSymbol> type
        = (JTypeReference<? extends JTypeSymbol>) actualTypeArg.getType();

    if(type.getReferencedSymbol().isFormalTypeParameter()){
      return realTypeArguments.get(type.getName());
    }

    List<ActualTypeArgument> newInnerTypeArguments = new ArrayList<>();
    for (ActualTypeArgument innerTypeArg : type.getActualTypeArguments()) {
      newInnerTypeArguments.add(replaceTypeParamByRealArgument(innerTypeArg, realTypeArguments));
    }

    final JTypeReference<? extends JTypeSymbol> newTypeRef
        = new JavaTypeSymbolReference(type.getName(), type.getEnclosingScope(), 0);
    newTypeRef.setActualTypeArguments(newInnerTypeArguments);
    return new ActualTypeArgument(false, false, newTypeRef);
  }


}
