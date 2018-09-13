/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.helper;

//XXX: https://git.rwth-aachen.de/montiarc/core/issues/45

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;

import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.java.types.JavaDSLHelper;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;

/**
 * Checks type compatibility of {@link JTypeReference}s.
 *
 * @author ahaber, Robert Heim
 */
public class TypeCompatibilityChecker {
  
  private static BiMap<String, String> primitiveToWrappers = HashBiMap
      .create();
  
  static {
    primitiveToWrappers.put("int", "Integer");
    primitiveToWrappers.put("double", "Double");
    primitiveToWrappers.put("boolean", "Boolean");
    primitiveToWrappers.put("byte", "Byte");
    primitiveToWrappers.put("char", "Character");
    primitiveToWrappers.put("long", "Long");
    primitiveToWrappers.put("float", "Float");
    primitiveToWrappers.put("short", "Short");
  }
  
  public static int getPositionInFormalTypeParameters(List<JTypeSymbol> formalTypeParameters,
      JTypeReference<? extends JTypeSymbol> searchedFormalTypeParameter) {
    int positionInFormal = 0;
    for (JTypeSymbol formalTypeParameter : formalTypeParameters) {
      if (formalTypeParameter.getName().equals(searchedFormalTypeParameter.getName())) {
        return positionInFormal;
      }
      positionInFormal++;
    }
    return -1;
  }
  
