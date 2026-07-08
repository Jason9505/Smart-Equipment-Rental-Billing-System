package service;

import java.util.List;
import model.Equipment;
import model.EquipmentCategory;
import repository.EquipmentRepository;

public class EquipmentService {
    private final EquipmentRepository equipmentRepo;

    public EquipmentService(EquipmentRepository equipmentRepo) {
        this.equipmentRepo = equipmentRepo;
    }

    public String addEquipment(String id, String name, EquipmentCategory category, double dailyRate) {
        if (id == null || id.trim().isEmpty()) return "Equipment ID is required.";
        if (name == null || name.trim().isEmpty()) return "Name is required.";
        if (category == null) return "Category is required.";
        if (dailyRate <= 0) return "Daily rate must be greater than 0.";
        if (equipmentRepo.exists(id.trim())) return "Equipment ID already exists.";

        equipmentRepo.add(new Equipment(id.trim(), name.trim(), category, dailyRate, true));
        return null;
    }

    public String updateEquipment(String id, String name, EquipmentCategory category, double dailyRate) {
        if (id == null || !equipmentRepo.exists(id)) return "Equipment not found.";
        if (dailyRate <= 0) return "Daily rate must be greater than 0.";

        Equipment e = equipmentRepo.getById(id);
        e.setName(name);
        e.setCategory(category);
        e.setDailyRate(dailyRate);
        equipmentRepo.update(e);
        return null;
    }

    public String deleteEquipment(String id) {
        if (id == null || !equipmentRepo.exists(id)) return "Equipment not found.";
        Equipment e = equipmentRepo.getById(id);
        if (!e.isAvailable()) return "Cannot delete equipment that is currently rented out.";
        equipmentRepo.remove(id);
        return null;
    }

    public List<Equipment> getAll() { return equipmentRepo.getAll(); }
    public List<Equipment> getAvailable() { return equipmentRepo.getAvailable(); }
    public Equipment getById(String id) { return equipmentRepo.getById(id); }
}
