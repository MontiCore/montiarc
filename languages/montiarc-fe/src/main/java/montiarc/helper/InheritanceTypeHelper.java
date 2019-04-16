package montiarc.helper;

import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.types.types._ast.ASTQualifiedName;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ComponentSymbolReference;
import montiarc._symboltable.PortSymbol;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper that is used to determine types in inheritance hierarchies
 */
public class InheritanceTypeHelper {
  /**
   * Prints the real type of the given type reference. This means that all type
   * parameters are replaced by their real type arguments, which are given in
   * realTypeArguments.
   * @param type The type to print
   * @param formalTypeParameters the formal type parameters of the type to print
   * @param realTypeArguments The actual type arguments of the type parameters
   * @return The printed type with replaced type parameters.
   */
  public static String determineRealType(
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

  public static List<ActualTypeArgument> getInitialTypeArguments(
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
  public static AbstractMap.Entry<List<? extends JTypeSymbol>, List<ActualTypeArgument>>
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
      return new AbstractMap.SimpleEntry<>(new ArrayList<>(), new ArrayList<>());
    }

    // Determine the type parameters of the defining component
    resultTypeParameters = currentComponent.getFormalTypeParameters();
    List<ActualTypeArgument> resultTypeArguments = new ArrayList<>();
    for (JTypeSymbol resultTypeParameter : resultTypeParameters) {
      resultTypeArguments.add(realTypeArguments.get(resultTypeParameter.getName()));
    }
    return new AbstractMap.SimpleEntry<>(resultTypeParameters, resultTypeArguments);
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
  private static ActualTypeArgument replaceTypeParamByRealArgument(
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
