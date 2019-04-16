package compInstTest.scStore;

import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
import java.lang.*;
import java.util.*;

class SubCompImpl     
implements IComputable<SubCompInput, SubCompResult> {

  public SubCompImpl() {
  }

  public SubCompResult getInitialValues() {
	  return new SubCompResult();
  }
 public SubCompResult compute(SubCompInput input) {
	 System.out.println("Test successful");
	 Double test = 0.0;
	 System.out.println(test);
	 return new SubCompResult();
}

}
