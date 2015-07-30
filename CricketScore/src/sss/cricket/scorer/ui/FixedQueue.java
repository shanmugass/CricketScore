package sss.cricket.scorer.ui;

import java.util.LinkedList;

public class FixedQueue<T> {

	private final int maxSize;
	public LinkedList<T> list = new LinkedList<T>();

	public FixedQueue(int maxSize) {
		this.maxSize = maxSize < 0 ? 0 : maxSize;
	}

	public void add(T t) {

		list.add(t);
		if (list.size() > maxSize) {
			list.remove(0);
		}
	}

	public T getLastAction() {
		if (list.size() < 2)
			return null;
		return list.get(list.size() - 1);
	}

	public T getLastBeforeAction() {

		if (list.size() < 2)
			return null;

		return list.get(list.size() - 2);
	}

	public void removeLast() {
		list.remove(list.size() - 1);
	}

}