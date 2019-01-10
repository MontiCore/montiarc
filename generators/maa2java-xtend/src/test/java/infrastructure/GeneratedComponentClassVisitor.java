/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package infrastructure;

import com.google.common.collect.Sets;
import de.montiarcautomaton.generator.helper.ComponentHelper;
import de.monticore.java.javadsl._ast.*;
import de.monticore.java.javadsl._visitor.JavaDSLVisitor;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.types.types._ast.*;
import de.se_rwth.commons.logging.Log;
import infrastructure.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;

/**
 * Visits the AST of Java classes and checks whether they are fulfilling the specifications.
 *
 * @author (last commit) Michael Mutert
 */
public class GeneratedComponentClassVisitor implements JavaDSLVisitor {

  private final String className;

  private Set<String> imports;
  private Set<Field> fields;
  private Set<Method> methods;
  private Set<String> interfaces;
  private Set<EnumType> enumTypes;
  private String superClass;
  private List<ActualTypeArgument> superClassTypeArgs;

  public Set<Constructor> getConstructors() {
    return constructors;
  }

  Set<Constructor> constructors;

  public GeneratedComponentClassVisitor(String className){
    this.className = className;
    this.imports = new HashSet<>();
    this.fields = new HashSet<>();
    this.methods = new HashSet<>();
    this.enumTypes = new HashSet<>();
    this.constructors = new HashSet<>();
    this.interfaces = new HashSet<>();
  }

  @Override
  public void visit(ASTPackageDeclaration node) {

  }

  @Override
  public void visit(ASTMethodDeclaration node){
    String methodString = printWithoutWhitespace(node.getMethodBody());

    final ASTMethodSignature signature = node.getMethodSignature();
    final String methodName = signature.getName();
    final Optional<Method> method = getMethod(methodName);
    if(method.isPresent() && method.get().getBodyElements().size() > 0) {
      int lastIndex = 0;
      for (String s : method.get().getBodyElements()) {
        if (!methodString.contains(s)) {
          Log.error("Missing statement in method " + methodName +
                        " of class " + className + ": " + s);
        } else {
          int foundIndex = lastIndex + methodString.substring(lastIndex).indexOf(s);
          if(lastIndex >= foundIndex){
            Log.error(String.format("Body element %s of method %s was " +
                                        "found in the wrong order.",
                s, methodName));
          }
          lastIndex = foundIndex;
        }
      }
    }
    String printedParameters = printWithoutWhitespace(signature.getFormalParameters());
    String printedReturnType = printWithoutWhitespace(signature.getReturnType());
    final boolean removed = methods.removeIf(m -> methodName.equals(m.getName())
        && printedParameters.equals(printWithoutWhitespace(m.getParams()))
        && printedReturnType.endsWith(m.getReturnType().replace(" ", ""))
    );
    if(!removed){
      Log.error("Found unexpected method in " + this.className + ": " + methodName);
    }
  }

  private Optional<Method> getMethod(String name){
    for (Method method : methods) {
      if(method.getName().equals(name)){
        return Optional.of(method);
      }
    }
    return Optional.empty();
  }

