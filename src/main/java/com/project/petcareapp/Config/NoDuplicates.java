package com.project.petcareapp.Config;

import java.util.PriorityQueue;

public class NoDuplicates<E> extends PriorityQueue<E> {
    @Override
    public boolean offer(E e) {
        boolean isAdded = false;
        if (!super.contains(e)) {
            isAdded = super.offer(e);
        }
        return isAdded;
    }
}
