/* (c) https://github.com/MontiCore/monticore */
package sim.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * The stream type implements all needed stream functions.
 *
 * @param <T> data type of this stream
 */
public class Stream<T> extends LinkedList<TickedMessage<T>> implements IStream<T> {

  private static final long serialVersionUID = -7673188197035601342L;

  /**
   * Points to last not processed message in the stream.
   */
  private int pointer;

  public Stream() {
    super();
  }

  /**
   * @see java.util.List#contains(java.lang.Object)
   */
  @Override
  public boolean contains(Object message) {
    boolean found = false;
    if (message instanceof Tick<?>) {
      for (TickedMessage<T> msg : this.getHistory()) {
        if (msg instanceof Tick<?> && msg == message) {
          found = true;
          break;
        }
      }
    } else if (message instanceof Message<?>) {
      for (TickedMessage<T> msg : this.getHistory()) {
        if (!msg.isTick() && msg == message) {
          found = true;
          break;
        }
      }
    } else {
      for (TickedMessage<T> msg : this.getHistory()) {
        if (!msg.isTick() && msg instanceof Message<?>) {
          if (((Message<T>) msg).getData().equals(message)) {
            found = true;
            break;
          }
        }
      }
    }
    return found;
  }

  /**
   * @see java.util.List#containsAll(java.util.Collection)
   */
  @Override
  public boolean containsAll(Collection<?> messages) {
    boolean containsAll = true;
    if (messages != null) {
      for (Object msg : messages) {
        if (!contains(msg)) {
          containsAll = false;
          break;
        }
      }
    } else {
      containsAll = false;
    }
    return containsAll;
  }

  /**
   * @see IStream#count(T)
   */
  @Override
  public int count(T message) {
    int count = 0;
    for (TickedMessage<T> msg : this.getHistory()) {
      if (!msg.isTick() && msg instanceof Message<?>) {
        if (((Message<T>) msg).getData().equals(message)) {
          count += 1;
        }
      }
    }
    return count;
  }

  /**
   * @see IStream#filter(java.util.Collection)
   */
  @Override
  public IStream<T> filter(Collection<T> e) {
    IStream<T> result = new Stream<T>();
    if (e != null && e.size() > 0) {
      for (int i = 0; i < this.size(); i++) {
        TickedMessage<T> msg = super.get(i);
        if (msg.isTick()) {
          result.add(msg);
          if (i < pointer) {
            result.pollLastMessage();
          }
        } else if (msg instanceof Message<?>) {
          Message<T> currentMsg = (Message<T>) msg;
          if (e.contains(currentMsg.getData())) {
            result.add(msg);
            if (i < pointer) {
              result.pollLastMessage();
            }
          }
        }
      }
    }
    return result;
  }

  /**
   * @see IStream#getBuffer()
   */
  @Override
  public IStream<T> getBuffer() {
    if (pointer < this.size()) {
      return (IStream<T>) this.subList(pointer, this.size());
    } else {
      return new Stream<T>();
    }
  }

  /**
   * @see java.util.LinkedList#get(int)
   */
  @Override
  public TickedMessage<T> get(int index) {
    if (index < pointer) {
      return super.get(index);
    } else {
      throw new IndexOutOfBoundsException("Unable to get message number " + index + ". History size is " + (pointer - 1) + ".");
    }
  }

  /**
   * @see IStream#getCurrentTime()
   */
  @Override
  public int getCurrentTime() {
    int time = 0;
    for (TickedMessage<T> msg : getHistory()) {
      if (msg.isTick()) {
        time += 1;
      }
    }
    return time;
  }

  /**
   * @see IStream#getHistory()
   */
  @Override
  public IStream<T> getHistory() {
    if (this.size() > 0 && pointer > 0) {
      return (IStream<T>) this.subList(0, pointer);
    } else {
      return new Stream<T>();
    }
  }

  /**
   * @see IStream#getHistory(int)
   */
  @Override
  public IStream<T> getHistory(int n) {
    IStream<T> result;
    if (n > 0 && this.size() > 0 && pointer > 0) {
      if (this.size() > n && pointer > n) {
        result = (IStream<T>) this.subList(pointer - n, pointer);
      } else {
        result = (IStream<T>) this.subList(0, pointer);
      }
    } else {
      result = new Stream<T>();
    }
    return result;
  }

  /**
   * @see IStream#getLastMessage()
   */
  @Override
  public T getLastMessage() {
    T lastMessage = null;
    for (int i = pointer; i > 0; i--) {
      TickedMessage<T> tickedMsg = super.get(i - 1);
      if (!tickedMsg.isTick() && tickedMsg instanceof Message<?>) {
        lastMessage = ((Message<T>) tickedMsg).getData();
        break;
      }
    }
    return lastMessage;
  }

  /**
   * @see IStream#getLastNTimeIntervals(int)
   */
  @Override
  public List<T> getLastNTimeIntervals(int n) {
    List<T> result;
    if (n < 0) {
      result = new LinkedList<T>();
    } else {
      int currentTimeUnit = this.getCurrentTime();
      if (n > currentTimeUnit) {
        result = getTimeSlice(0, currentTimeUnit);
      } else {
        result = getTimeSlice(currentTimeUnit - n, currentTimeUnit);
      }
    }
    return result;
  }

  /**
   * @see IStream#getLastTimeInterval()
   */
  @Override
  public List<T> getLastTimeInterval() {
    int currentTime = getCurrentTime();
    return getTimeSlice(currentTime, currentTime);
  }

