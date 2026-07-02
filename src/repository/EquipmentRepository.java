package repository;

import model.Equipment;
import model.EquipmentCategory;

import java.io.*;
import java.util.*;

public class EquipmentRepository {
    private static final String DATA_FILE = "data/equipment.txt";

    private final Map<String, Equipment> equipmentMap;

    public EquipmentRepository() {
        this.equipmentMap = new LinkedHashMap<>();
    }

    public void add(Equipment equipment) {
        equipmentMap.put(equipment.getEquipmentId(), equipment);
        saveToFile();
    }

    public Equipment getById(String id) {
        return equipmentMap.get(id);
    }

    public List<Equipment> getAll() {
        return new ArrayList<>(equipmentMap.values());
    }

    public List<Equipment> getAvailable() {
        List<Equipment> available = new ArrayList<>();
        for (Equipment e : equipmentMap.values()) {
            if (e.isAvailable()) available.add(e);
        }
        return available;
    }

    public boolean exists(String id) {
        return equipmentMap.containsKey(id);
    }

    public void update(Equipment equipment) {
        equipmentMap.put(equipment.getEquipmentId(), equipment);
        saveToFile();
    }

    public void remove(String id) {
        equipmentMap.remove(id);
        saveToFile();
    }

    public void saveToFile() {
        File file = new File(DATA_FILE);
        file.getParentFile().mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (Equipment e : equipmentMap.values()) {
                pw.println(toCsv(e));
            }
        } catch (IOException e) {
            System.err.println("Error saving equipment: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        equipmentMap.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Equipment e = fromCsv(line);
                if (e != null) {
                    equipmentMap.put(e.getEquipmentId(), e);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading equipment: " + e.getMessage());
        }
    }

    private String toCsv(Equipment e) {
        return e.getEquipmentId() + "," + e.getName() + "," + e.getCategory().name()
                + "," + e.getDailyRate() + "," + e.isAvailable();
    }

    private Equipment fromCsv(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 5) return null;
        try {
            String id = parts[0];
            String name = parts[1];
            EquipmentCategory category = EquipmentCategory.valueOf(parts[2]);
            double dailyRate = Double.parseDouble(parts[3]);
            boolean available = Boolean.parseBoolean(parts[4]);
            return new Equipment(id, name, category, dailyRate, available);
        } catch (Exception ex) {
            System.err.println("Error parsing equipment line: " + line);
            return null;
        }
    }
}
