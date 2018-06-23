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
import de.se_rwth.commons.logging.Log;

import java.util.*;

/**
 * TODO
 *
 * @author (last commit)
 * @version ,
 * @since TODO
 */
public class GeneratedComponentClassVisitor implements JavaDSLVisitor {

  Set<String> imports;
  Set<Field> fields;
  Set<Method> methods;

  public Set<Constructor> getConstructors() {
    return constructors;
  }

  Set<Constructor> constructors;

  private static final JavaDSLPrettyPrinter PRINTER
      = new JavaDSLPrettyPrinter(new IndentPrinter());

  public GeneratedComponentClassVisitor(){
    this.imports = new HashSet<>();
    this.fields = new HashSet<>();
    this.methods = new HashSet<>();
    this.constructors = new HashSet<>();
  }

  public GeneratedComponentClassVisitor(Set<String> imports,
                                        Set<Field> fields,
                                        Set<Method> methods,
                                        Set<Constructor> constructors) {

    this.imports = imports;
    this.fields = fields;
    this.methods = methods;
    this.constructors = constructors;
  }

  @Override
  public void visit(ASTPackageDeclaration node) {

  }

  @Override
  public void visit(ASTMethodDeclaration node){
    String methodString = PRINTER.prettyprint(node.getMethodBody().get());

    final ASTMethodSignature signature = node.getMethodSignature();
    final String methodName = signature.getName();
    final Optional<Method> method = getMethod(methodName);
    if(method.isPresent() && method.get().getBodyElements().size() > 0) {
      if (!methodString.contains(method.get().getBodyElements().get(0))) {
        Log.error("Missing statement in method bodyElements");
      }
    }

    methods.removeIf(m -> methodName.equals(m.getName())
                              && signature.getFormalParameters().deepEquals(m.getParams())
                              && signature.getReturnType().deepEquals(m.getReturnType()));
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
    boolean isComponentImplemented = false;
    for (ASTType type : node.getImplementedInterfaces()) {
      if(type instanceof ASTSimpleReferenceType){
        isComponentImplemented = ((ASTSimpleReferenceType) type).getNames()
                  .stream().anyMatch(s -> s.equals("IComponent"));
      }
    }
    if(!isComponentImplemented){
      Log.error("Component class does not implement IComponent");
    }
  }

  @Override
  public void visit(ASTConstructorDeclaration node){
    final ASTFormalParameters actualParams = node.getFormalParameters();
    final String actualName = node.getName();

    if(getConstructor(actualName).isPresent()){
      final Constructor constructor = getConstructor(actualName).get();
      if(!actualParams.deepEquals(constructor.getParameters(), true)){
        Log.error(String.format("Parameters of constructor %s do not fit the " +
                              "expected parameters", actualName));
      }

      final String printedBody = PRINTER.prettyprint(node.getConstructorBody());
      for (String bodyElement : constructor.getBodyElements()) {
        if(!printedBody.contains(bodyElement)) {
          Log.error(String.format("Missing element in constructor bodyElements: %s",
                          bodyElement));
        }
      }

    }
  }

  private Optional<Constructor> getConstructor(String name){
    return constructors
               .stream()
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
  public boolean allExpectedPresent(){
    boolean result;
    result = imports.isEmpty();
    result &= fields.isEmpty();
    result &= methods.isEmpty();
    return result;
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

}