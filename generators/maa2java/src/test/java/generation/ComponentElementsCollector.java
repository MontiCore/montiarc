/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package generation;

import com.google.common.collect.Lists;
import de.montiarcautomaton.generator.helper.ComponentHelper;
import de.monticore.java.javadsl._ast.ASTImportDeclaration;
import de.monticore.java.javadsl._ast.JavaDSLMill;
import de.monticore.java.types.HCJavaDSLTypeResolver;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeArgument;
import de.monticore.types.types._ast.ASTTypeArguments;
import montiarc._ast.*;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._visitor.MontiArcVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author (last commit) Michael Mutert
 */
public class ComponentElementsCollector implements MontiArcVisitor {

  protected GeneratedComponentClassVisitor classVisitor;
  protected GeneratedComponentClassVisitor inputVisitor;
  protected GeneratedComponentClassVisitor resultVisitor;
  protected GeneratedComponentClassVisitor implVisitor;
  private ComponentSymbol symbol;
  private ComponentHelper helper;


  private final String componentName;
  private final String inputName;
  private final String resultName;
  private final String implName;

  public ComponentElementsCollector(ComponentSymbol symbol, String name) {
    this.symbol = symbol;
    this.componentName = name;
    this.helper = new ComponentHelper(symbol);
    this.classVisitor = new GeneratedComponentClassVisitor(name);
    this.inputName = name + "Input";
    this.resultName= name + "Result";
    this.implName = name + "Impl";
    this.inputVisitor = new GeneratedComponentClassVisitor(inputName);
    this.implVisitor = new GeneratedComponentClassVisitor(implName);
    this.resultVisitor = new GeneratedComponentClassVisitor(resultName);
  }

  @Override
  public void visit(ASTParameter node) {
    final String parameterName = node.getName();
    final ASTType parameterType = node.getType();
    final Optional<ASTValuation> defaultValue = node.getDefaultValueOpt();

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

    // impl field
    addImplField();

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
    addCompute();

    // update
    methodBuilder = Method.getBuilder().setName("update");
    if(symbol.getSuperComponent().isPresent()){
      methodBuilder.addBodyElement("super.update();");
    }
    classVisitor.addMethod(methodBuilder.build());

    // setResult
    addSetResult();

    // initialize
    addInitialize();

    // Constructor
    addConstructors(node);

    addInputAndResultConstructor(symbol);
    addToString();
    addFixedImports(symbol);

    // Impl methods
    addGetInitialValues();
    addImplCompute();

    // Implemented interfaces
    classVisitor.addImplementedInterface("IComponent");
    resultVisitor.addImplementedInterface("IResult");
    inputVisitor.addImplementedInterface("IInput");

    ArrayList<String> names = Lists.newArrayList(inputName);
    ASTTypeArgument inputArg
        = JavaDSLMill.simpleReferenceTypeBuilder().setNameList(names).build();

    names = Lists.newArrayList(resultName);
    ASTTypeArgument resultArg
        = JavaDSLMill.simpleReferenceTypeBuilder().setNameList(names).build();

    final ArrayList<ASTTypeArgument> typeArguments = Lists.newArrayList(inputArg, resultArg);
    ASTTypeArguments typeArgs
        = JavaDSLMill.typeArgumentsBuilder().setTypeArgumentList(typeArguments).build();
    implVisitor.addImplementedInterface("IComputable", typeArgs);

    if(symbol.getSuperComponent().isPresent()) {
      final String fullName = symbol.getSuperComponent().get().getFullName();
      classVisitor.setSuperClass(fullName);
      resultVisitor.setSuperClass(fullName + "Result");
      inputVisitor.setSuperClass(fullName + "Input");
    }
  }

