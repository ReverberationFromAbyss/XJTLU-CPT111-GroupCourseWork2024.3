package Utils;

import java.io.Serializable;
import java.util.Map;

public class Pair<L, R>
    implements Map.Entry<L, R>, Comparable<Pair<L, R>>, Serializable {

private final L m_l_;
private final R m_r_;

@Override
public L getKey() {
  return m_l_;
}

/**
 * Returns the value corresponding to this entry.  If the mapping
 * has been removed from the backing map (by the iterator's
 * {@code remove} operation), the results of this call are undefined.
 *
 * @return the value corresponding to this entry
 * @throws IllegalStateException implementations may, but are not
 *                               required to, throw this exception if the entry has been
 *                               removed from the backing map.
 */
@Override
public R getValue() {
  return m_r_;
}

/**
 * Replaces the value corresponding to this entry with the specified
 * value (optional operation).  (Writes through to the map.)  The
 * behavior of this call is undefined if the mapping has already been
 * removed from the map (by the iterator's {@code remove} operation).
 *
 * @param value new value to be stored in this entry
 * @return old value corresponding to the entry
 * @throws UnsupportedOperationException if the {@code put} operation
 *                                       is not supported by the backing map
 * @throws ClassCastException            if the class of the specified value
 *                                       prevents it from being stored in the backing map
 * @throws NullPointerException          if the backing map does not permit
 *                                       null values, and the specified value is null
 * @throws IllegalArgumentException      if some property of this value
 *                                       prevents it from being stored in the backing map
 * @throws IllegalStateException         implementations may, but are not
 *                                       required to, throw this exception if the entry has been
 *                                       removed from the backing map.
 */
@Override
public R setValue(R value) {
  return null;
}


/**
 * Compares this object with the specified object for order.  Returns a
 * negative integer, zero, or a positive integer as this object is less
 * than, equal to, or greater than the specified object.
 * <p>The implementor must ensure {@link Integer#signum
 * signum}{@code (x.compareTo(y)) == -signum(y.compareTo(x))} for
 * all {@code x} and {@code y}.  (This implies that {@code
 * x.compareTo(y)} must throw an exception if and only if {@code
 * y.compareTo(x)} throws an exception.)
 * <p>The implementor must also ensure that the relation is transitive:
 * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
 * {@code x.compareTo(z) > 0}.
 * <p>Finally, the implementor must ensure that {@code
 * x.compareTo(y)==0} implies that {@code signum(x.compareTo(z))
 * == signum(y.compareTo(z))}, for all {@code z}.
 *
 * @param o the object to be compared.
 * @return a negative integer, zero, or a positive integer as this object
 *     is less than, equal to, or greater than the specified object.
 * @throws NullPointerException if the specified object is null
 * @throws ClassCastException   if the specified object's type prevents it
 *                              from being compared to this object.
 */
@Override
public int compareTo(Pair<L, R> o) {
  return 0;
}

public Pair(L l, R r) {
  m_l_ = l;
  m_r_ = r;
}

}
