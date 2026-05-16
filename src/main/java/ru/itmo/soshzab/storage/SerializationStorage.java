package ru.itmo.soshzab.storage;

import ru.itmo.soshzab.domain.ChecklistItem;
import ru.itmo.soshzab.domain.Task;

import java.io.*;
import java.nio.file.*;
import java.util.Map;

public class SerializationStorage {
    @SuppressWarnings("ClassCanBeRecord")
    public static class LabData implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        public final Map<Long, Task> tasks;
        public final Map<Long, ChecklistItem> items;

        public LabData(Map<Long, Task> tasks, Map<Long, ChecklistItem> items) {
            this.tasks = tasks;
            this.items = items;
        }
    }

    public void save(String path, Map<Long, Task> tasks, Map<Long, ChecklistItem> items) throws IOException {
        Path target = Paths.get(path);
        Path temp = Paths.get(path + ".tmp");

        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(temp))) {
            oos.writeObject(new LabData(tasks, items));
        }

        Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    public LabData load(String path) throws IOException, ClassNotFoundException {
        Path source = Paths.get(path);
        if (!Files.exists(source)) {
            throw new FileNotFoundException("Файл не найден: " + path);
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(source))) {
            Object obj = ois.readObject();
            if (!(obj instanceof LabData)) {
                throw new ClassCastException("Неверный формат файла: ожидается структура LabData");
            }
            return (LabData) obj;
        }
    }
}
