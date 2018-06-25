/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package generation;

import com.google.common.collect.Lists;
import de.montiarcautomaton.generator.helper.ComponentHelper;
import de.monticore.java.javadsl._ast.*;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.java.types.HCJavaDSLTypeResolver;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeArguments;
import de.monticore.types.types._ast.ASTVoidType;
import montiarc._ast.*;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._visitor.MontiArcVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TODO
 *
 * @author (last commit) Michael Mutert
 */
public class ComponentElementsCollector implements MontiArcVisitor {

  public static final ASTPrimitiveModifier PUBLIC_MODIFIER
      = ASTPrimitiveModifier.getBuilder().modifier(ASTConstantsJavaDSL.PUBLIC).build();
  public static final ASTVoidType VOID_TYPE = ASTVoidType.getBuilder().build();
  protected GeneratedComponentClassVisitor classVisitor;
  protected GeneratedComponentClassVisitor inputVisitor;
  protected GeneratedComponentClassVisitor resultVisitor;
  protected GeneratedComponentClassVisitor implVisitor;
  private ComponentSymbol symbol;
  private ComponentHelper helper;


  private final static JavaDSLPrettyPrinter PRINTER
      = new JavaDSLPrettyPrinter(new IndentPrinter());

  public ComponentElementsCollector(ComponentSymbol symbol, String name) {
    this.symbol = symbol;
    this.helper = new ComponentHelper(symbol);
    this.classVisitor = new GeneratedComponentClassVisitor(name);
    this.inputVisitor = new GeneratedComponentClassVisitor(name+"Input");
    this.implVisitor = new GeneratedComponentClassVisitor(name+"Impl");
    this.resultVisitor = new GeneratedComponentClassVisitor(name+"Result");
  }

  @Override
  public void visit(ASTParameter node) {
    final String parameterName = node.getName();
    final ASTType parameterType = node.getType();
    final Optional<ASTValuation> defaultValue = node.getDefaultValue();

    classVisitor.addField(parameterName, parameterType);
  }

  @Override
  /**
   * Visit the component node of the AST.
   *
   * Here the following information is embedded into the respective
   * visitors:
   *
   * Component Class:
   *  - Field for the behavior implementation
   *  - Information about standard methods
   *    - setUp
   *    - init
   *    - initialize
   *    - setResult
   *    - compute
   *    - update
   *  - Constructor
   *  - Implemented interface
   *
   * Result Class
   *  - Implemented interface
   *  - Constructor
   *  - toString
   *
   * Input Class
   *  - Implemented interface
   *  - Constructor
   *  - toString
   */
  public void visit(ASTComponent node){
    // Add elements which are not found by the visitor
    HCJavaDSLTypeResolver typeResolver = new HCJavaDSLTypeResolver();
    final String componentName = node.getName();

    // impl field
    addImplField(componentName);

    // Common methods
    // setup
    Method.Builder methodBuilder = Method.getBuilder();
    if(symbol.getSuperComponent().isPresent()){
      methodBuilder.addBodyElement("super.setUp();");
    }

    methodBuilder.setName("setUp").addBodyElement("this.initialize();");
    classVisitor.addMethod(methodBuilder.build());

    // init
    methodBuilder = Method.getBuilder().setName("init");
    if(symbol.getSuperComponent().isPresent()){
      methodBuilder.addBodyElement("super.init();");
    }
    classVisitor.addMethod(methodBuilder.build());

    // compute
    addCompute(componentName);

    // update
    methodBuilder = Method.getBuilder().setName("update");
    if(symbol.getSuperComponent().isPresent()){
      methodBuilder.addBodyElement("super.update();");
    }
    classVisitor.addMethod(methodBuilder.build());

    // setResult
    addSetResult(componentName);

    // initialize
    addInitialize(componentName);

    // Constructor
    addConstructor(node, componentName);

    addInputAndResultConstructor(symbol);
    addToString();
    addFixedImports(symbol);

    // Implemented interfaces
    classVisitor.addImplementedInterface("IComponent");
    resultVisitor.addImplementedInterface("IResult");
    inputVisitor.addImplementedInterface("IInput");
    if(symbol.getSuperComponent().isPresent()) {
      final String fullName = symbol.getSuperComponent().get().getFullName();
      classVisitor.setSuperClass(fullName);
      resultVisitor.setSuperClass(fullName + "Result");
      inputVisitor.setSuperClass(fullName + "Input");
    }
  }

