package portTest;

import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
import java.lang.*;
import java.util.*;

class InCompImpl     
implements IComputable<InCompInput, InCompResult> {

  public InCompImpl() {
  }

  public InCompResult getInitialValues() {
	  return new InCompResult();
  }
 public InCompResult compute(InCompInput input) {
	 if (input != null) {
		 System.out.println(input.inPort);
		
	}
	 return new InCompResult();

}

}
