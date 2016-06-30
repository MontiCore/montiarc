package sim.generic;

import java.util.Collection;
import java.util.List;

/**
 * 
 * The interface {@link IStream} describes stream operations.
 *
 * <br>
 * <br>
 * Copyright (c) 2011 RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $Author: ahaber $
 * @version $Date: 2015-02-09 16:59:52 +0100 (Mo, 09 Feb 2015) $<br>
 *          $Revision: 3128 $
 * @param <T> data type of the stream
 */
public interface IStream<T> extends List<TickedMessage<T>> {
    
    /*
     * (non-Javadoc)
     * @see java.util.List#contains(java.lang.Object)
     */
    @Override
    boolean contains(Object message);
    
    /*
     * (non-Javadoc)
     * @see java.util.List#containsAll(java.util.Collection)
     */
    @Override
    boolean containsAll(Collection<?> messages);
    
    /**
     * Counts the occurrence from the given message in the history using the
     * equal() method from the contained messages to compare them.
     * 
     * @param message message to count
     * @return how often occures the given message in the history
     */
    int count(T message);
    
    /**
     * Returns a copy from this stream, that only contains elements that are in
     * e.
     * 
     * @param e used as filter
     * @return this stream, where every element, that is not in e, is removed.
     */
    IStream<T> filter(Collection<T> e);
    
    /**
     * Returns all unsent messages. Note: The messages in the buffer will be
     * received in a future time unit. So this method must only be used by the
     * simulator to display unsent messages.
     * 
     * @return all unsent/buffered messages
     */
    IStream<T> getBuffer();
    
    /**
     * @return the current time in this stream
     */
    int getCurrentTime();
    
    /**
     * @return the trace/history from this stream, where the youngest message is
     *         the last list element and the oldest is the first. Only delivered
     *         messages are in the history.
     */
    IStream<T> getHistory();
    
    /**
     * @param n length
     * @return the last n messages from the history. If history is shorter then
     *         n, it returns the history.
     */
    IStream<T> getHistory(int n);
    
    /**
     * @return the last non tick message transmitted on this stream
     */
    T getLastMessage();
    
    /**
     * @param n amount of time slices
     * @return all messages in the last n time slices
     * @deprecated use {@link #getLastNTimeIntervals(int)} instead.
     */
    List<T> getLastNTimeSlices(int n);
    
    /**
     * Returns all messages contained in the last n time intervals.
     * @param n amount of time intervals.
     * @return all messages in the last n time intervals.
     */
    List<T> getLastNTimeIntervals(int n);
    
    /**
     * @return all messages in the current time slice
     * @deprecated use {@link #getLastTimeInterval()} instead.
     */
    List<T> getLastTimeSlice();
    
    /**
     * @return all messages in the current time interval.
     */
    List<T> getLastTimeInterval();
    
    /**
     * @param time TimeSlice
     * @return all messages in the given timeslice
     * @deprecated use {@link #getTimeInterval(int)} instead.
     */
    List<T> getTimeSlice(int time);
    
    /**
     * @param time time interval to get
     * @return all messages in the given interval
     */
    List<T> getTimeInterval(int time);
    
    /**
     * @param begin first time slice
     * @param end last time slice
     * 
     * @return all messages in the timeslices from begin to end (including begin
     *         and end)
     *         
     * @deprecated use {@link #getTimeInterval(int, int)} instead.
     */
    List<T> getTimeSlice(int begin, int end);
    
    /**
     * @param begin first time interval
     * @param end last time interval
     * 
     * @return all messages in the time interval from begin to end (including begin
     *         and end)
     */
    List<T> getTimeInterval(int begin, int end);
    
    /**
     * @param message message to find
     * @return the time interval from the given message. If the message is not
     *         transmitted in this stream, the return value will be -1.
     * @deprecated {@link IStream#firstTimeIntervalOf(T)} instead
     */
    int getTimeUnit(T message);
    
    /**
     * 
     * @param message
     *            message to find
     * @return the time interval in which the given <b>message</b> has been
     *         transmitted in this stream. If <b>message</b> is not transmitted
     *         in this stream, the return value will be -1.
     */
    int firstTimeIntervalOf(T message);
    
    /**
     * 
     * @param message
     *            message to find
     * @return an array of time intervals in which the given <b>message</a> has
     *         been transmitted. The array is empty, if the message has not been
     *         transmitted in this stream.
     * @since 2.5.0
     */
    int[] timeIntervalsOf(T message);
    
    /**
     * @return the stream without ticks
     */
    List<T> getUntimedHistory();
    
    /**
     * Checks, if this is the prefix from the given stream. In other
     * words: the given stream starts with this stream. Only the transmitted 
     * history (@see IStream.getHistory()) of both streams is compared. 
     * 
     * @param stream stream to check
     * @return true, if this is the prefix from the given stream, else false.
     */
    boolean isPrefix(IStream<T> stream);
    
    /**
     * Returns the last received message, without removing it from the buffer.
     * 
     * @return last received message
     */
    TickedMessage<T> peekLastMessage();
    
    /**
     * Returns the last received message and removes it from the buffer.
     * 
     * @return last received message
     */
    TickedMessage<T> pollLastMessage();
    
    /**
     * Creates a copy of this stream from fromIndex to toIndex.
     * Respects history and buffer from this stream.
     * 
     * @param fromIndex low endpoint (inclusive) of the subStream
     * @param toIndex high endpoint (exclusive) of the subList
     * @return a sub stream
     */
    IStream<T> subStream(int fromIndex, int toIndex);
    
    /**
     * @return a shallow copy of this streams with the same history/buffer. Copy
     *         contains links to the same message objects contained in this
     *         list.
     * @since 2.2.0
     */
    IStream<T> copy();
}
