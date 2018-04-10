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

import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.java.symboltable.JavaMethodSymbol;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.java.types.HCJavaDSLTypeResolver;
import de.monticore.java.types.JavaDSLHelper;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.symboltable.Scope;

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
    Scope scope = node.getEnclosingScope().get();
    String typeSymbolName = JavaDSLHelper.getEnclosingTypeSymbolName(scope);
    if (null == typeSymbolName) {
      typeSymbolName = name;
    }
    Optional<JavaTypeSymbol> typeSymbol = typeSymbol = scope
        .<JavaTypeSymbol> resolve(typeSymbolName, JavaTypeSymbol.KIND);
    JavaTypeSymbolReference expType = new JavaTypeSymbolReference(typeSymbolName, scope, 0);
    
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
    Optional<JavaFieldSymbol> resolvedFields = JavaDSLHelper.resolveFieldInSuperType(scope, name);
    if (resolvedFields.isPresent()) {
      JavaTypeSymbolReference fieldType = resolvedFields.get().getType();
      String completeTypeName = JavaDSLHelper.getCompleteName(fieldType);
      JavaTypeSymbolReference newType = new JavaTypeSymbolReference(completeTypeName,
          fieldType.getEnclosingScope(), fieldType.getDimension());
      newType.setActualTypeArguments(fieldType.getActualTypeArguments());
      setResult(newType);
      return;
    }
    
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
    Collection<JavaTypeSymbol> resolvedTypes = scope.resolveMany(name, JavaTypeSymbol.KIND);
    if (resolvedTypes.size() == 1) {
      JavaTypeSymbol type = resolvedTypes.iterator().next();
      setResult(new JavaTypeSymbolReference(type.getFullName(),
          scope, 0));
      return;
    }
    
    // no symbol found for this expression. it could be a package name
    fullName = name;
  }
}
