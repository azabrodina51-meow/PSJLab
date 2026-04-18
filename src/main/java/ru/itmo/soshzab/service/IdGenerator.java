package ru.itmo.soshzab.service;

public class IdGenerator {
    private long currentId = 1;

    public synchronized long nextId() {
        return currentId++;
    }
}