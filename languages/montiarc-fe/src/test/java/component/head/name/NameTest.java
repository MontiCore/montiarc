/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package component.head.name;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._parser.MontiArcParser;
import montiarc._symboltable.ComponentKind;
import montiarc._symboltable.ComponentSymbol;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.text.html.Option;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.fail;

/**
 * This class checks all context conditions related to component names
 *
 * @author (last commit) kirchhof
 * @version 4.2.0, 30.01.2018
 * @since 4.2.0
 */
public class NameTest {
  private static final String PATH = "src/test/resources/component/head/name/";

  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testNameClashNotPermitted() {
    // given
    MontiArcTool tool = new MontiArcTool();
    final Path filePath = Paths.get(PATH + "nameclash/NameClashA.arc");
    final File file = filePath.toFile();

    // when
    final Optional<ComponentSymbol> rootSym = tool
        .getComponentSymbol("component.head.name.nameclash.NameClashA", file);

    // then
    if (rootSym.get().getSuperComponent().equals(Optional.empty())) {
      return;
    }
    fail("NameClashB.arc should not be parseable.");
  }
}