  @Override
  public void visit(ASTClassDeclaration node) {
    for (ASTType type : node.getImplementedInterfaceList()) {
      if(type instanceof ASTSimpleReferenceType){
        final ASTSimpleReferenceType refType = (ASTSimpleReferenceType) type;
        String typeArgs = "";
        if(refType.getTypeArgumentsOpt().isPresent()){
          typeArgs = GeneratorTestConstants.PRINTER.prettyprint(refType.getTypeArguments());
        }
        String finalTypeArgs = typeArgs;
        boolean isComponentImplemented
            = refType.getNameList()
                  .stream().anyMatch(s -> interfaces.contains(s + finalTypeArgs));
        if(!isComponentImplemented){
          Log.error(String.format("Class %s does not implement interface %s", className,
              refType.getNameList()));
        }
      }
    }
    final Optional<ASTType> superClass = node.getSuperClassOpt();
    if(this.superClass != null && !superClass.isPresent()){
      // Case 1: There is an expected super class but no super class in the generated code
      Log.error(String.format("%s does not extend superclass %s",
          className, this.superClass));

    } else if(this.superClass == null && superClass.isPresent()){
      // Case 2: No expected super class but a super class in the code
      Log.error(String.format("%s unexpectedly extends a class %s",
          className, printWithoutWhitespace(superClass.get())));

    } else if(this.superClass != null){
      // Case 3: Expected super class and generated super class
      ComponentHelper.printTypeArguments(this.superClassTypeArgs);
      final ASTSimpleReferenceType superClassType = (ASTSimpleReferenceType) superClass.get();

      final String superClassFqn =
          superClassType.getNameList().stream().collect(Collectors.joining("."));
      if(!this.superClass.equals(superClassFqn)){
        // Case 3.1: Names of the expected and actual classes without type arguments
        //            do not match
        Log.error(String.format("The class %s extends the wrong class " +
                                    "%s instead of expected class %s",
            className, superClassFqn, this.superClass));

      } else if(!this.superClassTypeArgs.isEmpty() && !superClassType.getTypeArgumentsOpt().isPresent()) {
        // Case 3.2: Expected type arguments but found none
        Log.error(String.format("Expected type arguments '%s' in class '%s' " +
                                    "for super class '%s', but found none.",
            ComponentHelper.printTypeArguments(this.superClassTypeArgs),
            className,
            superClassFqn));

      } else if(this.superClassTypeArgs.isEmpty() && superClassType.getTypeArgumentsOpt().isPresent()) {
        // Case 3.3: Expected no type arguments, but there are generated type arguments
        Log.error(String.format("Expected no type arguments " +
                                    "for super class '%s' in class '%s', but found '%s'.",
            className,
            superClassFqn,
            GeneratorTestConstants.PRINTER.prettyprint(superClassType.getTypeArguments())));

      } else if(!this.superClassTypeArgs.isEmpty() && superClassType.getTypeArgumentsOpt().isPresent()){
        // Case 3.4: Expected type arguments and actual type arguments generated
        final List<ASTTypeArgument> actualSuperTypeArgList =
            superClassType.getTypeArguments().getTypeArgumentList();
        if(this.superClassTypeArgs.size() != actualSuperTypeArgList.size()){
          Log.error(String.format("Expected %d type arguments for super " +
                                      "class '%s' of class '%s', but found '%d'.",
              this.superClassTypeArgs.size(),
              this.superClass,
              this.className,
              actualSuperTypeArgList.size()));
        }
        // Check that the parameters match
        for (ActualTypeArgument superClassTypeArg : this.superClassTypeArgs) {
          int index = this.superClassTypeArgs.indexOf(superClassTypeArg);
          final ASTTypeArgument astTypeArgument = actualSuperTypeArgList.get(index);
          final String printedSuperTypeArg = printWithoutWhitespace(astTypeArgument);
          final String printedExpectedTypeArg = ComponentHelper.printTypeArgument(superClassTypeArg);
          if(!printedSuperTypeArg.contains(printedExpectedTypeArg)){
            Log.error(String.format("Actual type argument '%s' for super " +
                                        "class '%s' of class '%s' does not " +
                                        "match the expected type '%s'.",
                printedSuperTypeArg,
                this.superClass,
                this.className,
                printedExpectedTypeArg));
          }
        }
      }
    }
  }

  private String printWithoutWhitespace(ASTTypesNode node){
    return GeneratorTestConstants.PRINTER.prettyprint(node).replaceAll("\\s", "");
  }
  private String printWithoutWhitespace(ASTJavaDSLNode node){
    return GeneratorTestConstants.PRINTER.prettyprint(node).replaceAll("\\s", "");
  }

  @Override
  public void visit(ASTConstructorDeclaration node){
    final ASTFormalParameters actualParams = node.getFormalParameters();
    final String actualName = node.getName();

    boolean foundError = false;
    int paramSize = 0;
    if(actualParams.getFormalParameterListingOpt().isPresent()){
      paramSize = actualParams.getFormalParameterListing().getFormalParameterList().size();
    }
    final Optional<Constructor> constructorOptional
        = getConstructor(actualName, paramSize);
    if(constructorOptional.isPresent()){
      final Constructor constructor = constructorOptional.get();
      final boolean actualParamListPresent
          = actualParams.getFormalParameterListingOpt().isPresent();
      final boolean expectedParamListPresent
          = constructor.getParameters().getFormalParameterListingOpt().isPresent();

      if(actualParamListPresent && expectedParamListPresent){
        final ASTFormalParameterListing actualParamList
            = actualParams.getFormalParameterListing();
        final ASTFormalParameterListing expectedParamList
            = constructor.getParameters().getFormalParameterListing();

        final int actualParamListSize = actualParamList.getFormalParameterList().size();
        final int expectedParamListSize = expectedParamList.getFormalParameterList().size();
        if(actualParamListSize == expectedParamListSize){
          for (int index = 0; index < actualParamListSize; index++) {
            final String actualPrint
                = printWithoutWhitespace(actualParamList.getFormalParameter(index));
            final String expectedPrint
                = printWithoutWhitespace(expectedParamList.getFormalParameter(index));
            if(!actualPrint.contains(expectedPrint)){
              Log.error(String.format("Parameters of constructor %s do not " +
                                          "fit the expected parameters." +
                                          "Expected parameter: %s, " +
                                          "Actual parameter: %s",
                  actualName, expectedPrint, actualPrint));
              foundError = true;
            }
          }
        } else {
          Log.error(
              String.format("The number of expected parameters (%d) " +
                                "does not match the number of actual " +
                                "parameters (%d) of constructor '%s'",
                  expectedParamListSize, actualParamListSize, actualName));
        }
      } else if(actualParamListPresent || expectedParamListPresent){
        Log.error(String.format("Mismatch in FormalParameterListing " +
                                    "presence for constructor %s",
            actualName));
        foundError = true;
      }

      final String printedBody =
          printWithoutWhitespace(node.getConstructorBody());
      int lastIndex = 0;
      for (String bodyElement : constructor.getBodyElements()) {
        if(!printedBody.contains(bodyElement)) {
          Log.error(
              String.format("Missing element in constructor of class %s" +
                                ": %s",
                          className, bodyElement));
          foundError = true;
        }else {
          int foundIndex = printedBody.indexOf(bodyElement);
          if (lastIndex >= foundIndex) {
            Log.error(String.format("Body element %s of constructor " +
                                        "%s was " +
                                        "found in the wrong order.",
                bodyElement, className));
            foundError = true;
          }
          lastIndex = foundIndex;
        }
      }

      if(!foundError){
        constructors.remove(constructor);
      }
    } else {
      Log.error(String.format("Unexpected constructor in class %s. " +
                                  "Signature: %s%s",
          className,
          className,
          GeneratorTestConstants.PRINTER.prettyprint(node.getFormalParameters())));
    }
  }

