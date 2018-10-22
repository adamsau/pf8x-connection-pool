package com.pf8x.collection;

import com.pf8x.exception.Pf8xIllegalStateException;

import java.util.HashMap;

public class DoublyLinkedListQueue<T> implements SimpleQueue<T> {
    private final int capacity;

    private class Node {
        T val;
        Node prev;
        Node next;
    }

    private final HashMap<T, Node> hashMap;
    private Node head;
    private Node tail;
    private int size;

    public DoublyLinkedListQueue(int capacity) {
        this.capacity = capacity;
        hashMap = new HashMap<>(capacity);
    }

    @Override
    public synchronized void enqueue(T e) {
        if(size >= capacity) throw new Pf8xIllegalStateException("DoublyLinkedListQueue reaches maximum!");
        Node node = hashMap.get(e);
        node.val = e;
        if(head == null) head = tail = node;
        else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
        ++size;
    }

    @Override
    public synchronized T dequeue() {
        if(head == null) return null;
        Node node = head;
        head = head.next;
        if(head == null) tail = null;
        else head.prev = null;
        node.prev = null;
        node.next = null;
        --size;

        return node.val;
    }

    @Override
    public synchronized int size() {
        return size;
    }

    private synchronized boolean remove(T e) {
        Node node = hashMap.get(e);
        if(node != head && node.prev == null) return false;

        Node prev = node.prev;
        Node next = node.next;
        if(prev != null) prev.next = next;
        else head = next;
        if(next != null) next.prev = prev;
        else tail = prev;
        node.prev = null;
        node.next = null;
        --size;

        return true;
    }
    public synchronized T removeOrDequeue(T e) {
        if(e == null || !remove(e)) return dequeue();
        return e;
    }

    public synchronized void put(T e) {
        Node node = new Node();
        node.val = e;

        hashMap.put(e, node);
    }
}