  /**
   * Checks compatibility of {@link JTypeReference}s. The
   * sourceTypeFomalTypeParameters list all type parameters, while the
   * sourceTypeArguments define the current binding of them. E.g., a generic
   * source Type {@code A<X, Y>} could be bound to
   * <code>{@code A<List<Optional<Integer>>, String>}</code>. For a targetType
   * to match, it must recursively match all generic bindings. In the example,
   * the first recursion would check that formal type-parameters of {@code List}
   * are bound to the same type argument (here {@code Optional}) for both, the
   * source and the target type. The second recursion would check
   * {@code Optional}'s type arguments to be {@code Integer}. Then, the the
   * other type-arguments of {@code A} (here {@code Y}) are checked.
   *
   * @param sourceType
   * @param sourceTypeFormalTypeParameters
   * @param sourceTypeArguments
   * @param targetType
   * @param targetTypeFormalTypeParameters
   * @param targetTypeArguments
   * @return
   */
  public static boolean doTypesMatch(JTypeReference<? extends JTypeSymbol> sourceType,
      List<JTypeSymbol> sourceTypeFormalTypeParameters,
      List<JTypeReference<? extends JTypeSymbol>> sourceTypeArguments,
      JTypeReference<? extends JTypeSymbol> targetType,
      List<JTypeSymbol> targetTypeFormalTypeParameters,
      List<JTypeReference<? extends JTypeSymbol>> targetTypeArguments) {
    
    checkNotNull(sourceType);
    checkNotNull(targetType);
    
    boolean result = false;
    if (sourceType.getReferencedSymbol().isFormalTypeParameter()) {
      // bind the generic to the actual type
      int positionInFormal = getPositionInFormalTypeParameters(sourceTypeFormalTypeParameters,
          sourceType);
      if (!sourceTypeArguments.isEmpty() && positionInFormal!=-1) {
        sourceType = sourceTypeArguments.get(positionInFormal);
      }
    }
    if (targetType.getReferencedSymbol().isFormalTypeParameter()) {
      // bind the generic to the actual type
      int positionInFormal = getPositionInFormalTypeParameters(targetTypeFormalTypeParameters,
          targetType);
      if (!targetTypeArguments.isEmpty()&& positionInFormal!=-1) {
        targetType = targetTypeArguments.get(positionInFormal);
      }
    }
    
    if (sourceType.getReferencedSymbol().getFullName()
        .equals(targetType.getReferencedSymbol().getFullName()) &&
        sourceType.getDimension() == targetType.getDimension() &&
        sourceType.getActualTypeArguments().size() == targetType.getActualTypeArguments().size()) {
      result = true;
      // type without generics does match, now we must check that the type
      // arguments match
      List<ActualTypeArgument> sourceParams = sourceType.getActualTypeArguments();
      List<ActualTypeArgument> targetParams = targetType.getActualTypeArguments();
      for (int i = 0; i < sourceParams.size(); i++) {
        JTypeReference<? extends JTypeSymbol> sourceTypesCurrentTypeArgument = (JavaTypeSymbolReference) sourceParams
            .get(i)
            .getType();
        JTypeReference<? extends JTypeSymbol> targetTypesCurrentTypeArgument = (JavaTypeSymbolReference) targetParams
            .get(i)
            .getType();
        
        // // This is the case when we resolved a type which has no actual type
        // // arguments set. E.g. when we resolve the type List<K>, the actual
        // type
        // // argument is not set here. We then reuse the passed actual type
        // // arguments for further processing.
        if (sourceTypesCurrentTypeArgument.getReferencedSymbol().isFormalTypeParameter() && sourceTypeArguments.size() > i) {
          sourceTypesCurrentTypeArgument = sourceTypeArguments.get(i);
        }
        if (targetTypesCurrentTypeArgument.getReferencedSymbol().isFormalTypeParameter() && targetTypeArguments.size() > i) {
          targetTypesCurrentTypeArgument = targetTypeArguments.get(i);
        }
        
        List<JTypeSymbol> nextToCheckSourceTypeFormalTypeParameters = new ArrayList<>();
        List<JTypeReference<? extends JTypeSymbol>> nextToCheckSourceTypeArguments = new ArrayList<>();
        
        List<JTypeSymbol> nextToCheckTargetTypeFormalTypeParameters = new ArrayList<>();
        
        List<JTypeReference<? extends JTypeSymbol>> nextToCheckTargetTypeArguments = new ArrayList<>();
        
        // 1. source type has nested generics (e.g. List<T> -> List<String> T ->
        // String)) -> keep the old binded source type arguments and formal type
        // arguments as well
        if (hasNestedGenerics(sourceTypesCurrentTypeArgument)
            && !hasNestedGenerics(targetTypesCurrentTypeArgument)) {
          nextToCheckSourceTypeArguments = sourceTypeArguments;
          nextToCheckTargetTypeArguments = targetTypesCurrentTypeArgument
              .getActualTypeArguments().stream()
              .map(a -> (JTypeReference<?>) a.getType()).collect(Collectors.toList());
          
          nextToCheckSourceTypeFormalTypeParameters = sourceTypeFormalTypeParameters;
          nextToCheckTargetTypeFormalTypeParameters = targetTypesCurrentTypeArgument
              .getReferencedSymbol().getFormalTypeParameters()
              .stream()
              .map(p -> (JTypeSymbol) p).collect(Collectors.toList());
        }
        // 2. target type has nested generics (e.g. List<String> -> List<T> T ->
        // String)-> keep the old binded source type arguments and formal type
        // arguments as well
        else if (!hasNestedGenerics(sourceTypesCurrentTypeArgument)
            && hasNestedGenerics(targetTypesCurrentTypeArgument)) {
          nextToCheckTargetTypeArguments = targetTypeArguments;
          nextToCheckSourceTypeArguments = sourceTypesCurrentTypeArgument
              .getActualTypeArguments().stream()
              .map(a -> (JTypeReference<?>) a.getType()).collect(Collectors.toList());
          
          nextToCheckTargetTypeFormalTypeParameters = targetTypeFormalTypeParameters;
          nextToCheckSourceTypeFormalTypeParameters = sourceTypesCurrentTypeArgument
              .getReferencedSymbol().getFormalTypeParameters()
              .stream()
              .map(p -> (JTypeSymbol) p).collect(Collectors.toList());
        }
        
        // 3. source and target type have nested generics (e.g. List<T> ->
        // List<T> T -> String)-> keep the old binded source type arguments and
        // formal type arguments as well
        else if (hasNestedGenerics(sourceTypesCurrentTypeArgument)
            && hasNestedGenerics(targetTypesCurrentTypeArgument)) {
          nextToCheckSourceTypeArguments = sourceTypeArguments;
          nextToCheckTargetTypeArguments = targetTypeArguments;
          nextToCheckSourceTypeFormalTypeParameters = sourceTypeFormalTypeParameters;
          nextToCheckTargetTypeFormalTypeParameters = targetTypeFormalTypeParameters;
        }
        
        // 4. source and target type have no nested generics (e.g List<String>
        // -> List<String>)
        else if (!hasNestedGenerics(sourceTypesCurrentTypeArgument)
            && !hasNestedGenerics(targetTypesCurrentTypeArgument)) {
          nextToCheckSourceTypeArguments = sourceTypesCurrentTypeArgument
              .getActualTypeArguments().stream()
              .map(a -> (JTypeReference<?>) a.getType()).collect(Collectors.toList());
          
          nextToCheckTargetTypeArguments = targetTypesCurrentTypeArgument
              .getActualTypeArguments().stream()
              .map(a -> (JTypeReference<?>) a.getType()).collect(Collectors.toList());
          
          nextToCheckTargetTypeFormalTypeParameters = targetTypesCurrentTypeArgument
              .getReferencedSymbol().getFormalTypeParameters()
              .stream()
              .map(p -> (JTypeSymbol) p).collect(Collectors.toList());
          nextToCheckSourceTypeFormalTypeParameters = sourceTypesCurrentTypeArgument
              .getReferencedSymbol().getFormalTypeParameters()
              .stream()
              .map(p -> (JTypeSymbol) p).collect(Collectors.toList());
          
        }
        
        if (!doTypesMatch(sourceTypesCurrentTypeArgument,
            nextToCheckSourceTypeFormalTypeParameters,
            nextToCheckSourceTypeArguments,
            targetTypesCurrentTypeArgument, nextToCheckTargetTypeFormalTypeParameters,
            nextToCheckTargetTypeArguments)) {
          result = false;
          
          break;
        }
      }
    }
    else if (!sourceType.getReferencedSymbol().getFullName()
        .equals(targetType.getReferencedSymbol().getFullName())) {
      
      // Compare the names of generic component parameters without comparing
      // full name, as referenced Symbol is component.
      if (sourceType.getReferencedSymbol().isFormalTypeParameter()
          && targetType.getReferencedSymbol().isFormalTypeParameter()) {
        if (sourceType.getName().equals(targetType.getName())) {
          result = true;
        }
      }
      
      // Check if source and target type are primitive type or wrapper of
      // primitive type
      String sourceTypeName = sourceType.getReferencedSymbol().getName();
      String targetTypeName = targetType.getReferencedSymbol().getName();
      if (primitiveToWrappers.containsKey(sourceTypeName)) {
        if (primitiveToWrappers.inverse().containsKey(targetTypeName)) {
          if (primitiveToWrappers.get(sourceTypeName)
              .equals(targetTypeName)) {
            result = true;
          }
        }
      }
      else if (primitiveToWrappers.containsKey(targetTypeName)) {
        if (primitiveToWrappers.inverse().containsKey(sourceTypeName)) {
          if (primitiveToWrappers.get(targetTypeName)
              .equals(sourceTypeName)) {
            result = true;
          }
        }
      }
      
      // check, if superclass from sourceType is compatible with targetType
      if (!result && sourceType.getReferencedSymbol().getSuperClass().isPresent()) {
        JTypeReference<? extends JTypeSymbol> parent = sourceType.getReferencedSymbol()
            .getSuperClass().get();
        result = doTypesMatch(parent,
            parent.getReferencedSymbol().getFormalTypeParameters().stream()
                .map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
            sourceTypeArguments,
            targetType,
            targetTypeFormalTypeParameters,
            targetTypeArguments);
      }
      
      // check, if interface from sourceType is compatible with targetType
      if (!result && !sourceType.getReferencedSymbol().getInterfaces().isEmpty()) {
        
        for (JTypeReference<? extends JTypeSymbol> interf : sourceType.getReferencedSymbol()
            .getInterfaces()) {
          List<JTypeReference<?>> interfacesActualArgs = sourceTypeArguments;
          if (!interf.getActualTypeArguments().isEmpty() && !hasNestedGenerics(interf)) {
            interfacesActualArgs = interf.getActualTypeArguments().stream()
                .map(a -> (JTypeReference<?>) a.getType()).collect(Collectors.toList());
          }
          
          result = doTypesMatch(
              interf,
              interf.getReferencedSymbol().getFormalTypeParameters().stream()
                  .map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
              interfacesActualArgs,
              targetType,
              targetTypeFormalTypeParameters,
              targetTypeArguments);
          if (result) {
            break;
          }
        }
      }
    }
    return result;
    
  }
  
