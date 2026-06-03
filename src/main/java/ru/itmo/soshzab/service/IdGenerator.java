package ru.itmo.soshzab.service;

public class IdGenerator {
    private long currentId = 1;

    public synchronized long nextId() {
        return currentId++;
    }

    public synchronized void setCurrentId(long id) {
        this.currentId = id;
    }
}