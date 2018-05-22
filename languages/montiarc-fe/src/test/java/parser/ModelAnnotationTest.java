/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package parser;

import de.monticore.ast.Comment;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._parser.MontiArcParser;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Optional;

/**
 * Checks that all models have either been annotated as valid or invalid.
 * Furthermore, valid components should not contain "@implements" tags and
 * invalid components should have at least one "@implements" tag of the format
 * @implements [ABC18] CoCo-Name: Description (p.XX, Lst. X.YZ)
 *
 * @author (last commit) Michael Mutert
 * @version , 2018-05-22
 */
public class ModelAnnotationTest {

  private static final String MODEL_PATH = "src/test/resources";

  @Before
  public void setUp() throws Exception {
    Log.enableFailQuick(false);
    Log.getFindings().clear();
  }

  @Test
  public void testModelAnnotations() {
    FileWalker modelVisitor = new FileWalker(".arc");
    try {
      Files.walkFileTree(Paths .get(MODEL_PATH), modelVisitor);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  class FileWalker extends SimpleFileVisitor<Path>{
    private final String fileEnding;

    public FileWalker(String fileEnding) {
      this.fileEnding = fileEnding;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
        throws IOException {

      if(file.toFile().exists()
             && file.toString().toLowerCase().endsWith(fileEnding)
             && !ParserTest.expectedParseErrorModels.contains(file.toString())){

        MontiArcParser parser = new MontiArcParser();
        final Optional<ASTMACompilationUnit> astmaCompilationUnit
            = parser.parse(file.toString());
        if(!astmaCompilationUnit.isPresent()){
          return FileVisitResult.CONTINUE;
        }

        final ASTMACompilationUnit model = astmaCompilationUnit.get();
        final List<Comment> preComments
            = model.getComponent().get_PreComments();
        if(preComments.size() < 1){
          Log.warn(String.format("Model %s has no description!", file.toString()));
          return FileVisitResult.CONTINUE;
        }

        final Comment comment = preComments.get(preComments.size() - 1);
        if(comment.getText().toLowerCase().contains("valid")){
          if(comment.getText().toLowerCase().contains("invalid")){
            // Invalid model
            if(!comment.getText().toLowerCase().contains("@implements")){
              Log.warn(
                  String.format("Model %s is declared as invalid but does not" +
                                    "contain '@implements' tag!",
                      file.toString()));
            }
          } else {
            // Valid model
            if(comment.getText().toLowerCase().contains("@implements")){
              Log.warn(
                  String.format("Model %s is declared as valid but contains" +
                                    "'@implements' tag!",
                      file.toString()));
            }
          }
        } else {
          Log.warn(
              String.format("Description of model %s does not state " +
                                "whether it is valid or invalid!",
                  file.toString()));
        }
      }
      return FileVisitResult.CONTINUE;
    }
  }
}