  /**
   * @see IStream#getTimeInterval(int)
   */
  @Override
  public List<T> getTimeInterval(int time) {
    return getTimeSlice(time, time);
  }

  /**
   * @see IStream#getTimeInterval(int, int)
   */
  @Override
  public List<T> getTimeInterval(final int begin, final int end) {
    int localBegin = begin;
    int localEnd = end;

    if (localBegin < 0) {
      localBegin = 0;
    }
    if (localEnd < localBegin) {
      localEnd = localBegin;
    }
    List<T> returnList = new ArrayList<T>();
    int count = 0;
    for (int i = 0; i < pointer; i++) {
      TickedMessage<T> msg = super.get(i);
      if (msg.isTick()) {
        count += 1;
      } else if (count >= localBegin && count <= localEnd && msg instanceof Message<?>) {
        // type is checked before sending => suppress warning
        returnList.add(((Message<T>) msg).getData());

      } else if (count > localEnd) {
        break;
      }
    }
    return returnList;
  }

  /**
   * @see IStream#getTimeUnit(T)
   */
  @Override
  @Deprecated
  public int getTimeUnit(T message) {
    return firstTimeIntervalOf(message);
  }

  /**
   * @see IStream#getUntimedHistory()
   */
  @Override
  public List<T> getUntimedHistory() {
    List<T> returnList = new ArrayList<T>();
    for (TickedMessage<T> msg : getHistory()) {
      if (!msg.isTick() && msg instanceof Message<?>) {
        // type is checked before sending
        returnList.add(((Message<T>) msg).getData());
      }
    }
    return returnList;
  }

  /**
   * @see IStream#isPrefix(IStream)
   */
  @Override
  public boolean isPrefix(IStream<T> stream) {
    boolean prefix = true;
    if (stream != null) {
      IStream<T> thisHistory = this.getHistory();
      IStream<T> streamHistory = stream.getHistory();

      if (streamHistory.size() >= thisHistory.size()) {
        for (int i = 0; i < thisHistory.size(); i++) {
          if (!stream.get(i).equals(this.get(i))) {
            prefix = false;
            break;
          }
        }
      } else {
        prefix = false;
      }

    } else {
      prefix = false;
    }
    return prefix;
  }

  /**
   * @see IStream#peekLastMessage()
   */
  @Override
  public TickedMessage<T> peekLastMessage() {
    if (this.size() > 0 && pointer < this.size()) {
      return super.get(pointer);
    } else {
      return null;
    }
  }

  /**
   * @see IStream#pollLastMessage()
   */
  @Override
  public TickedMessage<T> pollLastMessage() {
    TickedMessage<T> msg = peekLastMessage();
    if (msg != null) {
      pointer += 1;
    }
    return msg;
  }

  /**
   * @see java.util.List#subList(int, int)
   */
  @Override
  public IStream<T> subList(int fromIndex, int toIndex) {
    return this.subStream(fromIndex, toIndex);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("<");
    String separator = "";
    for (TickedMessage<T> msg : getHistory()) {
      sb.append(separator);
      separator = ", ";
      sb.append(msg.toString());
    }
    sb.append("[");
    for (TickedMessage<T> msg : getBuffer()) {
      sb.append(separator);
      separator = ", ";
      sb.append(msg.toString());
    }
    sb.append("]>");
    return sb.toString();
  }

  /**
   * @see IStream#subStream(int, int)
   */
  @Override
  public IStream<T> subStream(int fromIndex, int toIndex) {
    IStream<T> subList = new Stream<T>();
    subList.addAll(super.subList(fromIndex, toIndex));
    for (int i = fromIndex; i < pointer; i++) {
      subList.pollLastMessage();
    }
    return subList;
  }

  /**
   * @see IStream#copy()
   */
  @Override
  public IStream<T> copy() {
    IStream<T> copy = new Stream<T>();
    for (int i = 0; i < this.size(); i++) {
      copy.add(super.get(i));
      if (i < pointer) {
        copy.pollLastMessage();
      }
    }
    return copy;
  }

  @Override
  @Deprecated
  public List<T> getLastNTimeSlices(int n) {
    return this.getLastNTimeIntervals(n);
  }

  @Override
  @Deprecated
  public List<T> getLastTimeSlice() {
    return this.getLastTimeInterval();
  }

  @Override
  @Deprecated
  public List<T> getTimeSlice(int time) {
    return this.getTimeInterval(time);
  }

  @Override
  @Deprecated
  public List<T> getTimeSlice(int begin, int end) {
    return this.getTimeInterval(begin, end);
  }

  @Override
  public int firstTimeIntervalOf(T message) {
    int returnValue = -1;
    int currentTime = 0;
    for (TickedMessage<T> currentMessage : getHistory()) {
      if (currentMessage.isTick()) {
        currentTime += 1;
      } else {
        Message<T> data = (Message<T>) currentMessage;
        if (data.getData().equals(message)) {
          returnValue = currentTime;
          break;
        }
      }
    }
    return returnValue;
  }

  @Override
  public int[] timeIntervalsOf(T message) {
    int currentTime = 0;
    ArrayList<Integer> intervals = new ArrayList<Integer>();
    for (TickedMessage<T> currentMessage : getHistory()) {
      if (currentMessage.isTick()) {
        currentTime += 1;
      } else {
        Message<T> data = (Message<T>) currentMessage;
        if (data.getData().equals(message)) {
          if (!intervals.contains(currentTime)) {
            intervals.add(currentTime);
          }
        }
      }
    }
    int[] res = new int[intervals.size()];
    int i = 0;
    for (Integer inter : intervals) {
      res[i++] = inter;
    }
    return res;
  }
}
