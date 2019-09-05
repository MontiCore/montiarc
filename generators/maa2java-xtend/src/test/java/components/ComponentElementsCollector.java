/* (c) https://github.com/MontiCore/monticore */
/*
 *
 * http://www.se-rwth.de/
 */
package components;

import static infrastructure.GeneratorTestConstants.PRINTER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import de.montiarcautomaton.generator.codegen.xtend.util.Identifier;
import de.montiarcautomaton.generator.codegen.xtend.util.Utils;
import de.montiarcautomaton.generator.helper.ComponentHelper;
import de.montiarcautomaton.generator.visitor.CDAttributeGetterTransformationVisitor;
import de.montiarcautomaton.generator.visitor.NamesInExpressionVisitor;
import de.montiarcautomaton.generator.visitor.NoDataUsageVisitor;
import de.monticore.java.javadsl._ast.ASTImportDeclaration;
import de.monticore.java.javadsl._ast.JavaDSLMill;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.java.types.HCJavaDSLTypeResolver;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.types.types._ast.ASTPrimitiveArrayType;
import de.monticore.types.types._ast.ASTPrimitiveType;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTSimpleReferenceTypeBuilder;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeArgument;
import de.monticore.types.types._ast.ASTTypeArguments;
import de.monticore.types.types._ast.ASTTypeArgumentsBuilder;
import infrastructure.Constructor;
import infrastructure.EnumType;
import infrastructure.Field;
import infrastructure.GeneratedComponentClassVisitor;
import infrastructure.GeneratorTestConstants;
import infrastructure.Method;
import montiarc._ast.ASTAutomaton;
import montiarc._ast.ASTAutomatonBehavior;
import montiarc._ast.ASTBlock;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTConnector;
import montiarc._ast.ASTElement;
import montiarc._ast.ASTIOAssignment;
import montiarc._ast.ASTInitialStateDeclaration;
import montiarc._ast.ASTJavaPBehavior;
import montiarc._ast.ASTParameter;
import montiarc._ast.ASTPort;
import montiarc._ast.ASTState;
import montiarc._ast.ASTStateDeclaration;
import montiarc._ast.ASTValuation;
import montiarc._ast.ASTValueList;
import montiarc._ast.ASTVariableDeclaration;
import montiarc._ast.MontiArcMill;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ComponentSymbolReference;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.TransitionSymbol;
import montiarc._symboltable.VariableSymbol;
import montiarc._visitor.MontiArcVisitor;

/**
 * Collects information about the generated java classes that is expected to be
 * present.
 *
 * @author (last commit) Michael Mutert
 */
public class ComponentElementsCollector implements MontiArcVisitor {
  