  /**
   * Resolves the type of the given java expression. If it is not possible to
   * resolve the type, return {@link Optional#empty()}.
   * 
   * @param expr the java expression
   * @return
   */
  public static Optional<? extends JavaTypeSymbolReference> getExpressionType(ASTExpression expr) {
    // TODO Don't use HCJavaDSLTypeResolver here because we want to resolve
    // JTypes instead of JavaTypes. Because HCJavaDSLTypeResolver is implemented
    // in JavaDSL, additional adapter may required e.g. CD2Java to use CD types
    // within Java expressions.
    Log.debug("Resolve type of java expression.", "TypeCompatibilityChecker");
    MontiArcHCJavaDSLTypeResolver typeResolver = new MontiArcHCJavaDSLTypeResolver();
    expr.accept(typeResolver);
    if (!typeResolver.getResult().isPresent()) {
      Log.info("Can't resolve type of expression: " + expr, "TypeCompatibilityChecker");
    }
    return typeResolver.getResult();
  }
  
  /**
   * Checks whether there exists a assignment conversion from the expression
   * type to <tt>target</tt> type.
   * 
   * @param from
   * @param target
   * @return
   */
  public static boolean doTypesMatch(ASTExpression expr,
      JTypeReference<? extends JTypeSymbol> targetType) {
    Optional<? extends JavaTypeSymbolReference> exprType = getExpressionType(expr);
    if (!exprType.isPresent()) {
      return false;
    }
    return doTypesMatch(exprType.get(),
        exprType.get().getReferencedSymbol().getFormalTypeParameters().stream()
            .map(p -> ((JTypeSymbol) p)).collect(Collectors.toList()),
        exprType.get().getActualTypeArguments().stream()
            .map(a -> (JTypeReference<?>) a.getType()).collect(Collectors.toList()),
        targetType, targetType.getReferencedSymbol().getFormalTypeParameters().stream()
            .map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
        targetType.getActualTypeArguments().stream()
            .map(a -> (JTypeReference<?>) a.getType()).collect(Collectors.toList()));
  }
  
  public static boolean hasNestedGenerics(JTypeReference<? extends JTypeSymbol> type) {
    boolean result = false;
    if (type.getReferencedSymbol().isFormalTypeParameter()) {
      result = true;
    }
    for (ActualTypeArgument arg : type.getActualTypeArguments()) {
      result = hasNestedGenerics((JTypeReference<?>) arg.getType());
      if (result) {
        break;
      }
    }
    
    return result;
  }
  
}