  private void addImplField() {
    final ASTSimpleReferenceType inputRefType
        = JavaDSLMill.simpleReferenceTypeBuilder()
              .setNameList(Lists.newArrayList(inputName))
              .build();
    ASTSimpleReferenceType resultRefType
        = JavaDSLMill.simpleReferenceTypeBuilder()
              .setNameList(Lists.newArrayList(resultName))
              .build();
    ASTTypeArguments typeArgs = JavaDSLMill.typeArgumentsBuilder()
                                    .setTypeArgumentList(
                                        Lists.newArrayList(inputRefType, resultRefType))
                                    .build();
    ASTType expectedType = JavaDSLMill.simpleReferenceTypeBuilder()
                               .setNameList(Lists.newArrayList("IComputable"))
                               .setTypeArguments(typeArgs)
                               .build();
    classVisitor.addField(GenerationConstants.BEHAVIOR_IMPL, expectedType);
  }

  private void addGetInitialValues(){
    ASTSimpleReferenceType returnType
        = JavaDSLMill.simpleReferenceTypeBuilder()
              .setNameList(Lists.newArrayList(resultName))
              .build();
    Method method = Method.getBuilder()
                        .setName("getInitialValues")
                        .setReturnType(returnType)
                        .build();
    this.implVisitor.addMethod(method);
  }

  private void addImplCompute(){
    ASTSimpleReferenceType returnType
        = JavaDSLMill.simpleReferenceTypeBuilder()
              .setNameList(Lists.newArrayList(resultName))
              .build();
    ASTSimpleReferenceType paramType
        = JavaDSLMill.simpleReferenceTypeBuilder()
              .setNameList(Lists.newArrayList(inputName))
              .build();
    Method method = Method.getBuilder()
                        .setName("compute")
                        .setReturnType(returnType)
                        .addParameter("input", paramType)
                        .build();
    this.implVisitor.addMethod(method);
  }

  /**
   * Add expected constructor to the class visitor.
   */
  private void addConstructors(ASTComponent node) {
    final Constructor.Builder builder = Constructor.getBuilder();
    Constructor.Builder implConstructor = Constructor.getBuilder();

    builder.setName(componentName);
    implConstructor.setName(implName);
    StringBuilder parameters = new StringBuilder();
    for (ASTParameter parameter : node.getHead().getParameterList()) {
      parameters.append(parameter.getName()).append(",");
      JTypeSymbol typeSymbol =
          (JTypeSymbol) symbol.getSpannedScope().resolve(parameter.getName(), JTypeSymbol.KIND).orElse(null);
      if(typeSymbol != null) {
        final String paramTypeName = helper.getParamTypeName((JFieldSymbol) typeSymbol);
        ASTType paramType =JavaDSLMill.simpleReferenceTypeBuilder()
                                .setNameList(Lists.newArrayList(paramTypeName))
                                .build();
        builder.addParameter(parameter.getName(), paramType);
        implConstructor.addParameter(parameter.getName(), paramType);
      }
    }
    if(parameters.length() > 0) {
      parameters.deleteCharAt(parameters.length() - 1);
    }
    builder.addBodyElement(String.format("behaviorImpl = new %sImpl(%s);",
        capitalizeFirst(componentName), parameters.toString()));

    this.implVisitor.addConstructor(implConstructor.build());
    this.classVisitor.addConstructor(builder.build());
  }

