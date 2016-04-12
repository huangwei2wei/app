package com.app.dispatch;
import java.io.Serializable;
import java.util.Arrays;
@SuppressWarnings("serial")
public class IntHashSet implements Serializable {
	public static final int MAX_SIZE = 1073741824;
	public final int ndv;
	private double _factor;
	private int _nmax;
	private int _size;
	private int _nlo;
	private int _nhi;
	private int _shift;
	private int _mask;
	private int[] _values;

	public IntHashSet() {
		this(8, 0.25D, -2147483648);
	}

	public IntHashSet(int capacity) {
		this(capacity, 0.25D, -2147483648);
	}

	public IntHashSet(int capacity, int noDataValue) {
		this(capacity, 0.25D, noDataValue);
	}

	public IntHashSet(int capacity, double factor, int noDataValue) {
		this.ndv = noDataValue;
		this._factor = factor;
		setCapacity(capacity);
	}

	public void clear() {
		this._size = 0;
		for (int i = 0; i < this._nmax; ++i)
			this._values[i] = this.ndv;
	}

	public int size() {
		return this._size;
	}

	public boolean isEmpty() {
		return (this._size == 0);
	}

	public int peek(int defaultValue) {
		for (int v = 0; v < this._values.length; ++v) {
			if (this._values[v] != this.ndv) {
				return this._values[v];
			}
		}
		return defaultValue;
	}

	public int[] getValues() {
		int index = 0;
		int[] values = new int[this._size];
		for (int v = 0; v < this._values.length; ++v) {
			if (this._values[v] != this.ndv) {
				values[(index++)] = this._values[v];
			}
		}
		return values;
	}

	public boolean contains(int value) {
		return (this._values[indexOf(value)] != this.ndv);
	}

	public boolean remove(int value) {
		int i = indexOf(value);
		if (this._values[i] == this.ndv) {
			return false;
		}
		this._size -= 1;
		while (true) {
			this._values[i] = this.ndv;
			int j = i;
			int r;
			do {
				i = i - 1 & this._mask;
				if (this._values[i] == this.ndv) {
					return true;
				}
				r = hash(this._values[i]);
			} while (((i <= r) && (r < j)) || ((r < j) && (j < i)) || ((j < i) && (i <= r)));
			this._values[j] = this._values[i];
		}
	}

	public boolean add(int value) {
		if (value == this.ndv) {
			throw new IllegalArgumentException("Can't add the 'no data' value");
		}
		int i = indexOf(value);
		if (this._values[i] == this.ndv) {
			this._size += 1;
			this._values[i] = value;
			if (this._size > 1073741824) {
				throw new RuntimeException("Too many elements (> 1073741824)");
			}
			if ((this._nlo < this._size) && (this._size <= this._nhi)) {
				setCapacity(this._size);
			}
			return true;
		}
		return false;
	}

	private int hash(int key) {
		return (1327217885 * key >> this._shift & this._mask);
	}

	private int indexOf(int value) {
		int i = hash(value);
		while (this._values[i] != this.ndv) {
			if (this._values[i] == value) {
				return i;
			}
			i = i - 1 & this._mask;
		}
		return i;
	}

	private void setCapacity(int capacity) {
		if (capacity < _size)
			capacity = _size;
		double factor = _factor >= 0.01D ? _factor <= 0.98999999999999999D ? _factor : 0.98999999999999999D : 0.01D;
		int nbit = 1;
		int nmax;
		for (nmax = 2; (double) nmax * factor < (double) capacity && nmax < 0x40000000; nmax *= 2)
			nbit++;
		int nold = _nmax;
		if (nmax == nold)
			return;
		_nmax = nmax;
		_nlo = (int) ((double) nmax * factor);
		_nhi = (int) (1073741824D * factor);
		_shift = 31 - nbit;
		_mask = nmax - 1;
		_size = 0;
		int values[] = _values;
		_values = new int[nmax];
		Arrays.fill(_values, ndv);
		if (values != null) {
			for (int i = 0; i < nold; i++) {
				int value = values[i];
				if (value != ndv) {
					_size++;
					_values[indexOf(value)] = value;
				}
			}
		}
	}

	public IntIterator iterator() {
		return new IntHashSetIterator();
	}

	public IntHashSet union(IntHashSet other) {
		IntHashSet n = new IntHashSet(size() + other.size());
		IntIterator it = iterator();
		while (it.hasNext()) {
			n.add(it.next());
		}
		it = other.iterator();
		while (it.hasNext()) {
			n.add(it.next());
		}
		return n;
	}

	public IntHashSet intersect(IntHashSet other) {
		IntHashSet n = new IntHashSet(size());
		IntIterator it = iterator();
		while (it.hasNext()) {
			int v = it.next();
			if (other.contains(v)) {
				n.add(v);
			}
		}
		return n;
	}

	public IntHashSet except(IntHashSet other) {
		IntHashSet n = new IntHashSet(size());
		IntIterator it = iterator();
		while (it.hasNext()) {
			int v = it.next();
			if (!(other.contains(v))) {
				n.add(v);
			}
		}
		return n;
	}

	public boolean containsAll(IntHashSet other) {
		IntIterator it = other.iterator();
		while (it.hasNext()) {
			if (!(contains(it.next()))) {
				return false;
			}
		}
		return true;
	}

	public boolean containsSome(IntHashSet other) {
		IntIterator it = other.iterator();
		while (it.hasNext()) {
			if (contains(it.next())) {
				return true;
			}
		}
		return false;
	}

	public boolean equals(Object other) {
		if (other instanceof IntHashSet) {
			IntHashSet s = (IntHashSet) other;
			return ((size() == s.size()) && (containsAll(s)));
		}
		return false;
	}

	public int hashCode() {
		int h = 936247625;
		IntIterator it = iterator();
		while (it.hasNext()) {
			h += it.next();
		}
		return h;
	}
	private class IntHashSetIterator implements IntIterator, Serializable {
		private int i = 0;

		public IntHashSetIterator() {
			this.i = 0;
		}

		public boolean hasNext() {
			while (this.i < IntHashSet.this._values.length) {
				if (IntHashSet.this._values[this.i] != IntHashSet.this.ndv) {
					return true;
				}
				this.i += 1;
			}
			return false;
		}

		public int next() {
			return IntHashSet.this._values[(this.i++)];
		}
	}
}