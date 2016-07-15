/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.montiarc._symboltable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import de.monticore.java.symboltable.JavaSymbolFactory;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTComplexArrayType;
import de.monticore.types.types._ast.ASTComplexReferenceType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeArgument;
import de.monticore.types.types._ast.ASTTypeParameters;
import de.monticore.types.types._ast.ASTTypeVariableDeclaration;
import de.monticore.types.types._ast.ASTWildcardType;

/**
 * TODO This class should be removed by putting its methods in JavaDSL (or even MC/Types) project.
 *
 * @author Robert Heim
 */
public class JavaHelper {
  private final static JavaSymbolFactory jSymbolFactory = new JavaSymbolFactory();
  
  /**
   * Adds the TypeParameters to the JavaTypeSymbol if the class or interface declares TypeVariables.
   * Example:
   * <p>
   * class Bla<T, S extends SomeClass<T> & SomeInterface>
   * </p>
   * T and S are added to Bla.
   *
   * @param typeSymbol
   * @param optionalTypeParameters
   * @return JavaTypeSymbol list to be added to the scope
   */
  // TODO see JavaSymbolTableCreator.addTypeParameters(...),
  // see ComponentSymbol addFormalTypeParameters etc.
  protected static List<JTypeSymbol> addTypeParametersToType(
      ComponentSymbol typeSymbol,
      Optional<ASTTypeParameters> optionalTypeParameters, Scope currentScope) {
    if (optionalTypeParameters.isPresent()) {
      ASTTypeParameters astTypeParameters = optionalTypeParameters.get();
      for (ASTTypeVariableDeclaration astTypeParameter : astTypeParameters
          .getTypeVariableDeclarations()) {
        // new type parameter
        
        // TypeParameters/TypeVariables are seen as type declarations.
        // For each variable instantiate a JavaTypeSymbol.
        final String typeVariableName = astTypeParameter.getName();
        JavaTypeSymbol javaTypeVariableSymbol = jSymbolFactory.createTypeVariable(typeVariableName);
        // TODO implement
        // // init type parameter
        // if (astTypeParameter.getTypeBound().isPresent()) {
        // // Treat type bounds are implemented interfaces, even though the first
        // // bound might be a class. See also JLS7.
        // addInterfacesToType(javaTypeVariableSymbol, astTypeParameter.getTypeBound().get()
        // .getTypes());
        // }
        // Treat type bounds are implemented interfaces, even though the
        // first bound might be a class. See also JLS7.
        List<ASTType> types = new ArrayList<ASTType>(astTypeParameter.getUpperBounds());
        
        addInterfacesToType(javaTypeVariableSymbol, types, currentScope);
        
        // add type parameter
        typeSymbol.addFormalTypeParameter(javaTypeVariableSymbol);
      }
    }
    return typeSymbol.getFormalTypeParameters();
  }
  
