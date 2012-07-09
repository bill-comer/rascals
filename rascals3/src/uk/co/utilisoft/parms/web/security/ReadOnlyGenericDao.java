package uk.co.utilisoft.parms.web.security;

import java.io.Serializable;
import java.util.List;

/**
 * @author Alison Verkroost
 * @version 1.0
 * @param <T> the <code>java.lang.Object</code> type
 * @param <PK> the <code>java.io.Serizable</code> type
 */
public interface ReadOnlyGenericDao<T extends Object, PK extends Serializable>
{
  /**
   * Retrieve an object using the given primary key id.
   * @param aId the id
   * @return the object with the id, if it exists; <code>null</code> if it
   *         does not exist
   */
  T getById(PK aId);

  /**
   * Retrieve a list of objects.
   * @return the List of objects
   */
  List<T> getAll();

}
