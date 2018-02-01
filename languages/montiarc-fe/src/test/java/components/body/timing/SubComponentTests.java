/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package components.body.timing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.cocos.helper.Assert;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JFieldSymbol;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ComponentSymbolReference;
import montiarc._symboltable.ConnectorSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.ValueSymbol;
import montiarc.cocos.ComponentInstanceNamesAreUnique;
import montiarc.cocos.ComponentWithTypeParametersHasInstance;
import montiarc.cocos.MontiArcCoCos;
import montiarc.cocos.SubcomponentParametersCorrectlyAssigned;
import montiarc.helper.SymbolPrinter;

/**
 * This class checks all context conditions related to the definition of
 * subcomponents
 *
 * @author Andreas Wortmann
 */
public class SubComponentTests extends AbstractCoCoTest {

  private static final String MP = "";

  private static final String PACKAGE = "components.body.timing";

  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testComponentEntryIsDelayed() {
    Scope symTab = new MontiArcTool().initSymbolTable("src/test/resources");
    ComponentSymbol parent = symTab.<ComponentSymbol> resolve(
        PACKAGE + "." + "Timing", ComponentSymbol.KIND).orElse(null);
    assertNotNull(parent);

    assertEquals(0, Log.getErrorCount());
    assertEquals(0, Log.getFindings().stream().filter(f -> f.isWarning()).count());

    ComponentSymbol child = parent.getInnerComponent("TimedInner").orElse(null);
    assertNotNull(child);
    assertFalse(child.hasDelay());

    child = parent.getInnerComponent("TimedDelayingInner").orElse(null);
    assertNotNull(child);
    assertTrue(child.hasDelay());

    child = parent.getInnerComponent("TimeSyncInner").orElse(null);
    assertNotNull(child);
    assertFalse(child.hasDelay());

    child = parent.getInnerComponent("TimeCausalSyncInner").orElse(null);
    assertNotNull(child);
    assertTrue(child.hasDelay());

    child = parent.getInnerComponent("UntimedInner").orElse(null);
    assertNotNull(child);
    assertFalse(child.hasDelay());
  }

}