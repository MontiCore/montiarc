/* (c) https://github.com/MontiCore/monticore */
package montiarc.parser;

import de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnitBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VariabilityParserTestHelper {
  static ASTMACompilationUnitBuilder getVariabilitySyntaxArcASTUnit() {
    return MontiArcMill.mACompilationUnitBuilder()
      .setPackage(MontiArcMill.mCQualifiedNameBuilder().setPartsList(List.of("parser")).build())
      .setComponentType(
        MontiArcMill.componentTypeBuilder().setName("VariabilitySyntax")
          .setHead(MontiArcMill.componentHeadBuilder().build())
          .setBody(MontiArcMill.componentBodyBuilder().setArcElementsList(
            Arrays.asList(
              MontiArcMill.arcFeatureDeclarationBuilder().setArcFeaturesList(Arrays.asList(
                MontiArcMill.arcFeatureBuilder().setName("a").build(),
                MontiArcMill.arcFeatureBuilder().setName("b").build())
              ).build(),
              MontiArcMill.arcVarIfBuilder()
                .setCondition(MontiArcMill.nameExpressionBuilder().setName("a").build())
                .setThen(MontiArcMill.arcBlockBuilder()
                  .setArcElementsList(Collections.singletonList(
                    MontiArcMill.componentInterfaceBuilder().setPortDeclarationsList(
                      Collections.singletonList(
                        MontiArcMill.portDeclarationBuilder()
                          .setPortDirection(MontiArcMill.portDirectionBuilder().setIn(true).build())
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
                .setOtherwiseAbsent()
                .build(),
              MontiArcMill.arcVarIfBuilder().setCondition(
                  MontiArcMill.nameExpressionBuilder().setName("b").build()
                ).setThen(
                  MontiArcMill.componentInterfaceBuilder().setPortDeclarationsList(
                    Collections.singletonList(
                      MontiArcMill.portDeclarationBuilder()
                        .setPortDirection(MontiArcMill.portDirectionBuilder().setIn(true).build())
                        .setPortsList(Collections.singletonList(MontiArcMill.portBuilder()
                          .setName("i")
                          .build()))
                        .setMCType(MontiArcMill.mCQualifiedTypeBuilder()
                          .setMCQualifiedName(MontiArcMill.mCQualifiedNameBuilder()
                            .setPartsList(Collections.singletonList("String")).build())
                          .build())
                        .build()
                    )).build()
                ).setOtherwise(
                  MontiArcMill.arcBlockBuilder()
                    .setArcElementsList(Collections.emptyList())
                    .build())
                .build(),
              MontiArcMill.componentTypeBuilder().setName("A").setHead(MontiArcMill.componentHeadBuilder().build()).setBody(
                MontiArcMill.componentBodyBuilder().setArcElementsList(
                  Collections.singletonList(MontiArcMill.arcFeatureDeclarationBuilder()
                    .setArcFeaturesList(Collections.singletonList(
                      MontiArcMill.arcFeatureBuilder().setName("c").build())
                    ).build())
                ).build()
              ).build(),
              MontiArcMill.componentInstantiationBuilder().setMCType(
                MontiArcMill.mCQualifiedTypeBuilder().setMCQualifiedName(
                  MontiArcMill.mCQualifiedNameBuilder().setPartsList(Collections.singletonList("A")
                  ).build()
                ).build()
              ).setComponentInstancesList(
                Collections.singletonList(MontiArcMill.componentInstanceBuilder().setName("a1").setArcArguments(
                  MontiArcMill.arcArgumentsBuilder().setArcArgumentsList(
                    Collections.singletonList(MontiArcMill.arcArgumentBuilder().setName("c").setExpression(
                      MontiArcMill.literalExpressionBuilder().setLiteral(
                        MontiArcMill.booleanLiteralBuilder()
                        .setSource(ASTConstantsMCCommonLiterals.TRUE)
                        .build()
                      ).build()
                    ).build())
                  ).build()
                ).build())
              ).build(),
              MontiArcMill.arcConstraintDeclarationBuilder().setExpression(
                MontiArcMill.booleanOrOpExpressionBuilder().setLeft(
                  MontiArcMill.equalsExpressionBuilder().setLeft(
                    MontiArcMill.nameExpressionBuilder().setName("a").build()
                  ).setOperator("==").setRight(
                    MontiArcMill.literalExpressionBuilder()
                      .setLiteral(MontiArcMill.booleanLiteralBuilder()
                        .setSource(ASTConstantsMCCommonLiterals.FALSE)
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
                          .setSource(ASTConstantsMCCommonLiterals.TRUE)
                          .build())
                        .build()
                    ).build()
                  ).setOperator("&&").setRight(
                    MontiArcMill.equalsExpressionBuilder().setLeft(
                      MontiArcMill.nameExpressionBuilder().setName("b").build()
                    ).setOperator("==").setRight(
                      MontiArcMill.literalExpressionBuilder()
                        .setLiteral(MontiArcMill.booleanLiteralBuilder()
                          .setSource(ASTConstantsMCCommonLiterals.FALSE)
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
