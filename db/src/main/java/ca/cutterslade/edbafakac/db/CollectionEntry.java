package ca.cutterslade.edbafakac.db;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

import com.google.common.base.Preconditions;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

public final class CollectionEntry<T> extends Entry {

  private final class CollectionView extends AbstractCollection<T> {

    public CollectionView() {
    }

    @Override
    public Iterator<T> iterator() {
      return new Iterator<T>() {

        private final Iterator<?> valueIter = getValues().iterator();

        private final Type<T> valueType = getValueType();

        private final Database database = getDatabase();

        @Override
        public boolean hasNext() {
          return valueIter.hasNext();
        }

        @Override
        public T next() {
          return valueType.convertExternal(valueIter.next(), isReadOnly());
        }

        @Override
        public void remove() {
          Preconditions.checkState(!isReadOnly());
          valueIter.remove();
        }
      };
    }

    @Override
    public int size() {
      return getValues().size();
    }

    @Override
    public boolean isEmpty() {
      return getValues().isEmpty();
    }

    @Override
    public boolean add(final T e) {
      Preconditions.checkState(!isReadOnly());
      return getValues().add(getValueType().convertInternal(e));
    }

    @Override
    public boolean remove(final Object o) {
      Preconditions.checkState(!isReadOnly());
      if (!getValueType().isInstance(o)) {
        return false;
      }
      final Object element = getValueType().convertInternal((T) o);
      return getValues().remove(element);
    }

    @Override
    public void clear() {
      Preconditions.checkState(!isReadOnly());
      getValues().clear();
    }
  }

  public CollectionEntry(final DBObject object, final Configuration configuration, final boolean readOnly) {
    super(object, configuration, readOnly);
  }

  private BasicDBList getValues() {
    return (BasicDBList) getObject().get(BasicField.COLLECTION_VALUES.getKey());
  }

  private Type<T> getValueType() {
    return (Type<T>) getFieldValue(BasicField.getFieldTypeField(getConfiguration()));
  }

  public Collection<T> asCollection() {
    return new CollectionView();
  }

}
