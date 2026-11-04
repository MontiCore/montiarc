/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc.trafo;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mccollectiontypes._ast.ASTMCBasicTypeArgument;
import de.monticore.types.mccomponenttypes._ast.ASTMCGenericComponentType;
import de.monticore.types.mccomponenttypes._ast.ASTMCQualifiedComponentType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import montiarc.MontiArcMill;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmbeddingComponentTest {

  @Test
  void shouldConvertQualifiedComponentType() {
    ASTMCQualifiedComponentType componentType = MontiArcMill.mCQualifiedComponentTypeBuilder()
      .setMCQualifiedName(MontiArcMill.mCQualifiedNameBuilder()
        .setPartsList(List.of("example", "Component"))
        .build())
      .build();

    var converted = EmbeddingComponent.toMCObjectType(componentType);

    assertTrue(converted.isPresent());
    ASTMCQualifiedType qualifiedType = assertInstanceOf(ASTMCQualifiedType.class, converted.get());
    assertEquals("example.Component", qualifiedType.getMCQualifiedName().getQName());
    assertNotSame(componentType.getMCQualifiedName(), qualifiedType.getMCQualifiedName());
  }

  @Test
  void shouldConvertGenericComponentType() {
    ASTMCBasicTypeArgument typeArgument = MontiArcMill.mCBasicTypeArgumentBuilder()
      .setMCQualifiedType(MontiArcMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(MontiArcMill.mCQualifiedNameBuilder()
          .addParts("String")
          .build())
        .build())
      .build();
    ASTMCGenericComponentType componentType = MontiArcMill.mCGenericComponentTypeBuilder()
      .setNamesList(List.of("example", "Component"))
      .addMCTypeArgument(typeArgument)
      .build();

    var converted = EmbeddingComponent.toMCObjectType(componentType);

    assertTrue(converted.isPresent());
    ASTMCBasicGenericType genericType = assertInstanceOf(ASTMCBasicGenericType.class, converted.get());
    assertEquals(List.of("example", "Component"), genericType.getNameList());
    assertEquals(1, genericType.getMCTypeArgumentList().size());
    assertNotSame(typeArgument, genericType.getMCTypeArgument(0));
  }
}
