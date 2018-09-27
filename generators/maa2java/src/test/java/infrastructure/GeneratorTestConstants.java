package infrastructure;

import com.google.common.collect.Lists;
import de.monticore.java.javadsl._ast.ASTConstantsJavaDSL;
import de.monticore.java.javadsl._ast.ASTPrimitiveModifier;
import de.monticore.java.javadsl._ast.JavaDSLMill;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTVoidType;
import montiarc._ast.MontiArcMill;

import java.util.HashMap;
import java.util.Map;

public class GeneratorTestConstants {

  public static final ASTPrimitiveModifier PUBLIC_MODIFIER
      = JavaDSLMill.primitiveModifierBuilder()
            .setModifier(ASTConstantsJavaDSL.PUBLIC)
            .build();

  public static final ASTVoidType VOID_TYPE
      = JavaDSLMill.voidTypeBuilder().build();

  public static final String VOID_STRING = "void";

  public final static JavaDSLPrettyPrinter PRINTER
      = new JavaDSLPrettyPrinter(new IndentPrinter());

  public static final Map<String, ASTType> types = new HashMap<>();

  static {
    types.put("STRING_TYPE", MontiArcMill.simpleReferenceTypeBuilder()
                                 .setNameList(Lists.newArrayList("String"))
                                 .build());
    types.put("CHARACTER_TYPE", MontiArcMill.simpleReferenceTypeBuilder()
                                    .setNameList(Lists.newArrayList("Character"))
                                    .build());
    types.put("BOOLEAN_TYPE", MontiArcMill.simpleReferenceTypeBuilder()
                                  .setNameList(Lists.newArrayList("Boolean"))
                                  .build());
    types.put("SHORT_TYPE", MontiArcMill.simpleReferenceTypeBuilder()
                                .setNameList(Lists.newArrayList("Short"))
                                .build());
    types.put("BYTE_TYPE", MontiArcMill.simpleReferenceTypeBuilder()
                               .setNameList(Lists.newArrayList("Byte"))
                               .build());
    types.put("INTEGER_TYPE", MontiArcMill.simpleReferenceTypeBuilder()
                                  .setNameList(Lists.newArrayList("Integer"))
                                  .build());
    types.put("LONG_TYPE", MontiArcMill.simpleReferenceTypeBuilder()
                               .setNameList(Lists.newArrayList("Long"))
                               .build());
    types.put("FLOAT_TYPE", MontiArcMill.simpleReferenceTypeBuilder()
                                .setNameList(Lists.newArrayList("Float"))
                                .build());
    types.put("DOUBLE_TYPE", MontiArcMill.simpleReferenceTypeBuilder()
                                 .setNameList(Lists.newArrayList("Double"))
                                 .build());
    types.put("OBJECT_TYPE", MontiArcMill.simpleReferenceTypeBuilder()
                                 .setNameList(Lists.newArrayList("Object"))
                                 .build());
  }
}
