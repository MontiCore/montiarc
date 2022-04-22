/* (c) https://github.com/MontiCore/monticore */
package sim.message;

import java.util.List;

/**
 * Helper class for printing streams.
 */
public class StreamPrinter {

  /**
   * Prevent utility class instantiation.
   */
  private StreamPrinter() {

  }

  private static final String NO_DATA = "";

  /**
   * Prints the given streams in an aligned form. This way, all time frames are aligned vertically.
   *
   * @param streams streams to print, must not be null.
   * @return printed streams.
   * @throws NullPointerException if {@code streams == null}
   */
  public static String print(IStream<?>... streams) {
    return print(new String[streams.length], streams);
  }

  /**
   * Prints the given streams in an aligned form. This way, all time frames are aligned vertically.
   *
   * @param label   a label for each stream, must not be null and {@code streams.lenght == label.length}.
   * @param streams streams to print, must not be null.
   * @return printed streams.
   * @throws NullPointerException     if {@code streams == null} || {@code label == null}.
   * @throws IllegalArgumentException if {@code streams.lenght != label.length}.
   */
  public static String print(String[] label, IStream<?>... streams) {
    checkPreconditions(label, streams);
    StringBuffer sb = new StringBuffer();

    StringBuilder[] portSBs = new StringBuilder[streams.length];
    int maxNameLength = getLengthOfLongestString(label);
    for (int i = 0; i < streams.length; i++) {
      portSBs[i] = new StringBuilder();
    }

    for (int i = 0; i < label.length; i++) {
      StringBuilder portString = portSBs[i];
      if (label[i] != null) {
        int pNameLength = label[i].length();
        int indent = maxNameLength - pNameLength;

        portString.append(label[i]);
        indent(portString, indent);
        portString.append(": ");
      }
      portString.append("<");
    }

    int maxTime = getMaxStreamTime(streams);
    for (int i = 0; i <= maxTime; i++) {

      String[] msgs = getMessagesOfTimeFrame(i, streams);

      int maxMsgLengt = getLengthOfLongestString(msgs);

      for (int j = 0; j < streams.length; j++) {
        StringBuilder portString = portSBs[j];
        String msg = msgs[j];
        int indent = maxMsgLengt - msg.length();
        portString.append(msg);
        indent(portString, indent);
        if (!msg.isEmpty()) {
          portString.append(", ");
        } else if (i > 0) {
          portString.append("  ");
        }
        if (i <= streams[j].getCurrentTime()) {
          portString.append("Tk");
          if (i < maxTime) {
            portString.append(", ");
          }
        } else {
          portString.append("  ");
          if (i < maxTime) {
            portString.append("  ");
          }

        }

      }
    }

    for (int i = 0; i < streams.length; i++) {
      StringBuilder portString = portSBs[i];
      portString.append(">\n\n");
      sb.append(portString.toString());
    }
    return sb.toString();
  }


  private static void checkPreconditions(String[] label, IStream<?>[] streams) {
    if (label == null) {
      throw new NullPointerException("label must not be null!");
    }
    if (streams == null) {
      throw new NullPointerException("streams must not be null!");
    }
    if (label.length != streams.length) {
      throw new IllegalArgumentException("label.length != streams.length");
    }
  }

  private static String[] getMessagesOfTimeFrame(int slot, IStream<?>[] streams) {
    String[] msgs = new String[streams.length];

    for (int i = 0; i < streams.length; i++) {
      msgs[i] = "";
      IStream<?> s = streams[i];
      if (slot <= s.getCurrentTime()) {
        List<?> msg = s.getTimeInterval(slot);
        String sep = "";
        for (Object obj : msg) {
          msgs[i] += sep;
          sep = ", ";
          msgs[i] += obj.toString();
        }
      } else {
        msgs[i] = NO_DATA;
      }
    }
    return msgs;
  }

  private static int getLengthOfLongestString(String[] strings) {
    int max = -1;
    for (String s : strings) {
      int pNameLength = -1;
      if (s != null) {
        pNameLength = s.length();
      }
      if (pNameLength > max) {
        max = pNameLength;
      }
    }
    return max;
  }

  /**
   * @param streams
   * @return
   */
  private static int getMaxStreamTime(IStream<?>[] streams) {
    int max = -1;
    for (IStream<?> s : streams) {
      int l = s.getCurrentTime();
      if (l > max) {
        max = l;
      }
    }
    return max;
  }

  private static void indent(StringBuilder portString, int indent) {
    for (int j = 0; j < indent; j++) {
      portString.append(" ");
    }
  }
}
