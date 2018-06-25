/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package generation;

import com.google.common.collect.Lists;
import de.montiarcautomaton.generator.helper.ComponentHelper;
import de.monticore.java.javadsl._ast.*;
import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.java.types.HCJavaDSLTypeResolver;
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
  public void visit(ASTComponent node){
    // Add elements which are not found by the visitor
    HCJavaDSLTypeResolver typeResolver = new HCJavaDSLTypeResolver();

    // impl field
    final String componentName = node.getName();
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
    classVisitor.addField("behaviorImpl", expectedType);

    // Common methods
    // setup
    Method.Builder methodBuilder = Method.getBuilder();
    methodBuilder.setName("setUp").addBodyElement("this.initialize();");
    classVisitor.addMethod(methodBuilder.build());

    methodBuilder = Method.getBuilder().setName("init");
    classVisitor.addMethod(methodBuilder.build());

    methodBuilder = Method.getBuilder().setName("compute");
    classVisitor.addMethod(methodBuilder.build());

    methodBuilder = Method.getBuilder().setName("update");
    classVisitor.addMethod(methodBuilder.build());

    // Constructor
    final Constructor.Builder builder = Constructor.getBuilder();
    builder.setName(componentName);
    for (ASTParameter parameter : node.getHead().getParameters()) {
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
    builder.addBodyElement(String.format("behaviorImpl=new %sImpl(%s);",
        capitalizeFirst(componentName), ""));

    classVisitor.addConstructor(builder.build());

    addInputAndResultConstructor(symbol);


    // Implemented interfaces
    classVisitor.addImplementedInterface("IComponent");
    resultVisitor.addImplementedInterface("IResult");
    inputVisitor.addImplementedInterface("IInput");
  }

  public void addInputAndResultConstructor(ComponentSymbol symbol){
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
        resultVisitor.addMethod(getter.setName("get" + capitalizeFirst(name)).setReturnType(type).build());
      } else if(node.isIncoming()) {
        inputVisitor.addMethod(getter.setName("get" + capitalizeFirst(name)).setReturnType(type).build());
      }
    }
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