  private void addImplField(String componentName) {
    final ASTSimpleReferenceType inputRefType
        = ASTSimpleReferenceType.getBuilder()
              .names(Lists.newArrayList(componentName + "Input"))
              .build();
    ASTSimpleReferenceType resultRefType
        = ASTSimpleReferenceType.getBuilder()
              .names(Lists.newArrayList(componentName + "Result"))
              .build();
    ASTTypeArguments typeArgs = ASTTypeArguments
                                    .getBuilder()
                                    .typeArguments(
                                        Lists.newArrayList(inputRefType, resultRefType))
                                    .build();
    ASTType expectedType = ASTSimpleReferenceType
                               .getBuilder()
                               .names(Lists.newArrayList("IComputable"))
                               .typeArguments(typeArgs)
                               .build();
    classVisitor.addField(GenerationStringConstants.BEHAVIOR_IMPL, expectedType);
  }

  private void addConstructor(ASTComponent node, String componentName) {
    final Constructor.Builder builder = Constructor.getBuilder();
    builder.setName(componentName);
    StringBuilder parameters = new StringBuilder();
    for (ASTParameter parameter : node.getHead().getParameters()) {
      parameters.append(parameter.getName()).append(",");
      JavaFieldSymbol typeSymbol = (JavaFieldSymbol) symbol.getSpannedScope().resolve(parameter.getName(), JavaFieldSymbol.KIND).orElse(null);
      if(typeSymbol != null) {
        final String paramTypeName = helper.getParamTypeName(typeSymbol);
        ASTType paramType = ASTSimpleReferenceType
                                .getBuilder()
                                .names(Lists.newArrayList(paramTypeName))
                                .build();
        builder.addParameter(parameter.getName(), paramType);
      }
    }
    if(parameters.length() > 0) {
      parameters.deleteCharAt(parameters.length() - 1);
    }
    builder.addBodyElement(String.format("behaviorImpl = new %sImpl(%s);",
        capitalizeFirst(componentName), parameters.toString()));

    classVisitor.addConstructor(builder.build());
  }

  private void addCompute(String componentName) {
    Method.Builder methodBuilder;
    methodBuilder = Method.getBuilder().setName("compute");
    StringBuilder incomingPorts = new StringBuilder();
    for (PortSymbol portSymbol : symbol.getIncomingPorts()) {
      incomingPorts.append(portSymbol.getName()).append(",");
    }
    if(incomingPorts.length() > 0){
      incomingPorts.deleteCharAt(incomingPorts.length() - 1);
    }

    methodBuilder.addBodyElement(
        String.format("final %sInput input = new %sInput(this.%s.getCurrentValue())",
            componentName, componentName, incomingPorts.toString()));
    methodBuilder.addBodyElement(
        String.format("try {\n"
            + "      // perform calculations\n"
            + "      final %sResult result = behaviorImpl.compute(input);\n"
            + "      \n"
            + "      // set results to ports\n"
            + "      setResult(result);\n"
            + "    } catch (Exception e) {\n"
            + "      Log.error(\"%s\", e);\n"
            + "    }", componentName, componentName));
    classVisitor.addMethod(methodBuilder.build());
  }

  private void addSetResult(String componentName) {
    Method.Builder methodBuilder;
    methodBuilder = Method.getBuilder().setName("setResult");
    ASTSimpleReferenceType resultType =
        ASTSimpleReferenceType
            .getBuilder()
            .names(Lists.newArrayList(componentName+"Result"))
            .build();
    methodBuilder.addParameter("result", resultType);
    classVisitor.addMethod(methodBuilder.build());
  }

