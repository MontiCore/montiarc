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
import de.monticore.types.types._ast.ASTTypesNode;
import de.se_rwth.commons.logging.Log;
import sim.help.SynchronizedQueue;

import java.util.*;

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
    this.constructors = new HashSet<>();
    this.interfaces = new HashSet<>();
  }

  @Override
  public void visit(ASTPackageDeclaration node) {

  }

  @Override
  public void visit(ASTMethodDeclaration node){
    String methodString = printWithoutWhitespace(node.getMethodBody().get());

    final ASTMethodSignature signature = node.getMethodSignature();
    final String methodName = signature.getName();
    final Optional<Method> method = getMethod(methodName);
    if(method.isPresent() && method.get().getBodyElements().size() > 0) {
      for (String s : method.get().getBodyElements()) {
        if (!methodString.contains(s)) {
          Log.error("Missing statement in method " + methodName + ": " + s);
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
    for (ASTType type : node.getImplementedInterfaces()) {
      if(type instanceof ASTSimpleReferenceType){
        boolean isComponentImplemented
            = ((ASTSimpleReferenceType) type).getNames()
                  .stream().anyMatch(s -> interfaces.contains(s));
        if(!isComponentImplemented){
          Log.error(String.format("Class %s does not implement interface %s", className,
              ((ASTSimpleReferenceType) type).getNames()));
        }
      }
    }
    final Optional<ASTType> superClass = node.getSuperClass();
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

    int paramSize = 0;
    if(actualParams.getFormalParameterListing().isPresent()){
      paramSize = actualParams.getFormalParameterListing().get().getFormalParameters().size();
    }
    if(getConstructor(actualName, paramSize).isPresent()){
      final Constructor constructor = getConstructor(actualName, paramSize).get();
      final boolean actualParamListPresent
          = actualParams.getFormalParameterListing().isPresent();
      final boolean expectedParamListPresent
          = constructor.getParameters().getFormalParameterListing().isPresent();

      if(actualParamListPresent && expectedParamListPresent){
        final ASTFormalParameterListing actualParamList
            = actualParams.getFormalParameterListing().get();
        final ASTFormalParameterListing expectedParamList
            = constructor.getParameters().getFormalParameterListing().get();

        final String actualPrint = printWithoutWhitespace(actualParamList);
        final String expectedPrint = printWithoutWhitespace(expectedParamList);

        if(!actualPrint.equals(expectedPrint)){
//        if(!actualParamList.deepEquals(expectedParamList, true)){
          Log.error(String.format("Parameters of constructor %s do not " +
                                      "fit the expected parameters",
              actualName));
        }
      } else if(actualParamListPresent || expectedParamListPresent){
        Log.error(String.format("Mismatch in FormalParameterListing " +
                                    "presence for constructor %s",
            actualName));
      }

      final String printedBody =
          printWithoutWhitespace(node.getConstructorBody());
      for (String bodyElement : constructor.getBodyElements()) {
        if(!printedBody.contains(bodyElement)) {
          Log.error(
              String.format("Missing element in constructor bodyElements: %s",
                          bodyElement));
        }
      }
    }
  }

  private Optional<Constructor> getConstructor(String name, int paramCount){
    final Optional<Constructor> result = constructors
        .stream()
        .filter(constructor -> constructor.getName().equals(name))
        .findFirst();
    if(result.isPresent()){
      if(result.get().getParameters().getFormalParameterListing().isPresent()){
        if(result.get().getParameters().getFormalParameterListing().get()
            .getFormalParameters().size() != paramCount){
          return Optional.empty();
        }
      }
    }
    return result;
  }

  @Override
  public void visit(ASTImportDeclaration node) {
    final String fqImport = node.getQualifiedName().toString();
    imports.removeIf(s -> s.equals(fqImport));
  }

  @Override
  public void visit(ASTFieldDeclaration node){
    final ASTType type = node.getType();
    for (ASTVariableDeclarator declarator : node.getVariableDeclarators()) {
      final String name = declarator.getDeclaratorId().getName();
      fields.removeIf(field -> field.getName().equals(name) && field.getType().deepEquals(type));
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
}