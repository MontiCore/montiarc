/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package component.head.name;

import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.fail;

/**
 * This class checks all context conditions related to component names
 *
 * @author (last commit) kirchhof
 * @version 4.2.0, 30.01.2018
 * @since 4.2.0
 */
public class NameTest {
  private static final String PATH = "src/test/resources/";

  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testNameClashNotPermitted() {
    // given
    MontiArcTool tool = new MontiArcTool();
    final Path filePath = Paths.get(PATH);
    final File file = filePath.toFile();

    // when
    try {
      tool.getComponentSymbol("component.head.name.nameclash.NameClashA", file);
    }
    catch (Exception e) {
      return;
    }

    // then
    fail(
        "NameClashB.arc should not be parseable because 'component.head.name.nameclash.NameClashA' is ambiguous.");
  }
}