  private void addInitialize(String componentName) {
    Method.Builder methodBuilder;
    methodBuilder = Method.getBuilder().setName("initialize");
    methodBuilder.addBodyElement(
        String.format("final %sResult result = %s.%s;",
            componentName,
            GenerationStringConstants.BEHAVIOR_IMPL,
            GenerationStringConstants.METHOD_INITIAL_VALUES));
    methodBuilder.addBodyElement("setResult(result);");
    classVisitor.addMethod(methodBuilder.build());
  }

  /**
   * Add expected toString methods to the respective visitors
   */
  private void addToString(){
    ASTSimpleReferenceType stringType = ASTSimpleReferenceType.getBuilder()
        .names(Lists.newArrayList("String"))
        .build();

    Method toString =
        Method
            .getBuilder()
            .setName("toString")
            .setReturnType(stringType) // TODO Return type
            .addBodyElement("String result = \"[\"")
            // TODO port body elements
            .addBodyElement("return result+\"]\"")
            .build();
    this.resultVisitor.addMethod(toString);
    this.inputVisitor.addMethod(toString);

  }

  /**
   * Add fixed imports to the respective visitors
   */
  private void addFixedImports(ComponentSymbol componentSymbol) {
    classVisitor.addImport(componentSymbol.getPackageName() +
                               componentSymbol.getName() + "Result");
    classVisitor.addImport(componentSymbol.getPackageName() +
                               componentSymbol.getName() + "Input");
    classVisitor.addImport("de.montiarcautomaton.runtimes" +
                               ".timesync.delegation.IComponent");
    classVisitor.addImport("de.montiarcautomaton.runtimes" +
                               ".timesync.delegation.Port");
    classVisitor.addImport("de.montiarcautomaton.runtimes" +
                               ".timesync.implementation.IComputable");
    classVisitor.addImport("de.montiarcautomaton.runtimes." +
                               "Log");

    inputVisitor.addImport("de.montiarcautomaton.runtimes.timesync.implementation.IInput");
    resultVisitor.addImport("de.montiarcautomaton.runtimes.timesync.implementation.IResult");
  }

  /**
   * Adds expected constructors to the Input and Result visitors from the
   * given ComponentSymbol
   * @param symbol ComponentSymbol of the component for which the information
   *               is currently collected.
   */
  private void addInputAndResultConstructor(ComponentSymbol symbol){
    final Constructor.Builder builder = Constructor.getBuilder();
    if(symbol.getSuperComponent().isPresent()){
      builder.addBodyElement("super();");
    }
    builder.setName(symbol.getName() + "Input");
    this.inputVisitor.addConstructor(builder.build());

    builder.setName(symbol.getName() + "Result");
    this.resultVisitor.addConstructor(builder.build());
  }

  @Override
  public void visit(ASTInterface node){
//    node.getPorts()
  }

