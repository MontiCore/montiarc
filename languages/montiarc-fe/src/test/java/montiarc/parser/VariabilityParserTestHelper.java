/* (c) https://github.com/MontiCore/monticore */
package montiarc.parser;

import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnitBuilder;

import java.util.Arrays;
import java.util.Collections;

public class VariabilityParserTestHelper {
  static ASTMACompilationUnitBuilder getVariabilitySyntaxArcASTUnit() {
    return MontiArcMill.mACompilationUnitBuilder()
      .setPackage(MontiArcMill.mCQualifiedNameBuilder().setPartsList(Arrays.asList("montiarc", "parser")).build())
      .setComponentType(
        MontiArcMill.componentTypeBuilder().setName("VariabilitySyntax")
          .setHead(MontiArcMill.componentHeadBuilder().build())
          .setBody(MontiArcMill.componentBodyBuilder().setArcElementsList(
            Arrays.asList(
              MontiArcMill.arcFeatureDeclarationBuilder().setArcFeaturesList(Arrays.asList(
                MontiArcMill.arcFeatureBuilder().setName("a").build(),
                MontiArcMill.arcFeatureBuilder().setName("b").build())
              ).build(),
              MontiArcMill.arcIfStatementBuilder()
                .setCondition(MontiArcMill.nameExpressionBuilder().setName("a").build())
                .setThen(MontiArcMill.arcBlockBuilder()
                  .setArcElementsList(Collections.singletonList(
                    MontiArcMill.componentInterfaceBuilder().setPortDeclarationsList(
                      Collections.singletonList(
                        MontiArcMill.portDeclarationBuilder()
                          .setPortDirection(MontiArcMill.portDirectionInBuilder().build())
                          .setPortsList(
                            Collections.singletonList(
                              MontiArcMill.portBuilder().setName("i").build()
                            ))
                          .setMCType(MontiArcMill.mCQualifiedTypeBuilder()
                            .setMCQualifiedName(MontiArcMill.mCQualifiedNameBuilder()
                              .setPartsList(Collections.singletonList("String"))
                              .build())
                            .build())
                          .build()
                      )).build()))
                  .build())
                .setArcElseStatementAbsent()
                .build(),
              MontiArcMill.arcIfStatementBuilder().setCondition(
                MontiArcMill.nameExpressionBuilder().setName("b").build()
              ).setThen(
                MontiArcMill.componentInterfaceBuilder().setPortDeclarationsList(
                  Collections.singletonList(
                    MontiArcMill.portDeclarationBuilder()
                      .setPortDirection(MontiArcMill.portDirectionInBuilder().build())
                      .setPortsList(Collections.singletonList(MontiArcMill.portBuilder()
                        .setName("i")
                        .build()))
                      .setMCType(MontiArcMill.mCQualifiedTypeBuilder()
                        .setMCQualifiedName(MontiArcMill.mCQualifiedNameBuilder()
                          .setPartsList(Collections.singletonList("String")).build())
                        .build())
                      .build()
                  )).build()
              ).setArcElseStatement(
                MontiArcMill.arcElseStatementBuilder().setElse(MontiArcMill.arcBlockBuilder()
                  .setArcElementsList(Collections.emptyList())
                  .build())
                .build())
              .build(),
              MontiArcMill.arcConstraintDeclarationBuilder().setExpression(
                MontiArcMill.booleanOrOpExpressionBuilder().setLeft(
                  MontiArcMill.equalsExpressionBuilder().setLeft(
                    MontiArcMill.nameExpressionBuilder().setName("a").build()
                  ).setOperator("==").setRight(
                    MontiArcMill.literalExpressionBuilder()
                      .setLiteral(MontiArcMill.booleanLiteralBuilder()
                        .setSource(1)
                        .build())
                      .build()
                  ).build()
                ).setOperator("||").setRight(
                  MontiArcMill.booleanAndOpExpressionBuilder().setLeft(
                    MontiArcMill.equalsExpressionBuilder().setLeft(
                      MontiArcMill.nameExpressionBuilder().setName("a").build()
                    ).setOperator("==").setRight(
                      MontiArcMill.literalExpressionBuilder()
                        .setLiteral(MontiArcMill.booleanLiteralBuilder()
                          .setSource(3)
                          .build())
                        .build()
                    ).build()
                  ).setOperator("&&").setRight(
                    MontiArcMill.equalsExpressionBuilder().setLeft(
                      MontiArcMill.nameExpressionBuilder().setName("b").build()
                    ).setOperator("==").setRight(
                      MontiArcMill.literalExpressionBuilder()
                        .setLiteral(MontiArcMill.booleanLiteralBuilder()
                          .setSource(1)
                          .build())
                        .build()
                    ).build()
                  ).build()
                ).build()
              ).build()
            )
          ).build())
          .build()
      );
  }
}