  /**
   * Adds the given ASTTypes as interfaces to the JavaTypeSymbol. The JavaTypeSymbol can be a type
   * variable. Interfaces may follow after the first extended Type. We treat the first Type also as
   * interface even though it may be a class.
   * <p>
   * class Bla implements SomeInterface, AnotherInterface, ... <br>
   * class Bla&ltT extends SomeClassOrInterface & SomeInterface & ...&gt
   * </p>
   * See also JLS7.
   *
   * @param astInterfaceTypeList
   */
  // TODO this is implemented in JavaDSL, but reimplemented because of ArcTypeSymbol. This should
  // somehow be extracted and implemented only once
  protected static void addInterfacesToType(JavaTypeSymbol arcTypeSymbol,
      List<ASTType> astInterfaceTypeList, Scope currentScope) {
    for (ASTType astInterfaceType : astInterfaceTypeList) {
      JavaTypeSymbolReference javaInterfaceTypeSymbolReference = new JavaTypeSymbolReference(
          TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astInterfaceType), currentScope,
          0);
      List<ActualTypeArgument> actualTypeArguments = new ArrayList<>();
      
      // Add the ASTTypeArguments to astInterfaceType
      // Before we can do that we have to cast.
      if (astInterfaceType instanceof ASTSimpleReferenceType) {
        // TODO
        // addTypeParametersToType(javaInterfaceTypeSymbolReference, astInterfaceType);
      }
      else if (astInterfaceType instanceof ASTComplexReferenceType) {
        ASTComplexReferenceType astComplexReferenceType = (ASTComplexReferenceType) astInterfaceType;
        for (ASTSimpleReferenceType astSimpleReferenceType : astComplexReferenceType
            .getSimpleReferenceTypes()) {
          // TODO javaInterfaceTypeSymbolReference.getEnclosingScope().resolve("Boolean",
          // JTypeSymbol.KIND).get()
          // javaInterfaceTypeSymbolReference.getEnclosingScope().resolve("Boolean",
          // JTypeSymbol.KIND))
          if (astSimpleReferenceType.getTypeArguments().isPresent()) {
            for (ASTTypeArgument argument : astSimpleReferenceType.getTypeArguments().get()
                .getTypeArguments()) {
                
              if (!handleSimpleReferenceType(argument, 0, actualTypeArguments,
                  javaInterfaceTypeSymbolReference.getEnclosingScope(), false, false, null) &&
                  (argument instanceof ASTComplexArrayType)) {
                ASTComplexArrayType arrayArg = (ASTComplexArrayType) argument;
                ASTType cmpType = arrayArg.getComponentType();
                handleSimpleReferenceType(cmpType, arrayArg.getDimensions(),
                    actualTypeArguments, javaInterfaceTypeSymbolReference.getEnclosingScope(),
                    false, false, null);
              }
            }
            
          }
          
          arcTypeSymbol.addInterface(javaInterfaceTypeSymbolReference);
        }
      }
      javaInterfaceTypeSymbolReference.setActualTypeArguments(actualTypeArguments);
    }
  }
  
  /** if you have questions ask JP ;) */
  protected static boolean handleSimpleReferenceType(ASTTypeArgument argument,
      final int dim, final List<ActualTypeArgument> actualTypeArguments, final Scope symbolTable,
      final boolean isLowerBound, final boolean isUpperBound,
      @Nullable ActualTypeArgument typeArgument) {
    if (argument instanceof ASTSimpleReferenceType) {
      ASTSimpleReferenceType simpleArg = (ASTSimpleReferenceType) argument;
      String name = simpleArg.getNames().stream().collect(Collectors.joining("."));
      Optional<JTypeSymbol> symbol = symbolTable.resolve(name, JTypeSymbol.KIND);
      if (symbol.isPresent() && symbol.get().getEnclosingScope() != null) {
        if (typeArgument == null) {
          typeArgument = new ActualTypeArgument(isLowerBound, isUpperBound,
              new JavaTypeSymbolReference(
                  symbol.get().getName(),
                  symbol.get().getEnclosingScope(), dim));
          actualTypeArguments.add(typeArgument);
        }
        if (simpleArg.getTypeArguments().isPresent()) {
          List<ActualTypeArgument> actualTypeArguments2 = new ArrayList<>();
          for (ASTTypeArgument astTypeArgument : simpleArg.getTypeArguments().get()
              .getTypeArguments()) {
            handleSimpleReferenceType(astTypeArgument, 0, actualTypeArguments2, symbolTable, false,
                false, typeArgument);
          }
          typeArgument.getType().setActualTypeArguments(actualTypeArguments2);
        }
      }
      return true;
    }
    else if (argument instanceof ASTComplexReferenceType) {
      ASTComplexReferenceType complexArg = (ASTComplexReferenceType) argument;
      complexArg.getSimpleReferenceTypes().stream().forEachOrdered(t -> handleSimpleReferenceType(t,
          dim, actualTypeArguments, symbolTable, isLowerBound, isUpperBound, null));
      return true;
    }
    else if (argument instanceof ASTWildcardType) {
      ASTWildcardType wildArg = (ASTWildcardType) argument;
      if (wildArg.getLowerBound().isPresent()) {
        return handleSimpleReferenceType(wildArg.getLowerBound().get(), dim, actualTypeArguments,
            symbolTable, true, false, null);
      }
      else if (wildArg.getUpperBound().isPresent()) {
        return handleSimpleReferenceType(wildArg.getUpperBound().get(), dim, actualTypeArguments,
            symbolTable, false, true, null);
      }
    }
    else if (argument instanceof ASTComplexArrayType) {
      ASTComplexArrayType arrayArg = (ASTComplexArrayType) argument;
      ASTType cmpType = arrayArg.getComponentType();
      return handleSimpleReferenceType(cmpType, arrayArg.getDimensions(),
          actualTypeArguments, symbolTable, false, false, null);
    }
    return false;
  }
  
  // TODO this should be part of JavaDSL
  public static void addJavaDefaultTypes(GlobalScope globalScope) {
    // we add default types by putting mock implementations of java.lang and java.util in
    // src/main/resources/defaultTypes and adding it to the model path when creating the global
    // scope!
    // TODO This, however, should be done in JavaDSL and the MontiArcLanguage somehow...
  }
  
  // TODO this should be part of JavaDSL
  public static void addJavaPrimitiveTypes(GlobalScope globalScope) {
    globalScope.add(jSymbolFactory.createTypeVariable("boolean"));
    globalScope.add(jSymbolFactory.createTypeVariable("byte"));
    globalScope.add(jSymbolFactory.createTypeVariable("char"));
    globalScope.add(jSymbolFactory.createTypeVariable("double"));
    globalScope.add(jSymbolFactory.createTypeVariable("float"));
    globalScope.add(jSymbolFactory.createTypeVariable("int"));
    globalScope.add(jSymbolFactory.createTypeVariable("long"));
    globalScope.add(jSymbolFactory.createTypeVariable("short"));
  }
  
  /**
   * Adds the default imports of the java language to make default types resolvable without
   * qualification (e.g., "String" instead of "java.lang.String").
   *
   * @param imports
   */
  public static void addJavaDefaultImports(List<ImportStatement> imports) {
    imports.add(new ImportStatement("java.lang", true));
    imports.add(new ImportStatement("java.util", true));
  }
}
