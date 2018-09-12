package generation;

import com.google.common.collect.Lists;
import de.monticore.java.javadsl._ast.ASTConstantsJavaDSL;
import de.monticore.java.javadsl._ast.ASTPrimitiveModifier;
import de.monticore.java.javadsl._ast.JavaDSLMill;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTVoidType;

public class GenerationConstants {

  public static final ASTPrimitiveModifier PUBLIC_MODIFIER
      = JavaDSLMill.primitiveModifierBuilder()
            .setModifier(ASTConstantsJavaDSL.PUBLIC)
            .build();

  public static final ASTVoidType VOID_TYPE
      = JavaDSLMill.voidTypeBuilder().build();

  public static final String VOID_STRING = "void";

  public final static JavaDSLPrettyPrinter PRINTER
      = new JavaDSLPrettyPrinter(new IndentPrinter());
}
