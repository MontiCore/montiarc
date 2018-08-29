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
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.types.types._ast.*;
import montiarc._ast.*;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ConnectorSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._visitor.MontiArcVisitor;

import java.util.*;
import java.util.stream.Collectors;

import static generation.GenerationConstants.PRINTER;

/**
 * Collects information about the generated java classes that is expected
 * to be present.
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
  private Map<String, ASTType> types;

  public ComponentElementsCollector(ComponentSymbol symbol, String name) {
    this.symbol = symbol;
    this.componentName = name;
    this.helper = new ComponentHelper(symbol);
    this.classVisitor = new GeneratedComponentClassVisitor(name);
    this.inputName = name + "Input";
    this.resultName = name + "Result";
    this.implName = name + "Impl";
    this.inputVisitor = new GeneratedComponentClassVisitor(inputName);
    this.implVisitor = new GeneratedComponentClassVisitor(implName);
    this.resultVisitor = new GeneratedComponentClassVisitor(resultName);
    initTypes();
  }

  private void initTypes() {
    types = new HashMap<>();

    if(!symbol.getFormalTypeParameters().isEmpty()) {
      ASTTypeArgumentsBuilder typeArgumentsBuilder = JavaDSLMill.typeArgumentsBuilder();
      for (JTypeSymbol typeSymbol : symbol.getFormalTypeParameters()) {
        final ASTSimpleReferenceType typeParam
            = JavaDSLMill
                  .simpleReferenceTypeBuilder()
                  .addName(typeSymbol.getName())
                  .build();
        typeArgumentsBuilder.addTypeArgument(typeParam);
      }

      types.put("INPUT_CLASS_TYPE",
          JavaDSLMill.simpleReferenceTypeBuilder()
              .setNameList(Lists.newArrayList(inputName))
              .setTypeArguments(typeArgumentsBuilder.build())
              .build());
      types.put("RESULT_CLASS_TYPE",
          JavaDSLMill.simpleReferenceTypeBuilder()
              .setNameList(Lists.newArrayList(resultName))
              .setTypeArguments(typeArgumentsBuilder.build())
              .build());
    } else {
      types.put("INPUT_CLASS_TYPE",
          JavaDSLMill.simpleReferenceTypeBuilder()
              .setNameList(Lists.newArrayList(inputName))
              .build());
      types.put("RESULT_CLASS_TYPE",
          JavaDSLMill.simpleReferenceTypeBuilder()
              .setNameList(Lists.newArrayList(resultName))
              .build());
    }

    types.put("STRING_TYPE", JavaDSLMill.simpleReferenceTypeBuilder()
                                 .setNameList(Lists.newArrayList("String"))
                                 .build());
    types.put("CHARACTER_TYPE", JavaDSLMill.simpleReferenceTypeBuilder()
                                 .setNameList(Lists.newArrayList("Character"))
                                 .build());
    types.put("BOOLEAN_TYPE", JavaDSLMill.simpleReferenceTypeBuilder()
                                 .setNameList(Lists.newArrayList("Boolean"))
                                 .build());
    types.put("SHORT_TYPE", JavaDSLMill.simpleReferenceTypeBuilder()
                                 .setNameList(Lists.newArrayList("Short"))
                                 .build());
    types.put("BYTE_TYPE", JavaDSLMill.simpleReferenceTypeBuilder()
                                 .setNameList(Lists.newArrayList("Byte"))
                                 .build());
    types.put("INTEGER_TYPE", JavaDSLMill.simpleReferenceTypeBuilder()
                                 .setNameList(Lists.newArrayList("Integer"))
                                 .build());
    types.put("LONG_TYPE", JavaDSLMill.simpleReferenceTypeBuilder()
                                 .setNameList(Lists.newArrayList("Long"))
                                 .build());
    types.put("FLOAT_TYPE", JavaDSLMill.simpleReferenceTypeBuilder()
                                 .setNameList(Lists.newArrayList("Float"))
                                 .build());
    types.put("DOUBLE_TYPE", JavaDSLMill.simpleReferenceTypeBuilder()
                                 .setNameList(Lists.newArrayList("Double"))
                                 .build());
    types.put("OBJECT_TYPE", JavaDSLMill.simpleReferenceTypeBuilder()
                                 .setNameList(Lists.newArrayList("Object"))
                                 .build());
  }

  @Override
  public void visit(ASTParameter node) {
    final String parameterName = node.getName();
    final ASTType parameterType = boxPrimitiveType(node.getType());
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
  public void visit(ASTComponent node) {
    // Add elements which are not found by the visitor
    HCJavaDSLTypeResolver typeResolver = new HCJavaDSLTypeResolver();

    // impl field
    addImplField();

    // Common methods
    // setup
    addSetUp();

    Method.Builder methodBuilder;

    // init
    addInit();

    // compute
    addCompute();

    // update
    addUpdate();

    // setResult
    addSetResult();

    // initialize
    addInitialize();

    // Constructor
    addConstructors(node);
    addInputAndResultConstructor();

    addToString();
    addFixedImports();

    // Impl methods
    addGetInitialValues();
    addImplCompute();

    // Subcomponents
    addSubcomponents();

    // Implemented interfaces
    classVisitor.addImplementedInterface("IComponent");
    resultVisitor.addImplementedInterface("IResult");
    inputVisitor.addImplementedInterface("IInput");

    // Determine generic type parameters for the implementation class
    ASTTypeArgument inputArg = this.types.get("INPUT_CLASS_TYPE");
    ASTTypeArgument resultArg = this.types.get("RESULT_CLASS_TYPE");

    final ArrayList<ASTTypeArgument> typeArguments = Lists.newArrayList(inputArg, resultArg);
    ASTTypeArguments typeArgs
        = JavaDSLMill.typeArgumentsBuilder().setTypeArgumentList(typeArguments).build();
    implVisitor.addImplementedInterface("IComputable", typeArgs);

    // Add super classes to the signatures
    if (symbol.getSuperComponent().isPresent()) {
      final String fullName = symbol.getSuperComponent().get().getFullName();
      classVisitor.setSuperClass(fullName);
      resultVisitor.setSuperClass(fullName + "Result");
      inputVisitor.setSuperClass(fullName + "Input");
    }
  }

  private void addInit() {
    Method.Builder methodBuilder = Method.getBuilder().setName("init");

    if (symbol.getSuperComponent().isPresent()) {
      methodBuilder.addBodyElement("super.init();");
    }

    // Set up unused input ports
    for (PortSymbol inPort : this.symbol.getIncomingPorts()) {
      methodBuilder.addBodyElement(
          String.format("if (this.%s == null) {this.%s = Port.EMPTY;}",
              inPort.getName(), inPort.getName()));
    }

    if (this.symbol.isAtomic()) {
      classVisitor.addMethod(methodBuilder.build());
      return;
    }
    for (ConnectorSymbol connector : this.symbol.getConnectors()) {
      if (helper.isIncomingPort(this.symbol, connector, false, connector.getTarget())) {
        methodBuilder.addBodyElement(
            String.format("%s.setPort%s(%s.getPort%s());",
                helper.getConnectorComponentName(connector, false),
                capitalizeFirst(this.helper.getConnectorPortName(connector, false)),
                helper.getConnectorComponentName(connector, true),
                capitalizeFirst(this.helper.getConnectorPortName(connector, true))));
      }
    }

    // init subcomponents
    for (ComponentInstanceSymbol subCompInstance : this.symbol.getSubComponents()) {
      methodBuilder.addBodyElement(
          String.format("this.%s.init();", subCompInstance.getName()));
    }

    classVisitor.addMethod(methodBuilder.build());
  }

  private void addSetUp() {
    Method.Builder methodBuilder = Method.getBuilder();

    // Add reference to super component
    if (this.symbol.getSuperComponent().isPresent()) {
      methodBuilder.addBodyElement("super.setUp();");
    }

    methodBuilder.setName("setUp");

    // Decomposed components require additional statements
    if (this.symbol.isDecomposed()) {
      for (ComponentInstanceSymbol subCompInstance : this.symbol.getSubComponents()) {

        String parameterString = subCompInstance.getConfigArguments()
            .stream()
            .map(PRINTER::prettyprint)
            .collect(Collectors.joining(", "));

        // TODO Add parameters
        methodBuilder.addBodyElement(
            String.format("this.%s = new %s(%s)",
                subCompInstance.getName(),
                helper.getSubComponentTypeName(subCompInstance),
                parameterString)
        );
      }

      // There should be a line present for each subcomponent which calls
      // the subcomponents setUp method
      for (ComponentInstanceSymbol subCompInstance : this.symbol.getSubComponents()) {
        methodBuilder.addBodyElement(
            String.format("this.%s.setUp();", subCompInstance.getName()));
      }
    }
    // Output ports
    for (PortSymbol outPort : this.symbol.getOutgoingPorts()) {
      final ASTPort astPort = (ASTPort) outPort.getAstNode().get();
      methodBuilder.addBodyElement(
          String.format("this.%s = new Port<%s>();",
              outPort.getName(),
              PRINTER.prettyprint(boxPrimitiveType(astPort.getType()))));
    }


    if (this.symbol.isAtomic()) {
      methodBuilder.addBodyElement("this.initialize();");
    } else {
      for (ConnectorSymbol connector : this.symbol.getConnectors()) {
        if (!helper.isIncomingPort(this.symbol, connector, false, connector.getTarget())) {
          methodBuilder.addBodyElement(
              String.format("%s.setPort%s(%s.getPort%s());",
                  helper.getConnectorComponentName(connector, false),
                  capitalizeFirst(this.helper.getConnectorPortName(connector, false)),
                  helper.getConnectorComponentName(connector, true),
                  capitalizeFirst(this.helper.getConnectorPortName(connector, true))));
        }
      }
    }

    this.classVisitor.addMethod(methodBuilder.build());
  }

  private void addUpdate() {
    Method.Builder methodBuilder = Method.getBuilder().setName("update");
    if (symbol.getSuperComponent().isPresent()) {
      methodBuilder.addBodyElement("super.update();");
    }

    if (this.symbol.isAtomic()) {
      for (PortSymbol outPort : this.symbol.getOutgoingPorts()) {
        methodBuilder.addBodyElement(
            String.format("this.%s.update();", outPort.getName()));
      }
    } else {
      for (ComponentInstanceSymbol subComp : this.symbol.getSubComponents()) {
        methodBuilder.addBodyElement(
            String.format("this.%s.update();", subComp.getName()));
      }
    }
    classVisitor.addMethod(methodBuilder.build());
  }

  private void addSubcomponents() {
    if (symbol.getSubComponents().isEmpty()) {
      return;
    }

    for (ComponentInstanceSymbol instanceSymbol : symbol.getSubComponents()) {
      final ComponentSymbol componentSymbol
          = instanceSymbol.getComponentType().getReferencedSymbol();
      final String componentName = componentSymbol.getName();
      final ASTSimpleReferenceType fieldType
          = JavaDSLMill.simpleReferenceTypeBuilder().addName(componentName).build();
      this.classVisitor.addField(instanceSymbol.getName(), fieldType);

      // Add getter
      final Method.Builder builder = Method.getBuilder();
      builder.setName(
          String.format("getComponent%s",
              capitalizeFirst(instanceSymbol.getName())));
      builder.addBodyElement("return this." + instanceSymbol.getName());
      builder.setReturnType(fieldType);
      this.classVisitor.addMethod(builder.build());
    }

  }

  private void addImplField() {
    // Precondition: The component is not a composed component
    if (!symbol.isAtomic()) {
      return;
    }
    ASTTypeArgumentsBuilder typeArgs
        = JavaDSLMill.typeArgumentsBuilder();

    typeArgs.setTypeArgumentList(
        Lists.newArrayList(
            this.types.get("INPUT_CLASS_TYPE"),
            this.types.get("RESULT_CLASS_TYPE")));

    ASTType expectedType = JavaDSLMill.simpleReferenceTypeBuilder()
                               .setNameList(Lists.newArrayList("IComputable"))
                               .setTypeArguments(typeArgs.build())
                               .build();
    classVisitor.addField("behaviorImpl", expectedType);
  }

  private void addGetInitialValues() {
    Method method = Method.getBuilder()
                        .setName("getInitialValues")
                        .setReturnType(this.types.get("RESULT_CLASS_TYPE"))
                        .build();
    this.implVisitor.addMethod(method);
  }

  private void addImplCompute() {
    if (this.symbol.isDecomposed()) {
      return;
    }

    ASTType paramType = this.types.get("INPUT_CLASS_TYPE");
    Method method = Method.getBuilder()
                        .setName("compute")
                        .setReturnType(this.types.get("RESULT_CLASS_TYPE"))
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
    for (JFieldSymbol paramSymbol : this.symbol.getConfigParameters()) {
      parameters.append(paramSymbol.getName()).append(",");

      if (paramSymbol.getType().existsReferencedSymbol()) {
        final String fullParameterTypeName
            = paramSymbol.getType().getReferencedSymbol().getFullName();

        ASTType paramType =
            JavaDSLMill.simpleReferenceTypeBuilder()
                .setNameList(Lists.newArrayList(fullParameterTypeName))
                .build();

        builder.addParameter(paramSymbol.getName(), paramType);
        implConstructor.addParameter(paramSymbol.getName(), paramType);
      }
    }
    if (parameters.length() > 0) {
      parameters.deleteCharAt(parameters.length() - 1);
    }

    // Expect impl instance if not decomposed
    if (this.symbol.isAtomic()) {
      StringBuilder implAssignment = new StringBuilder("behaviorImpl = new ");
      implAssignment.append(capitalizeFirst(componentName)).append("Impl");
      if (!symbol.getFormalTypeParameters().isEmpty()) {
        implAssignment.append(getTypeParameterList());
      }
      implAssignment.append("(").append(parameters.toString()).append(");");
      builder.addBodyElement(implAssignment.toString());
    }

    for (ASTParameter parameter : node.getHead().getParameterList()) {
      builder.addBodyElement(String.format("this.%s=%s",
          parameter.getName(), parameter.getName()));
    }


    this.implVisitor.addConstructor(implConstructor.build());
    this.classVisitor.addConstructor(builder.build());
  }

  /**
   * Add expected compute method to the class visitor.
   */
  private void addCompute() {
    Method.Builder methodBuilder;
    methodBuilder = Method.getBuilder().setName("compute");

    if (this.symbol.isAtomic()) {
      StringBuilder inputVariable = new StringBuilder();
      inputVariable.append("final ").append(componentName).append("Input");

      if (!this.symbol.getFormalTypeParameters().isEmpty()) {
        inputVariable.append(getTypeParameterList());
      }

      inputVariable.append("input = new ");
      inputVariable.append(componentName).append("Input");

      // Generic Type arguments
      if(!this.symbol.getFormalTypeParameters().isEmpty()){
        inputVariable.append(getTypeParameterList());
      }

      inputVariable.append("(");
      final String paramList
          = symbol.getIncomingPorts()
                .stream()
                .map(p -> "this." + p.getName() + ".getCurrentValue()")
                .collect(Collectors.joining(", "));
      inputVariable.append(paramList);
      inputVariable.append(");");
      methodBuilder.addBodyElement(inputVariable.toString());

      StringBuilder tryBlock = new StringBuilder();
      tryBlock
          .append("try {")
          .append("// perform calculations")
          .append("final ").append(componentName).append("Result");
      if (!this.symbol.getFormalTypeParameters().isEmpty()) {
        tryBlock.append(getTypeParameterList());
      }
      tryBlock.append("result = behaviorImpl.compute(input);");
      tryBlock.append("// set results to ports");
      tryBlock.append("setResult(result);");
      tryBlock.append("} catch (Exception e) { ");
      tryBlock.append("Log.error(")
          .append("\"").append(componentName).append("\"")
          .append(", e);}");
      methodBuilder.addBodyElement(tryBlock.toString());

    } else {
      for (ComponentInstanceSymbol subCompInstance : this.symbol.getSubComponents()) {
        methodBuilder.addBodyElement(
            String.format("this.%s.compute();", subCompInstance.getName()));
      }
    }
    classVisitor.addMethod(methodBuilder.build());
  }

  private String getTypeParameterList() {
    StringBuilder typeParamList = new StringBuilder();
    typeParamList.append("<");
    final String typeParameterList
        = this.symbol.getFormalTypeParameters()
              .stream()
              .map(Symbol::getName)
              .collect(Collectors.joining(", "));
    typeParamList.append(typeParameterList);
    typeParamList.append(">");
    return typeParamList.toString();
  }

  /**
   * Add expected setResult method to the class visitor.
   */
  private void addSetResult() {
    // Precondition: Component has to be atomic
    if (this.symbol.isDecomposed()) {
      return;
    }
    Method.Builder methodBuilder;
    methodBuilder = Method.getBuilder().setName("setResult");

    methodBuilder.addParameter("result", this.types.get("RESULT_CLASS_TYPE"));
    classVisitor.addMethod(methodBuilder.build());
  }

  /**
   * Add expected initialize method to the class visitor.
   */
  private void addInitialize() {
    // Precondition: Component has to be atomic
    if (this.symbol.isDecomposed()) {
      return;
    }
    Method.Builder methodBuilder;
    methodBuilder = Method.getBuilder().setName("initialize");
    final StringBuilder resultString = new StringBuilder();
    resultString.append("final ").append(componentName).append("Result");
    if(!this.symbol.getFormalTypeParameters().isEmpty()){
      resultString.append(getTypeParameterList());
    }
    resultString
        .append(" result = ")
        .append("behaviorImpl").append(".").append("getInitialValues()").append(";");
    methodBuilder.addBodyElement(resultString.toString());
    methodBuilder.addBodyElement("setResult(result);");
    classVisitor.addMethod(methodBuilder.build());
  }

  /**
   * Add expected toString methods to the respective visitors
   */
  private void addToString() {
    Method.Builder toStringBuilder =
        Method
            .getBuilder()
            .setName("toString")
            .setReturnType(this.types.get("STRING_TYPE"))
            .addBodyElement("String result = \"[\"");

    for (PortSymbol portSymbol : symbol.getOutgoingPorts()) {
      toStringBuilder.addBodyElement(
          String.format("result += \"%s: \" + this.%s + \" \";",
              portSymbol.getName(), portSymbol.getName()));
    }
    toStringBuilder.addBodyElement("return result+\"]\"");
    this.resultVisitor.addMethod(toStringBuilder.build());

    toStringBuilder.clearBodyElements();
    toStringBuilder.addBodyElement("String result = \"[\"");
    for (PortSymbol portSymbol : symbol.getIncomingPorts()) {
      toStringBuilder.addBodyElement(
          String.format("result += \"%s: \" + this.%s + \" \";",
              portSymbol.getName(), portSymbol.getName()));
    }
    toStringBuilder.addBodyElement("return result+\"]\"");
    this.inputVisitor.addMethod(toStringBuilder.build());

  }

  /**
   * Add fixed imports to the respective visitors
   */
  private void addFixedImports() {
    classVisitor.addImport(this.symbol.getPackageName() +
                               this.symbol.getName() + "Result");
    classVisitor.addImport(this.symbol.getPackageName() +
                               this.symbol.getName() + "Input");
    classVisitor.addImport("de.montiarcautomaton.runtimes" +
                               ".timesync.delegation.IComponent");
    classVisitor.addImport("de.montiarcautomaton.runtimes" +
                               ".timesync.delegation.Port");
    classVisitor.addImport("de.montiarcautomaton.runtimes" +
                               ".timesync.implementation.IComputable");
    classVisitor.addImport("de.montiarcautomaton.runtimes." +
                               "Log");

    inputVisitor.addImport("de.montiarcautomaton." +
                               "runtimes.timesync.implementation.IInput");
    resultVisitor.addImport("de.montiarcautomaton." +
                                "runtimes.timesync.implementation.IResult");
  }

  /**
   * Adds expected constructors to the Input and Result visitors from the
   * given ComponentSymbol
   */
  private void addInputAndResultConstructor() {
    Constructor.Builder builder = Constructor.getBuilder();

    // Add empty constructors
    if (symbol.getSuperComponent().isPresent()) {
      builder.addBodyElement("super();");
    }
    builder.setName(symbol.getName() + "Input");
    this.inputVisitor.addConstructor(builder.build());
    // Add parameterized constructors
    for (PortSymbol inPort : this.symbol.getIncomingPorts()) {
      ASTSimpleReferenceType type
          = JavaDSLMill.simpleReferenceTypeBuilder()
                .addAllNames(Lists.newArrayList(helper.getPortTypeName(inPort).split("\\.")))
                .build();
      builder.addParameter(inPort.getName(), type);
    }

    if (!symbol.getIncomingPorts().isEmpty()) {
      for (PortSymbol port : symbol.getIncomingPorts()) {
        final ASTPort astNode = (ASTPort) port.getAstNode().get();
        final String portName = port.getName();
//        builder.addParameter(portName, astNode.getType());

        builder.addBodyElement(String.format("this.%s=%s", portName, portName));
      }

      // TODO Call to super class.
      if (symbol.getSuperComponent().isPresent()) {
        StringBuilder superCall = new StringBuilder("super(");
//        final ComponentSymbol superSymbol = symbol.getSuperComponent().get().getReferencedSymbol();
//        for (PortSymbol superInPort : superSymbol.getIncomingPorts()) {
//          superCall.append(superInPort.getName()).append(",");
//        }
//        if(superCall.charAt(superCall.length() - 1) == 'c'){
//          superCall.deleteCharAt(superCall.length() - 1);
//        }
        superCall.append(")");
//        builder.addBodyElement(superCall.toString();
      }

      this.inputVisitor.addConstructor(builder.build());
    }

    builder = Constructor.getBuilder();
    builder.setName(symbol.getName() + "Result");
    this.resultVisitor.addConstructor(builder.build());

    if (!symbol.getOutgoingPorts().isEmpty()) {
      builder = Constructor.getBuilder();
      builder.setName(symbol.getName() + "Result");
      for (PortSymbol port : symbol.getOutgoingPorts()) {
        final ASTPort astNode = (ASTPort) port.getAstNode().get();
        final String portName = port.getName();
//        builder.addParameter(portName, astNode.getType());

        builder.addBodyElement(String.format("this.%s=%s", portName, portName));
      }

      for (PortSymbol outPort : this.symbol.getOutgoingPorts()) {
        ASTSimpleReferenceType type
            = JavaDSLMill.simpleReferenceTypeBuilder()
                  .addAllNames(Lists.newArrayList(helper.getPortTypeName(outPort).split("\\.")))
                  .build();
        builder.addParameter(outPort.getName(), type);
      }

      this.resultVisitor.addConstructor(builder.build());
    }
  }

  @Override
  public void visit(ASTPort node) {
    final PortSymbol symbol = (PortSymbol) node.getSymbolOpt().get();
    ASTType type = boxPrimitiveType(node.getType());
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

    // Add fields for the ports to the visitors
    classVisitor.addFields(names, expectedType);
    if (node.isOutgoing()) {
      resultVisitor.addFields(names, type);
    }
    if (node.isIncoming()) {
      inputVisitor.addFields(names, type);
    }

    // Add expectations for setter and getter methods
    // Setter
    for (String name : names) {
      final String portNameCapitalized = capitalizeFirst(name);
      if (this.symbol.isDecomposed() || node.isIncoming()) {
        Method.Builder setter
            = Method
                  .getBuilder()
                  .setReturnType(GenerationConstants.VOID_TYPE)
                  .addParameter("port", expectedType)
                  .addBodyElement("this." + name + " = port;")
                  .setName("setPort" + portNameCapitalized);
        classVisitor.addMethod(setter.build());
      }

      // Different object, due to naming differences between component
      // class and result class
      if (node.isOutgoing()) {
        Method.Builder setter
            = Method
                  .getBuilder()
                  .setReturnType(GenerationConstants.VOID_TYPE)
                  .addParameter(name, type)
                  .addBodyElement("return this." + name + ";")
                  .setName("set" + portNameCapitalized);
        resultVisitor.addMethod(setter.build());
      }
    }

    // Getter
    for (String name : names) {
      final Method.Builder getter =
          Method
              .getBuilder()
              .setName("getPort" + capitalizeFirst(name))
              .addBodyElement(String.format("return this.%s;", name))
              .setReturnType(expectedType);
      classVisitor.addMethod(getter.build());
      if (node.isOutgoing()) {
        resultVisitor.addMethod(getter
                                    .setName("get" + capitalizeFirst(name))
                                    .setReturnType(type)
                                    .build());
      } else if (node.isIncoming()) {
        inputVisitor.addMethod(getter
                                   .setName("get" + capitalizeFirst(name))
                                   .setReturnType(type)
                                   .build());
      }
    }
  }

  private ASTType boxPrimitiveType(ASTType type){
    if(type instanceof ASTPrimitiveArrayType){
      // Box the inner type of a primitive array type
      final ASTPrimitiveArrayType primitiveArrayType = (ASTPrimitiveArrayType) type;
      ASTType boxedType = boxPrimitiveType(primitiveArrayType.getComponentType());
      return JavaDSLMill.primitiveArrayTypeBuilder()
          .setDimensions(primitiveArrayType.getDimensions())
          .setComponentType(boxedType)
          .build();

    } else if(type instanceof ASTSimpleReferenceType){
      final ASTSimpleReferenceType simpleReferenceType = (ASTSimpleReferenceType) type;
      ASTSimpleReferenceTypeBuilder resultSimpleRef
          = JavaDSLMill.simpleReferenceTypeBuilder();
      resultSimpleRef.addAllNames(simpleReferenceType.getNameList());
      if (simpleReferenceType.getTypeArgumentsOpt().isPresent()) {
        final ASTTypeArgumentsBuilder typeArgumentsBuilder = JavaDSLMill.typeArgumentsBuilder();
        for (ASTTypeArgument typeArgument : simpleReferenceType.getTypeArguments().getTypeArgumentList()) {
          ASTType boxedTypeArg = boxPrimitiveType((ASTType) typeArgument);
          typeArgumentsBuilder.addTypeArgument(boxedTypeArg);
        }
        resultSimpleRef.setTypeArguments(typeArgumentsBuilder.build());
      }
      return resultSimpleRef.build();

    } else if(type instanceof ASTPrimitiveType){
      ASTPrimitiveType primitiveType = (ASTPrimitiveType) type;
      ASTType result = types.get("OBJECT_TYPE");
      if (primitiveType.isBoolean()) {
        result = types.get("BOOLEAN_TYPE");
      } else if(primitiveType.isByte()){
        result = types.get("BYTE_TYPE");
      } else if(primitiveType.isChar()){
        result = types.get("CHARACTER_TYPE");
      } else if(primitiveType.isDouble()){
        result = types.get("DOUBLE_TYPE");
      } else if(primitiveType.isFloat()){
        result = types.get("FLOAT_TYPE");
      } else if(primitiveType.isInt()){
        result = types.get("INTEGER_TYPE");
      } else if(primitiveType.isLong()){
        result = types.get("LONG_TYPE");
      } else if(primitiveType.isShort()){
        result = types.get("SHORT_TYPE");
      }
      return result;
    }
    return type;
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

  private Optional<Method> getMethod(String name) {
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

  public GeneratedComponentClassVisitor getClassVisitor() {
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
