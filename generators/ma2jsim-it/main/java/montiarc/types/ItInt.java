/* (c) https://github.com/MontiCore/monticore */
package montiarc.types;

import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ItInt extends ItIntTOP {

  public ItInt(Integer... i) {
    list = Arrays.asList(i);
  }

  private final List<Integer> list;

  @Override @NotNull
  public Iterator<Integer> iterator() {
    return list.iterator();
  }
}
