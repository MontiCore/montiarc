/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.helper;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Strings;

import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.java.symboltable.JavaMethodSymbol;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.java.types.HCJavaDSLTypeResolver;
import de.monticore.java.types.JavaDSLHelper;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.mcexpressions._ast.ASTQualifiedNameExpression;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class MontiArcHCJavaDSLTypeResolver extends HCJavaDSLTypeResolver {
  
  @Override
  public void handle(ASTNameExpression node) {
    setResult(null);
    String name = node.getName();
    Scope scope = node.getEnclosingScopeOpt().get();
    String typeSymbolName = JavaDSLHelper.getEnclosingTypeSymbolName(scope); // is
                                                                             // null
                                                                             // as
                                                                             // there
                                                                             // is
                                                                             // no
                                                                             // enclosing
                                                                             // JAVA
                                                                             // type
                                                                             // symbol
    if (null == typeSymbolName) {
      typeSymbolName = name;
    }
    Optional<PortSymbol> portSymbol = scope.<PortSymbol> resolve(typeSymbolName, PortSymbol.KIND);
    Optional<VariableSymbol> varSymbol = scope.<VariableSymbol> resolve(typeSymbolName,
        VariableSymbol.KIND);
    Optional<JavaFieldSymbol> fieldSymbol = scope.<JavaFieldSymbol> resolve(typeSymbolName,
        JavaFieldSymbol.KIND);
    JavaTypeSymbolReference expType = new JavaTypeSymbolReference(typeSymbolName, scope, 0);
    
    if (portSymbol.isPresent()) {
      expType = (JavaTypeSymbolReference) portSymbol.get().getTypeReference();
    }
    else if (varSymbol.isPresent()) {
      expType = (JavaTypeSymbolReference) varSymbol.get().getTypeReference();
    }
    else if (fieldSymbol.isPresent()) {
      expType = (JavaTypeSymbolReference) fieldSymbol.get().getType();
    }
    Optional<JavaTypeSymbol> typeSymbol = Optional.ofNullable(expType.getReferencedSymbol());
    
    setResult(expType);
    
    if (!parameterStack.isEmpty() && parameterStack.peek() != null && isCallExpr
        && typeSymbol.isPresent()) {
      // try to resolve a method
      List<JavaTypeSymbolReference> paramTypes = parameterStack.peek();
      HashMap<JavaMethodSymbol, JavaTypeSymbolReference> resolvedMethods = JavaDSLHelper
          .resolveManyInSuperType(name, false, expType, typeSymbol.get(), null,
              paramTypes);
      
      if (resolvedMethods.size() == 1) {
        JavaMethodSymbol methodSymbol = resolvedMethods.entrySet().iterator().next().getKey();
        HashMap<String, JavaTypeSymbolReference> substituted = JavaDSLHelper
            .getSubstitutedTypes(typeSymbol.get(), expType);
        JavaTypeSymbolReference returnType = JavaDSLHelper.applyTypeSubstitution(substituted,
            methodSymbol.getReturnType());
        setResult(returnType);
        return;
      }
    }
    
    // try to resolve a local variable or a field (in super type)
    // Optional<JavaFieldSymbol> resolvedFields =
    // JavaDSLHelper.resolveFieldInSuperType(scope, name);
    // if (resolvedFields.isPresent()) {
    // JavaTypeSymbolReference fieldType = resolvedFields.get().getType();
    // String completeTypeName = JavaDSLHelper.getCompleteName(fieldType);
    // JavaTypeSymbolReference newType = new
    // JavaTypeSymbolReference(completeTypeName,
    // fieldType.getEnclosingScope(), fieldType.getDimension());
    // newType.setActualTypeArguments(fieldType.getActualTypeArguments());
    // setResult(newType);
    // return;
    // }
    
    // try to resolve a local constant
    if (typeSymbol.isPresent()) {
      Collection<JavaTypeSymbol> resolvedConstants = typeSymbol.get().getSpannedScope()
          .resolveDownMany(name, JavaTypeSymbol.KIND);
      if (resolvedConstants.size() == 1) {
        JavaTypeSymbol type = resolvedConstants.iterator().next();
        setResult(new JavaTypeSymbolReference(type.getFullName(),
            scope, 0));
        return;
      }
    }
    
    // try to resolve a type
    if (!getResult().isPresent() && Character.isUpperCase(name.charAt(0))) {
      Collection<JavaTypeSymbol> resolvedTypes = scope.resolveMany(name, JavaTypeSymbol.KIND);
      if (resolvedTypes.size() == 1) {
        JavaTypeSymbol type = resolvedTypes.iterator().next();
        setResult(new JavaTypeSymbolReference(type.getFullName(),
            scope, 0));
        return;
      }
    }
    
    // no symbol found for this expression. it could be a package name
    fullName = name;
  }
  
  @Override
  public void handle(ASTQualifiedNameExpression node) {
    if (!isCallExpr) {
      // push dummy element, otherwise we would resolve a method while searching
      // for a field
      parameterStack.push(null);
    }
    isCallExpr = false;
    handle(node.getExpression());
    String name = node.getName();
    Scope scope = node.getEnclosingScopeOpt().get();
    if (!getResult().isPresent()) {
      // expression could be a package name. try to resolve the fullname
      fullName += "." + name;
      Collection<JavaTypeSymbol> resolvedTypes = scope.resolveMany(fullName, JavaTypeSymbol.KIND);
      if (resolvedTypes.size() == 1) {
        JavaTypeSymbol typeSymbol = resolvedTypes.iterator().next();
        setResult(new JavaTypeSymbolReference(typeSymbol.getFullName(), scope, 0));
      }
      // the result is either empty, because name is a package name again, or we
      // found an acutal symbol
      parameterStack.pop();
      return;
    }
    JavaTypeSymbolReference expType = getResult().get();
    setResult(null);
    
    if (expType.getDimension() > 0 && "length".equals(name)) {
      setResult(new JavaTypeSymbolReference("int", expType.getEnclosingScope(), 0));
      parameterStack.pop();
      return;
    }
    
    Optional<JavaTypeSymbol> typeSymbolOpt = scope.<JavaTypeSymbol> resolve(expType.getName(),
        JavaTypeSymbol.KIND);
    
    if (typeSymbolOpt.isPresent()) {
      JavaTypeSymbol typeSymbol = typeSymbolOpt.get();
      if (!parameterStack.isEmpty() && parameterStack.peek() != null) {
        // try to resolve a method
        List<JavaTypeSymbolReference> paramTypes = parameterStack.peek();
        HashMap<JavaMethodSymbol, JavaTypeSymbolReference> resolvedMethods = JavaDSLHelper
            .resolveManyInSuperType(name, false, expType, typeSymbol, null,
                paramTypes);
        
        if (resolvedMethods.size() == 1) {
          JavaMethodSymbol methodSymbol = resolvedMethods.entrySet().iterator().next().getKey();
          HashMap<String, JavaTypeSymbolReference> substituted = JavaDSLHelper
              .getSubstitutedTypes(typeSymbol, expType);
          JavaTypeSymbolReference returnType = JavaDSLHelper.applyTypeSubstitution(substituted,
              methodSymbol.getReturnType());
          setResult(returnType);
        }
        return;
      }
      
      // try to resolve a local field
      Collection<JavaFieldSymbol> resolvedFields = typeSymbol.getSpannedScope()
          .resolveDownMany(name, JavaFieldSymbol.KIND);
      if (resolvedFields.size() == 1) {
        JavaTypeSymbolReference fieldType = resolvedFields.iterator().next().getType();
        String completeTypeName = JavaDSLHelper.getCompleteName(fieldType);
        fieldType = new JavaTypeSymbolReference(completeTypeName, fieldType.getEnclosingScope(),
            fieldType.getDimension());
        fieldType.setActualTypeArguments(fieldType.getActualTypeArguments());
        setResult(fieldType);
        parameterStack.pop();
        return;
      }
      
      // try to resolve a local constant
      Collection<JavaTypeSymbol> resolvedConstants = typeSymbol.getSpannedScope()
          .resolveDownMany(name, JavaTypeSymbol.KIND);
      if (resolvedConstants.size() == 1) {
        JavaTypeSymbol type = resolvedConstants.iterator().next();
        setResult(new JavaTypeSymbolReference(type.getFullName(),
            scope, 0));
        parameterStack.pop();
        return;
      }
      
      // try to resolve a type
      Collection<JavaTypeSymbol> resolvedTypes = scope.resolveMany(name, JavaTypeSymbol.KIND);
      if (resolvedTypes.size() == 1) {
        JavaTypeSymbol type = resolvedTypes.iterator().next();
        setResult(new JavaTypeSymbolReference(type.getFullName(),
            scope, 0));
        parameterStack.pop();
        return;
      }
    }
  }
  
}