  @Override
  public void visit(ASTPort node) {
    final PortSymbol symbol = (PortSymbol) node.getSymbol().get();
    final ASTSimpleReferenceType type = (ASTSimpleReferenceType) node.getType();

    // Type parameters for the Port field type
    // The type parameters consist of the type of the ASTPort type
    final ASTTypeArguments typeArgs = ASTTypeArguments
                                          .getBuilder()
                                          .typeArguments(Lists.newArrayList(type))
                                          .build();

    // Build the AST node for the type Port<Type>
    ASTType expectedType = ASTSimpleReferenceType
                               .getBuilder()
                               .names(Lists.newArrayList("Port"))
                               .typeArguments(typeArgs)
                               .build();
    final List<String> names = node.getNames();
    classVisitor.addFields(names, expectedType);
    if (node.isOutgoing()) {
      resultVisitor.addFields(names, type);
    }
    if(node.isIncoming()) {
      inputVisitor.addFields(names, type);
    }

    // Add expectations for setter and getter methods
    // Setter
    for (String name : names) {
      Method.Builder setter
          = Method
                .getBuilder()
                .setReturnType(VOID_TYPE)
                .addParameter("port", expectedType)
                .setName("setPort" + capitalizeFirst(name));
      if (node.isIncoming()) {
        classVisitor.addMethod(setter.build());
      } else if (node.isOutgoing()) {
        setter = Method
            .getBuilder()
            .setReturnType(VOID_TYPE)
            .addParameter(name, type)
            .setName("set" + capitalizeFirst(name));
        resultVisitor.addMethod(setter.build());
      }
    }

    // Getter
    for(String name: names) {
      final Method.Builder getter =
          Method
              .getBuilder()
              .setName("getPort" + capitalizeFirst(name))
              .addBodyElement(String.format("return this.%s;", name))
              .setReturnType(expectedType);
      classVisitor.addMethod(getter.build());
      if(node.isOutgoing()) {
        resultVisitor.addMethod(getter
                                    .setName("get" + capitalizeFirst(name))
                                    .setReturnType(type)
                                    .build());
      } else if(node.isIncoming()) {
        inputVisitor.addMethod(getter
                                   .setName("get" + capitalizeFirst(name))
                                   .setReturnType(type)
                                   .build());
      }
    }

    // Methods
    if(node.isOutgoing()) {
      final Optional<Method> setUp = getMethod("setUp");
      if (setUp.isPresent()) {
        for (String name : names) {
          setUp.get().addBodyElement(
              String.format("this.%s = new Port<%s>();",
                  name, PRINTER.prettyprint(type)), -1);
        }
      }
      final Optional<Method> update = getMethod("update");
      if (update.isPresent()) {
        for (String name : names) {
          update.get().addBodyElement(
              String.format("this.%s.update();", name));
        }
      }
    }
    if(node.isIncoming()){
      final Optional<Method> init = getMethod("init");
      if (init.isPresent()) {
        for (String name : names) {
          init.get().addBodyElement(
              String.format("if (this.%s == null) {this.%s = Port.EMPTY;}", name, name));
        }
      }
    }
  }

  private Optional<Method> getMethod(String name){
    return classVisitor.getMethods()
               .stream()
               .filter(m -> m.getName().equals(name))
               .findFirst();
  }

  private ASTFormalParameters buildFormalParameters(List<String> parameterNames,
                                                    ASTType type){
    List<ASTType> paramTypes = new ArrayList<>();
    for(int i = 0; i < parameterNames.size(); ++i){
      paramTypes.add(type);
    }
    return buildFormalParameters(parameterNames, paramTypes);
  }

  private ASTFormalParameters buildFormalParameters(
      List<String> parameterNames, List<ASTType> types){

    List<ASTFormalParameter> params = new ArrayList<>();
    for (String name : parameterNames) {

      // Build the node for the name of the parameter
      ASTDeclaratorId declaratorId = ASTDeclaratorId
                                               .getBuilder()
                                               .name(name)
                                               .build();
      // Build the parameter node of the setter
      ASTFormalParameter param
          = ASTFormalParameter
                .getBuilder()
                .type(types.get(parameterNames.indexOf(name)))
                .declaratorId(declaratorId)
                .build();
      params.add(param);
    }

    ASTFormalParameterListing listing
        = ASTFormalParameterListing
              .getBuilder()
              .formalParameters(params)
              .build();
    return ASTFormalParameters.getBuilder().formalParameterListing(listing).build();
  }

  private String capitalizeFirst(String input) {
    return input.substring(0, 1).toUpperCase() + input.substring(1);
  }

  @Override
  public void visit(ASTImportDeclaration node) {
    classVisitor.addImport(node.getQualifiedName().toString());
  }

  public GeneratedComponentClassVisitor getClassVisitor(){
    return classVisitor;
  }

  public GeneratedComponentClassVisitor getInputVisitor() {
    return inputVisitor;
  }

  public GeneratedComponentClassVisitor getResultVisitor() {
    return resultVisitor;
  }

  public GeneratedComponentClassVisitor getImplVisitor() {
    return implVisitor;
  }
}
