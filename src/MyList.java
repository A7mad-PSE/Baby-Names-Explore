public class MyList<E> {
    private static final int INITIAL_CAPACITY = 16;
    private E[] elements;
    private int size;

    public MyList() {
        elements = (E[]) new Object[INITIAL_CAPACITY];
        size = 0;
    }

    public void add(E item) {
        ensureCapacity(size + 1);
        elements[size] = item;
        size++;
    }

    public void add(int index, E item) {
        checkIndexForAdd(index);
        ensureCapacity(size + 1);
        for (int i = size; i > index; i--) {
            elements[i] = elements[i - 1];
        }
        elements[index] = item;
        size++;
    }

    public E get(int index) {
        checkIndex(index);
        return elements[index];
    }

    public E set(int index, E item) {
        checkIndex(index);
        E old = elements[index];
        elements[index] = item;
        return old;
    }

    public E remove(int index) {
        checkIndex(index);
        E removed = elements[index];
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }
        elements[size - 1] = null;
        size--;
        return removed;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int indexOf(E item) {
        for (int i = 0; i < size; i++) {
            if (item == null) {
                if (elements[i] == null) return i;
            } else if (item.equals(elements[i])) {
                return i;
            }
        }
        return -1;
    }
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }
    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= elements.length) return;
        int newCapacity = elements.length * 2;
        if (newCapacity < minCapacity) newCapacity = minCapacity;
        E[] bigger = (E[]) new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            bigger[i] = elements[i];
        }
        elements = bigger;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    private void checkIndexForAdd(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }
}
