package portTest;

import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
import java.lang.*;
import java.util.*;

class OutCompImpl     
implements IComputable<OutCompInput, OutCompResult> {

  public OutCompImpl() {
  }

  public OutCompResult getInitialValues() {
	  OutCompResult result = new OutCompResult();
	  result.setOutPort("test1");
	  return result;
  }
 public OutCompResult compute(OutCompInput input) {
	 OutCompResult result = new OutCompResult();
	  result.setOutPort("test2");
	  return result;
 }
}
