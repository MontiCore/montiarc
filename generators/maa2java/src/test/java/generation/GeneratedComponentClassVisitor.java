/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package generation;

import com.google.common.collect.Sets;
import de.monticore.java.javadsl._ast.*;
import de.monticore.java.javadsl._visitor.JavaDSLVisitor;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeArguments;
import de.monticore.types.types._ast.ASTTypesNode;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

  public Set<Constructor> getConstructors() {
    return constructors;
  }

  Set<Constructor> constructors;

  private static final JavaDSLPrettyPrinter PRINTER
      = new JavaDSLPrettyPrinter(new IndentPrinter());

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
          int foundIndex = methodString.indexOf(s);
          if(lastIndex >= foundIndex){
            Log.error(String.format("Body element %s of method %s was " +
                                        "found in the wrong order.",
                s, methodName));
          }
          lastIndex = foundIndex;
        }
      }
    }

    final boolean removed = methods.removeIf(m -> methodName.equals(m.getName())
        && signature.getFormalParameters().deepEquals(m.getParams())
        && signature.getReturnType().deepEquals(m.getReturnType()));
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
          typeArgs = PRINTER.prettyprint(refType.getTypeArguments());
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
      Log.error(String.format("%s does not extend superclass %s",
          className, this.superClass));
    } else if(this.superClass == null && superClass.isPresent()){
      Log.error(String.format("%s unexpectedly extends a class %s",
          className, printWithoutWhitespace(superClass.get())));
    } else if(this.superClass != null){
      if(!this.superClass.equals(printWithoutWhitespace(superClass.get()))){
        Log.error(String.format("The class %s extends the wrong class " +
                                    "%s instead of expected class %s",
            className,
            printWithoutWhitespace(superClass.get()),
            this.superClass));
      }
    }
  }

  private String printWithoutWhitespace(ASTTypesNode node){
    return PRINTER.prettyprint(node).replaceAll("\\s", "");
  }
  private String printWithoutWhitespace(ASTJavaDSLNode node){
    return PRINTER.prettyprint(node).replaceAll("\\s", "");
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

        final String actualPrint = printWithoutWhitespace(actualParamList);
        final String expectedPrint = printWithoutWhitespace(expectedParamList);

        if(!actualPrint.equals(expectedPrint)){
//        if(!actualParamList.deepEquals(expectedParamList, true)){
          Log.error(String.format("Parameters of constructor %s do not " +
                                      "fit the expected parameters",
              actualName));
          foundError = true;
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
          PRINTER.prettyprint(node.getFormalParameters())));
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
      fields.removeIf(field -> field.getName().equals(name) && field.getType().deepEquals(type));
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
    // TODO Reenable after fixing fully qualified constructor parameters
//    assertTrue(String.format("Did not find all required constructors in %s: \n%s",
//            className, constructors.toString()), constructors.isEmpty());
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
  public void addFields(List<String> names, ASTType type) {
    for (String name : names) {
      addField(new Field(name, type));
    }
  }

  /**
   * Add a field to the set of expected fields
   * @param name Name of the field
   * @param type Type of the field
   */
  public void addField(String name, ASTType type){
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

  public String getSuperClass() {
    return superClass;
  }

  public void addImplementedInterface(String name, ASTTypeArguments typeArgs) {
    final String printedArgs = PRINTER.prettyprint(typeArgs);
    this.interfaces.add(name + printedArgs);
  }

  public void addEnumType(EnumType enumType){
    this.enumTypes.add(enumType);
  }
}