  private Optional<Constructor> getConstructor(String name, int paramCount){
    return constructors
          .stream()
          .filter(constructor -> {
            if(!constructor.getParameters().isPresentFormalParameterListing()){
              return paramCount == 0;
            } else {
              return constructor.getParameters()
                         .getFormalParameterListing()
                         .getFormalParameterList().size() == paramCount;
            }
          })
          .filter(constructor -> constructor.getName().equals(name))
          .findFirst();
  }

  @Override
  public void visit(ASTImportDeclaration node) {
    final String fqImport = node.getQualifiedName().toString();
    imports.removeIf(s -> s.equals(fqImport));
  }

  @Override
  public void visit(ASTFieldDeclaration node){
    final ASTType type = node.getType();
    for (ASTVariableDeclarator declarator : node.getVariableDeclaratorList()) {
      final String name = declarator.getDeclaratorId().getName();
      final String printedType = printWithoutWhitespace(type);
      final boolean removed
          = fields.removeIf(
              field -> field.getName().equals(name)
                           && printedType.endsWith(field.getType().replace(" ", "")));
      if(!removed){
        Log.error(
            String.format("Found unexpected field in %s: %s",
                this.className,
                declarator.getDeclaratorId().getName()));
      }
    }
  }

  @Override
  public void visit(ASTEnumDeclaration node){
    boolean found;

    final String actualName = node.getName();

    // TODO Enum contents

    found = this.enumTypes.removeIf(e -> e.getName().equals(actualName));

    if(!found){
      Log.error("Found unexpected enum in " + this.className + ": " + actualName);
    }
  }

  /**
   * Check whether everything that should be present in the generated class
   * is present
   * @return true, if all elements have been found
   */
  public void allExpectedPresent(){
//    assertTrue(String.format("Did not find all required imports in %s: \n%s",
//            className, imports.toString()), imports.isEmpty());
    assertTrue(String.format("Did not find all required fields in %s: \n%s",
        className, fields.toString()), fields.isEmpty());
    assertTrue(String.format("Did not find all required methods in %s: \n%s",
            className, methods.toString()), methods.isEmpty());
    assertTrue(String.format("Did not find all required constructors in %s: \n%s",
            className, constructors.toString()), constructors.isEmpty());
    assertTrue(String.format("Did not find all required enums in %s: \n%s",
            className, enumTypes.toString()), enumTypes.isEmpty());
  }

  public void addImport(String fullQualifiedImport){
    imports.add(fullQualifiedImport);
  }

  public void addImports(Set<String> imports){
    this.imports.addAll(imports);
  }

  public void addImports(String... imports){
    addImports(Sets.newHashSet(imports));
  }

  /**
   * Add fields to the set of expected fields
   * @param names Names of the fields
   * @param type Type of the field
   */
  public void addFields(List<String> names, String type) {
    for (String name : names) {
      addField(new Field(name, type));
    }
  }

  /**
   * Add a field to the set of expected fields
   * @param name Name of the field
   * @param type Type of the field
   */
  public void addField(String name, String type){
    this.fields.add(new Field(name, type));
  }

  public void addField(Field field){
    this.fields.add(field);
  }

  public void addMethod(Method method){
    this.methods.add(method);
  }

  public void addConstructor(Constructor constructor){
    this.constructors.add(constructor);
  }

  public Set<Method> getMethods() {
    return this.methods;
  }

  /**
   * Add an interface to the set of expected implemented interfaces.
   *
   * @param interfaceName Unqualified name of the interface
   */
  public void addImplementedInterface(String interfaceName){
    this.interfaces.add(interfaceName);
  }

  public void setSuperClass(String superClass) {
    this.superClass = superClass;
  }

  /*
   *
   */
  public void setSuperClass(String superClass, List<ActualTypeArgument> typeArgs){
    setSuperClass(superClass);
    this.superClassTypeArgs = typeArgs;
  }

  public String getSuperClass() {
    return superClass;
  }

  public void addImplementedInterface(String name, ASTTypeArguments typeArgs) {
    final String printedArgs = GeneratorTestConstants.PRINTER.prettyprint(typeArgs);
    this.interfaces.add(name + printedArgs);
  }

  public void addEnumType(EnumType enumType){
    this.enumTypes.add(enumType);
  }
}