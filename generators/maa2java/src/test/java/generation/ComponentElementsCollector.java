/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package generation;

import com.google.common.collect.Lists;
import de.monticore.java.javadsl._ast.*;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeArguments;
import de.monticore.types.types._ast.ASTVoidType;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTInterface;
import montiarc._ast.ASTParameter;
import montiarc._ast.ASTPort;
import montiarc._visitor.MontiArcVisitor;

import java.util.List;

/**
 * TODO
 *
 * @author (last commit)
 */
public class ComponentElementsCollector implements MontiArcVisitor {

  protected GeneratedComponentClassVisitor.Builder resultBuilder;

  public ComponentElementsCollector() {
    this.resultBuilder = new GeneratedComponentClassVisitor.Builder();
  }

  @Override
  public void visit(ASTParameter node) {

  }

  @Override
  public void visit(ASTComponent node){
    // Add elements which are not found by the visitor

    // impl field
    final ASTSimpleReferenceType inputRefType
        = ASTSimpleReferenceType.getBuilder()
              .names(Lists.newArrayList(node.getName() + "Input"))
              .build();
    ASTSimpleReferenceType resultRefType
        = ASTSimpleReferenceType.getBuilder()
              .names(Lists.newArrayList(node.getName() + "Result"))
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
    resultBuilder.addVariable("behaviorImpl", expectedType);

    // Common methods
    // setup
    resultBuilder.addMethod(ASTVoidType.getBuilder().build(), "setUp",
        ASTFormalParameters.getBuilder().build(), "this.initialize();");
    resultBuilder.addMethod(ASTVoidType.getBuilder().build(), "init",
        ASTFormalParameters.getBuilder().build());
    resultBuilder.addMethod(ASTVoidType.getBuilder().build(), "compute",
        ASTFormalParameters.getBuilder().build());
    resultBuilder.addMethod(ASTVoidType.getBuilder().build(), "update",
        ASTFormalParameters.getBuilder().build());
  }

  @Override
  public void visit(ASTInterface node){
//    node.getPorts()
  }

  @Override
  public void visit(ASTPort node) {
    final ASTSimpleReferenceType type = (ASTSimpleReferenceType) node.getType();
    final ASTTypeArguments typeArgs = ASTTypeArguments
                                          .getBuilder()
                                          .typeArguments(Lists.newArrayList(type))
                                          .build();
    ASTType expectedType = ASTSimpleReferenceType
                               .getBuilder()
                               .names(Lists.newArrayList("Port"))
                               .typeArguments(typeArgs)
                               .build();
    final List<String> names = node.getNames();
    resultBuilder.addVariables(names, expectedType);

    // Setter
    if(node.isIncoming()){
      final ASTDeclaratorId declaratorId = ASTDeclaratorId
                                               .getBuilder()
                                               .name("port")
                                               .build();
      final ASTFormalParameter param = ASTFormalParameter
                                           .getBuilder()
                                           .type(expectedType)
                                           .declaratorId(declaratorId)
                                           .build();
      ASTFormalParameterListing listing
          = ASTFormalParameterListing
                .getBuilder()
                .formalParameters(Lists.newArrayList(param))
                .build();

      for (String name : names) {
        final ASTFormalParameters params
            = ASTFormalParameters
                  .getBuilder()
                  .formalParameterListing(listing)
                  .build();
        resultBuilder.addMethod(ASTVoidType.getBuilder().build(),
            "setPort" + name.substring(0, 1).toUpperCase() + name.substring(1),
            params,
            "");
      }
    }

    // Getter
  }

  @Override
  public void visit(ASTImportDeclaration node) {
    resultBuilder.addImport(node.getQualifiedName().toString());
  }

  public GeneratedComponentClassVisitor getClassVisitor(){
    return resultBuilder.build();
  }
}
