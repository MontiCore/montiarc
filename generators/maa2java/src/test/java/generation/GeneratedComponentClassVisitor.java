/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package generation;

import com.google.common.collect.Sets;
import de.montiarcautomaton.runtimes.Log;
import de.monticore.java.javadsl._ast.*;
import de.monticore.java.javadsl._visitor.JavaDSLVisitor;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.types._ast.ASTReturnType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * TODO
 *
 * @author (last commit)
 * @version ,
 * @since TODO
 */
public class GeneratedComponentClassVisitor implements JavaDSLVisitor {

  Set<String> imports;
  Set<Field> variables;
  Set<Method> methods;

  public GeneratedComponentClassVisitor(Set<String> imports,
                                        Set<Field> variables,
                                        Set<Method> methods) {

    this.imports = imports;
    this.variables = variables;
    this.methods = methods;
  }

  @Override
  public void visit(ASTPackageDeclaration node) {

  }

  @Override
  public void visit(ASTMethodDeclaration node){
    JavaDSLPrettyPrinter printer = new JavaDSLPrettyPrinter(new IndentPrinter());
    String method = printer.prettyprint(node.getMethodBody().get());

    final ASTMethodSignature signature = node.getMethodSignature();
    if(getMethod(signature.getName()).isPresent()) {
      if (!method.contains(getMethod(signature.getName()).get().body)) {
        Log.error("GeneratorTest", "Missing statement in method body");
      }
    }

    methods.removeIf(m -> signature.getName().equals(m.name)
                              && signature.getFormalParameters().deepEquals(m.params)
                              && signature.getReturnType().deepEquals(m.returnType));
  }

  private Optional<Method> getMethod(String name){
    for (Method method : methods) {
      if(method.name.equals(name)){
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
      Log.error("Template/Generator",
          "Component class does not implement IComponent");
    }
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
      variables.removeIf(field -> field.name.equals(name) && field.type.deepEquals(type));
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
    result &= variables.isEmpty();
    result &= methods.isEmpty();
    return result;
  }

  static class Builder {
    private Set<String> imports;
    private Set<Field> variables;
    private Set<Method> methods;

    public Builder() {
      imports = new HashSet<>();
      variables = new HashSet<>();
      methods = new HashSet<>();
    }

    public Builder addImport(String fullQualifiedImport){
      imports.add(fullQualifiedImport);
      return this;
    }

    public Builder addImports(String... imports){
      return addImports(Sets.newHashSet(imports));
    }

    public Builder addImports(Set<String> imports){
      this.imports.addAll(imports);
      return this;
    }

    public GeneratedComponentClassVisitor build(){

      return new GeneratedComponentClassVisitor(
          this.imports,
          this.variables,
          this.methods);
    }

    public Builder addVariables(List<String> names, ASTType type) {
      for (String name : names) {
        this.variables.add(new Field(name, type));
      }
      return this;
    }

    public Builder addVariable(String name, ASTType type){
      this.variables.add(new Field(name, type));
      return this;
    }

    public Builder addMethod(ASTReturnType returnType, String name, ASTFormalParameters params){
      return addMethod(returnType, name, params, "");
    }

    public Builder addMethod(ASTReturnType returnType, String name,
                             ASTFormalParameters params, String body){
      this.methods.add(new Method(returnType, name, params, body));
      return this;
    }
  }

  static class Method{
    private final ASTReturnType returnType;
    private final String name;
    private final ASTFormalParameters params;
    private String body;

    public Method(ASTReturnType returnType, String name, ASTFormalParameters params, String body) {
      this.returnType = returnType;
      this.name = name;
      this.params = params;
      this.body = body;
    }
  }

  static class Field{
    private final String name;
    private final ASTType type;

    Field(String name, ASTType type) {
      this.name = name;
      this.type = type;
    }
  }

}