  protected Map<String, GeneratedComponentClassVisitor> visitors = new HashMap<>();
  
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
    this.classVisitor = new GeneratedComponentClassVisitor(name, symbol.getPackageName());
    this.inputName = name + "Input";
    this.resultName = name + "Result";
    this.implName = name + "Impl";
    this.inputVisitor = new GeneratedComponentClassVisitor(inputName, symbol.getPackageName());
    this.implVisitor = new GeneratedComponentClassVisitor(implName, symbol.getPackageName());
    this.resultVisitor = new GeneratedComponentClassVisitor(resultName, symbol.getPackageName());
    initTypes();
  }
  
  private void initTypes() {
    types = new HashMap<>();
    
    if (!symbol.getFormalTypeParameters().isEmpty()) {
      ASTTypeArgumentsBuilder typeArgumentsBuilder = MontiArcMill.typeArgumentsBuilder();
      for (JTypeSymbol typeSymbol : symbol.getFormalTypeParameters()) {
        final ASTSimpleReferenceType typeParam = MontiArcMill
            .simpleReferenceTypeBuilder()
            .addName(typeSymbol.getName())
            .build();
        typeArgumentsBuilder.addTypeArgument(typeParam);
      }
      
      types.put("INPUT_CLASS_TYPE",
          MontiArcMill.simpleReferenceTypeBuilder()
              .setNameList(Lists.newArrayList(inputName))
              .setTypeArguments(typeArgumentsBuilder.build())
              .build());
      types.put("RESULT_CLASS_TYPE",
          MontiArcMill.simpleReferenceTypeBuilder()
              .setNameList(Lists.newArrayList(resultName))
              .setTypeArguments(typeArgumentsBuilder.build())
              .build());
    }
    else {
      types.put("INPUT_CLASS_TYPE",
          MontiArcMill.simpleReferenceTypeBuilder()
              .setNameList(Lists.newArrayList(inputName))
              .build());
      types.put("RESULT_CLASS_TYPE",
          MontiArcMill.simpleReferenceTypeBuilder()
              .setNameList(Lists.newArrayList(resultName))
              .build());
    }
  }
  
  /**
   * Visit the component node of the AST. Here the following information is
   * embedded into the respective visitors: Component Class: - Field for the
   * behavior implementation - Information about standard methods - setUp - init
   * - initialize - setResult - compute - update - Constructor - Implemented
   * interface Result Class - Implemented interface - Constructor - toString
   * Input Class - Implemented interface - Constructor - toString
   */
  public void handleComponent() {
    
    ASTComponent node = (ASTComponent) symbol.getAstNode().get();
    
    // Add elements which are not found by the visitor
    HCJavaDSLTypeResolver typeResolver = new HCJavaDSLTypeResolver();
    
    // Parameters
    for (ASTParameter parameter : node.getHead().getParameterList()) {
      final String parameterName = parameter.getName();
      final Optional<ASTValuation> defaultValue = parameter.getDefaultValueOpt();
      final String type = PRINTER.prettyprint(boxPrimitiveType(parameter.getType()));
      classVisitor.addField(parameterName, type);
      if (this.symbol.isAtomic() && this.symbol.hasBehavior()) {
        implVisitor.addField(parameterName, type);
      }
    }
    
    // impl field and Implementations
    addImplField();
    
    Optional<ASTAutomaton> automaton = getAutomaton();
    if (automaton.isPresent()) {
      handleAutomaton(automaton.get());
    }
    Optional<ASTJavaPBehavior> ajava = getJavaPBehavior();
    if (ajava.isPresent()) {
      handleJavaPBehavior(ajava.get());
    }
    
    // Component variables
    addComponentVariableFields();
    
    // Common methods
    // setup
    addSetUp();
    
    Method.Builder methodBuilder;
    
    // Ports
    for (PortSymbol portSymbol : symbol.getPorts()) {
      handlePort(portSymbol);
    }
    
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
    ASTTypeArguments typeArgs = MontiArcMill.typeArgumentsBuilder()
        .setTypeArgumentList(typeArguments).build();
    implVisitor.addImplementedInterface("IComputable", typeArgs);
    
    // Add super classes to the signatures
    if (symbol.getSuperComponent().isPresent()) {
      String fullName = Utils.printSuperClassFQ(symbol);
      fullName = fullName.replaceAll("\\s", "");
      final List<ActualTypeArgument> superTypeArguments = symbol.getSuperComponent().get()
          .getActualTypeArguments();
      classVisitor.setSuperClass(fullName, superTypeArguments);
      resultVisitor.setSuperClass(fullName + "Result", superTypeArguments);
      inputVisitor.setSuperClass(fullName + "Input", superTypeArguments);
    }
  }
  
  private Optional<ASTAutomaton> getAutomaton() {
    ASTComponent astNode = (ASTComponent) symbol.getAstNode().get();
    
    for (ASTElement element : astNode.getBody().getElementList()) {
      if (element instanceof ASTAutomatonBehavior) {
        ASTAutomaton automaton = ((ASTAutomatonBehavior) element).getAutomaton();
        return Optional.of(automaton);
      }
    }
    return Optional.empty();
  }
  
  private Optional<ASTJavaPBehavior> getJavaPBehavior() {
    ASTComponent astNode = (ASTComponent) symbol.getAstNode().get();
    
    for (ASTElement element : astNode.getBody().getElementList()) {
      if (element instanceof ASTJavaPBehavior) {
        return Optional.of((ASTJavaPBehavior) element);
      }
    }
    return Optional.empty();
  }
  
  /**
   * Adds fields for component variables to the set of expected fields.
   */
  private void addComponentVariableFields() {
    for (VariableSymbol variableSymbol : this.symbol.getVariables()) {
      
      if (variableSymbol.getAstNode().isPresent()) {
        final ASTVariableDeclaration astNode = (ASTVariableDeclaration) variableSymbol.getAstNode()
            .get();
        final String fieldTypeString = PRINTER.prettyprint(boxPrimitiveType(astNode.getType()));
        Field field = new Field(variableSymbol.getName(), fieldTypeString);
        
        // The field for the component variable is added to the component
        // visitor
        // and if applicable to the implementation visitor
        classVisitor.addField(field);
        if (this.symbol.isAtomic() && this.symbol.hasBehavior()) {
          implVisitor.addField(field);
        }
      }
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
    for (ASTConnector connector : ((ASTComponent) this.symbol.getAstNode().get()).getConnectors()) {
      for (ASTQualifiedName target : connector.getTargetsList()) {
        if (helper.isIncomingPort(this.symbol, connector.getSource(), target, false)) {
          methodBuilder.addBodyElement(
              String.format("%s.setPort%s(%s.getPort%s());",
                  helper.getConnectorComponentName(connector.getSource(), target, false),
                  capitalizeFirst(
                      this.helper.getConnectorPortName(connector.getSource(), target, false)),
                  helper.getConnectorComponentName(connector.getSource(), target, true),
                  capitalizeFirst(
                      this.helper.getConnectorPortName(connector.getSource(), target, true))));
        }
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
    
    // Decomposed components require additional statements for instantiating the
    // supcomponents
    if (this.symbol.isDecomposed()) {
      for (ComponentInstanceSymbol subCompInstance : this.symbol.getSubComponents()) {
        
        // TODO: Default parameters are missing in the parameters
        final List<ASTExpression> configArguments = subCompInstance.getConfigArguments();
        String parameterString = configArguments
            .stream()
            .map(p -> ComponentHelper.autobox(PRINTER.prettyprint(p)))
            .collect(Collectors.joining(", "));
        
        // Determine the length of the parameter list of the subcomponent to
        // check
        // whether it is necessary to add default values
        final List<ASTExpression> defaultValues = new ArrayList<>();
        final ComponentSymbolReference subCompType = subCompInstance.getComponentType();
        final ComponentSymbol subCompSymbol = subCompType.getReferencedSymbol();
        final List<JFieldSymbol> subCompConfigParams = subCompSymbol.getConfigParameters();
        if (configArguments.size() < subCompConfigParams.size()) {
          int offset = configArguments.size();
          final List<ASTParameter> compParamList = ((ASTComponent) subCompSymbol.getAstNode().get())
              .getHead().getParameterList();
          for (int i = 0; i < subCompConfigParams.size() - configArguments.size(); i++) {
            final ASTParameter astParameter = compParamList.get(offset + i);
            if (astParameter.getDefaultValueOpt().isPresent()) {
              defaultValues.add(astParameter.getDefaultValue().getExpression());
            }
          }
          
          parameterString += ", ";
          parameterString += defaultValues
              .stream()
              .map(p -> ComponentHelper.autobox(PRINTER.prettyprint(p)))
              .collect(Collectors.joining(", "));
        }
        
        methodBuilder.addBodyElement(
            String.format("this.%s = new %s(%s)",
                subCompInstance.getName(),
                helper.getSubComponentTypeName(subCompInstance),
                parameterString));
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
    }
    else {
      for (ASTConnector connector : ((ASTComponent) this.symbol.getAstNode().get())
          .getConnectors()) {
        for (ASTQualifiedName target : connector.getTargetsList()) {
          if (!helper.isIncomingPort(this.symbol, connector.getSource(), target, false)) {
            methodBuilder.addBodyElement(
                String.format("%s.setPort%s(%s.getPort%s());",
                    helper.getConnectorComponentName(connector.getSource(), target, false),
                    capitalizeFirst(
                        this.helper.getConnectorPortName(connector.getSource(), target, false)),
                    helper.getConnectorComponentName(connector.getSource(), target, true),
                    capitalizeFirst(
                        this.helper.getConnectorPortName(connector.getSource(), target, true))));
          }
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
    }
    else {
      for (ComponentInstanceSymbol subComp : this.symbol.getSubComponents()) {
        methodBuilder.addBodyElement(
            String.format("this.%s.update();", subComp.getName()));
      }
    }
    classVisitor.addMethod(methodBuilder.build());
  }
  
  /**
   * TODO Add documentation
   */
  private void addSubcomponents() {
    for (ComponentInstanceSymbol instanceSymbol : symbol.getSubComponents()) {
      final ComponentSymbol componentSymbol = instanceSymbol.getComponentType()
          .getReferencedSymbol();
      String componentTypeString = componentSymbol.getName();
      
      if (instanceSymbol.getComponentType().hasActualTypeArguments()) {
        final String printedTypeArguments = ComponentHelper
            .printTypeArguments(instanceSymbol.getComponentType().getActualTypeArguments());
        componentTypeString += printedTypeArguments;
      }
      this.classVisitor.addField(instanceSymbol.getName(), componentTypeString);
      
      // Add getter
      final Method.Builder builder = Method.getBuilder();
      builder.setName(
          String.format("getComponent%s",
              capitalizeFirst(instanceSymbol.getName())));
      builder.addBodyElement("return this." + instanceSymbol.getName());
      
      builder.setReturnType(componentTypeString);
      this.classVisitor.addMethod(builder.build());
    }
    
  }
  
  private void addImplField() {
    // Precondition: The component is not a composed component
    if (!symbol.isAtomic()) {
      return;
    }
    ASTTypeArgumentsBuilder typeArgs = JavaDSLMill.typeArgumentsBuilder();
    
    typeArgs.setTypeArgumentList(
        Lists.newArrayList(
            this.types.get("INPUT_CLASS_TYPE"),
            this.types.get("RESULT_CLASS_TYPE")));
    
    String implVarName = "behaviorImpl";
    if (new Identifier().containsIdentifier(implVarName, symbol)) {
      implVarName = "r__behaviorImpl";
    }
    
    ASTType expectedType = JavaDSLMill.simpleReferenceTypeBuilder()
        .setNameList(Lists.newArrayList("IComputable"))
        .setTypeArguments(typeArgs.build())
        .build();
    classVisitor.addField(implVarName, PRINTER.prettyprint(expectedType));
  }
  
  private void addImplCompute(ASTAutomaton node) {
    if (this.symbol.isDecomposed()) {
      return;
    }
    
    Method.Builder method = Method.getBuilder().setName("compute");
    String inputVarName = determineIdentifierName("input");
    
    // Local variable assignment and extraction from input
    for (PortSymbol port : symbol.getAllIncomingPorts()) {
      String portTypeString = ComponentHelper.getRealPortTypeString(symbol, port);
      method.addBodyElement(
          "final" + portTypeString + " " + port.getName() + " = "
              + inputVarName + ".get" + capitalizeFirst(port.getName()) + "();");
    }
    
    // Result variable
    String resultVarName = determineIdentifierName("result");
    method.addBodyElement(
        getVariableDeclAndInitialization(resultVarName, resultType()));
    
    // Switch statement representing the automaton transitions
    // TODO
    method.addBodyElement("switch(" + determineIdentifierName("currentState") + "){");
    for (ASTStateDeclaration stateDeclaration : node.getStateDeclarationList()) {
      for (ASTState state : stateDeclaration.getStateList()) {
        method.addBodyElement("case " + state.getName() + ":");
        
        // Transitions from the current state
        List<TransitionSymbol> transitions = symbol.getSpannedScope().getSubScopes()
            .stream()
            .flatMap(
                scope -> scope.<TransitionSymbol> resolveLocally(TransitionSymbol.KIND).stream())
            .filter(t -> t.getSource().getName().equals(state.getName()))
            .collect(Collectors.toList());
        
        for (TransitionSymbol transition : transitions) {
          String guard = "true";
          if (transition.getGuardAST().isPresent()) {
            guard = printNullExpr(
                transition.getGuardAST().get().getGuardExpression().getExpression(), symbol)
                + "(" + helper.printExpression(
                    transition.getGuardAST().get().getGuardExpression().getExpression(), false)
                + ")";
          }
          method.addBodyElement("if(" + guard + ")" + "{");
          
          // Reaction
          if (transition.getReactionAST().isPresent()) {
            for (ASTIOAssignment assignment : transition.getReactionAST().get()
                .getIOAssignmentList()) {
              method.addBodyElement(printReaction(resultVarName, assignment));
            }
          }
          
          // State change
          method.addBodyElement(
              determineIdentifierName("currentState") + " = "
                  + this.symbol.getName() + "State."
                  + transition.getTarget().getName() + ";");
          method.addBodyElement("break;");
          method.addBodyElement("}");
        }
      }
    }
    // Return type and parameters
    method
        .setReturnType(resultType())
        .addParameter(inputVarName, this.types.get("INPUT_CLASS_TYPE"));
    this.implVisitor.addMethod(method.build());
  }
  
  /**
   * Add the compute method to the expected methods for models with a
   * JavaP/AJava implementation.
   * 
   * @param node
   */
  private void addImplCompute(ASTJavaPBehavior node) {
    if (this.symbol.isDecomposed()) {
      return;
    }
    
    Method.Builder method = Method.getBuilder().setName("compute");
    
    String inputVarName = determineIdentifierName("input");
    
    // TODO Impl Add compute body
    method
        .setReturnType(resultType())
        .addParameter(inputVarName, this.types.get("INPUT_CLASS_TYPE"));
    this.implVisitor.addMethod(method.build());
  }
  
  /**
   * In case identifier names are used in the model the names in the generated
   * classes should be adapted
   * 
   * @param name Name to check
   * @return The adapted name, if the name occurs in the model. The name,
   * otherwise.
   */
  private String determineIdentifierName(String name) {
    if (new Identifier().containsIdentifier(name, symbol)) {
      return "r__" + name;
    }
    else {
      return name;
    }
  }
  
  /**
   * Add expected constructor to the class visitor. Add the constructor to the
   * impl visitor in case it is applicable.
   */
  private void addConstructors(ASTComponent node) {
    final Constructor.Builder builder = Constructor.getBuilder();
    final Constructor.Builder implConstructorBuilder = Constructor.getBuilder();
    
    builder.setName(componentName);
    implConstructorBuilder.setName(implName);
    
    // Names of the component parameters
    String paramNamesListString = node.getHead().getParameterList()
        .stream()
        .map(ASTParameter::getName)
        .collect(Collectors.joining(", "));
    
    // Add the component parameters as expected parameters for the
    // components class and the implementation class
    for (ASTParameter astParameter : node.getHead().getParameterList()) {
      final String parameterName = astParameter.getName();
      ASTType paramType = boxPrimitiveType(astParameter.getType());
      builder.addParameter(parameterName, eraseTypes(paramType));
      implConstructorBuilder.addParameter(parameterName, paramType);
    }
    
    // Expect impl instance if not decomposed
    if (this.symbol.isAtomic()) {
      
      // Deal with possible naming problems
      String implVarName = "behaviorImpl";
      if (new Identifier().containsIdentifier(implVarName, symbol)) {
        implVarName = "r__behaviorImpl";
      }
      
      StringBuilder implAssignment = new StringBuilder(implVarName + " = new ");
      implAssignment.append(capitalizeFirst(componentName)).append("Impl");
      if (!symbol.getFormalTypeParameters().isEmpty()) {
        implAssignment.append(getTypeParameterList());
      }
      implAssignment.append("(").append(paramNamesListString).append(");");
      builder.addBodyElement(implAssignment.toString());
      
    }
    
    for (ASTParameter parameter : node.getHead().getParameterList()) {
      builder.addBodyElement(
          String.format("this.%s=%s", parameter.getName(), parameter.getName()));
      implConstructorBuilder.addBodyElement(
          String.format("this.%s=%s", parameter.getName(), parameter.getName()));
    }
    
    this.implVisitor.addConstructor(implConstructorBuilder.build());
    this.classVisitor.addConstructor(builder.build());
  }
  
  private ASTType eraseTypes(ASTType paramType) {
    if (paramType instanceof ASTSimpleReferenceType) {
      final ASTSimpleReferenceType simpleRefParamType = (ASTSimpleReferenceType) paramType;
      if (simpleRefParamType.getTypeArgumentsOpt().isPresent()) {
        simpleRefParamType.setTypeArgumentsAbsent();
      }
    }
    return paramType;
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
      if (!this.symbol.getFormalTypeParameters().isEmpty()) {
        inputVariable.append(getTypeParameterList());
      }
      
      inputVariable.append("(");
      final String paramList = symbol.getAllIncomingPorts()
          .stream()
          .map(p -> "this.getPort" + capitalizeFirst(p.getName()) + "()"
              + ".getCurrentValue()")
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
      String implVarName = "behaviorImpl";
      if (new Identifier().containsIdentifier(implVarName, symbol)) {
        implVarName = "r__behaviorImpl";
      }
      tryBlock.append("result = ").append(implVarName)
          .append(".compute(").append("input").append(");");
      tryBlock.append("// set results to ports");
      tryBlock.append("setResult(result);");
      tryBlock.append("} catch (Exception e) { ");
      tryBlock.append("Log.error(")
          .append("\"").append(componentName).append("\"")
          .append(", e);}");
      methodBuilder.addBodyElement(tryBlock.toString());
      
    }
    else {
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
    final String typeParameterList = this.symbol.getFormalTypeParameters()
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
    if (!this.symbol.getFormalTypeParameters().isEmpty()) {
      resultString.append(getTypeParameterList());
    }
    
    String implVarName = "behaviorImpl";
    if (new Identifier().containsIdentifier(implVarName, symbol)) {
      implVarName = "r__behaviorImpl";
    }
    resultString
        .append(" result = ")
        .append(implVarName).append(".").append("getInitialValues()").append(";");
    methodBuilder.addBodyElement(resultString.toString());
    methodBuilder.addBodyElement("setResult(result);");
    methodBuilder.addBodyElement("this.update();");
    classVisitor.addMethod(methodBuilder.build());
  }
  
  /**
   * Add expected toString methods to the respective visitors
   */
  private void addToString() {
    Method.Builder toStringBuilder = Method
        .getBuilder()
        .setName("toString")
        .setReturnType(PRINTER.prettyprint(GeneratorTestConstants.types.get("STRING_TYPE")))
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
   * Adds expected constructors to the Input and Result visitors from the given
   * ComponentSymbol
   */
  private void addInputAndResultConstructor() {
    Constructor.Builder inputConstructorBuilder = Constructor.getBuilder();
    
    // Add default constructor as expected constructor
    if (symbol.getSuperComponent().isPresent()) {
      inputConstructorBuilder.addBodyElement("super();");
    }
    inputConstructorBuilder.setName(symbol.getName() + "Input");
    this.inputVisitor.addConstructor(inputConstructorBuilder.build());
    
    // Add parameterized constructors which have a parameter for each incoming
    // port and a call to the super component, if present, with the input ports
    // inherited from the super component.
    // This constructor is only expected if the number of incoming ports,
    // inherited and not inherited is greater than 0.
    final Collection<PortSymbol> incomingPorts = this.symbol.getAllIncomingPorts();
    
    if (!incomingPorts.isEmpty()) {
      inputConstructorBuilder = Constructor.getBuilder();
      inputConstructorBuilder.setName(symbol.getName() + "Input");
      
      for (PortSymbol inPort : incomingPorts) {
        ASTSimpleReferenceType type = JavaDSLMill.simpleReferenceTypeBuilder()
            .addAllNames(Lists.newArrayList(
                ComponentHelper.autobox(
                    ComponentHelper.getRealPortTypeString(this.symbol, inPort)).split("\\.")))
            .build();
        inputConstructorBuilder.addParameter(inPort.getName(), type);
      }
      
      // Adds expected call to the constructor of the super components *Input
      // constructor with parameters for all inherited ports
      if (symbol.getSuperComponent().isPresent()) {
        final ComponentSymbol superSymbol = symbol.getSuperComponent().get();
        StringBuilder superCall = new StringBuilder("super(");
        final List<PortSymbol> superCompAllIncomingPorts = superSymbol.getAllIncomingPorts();
        final String superCallParameters = superCompAllIncomingPorts
            .stream()
            .map(CommonSymbol::getName)
            .collect(Collectors.joining(", "));
        superCall.append(superCallParameters);
        superCall.append(")");
        inputConstructorBuilder.addBodyElement(superCall.toString());
      }
      
      // Add the expected initializers for all incoming ports that are not
      // inherited
      for (PortSymbol port : symbol.getIncomingPorts()) {
        final String portName = port.getName();
        inputConstructorBuilder.addBodyElement(
            String.format("this.%s=%s", portName, portName));
      }
      
      this.inputVisitor.addConstructor(inputConstructorBuilder.build());
    }
    
    Constructor.Builder resultConstructorBuilder = Constructor.getBuilder();
    resultConstructorBuilder.setName(symbol.getName() + "Result");
    // Add default constructor as expected constructor
    if (symbol.getSuperComponent().isPresent()) {
      resultConstructorBuilder.addBodyElement("super();");
    }
    this.resultVisitor.addConstructor(resultConstructorBuilder.build());
    
    // Add parameterized constructors which have a parameter for each outgoing
    // port and a call to the super component, if present, with the output ports
    // inherited from the super component.
    // This constructor is only expected if the number of outgoing ports,
    // inherited and not inherited is greater than 0.
    if (!symbol.getAllOutgoingPorts().isEmpty()) {
      resultConstructorBuilder = Constructor.getBuilder();
      resultConstructorBuilder.setName(symbol.getName() + "Result");
      
      for (PortSymbol outPort : this.symbol.getAllOutgoingPorts()) {
        ASTSimpleReferenceType type = JavaDSLMill.simpleReferenceTypeBuilder()
            .addAllNames(Lists.newArrayList(
                ComponentHelper.autobox(
                    ComponentHelper.getRealPortTypeString(this.symbol, outPort)).split("\\.")))
            .build();
        resultConstructorBuilder.addParameter(outPort.getName(), type);
      }
      
      // Adds expected call to the constructor of the super components *Input
      // constructor with parameters for all inherited ports
      if (symbol.getSuperComponent().isPresent()) {
        final ComponentSymbol superSymbol = symbol.getSuperComponent().get();
        
        StringBuilder superCall = new StringBuilder("super(");
        final List<PortSymbol> superCompAllOutgoingPorts = superSymbol.getAllOutgoingPorts();
        final String superCallParameters = superCompAllOutgoingPorts
            .stream()
            .map(CommonSymbol::getName)
            .collect(Collectors.joining(", "));
        superCall.append(superCallParameters).append(")");
        
        inputConstructorBuilder.addBodyElement(superCall.toString());
      }
      
      // Add the expected initializers for all outgoing ports that are not
      // inherited
      for (PortSymbol port : symbol.getOutgoingPorts()) {
        final String portName = port.getName();
        resultConstructorBuilder.addBodyElement(
            String.format("this.%s=%s", portName, portName));
      }
      
      this.resultVisitor.addConstructor(resultConstructorBuilder.build());
    }
  }
  
  public void handlePort(PortSymbol symbol) {
    ASTPort node = (ASTPort) symbol.getAstNode().get();
    final ASTType type = boxPrimitiveType(node.getType());
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
    classVisitor.addFields(names, PRINTER.prettyprint(expectedType));
    final String printedType = PRINTER.prettyprint(type);
    if (node.isOutgoing()) {
      resultVisitor.addFields(names, printedType);
    }
    if (node.isIncoming()) {
      inputVisitor.addFields(names, printedType);
    }
    
    // Add expectations for setter and getter methods
    // Setter
    for (String name : names) {
      final String portNameCapitalized = capitalizeFirst(name);
      Method.Builder setter = Method
          .getBuilder()
          .setReturnType(GeneratorTestConstants.VOID_STRING)
          .addParameter(name, expectedType)
          .addBodyElement("this." + name + " = " + name + ";")
          .setName("setPort" + portNameCapitalized);
      classVisitor.addMethod(setter.build());
      
      // Different object, due to naming differences between component
      // class and result class
      if (node.isOutgoing()) {
        setter = Method
            .getBuilder()
            .setReturnType(GeneratorTestConstants.VOID_STRING)
            .addParameter(name, type)
            .addBodyElement("this." + name + " = " + name + ";")
            .setName("set" + portNameCapitalized);
        resultVisitor.addMethod(setter.build());
      }
    }
    
    // Getter
    for (String name : names) {
      final Method.Builder getter = Method
          .getBuilder()
          .setName("getPort" + capitalizeFirst(name))
          .addBodyElement(String.format("return this.%s;", name))
          .setReturnType(PRINTER.prettyprint(expectedType));
      classVisitor.addMethod(getter.build());
      if (node.isOutgoing()) {
        resultVisitor.addMethod(getter
            .setName("get" + capitalizeFirst(name))
            .setReturnType(printedType)
            .build());
      }
      else if (node.isIncoming()) {
        inputVisitor.addMethod(getter
            .setName("get" + capitalizeFirst(name))
            .setReturnType(printedType)
            .build());
      }
    }
  }
  
  /**
   * Box the primitive types.
   * 
   * @param type
   * @return The node of the boxed primitive type.
   */
  private ASTType boxPrimitiveType(ASTType type) {
    if (type instanceof ASTPrimitiveArrayType) {
      // Box the type of a primitive array type
      final ASTPrimitiveArrayType primitiveArrayType = (ASTPrimitiveArrayType) type;
      ASTType boxedType = boxPrimitiveType(primitiveArrayType.getComponentType());
      return MontiArcMill.primitiveArrayTypeBuilder()
          .setDimensions(primitiveArrayType.getDimensions())
          .setComponentType(boxedType)
          .build();
      
    }
    else if (type instanceof ASTSimpleReferenceType) {
      // Box all type parameters of the reference type
      final ASTSimpleReferenceType simpleReferenceType = (ASTSimpleReferenceType) type;
      ASTSimpleReferenceTypeBuilder resultSimpleRefTypeBuilder = MontiArcMill
          .simpleReferenceTypeBuilder();
      resultSimpleRefTypeBuilder.addAllNames(simpleReferenceType.getNameList());
      
      if (simpleReferenceType.getTypeArgumentsOpt().isPresent()) {
        final ASTTypeArgumentsBuilder typeArgumentsBuilder = MontiArcMill.typeArgumentsBuilder();
        for (ASTTypeArgument typeArgument : simpleReferenceType.getTypeArguments()
            .getTypeArgumentList()) {
          // Recoursively box type arguments
          ASTType boxedTypeArg = boxPrimitiveType((ASTType) typeArgument);
          typeArgumentsBuilder.addTypeArgument(boxedTypeArg);
        }
        resultSimpleRefTypeBuilder.setTypeArguments(typeArgumentsBuilder.build());
      }
      return resultSimpleRefTypeBuilder.build();
      
    }
    else if (type instanceof ASTPrimitiveType) {
      // Base case: Box primitive types
      ASTPrimitiveType primitiveType = (ASTPrimitiveType) type;
      ASTType result = GeneratorTestConstants.types.get("OBJECT_TYPE");
      if (primitiveType.isBoolean()) {
        result = GeneratorTestConstants.types.get("BOOLEAN_TYPE");
      }
      else if (primitiveType.isByte()) {
        result = GeneratorTestConstants.types.get("BYTE_TYPE");
      }
      else if (primitiveType.isChar()) {
        result = GeneratorTestConstants.types.get("CHARACTER_TYPE");
      }
      else if (primitiveType.isDouble()) {
        result = GeneratorTestConstants.types.get("DOUBLE_TYPE");
      }
      else if (primitiveType.isFloat()) {
        result = GeneratorTestConstants.types.get("FLOAT_TYPE");
      }
      else if (primitiveType.isInt()) {
        result = GeneratorTestConstants.types.get("INTEGER_TYPE");
      }
      else if (primitiveType.isLong()) {
        result = GeneratorTestConstants.types.get("LONG_TYPE");
      }
      else if (primitiveType.isShort()) {
        result = GeneratorTestConstants.types.get("SHORT_TYPE");
      }
      return result;
    }
    return type;
  }
  
  public void handleAutomaton(ASTAutomaton node) {
    // Add the currentState field
    String currentStateVarName = "currentState";
    if (new Identifier().containsIdentifier(currentStateVarName, symbol)) {
      currentStateVarName = "r__" + currentStateVarName;
    }
    this.implVisitor.addField(currentStateVarName, this.symbol.getName() + "State");
    
    final Set<String> stateNames = new HashSet<>();
    for (ASTStateDeclaration stateList : node.getStateDeclarationList()) {
      for (ASTState state : stateList.getStateList()) {
        stateNames.add(state.getName());
      }
    }
    addGetInitialValues(node);
    addImplCompute(node);
    
    // States
    EnumType enumType = new EnumType(symbol.getName() + "State", stateNames);
    this.implVisitor.addEnumType(enumType);
  }
  
  public void handleJavaPBehavior(ASTJavaPBehavior node) {
    addGetInitialValues(node);
    addImplCompute(node);
  }
  
  /**
   * Adds the expected initial values method to the impl visitor
   * 
   * @param node
   */
  private void addGetInitialValues(ASTAutomaton node) {
    // TODO adapt names if conflicted
    
    Method.Builder method = Method.getBuilder()
        .setName("getInitialValues")
        .setReturnType(resultType());
    
    final String resultVarName = "result";
    String resultVarDeclAndInit = getVariableDeclAndInitialization(resultVarName, resultType());
    
    // Initial reaction
    final Optional<ASTInitialStateDeclaration> initialState = node.getInitialStateDeclarationList()
        .stream().findFirst();
    assert initialState.isPresent();
    
    method.addBodyElement(resultVarDeclAndInit);
    
    String initialReactionString = "";
    if (initialState.get().getBlockOpt().isPresent()) {
      final ASTBlock initialReaction = initialState.get().getBlock();
      for (ASTIOAssignment astioAssignment : initialReaction.getIOAssignmentList()) {
        initialReactionString = printReaction(resultVarName, astioAssignment);
      }
      method.addBodyElement(initialReactionString);
    }
    
    String currentStateName = "currentState";
    String initialStateStmt = currentStateName + " = " + this.componentName + "State. "
        + initialState.get().getName() + ";";
    String returnStmt = "return " + resultVarName + ";";
    
    method.addBodyElement(initialStateStmt)
        .addBodyElement(returnStmt);
    
    this.implVisitor.addMethod(method.build());
  }
  
  /**
   * TODO: Write me!
   * 
   * @param resultVarName
   * @param astioAssignment
   */
  private String printReaction(final String resultVarName,
      ASTIOAssignment astioAssignment) {
    StringBuilder reactionStringBuilder = new StringBuilder();
    if (astioAssignment.isAssignment()) {
      // Name = (ValueList | Alternative)
      final String assigneeName = astioAssignment.getName();
      final List<String> outgoingPortNames = symbol.getAllOutgoingPorts()
          .stream()
          .map(CommonSymbol::getName)
          .collect(Collectors.toList());
      if (outgoingPortNames.contains(assigneeName)) {
        // Assignee is a port
        reactionStringBuilder.append(String.format(
            "%s.set%s(%s);",
            resultVarName,
            toFirstUpper(assigneeName),
            printRightHand(astioAssignment)));
      }
      else if (symbol.getVariable(assigneeName).isPresent()) {
        reactionStringBuilder
            .append(assigneeName)
            .append(" = ").append(printRightHand(astioAssignment))
            .append(";");
      }
    }
    else {
      // Function call
      reactionStringBuilder.append(printRightHand(astioAssignment));
    }
    return reactionStringBuilder.toString();
  }
  
  private String printRightHand(ASTIOAssignment astioAssignment) {
    String result = "";
    if (astioAssignment.isPresentValueList()) {
      final ASTValueList valueList = astioAssignment.getValueList();
      if (valueList.isPresentValuation()) {
        result = helper.printExpression(valueList.getValuation().getExpression(),
            astioAssignment.isAssignment());
      }
    }
    return result;
  }
  
  private String toFirstUpper(String input) {
    String result = Character.toUpperCase(input.charAt(0)) + "";
    if (input.length() > 1) {
      result += input.substring(1);
    }
    return result;
  }
  
  private String printNullExpr(ASTExpression expr, ComponentSymbol comp) {
    StringBuilder builder = new StringBuilder();
    NamesInExpressionVisitor visitor = new NamesInExpressionVisitor();
    expr.accept(visitor);
    NoDataUsageVisitor nodataVisitor = new NoDataUsageVisitor();
    expr.accept(nodataVisitor);
    Set<String> portsComparedToNoData = nodataVisitor.getPortsComparedToNoData();
    for (String name : visitor.getFoundNames()) {
      if (symbol.getSpannedScope().resolve(name, VariableSymbol.KIND).isPresent()
          || symbol.getSpannedScope().resolve(name, PortSymbol.KIND).isPresent()) {
        if (!portsComparedToNoData.contains(name)) {
          builder.append(" " + name + "!=null &&");
        }
      }
      
    }
    return builder.toString();
  }
  
  /**
   * Prints the String for declaring and initializing a variable
   * 
   * @param varName Name of the variable
   * @param varType String representing the type of the variable
   * @param parameters The parameters at the initialization
   * @return The String of the Java statement
   */
  private String getVariableDeclAndInitialization(
      String varName, String varType, String parameters, String generics) {
    
    if (generics != "") {
      generics = "<" + generics + ">";
    }
    return String.format("final %s%s %s = new %s%s(%s);",
        resultType(), generics, varName, resultType(), generics, parameters);
  }
  
  /**
   * Prints the String for declaring and initializing a variable
   * 
   * @param varName Name of the variable
   * @param varType String representing the type of the variable
   * @return The String of the Java statement
   */
  private String getVariableDeclAndInitialization(
      String varName, String varType) {
    
    return getVariableDeclAndInitialization(varName, varType, "", "");
  }
  
  /**
   * @return The pretty printed type of the components result class
   */
  private String resultType() {
    return PRINTER.prettyprint(this.types.get("RESULT_CLASS_TYPE"));
  }
  
  private void addGetInitialValues(ASTJavaPBehavior node) {
    final String resultVarName = "result";
    String resultVar = getVariableDeclAndInitialization(resultVarName, resultType(), "",
        printTypeParameters(symbol));
    String returnStmt = "return " + resultVarName + ";";
    Method method = Method.getBuilder()
        .setName("getInitialValues")
        .setReturnType(resultType())
        .addBodyElement(resultVar)
        .addBodyElement(returnStmt)
        .build();
    
    // TODO Impl Add getInitialValuesBody for AJava
    this.implVisitor.addMethod(method);
  }
  
  private String printTypeParameters(ComponentSymbol comp) {
    ASTComponent astNode = (ASTComponent) comp.getAstNode().get();
    if (astNode.getHead().isPresentGenericTypeParameters()) {
      return "<" + astNode.getHead()
          .getGenericTypeParameters()
          .getTypeVariableDeclarationList()
          .stream()
          .map(typeVar -> typeVar.getName())
          .collect(Collectors.joining(","))
          + ">";
    }
    
    return "";
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
  
  // @Override
  // public void visit(ASTMACompilationUnit node) {
  // handleComponent();
  // }
  
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
