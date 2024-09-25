/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.logging;

import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.ILogHook;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A log hook that only logs messages that pass a filter.
 * <br>
 * The actual logging of filter-passing messages is delegated to other log hooks.
 * <br>
 * For the filters, one can specify
 * <ul>
 *   <li>Elements to be monitored. If no elements are specified, all elements are monitored.
 *     Specified elements may contain wildcards:
 *     <ul>
 *       <li><b>*</b> matches an arbitrary number of characters</li>
 *       <li><b>+</b> matches an identifier (a combination of alpha-numeric characters and underscores)</li>
 *     </ul>
 *   </li>
 *   <li>Logging aspects to be monitored. If no aspect is specified, all aspects are monitored</li>
 * </ul>
 * <br>
 * Examples for wildcard usage:
 * <ul>
 *   <li>{@code "my.comp.*"} will monitor {@code "my.comp.subcomp}, {@code "my.comp.subcomp.grandchildcomp},
 *     and {@code "my.comp.port}, but not {@code "my.comp"} itself</li>
 *   </li>
 *   <li>{@code "my.comp.+"} will monitor {@code "my.comp.subcomp} and {@code "my.comp.port}, but neither
 *     {@code "my.comp.subcomp.grandchildcomp} nor {@code "my.comp}
 *   </li>
 *   <li>{@code "my.comp.+.foo"} will monitor any grandchildren of {@code "my.comp} named {@code "foo"}</li>
 * </ul>
 */
public class FilterHook implements ILogHook {

  public static final String ANY_WILDCARD = "*";
  public static final String IDENTIFIER_WILDCARD = "+";

  private final List<ILogHook> delegates;

  private final Set<Pattern> monitoredElementsFilter;
  private final Set<String> monitoredAspects;

  /**
   *
   * @param realHooks The hooks to which filter-passing messages are forwarded in order to actually log them.
   * @param monitoredElements see {@link FilterHook}
   * @param monitoredAspects see {@link FilterHook}
   */
  public FilterHook(Collection<ILogHook> realHooks, Collection<String> monitoredElements, Collection<String> monitoredAspects) {
    this.delegates = List.copyOf(realHooks);

    this.monitoredElementsFilter =
      monitoredElements.stream()
        .map(el -> el.replace(".", "\\."))  // Prevention of the dot being interpreted as a regex wildcard
        .map(el -> el.replace(ANY_WILDCARD, ".*"))   // Turn "*" into a regex wildcard
        .map(el -> el.replace(IDENTIFIER_WILDCARD, "[\\p{Alnum}_]*"))  // Turn "+" into an identifier wildcard
        .map(Pattern::compile)
        .collect(Collectors.toSet());

    this.monitoredAspects = Set.copyOf(monitoredAspects);

    this.printInitialInfo();
  }

  private void printInitialInfo() {
    String elements =
      this.monitoredElementsFilter.isEmpty() ? "*"
      : this.monitoredElementsFilter.stream()
        .map(Pattern::pattern)
        .collect(Collectors.joining(", ", "[", "]"));

    String aspects =
      this.monitoredAspects.isEmpty() ? "*"
      : "[" + String.join(", ", this.monitoredAspects) + "]";

    this.doPrintln(String.format("Hook is filtered: Monitored elements: %s; Monitored aspects: %s", elements, aspects));
  }

  private boolean logNamePassesFilter(String logName) {
    String[] parts = logName.split("#");

    if (parts.length == 2) {
      return passesFilter(parts[0], parts[1]);
    } else {
      return false;
    }
  }

  private boolean findingPassesFilter(Finding finding) {
    // Logged element and aspect are expected to be encoded in the message at the beginning
    String[] hashtagParts = finding.getMsg().split("#");
    if (hashtagParts.length < 2) {
      return false;
    }

    String[] partsWithAspect = hashtagParts[1].split(" ");
    if (partsWithAspect.length < 1) {
      return false;
    }

    return passesFilter(hashtagParts[0], partsWithAspect[0]);
  }

  private boolean passesFilter(String elementName, String aspect) {
    return (monitoredAspects.isEmpty() || monitoredAspects.contains(aspect))
      && (monitoredElementsFilter.isEmpty()
          || monitoredElementsFilter.stream().anyMatch(p -> p.matcher(elementName).matches()));
  }



  /* DELEGATION: */

  @Override
  public void doTrace(String msg, String logName) {
    if (logNamePassesFilter(logName)) {
      this.delegates.forEach(d -> d.doTrace(msg, logName));
    }
  }

  @Override
  public void doTrace(String msg, Throwable t, String logName) {
    if (logNamePassesFilter(logName)) {
      this.delegates.forEach(d -> d.doTrace(msg, t, logName));
    }
  }

  @Override
  public void doDebug(String msg, String logName) {
    if (logNamePassesFilter(logName)) {
      this.delegates.forEach(d -> d.doDebug(msg, logName));
    }
  }

  @Override
  public void doDebug(String msg, SourcePosition pos, String logName) {
    if (logNamePassesFilter(logName)) {
      this.delegates.forEach(d -> d.doDebug(msg, pos, logName));
    }
  }

  @Override
  public void doDebug(String msg, SourcePosition start, SourcePosition end, String logName) {
    if (logNamePassesFilter(logName)) {
      this.delegates.forEach(d -> d.doDebug(msg, start, end, logName));
    }
  }

  @Override
  public void doDebug(String msg, Throwable t, String logName) {
    if (logNamePassesFilter(logName)) {
      this.delegates.forEach(d -> d.doDebug(msg, t, logName));
    }
  }

  @Override
  public void doInfo(String msg, String logName) {
    if (logNamePassesFilter(logName)) {
      this.delegates.forEach(d -> d.doInfo(msg, logName));
    }
  }

  @Override
  public void doInfo(String msg, Throwable t, String logName) {
    if (logNamePassesFilter(logName)) {
      this.delegates.forEach(d -> d.doInfo(msg, t, logName));
    }
  }

  @Override
  public void doWarn(Finding warn) {
    if (findingPassesFilter(warn)) {
      this.delegates.forEach(d -> d.doWarn(warn));
    }
  }

  @Override
  public void doWarn(Finding warn, Throwable t) {
    if (findingPassesFilter(warn)) {
      this.delegates.forEach(d -> d.doWarn(warn, t));
    }
  }

  @Override
  public void doError(Finding error) {
    if (findingPassesFilter(error)) {
      this.delegates.forEach(d -> d.doError(error));
    }
  }

  @Override
  public void doError(Finding error, Throwable t) {
    if (findingPassesFilter(error)) {
      this.delegates.forEach(d -> d.doError(error, t));
    }
  }

  @Override
  public void doErrorUser(Finding error) {
    if (findingPassesFilter(error)) {
      this.delegates.forEach(d -> d.doErrorUser(error));
    }
  }

  @Override
  public void doErrorUser(Finding error, Throwable t) {
    if (findingPassesFilter(error)) {
      this.delegates.forEach(d -> d.doErrorUser(error, t));
    }
  }


  @Override
  public void doPrintln(String msg) {
    this.delegates.forEach(d -> d.doPrintln(msg));
  }

  @Override
  public void doErrPrint(String msg) {
    this.delegates.forEach(d -> d.doErrPrint(msg));
  }

  @Override
  public void doPrintStackTrace(Throwable t) {
    this.delegates.forEach(d -> d.doPrintStackTrace(t));
  }

  @Override
  public void doErrPrintStackTrace(Throwable t) {
    this.delegates.forEach(d -> d.doErrPrintStackTrace(t));
  }

  @Override
  public void doPrint(String msg) {
    this.delegates.forEach(d -> d.doPrint(msg));
  }
}