  /**
   * Add expected compute method to the class visitor.
   */
  private void addCompute() {
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

  /**
   * Add expected setResult method to the class visitor.
   */
  private void addSetResult() {
    Method.Builder methodBuilder;
    methodBuilder = Method.getBuilder().setName("setResult");
    ASTSimpleReferenceType resultType =
        JavaDSLMill.simpleReferenceTypeBuilder()
            .setNameList(Lists.newArrayList(componentName+"Result"))
            .build();
    methodBuilder.addParameter("result", resultType);
    classVisitor.addMethod(methodBuilder.build());
  }

  /**
   * Add expected initialize method to the class visitor.
   */
  private void addInitialize() {
    Method.Builder methodBuilder;
    methodBuilder = Method.getBuilder().setName("initialize");
    methodBuilder.addBodyElement(
        String.format("final %sResult result = %s.%s;",
            componentName,
            GenerationConstants.BEHAVIOR_IMPL,
            GenerationConstants.METHOD_INITIAL_VALUES));
    methodBuilder.addBodyElement("setResult(result);");
    classVisitor.addMethod(methodBuilder.build());
  }

  /**
   * Add expected toString methods to the respective visitors
   */
  private void addToString(){
    ASTSimpleReferenceType stringType
        = JavaDSLMill.simpleReferenceTypeBuilder()
              .setNameList(Lists.newArrayList("String"))
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
    // Add empty constructors
    if(symbol.getSuperComponent().isPresent()){
      builder.addBodyElement("super();");
    }
    builder.setName(symbol.getName() + "Input");
    this.inputVisitor.addConstructor(builder.build());
    // Add parameterized constructors
    if (!symbol.getIncomingPorts().isEmpty()) {
      for (PortSymbol port : symbol.getIncomingPorts()){
        final ASTPort astNode = (ASTPort) port.getAstNode().get();
        final String portName = port.getName();
        builder.addParameter(portName, astNode.getType());

        builder.addBodyElement(String.format("this.%s=%s", portName, portName));
      }

      // TODO Call to super class.
//      if(symbol.getSuperComponent().isPresent()){
//        StringBuilder superCall = new StringBuilder("super(");
//        final ComponentSymbol superSymbol = symbol.getSuperComponent().get().getReferencedSymbol();
//        for (PortSymbol superInPort : superSymbol.getIncomingPorts()) {
//          superCall.append(superInPort.getName()).append(",");
//        }
//        if(superCall.charAt(superCall.length() - 1) == 'c'){
//          superCall.deleteCharAt(superCall.length() - 1);
//        }
//        superCall.append(")");
//        builder.addBodyElement(superCall.toString();
//      }

      this.inputVisitor.addConstructor(builder.build());
    }


    builder.setName(symbol.getName() + "Result");
    this.resultVisitor.addConstructor(builder.build());


    if(!symbol.getOutgoingPorts().isEmpty()){

    }


  }

  @Override
  public void visit(ASTInterface node){
//    node.getPorts()
  }

  @Override
  public void visit(ASTPort node) {
    final PortSymbol symbol = (PortSymbol) node.getSymbolOpt().get();
    final ASTSimpleReferenceType type = (ASTSimpleReferenceType) node.getType();

    // Type parameters for the Port field type
    // The type parameters consist of the type of the ASTPort type
    final ASTTypeArguments typeArgs = JavaDSLMill.typeArgumentsBuilder()
                                          .setTypeArgumentList(Lists.newArrayList(type))
                                          .build();

    // Build the AST node for the type Port<Type>
    ASTType expectedType = JavaDSLMill.simpleReferenceTypeBuilder()
                               .setNameList(Lists.newArrayList("Port"))
                               .setTypeArguments(typeArgs)
                               .build();
    final List<String> names = node.getNameList();
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
                .setReturnType(GenerationConstants.VOID_TYPE)
                .addParameter("port", expectedType)
                .setName("setPort" + capitalizeFirst(name));
      if (node.isIncoming()) {
        classVisitor.addMethod(setter.build());
      } else if (node.isOutgoing()) {
        setter = Method
            .getBuilder()
            .setReturnType(GenerationConstants.VOID_TYPE)
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
                  name, GenerationConstants.PRINTER.prettyprint(type)), -1);
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


  @Override
  public void visit(ASTAutomaton node) {
    // Add the currentState field
    final ArrayList<String> state = Lists.newArrayList("State");
    ASTSimpleReferenceType currentStateType
        = JavaDSLMill.simpleReferenceTypeBuilder().setNameList(state).build();
    this.implVisitor.addField("currentState", currentStateType);
  }

  @Override
  public void visit(ASTStateDeclaration node) {
    final Set<String> stateNames = node.getStateList().stream().map(ASTState::getName).collect(Collectors.toSet());
    EnumType enumType = new EnumType("State", stateNames);

    this.implVisitor.addEnumType(enumType);
  }

  @Override
  public void visit(ASTState node) {

  }

  @Override
  public void visit(ASTInitialStateDeclaration node) {

  }

  @Override
  public void visit(ASTTransition node) {

  }

  private Optional<Method> getMethod(String name){
    return classVisitor.getMethods()
               .stream()
               .filter(m -> m.getName().equals(name))
               .findFirst();
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
