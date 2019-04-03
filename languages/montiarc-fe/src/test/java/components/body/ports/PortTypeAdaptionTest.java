package components.body.ports;

import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.symboltable.Scope;
import infrastructure.AbstractCoCoTest;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;

public class PortTypeAdaptionTest extends AbstractCoCoTest {

	public static final String PORT_NAME = "components.body.ports.BumpControl.distance";

	@Test
	public void resolvingAdaptedResolvingFilterTest() {
		Optional<ComponentSymbol> optComp = loadDefaultSymbolTable()
				.<ComponentSymbol>resolve("components.body.ports.BumpControl", ComponentSymbol.KIND);

		assertTrue(optComp.isPresent());
		if (optComp.isPresent()) {
			Scope compScope = optComp.get().getSpannedScope();
			assertTrue(compScope.<PortSymbol>resolve(PORT_NAME, PortSymbol.KIND).isPresent());
			assertTrue(compScope.<PortSymbol>resolveLocally(PORT_NAME, PortSymbol.KIND).isPresent());
			assertTrue(compScope.<PortSymbol>resolveLocally(PortSymbol.KIND).size() > 0);

			assertTrue(compScope.<JavaFieldSymbol>resolveLocally(PORT_NAME, JavaFieldSymbol.KIND).isPresent());
			assertTrue(compScope.<JavaFieldSymbol>resolveLocally(JavaFieldSymbol.KIND).size() > 0);
		}
	}

}
