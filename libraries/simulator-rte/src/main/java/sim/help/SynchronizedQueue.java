/* (c) https://github.com/MontiCore/monticore */
/**
 * 
 */
package sim.help;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 
 * Synchronized wrapper to a queue realized by a linked list.
 *
 *
 * @param <E> type
 */
public class SynchronizedQueue<E> extends LinkedList<E> implements Queue<E> {

    /**
     * 
     */
    private static final long serialVersionUID = -7576403923636700600L;

    /* (non-Javadoc)
     * @see java.util.Collection#size()
     */
    @Override
    public synchronized int size() {
        return super.size();
    }

    /* (non-Javadoc)
     * @see java.util.Collection#isEmpty()
     */
    @Override
    public synchronized boolean isEmpty() {
        return super.isEmpty();
    }

    /* (non-Javadoc)
     * @see java.util.Collection#contains(java.lang.Object)
     */
    @Override
    public synchronized boolean contains(Object o) {
        return super.contains(o);
    }

    /* (non-Javadoc)
     * @see java.util.Collection#iterator()
     */
    @Override
    public synchronized Iterator<E> iterator() {
        return super.iterator();
    }

    /* (non-Javadoc)
     * @see java.util.Collection#toArray()
     */
    @Override
    public synchronized Object[] toArray() {
        return super.toArray();
    }

    /* (non-Javadoc)
     * @see java.util.Collection#toArray(T[])
     */
    @Override
    public synchronized <T> T[] toArray(T[] a) {
        return super.toArray(a);
    }

    /* (non-Javadoc)
     * @see java.util.Collection#remove(java.lang.Object)
     */
    @Override
    public synchronized boolean remove(Object o) {
        return super.remove(o);
    }

    /* (non-Javadoc)
     * @see java.util.Collection#containsAll(java.util.Collection)
     */
    @Override
    public synchronized boolean containsAll(Collection<?> c) {
        return super.containsAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    @Override
    public synchronized boolean addAll(Collection<? extends E> c) {
        return super.addAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    @Override
    public synchronized boolean removeAll(Collection<?> c) {
        return super.removeAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    @Override
    public synchronized boolean retainAll(Collection<?> c) {
        return super.removeAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.Collection#clear()
     */
    @Override
    public synchronized void clear() {
        super.clear();
    }

    /* (non-Javadoc)
     * @see java.util.Queue#add(java.lang.Object)
     */
    @Override
    public synchronized boolean add(E e) {
        return super.add(e);
    }

    /* (non-Javadoc)
     * @see java.util.Queue#offer(java.lang.Object)
     */
    @Override
    public synchronized boolean offer(E e) {
        return super.offer(e);
    }

    /* (non-Javadoc)
     * @see java.util.Queue#remove()
     */
    @Override
    public synchronized E remove() {
        return super.remove();
    }

    /* (non-Javadoc)
     * @see java.util.Queue#poll()
     */
    @Override
    public synchronized E poll() {
        return super.poll();
    }

    /* (non-Javadoc)
     * @see java.util.Queue#element()
     */
    @Override
    public synchronized E element() {
        return super.element();
    }

    /* (non-Javadoc)
     * @see java.util.Queue#peek()
     */
    @Override
    public synchronized E peek() {
        return super.peek();
    }
    
}
