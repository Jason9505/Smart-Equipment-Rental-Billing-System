package repository;

import model.Rental;
import model.RentalBill;
import model.RentalStatus;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class RentalRepository {
    private static final String DATA_FILE = "data/rentals.txt";

    private final Map<String, Rental> rentalMap;

    public RentalRepository() {
        this.rentalMap = new LinkedHashMap<>();
    }

    public void add(Rental rental) {
        rentalMap.put(rental.getRentalId(), rental);
        saveToFile();
    }

    public Rental getById(String id) {
        return rentalMap.get(id);
    }

    public List<Rental> getAll() {
        return new ArrayList<>(rentalMap.values());
    }

    public List<Rental> getByStatus(RentalStatus status) {
        List<Rental> result = new ArrayList<>();
        for (Rental r : rentalMap.values()) {
            if (r.getStatus() == status) result.add(r);
        }
        return result;
    }

    public boolean exists(String id) {
        return rentalMap.containsKey(id);
    }

    public void update(Rental rental) {
        rentalMap.put(rental.getRentalId(), rental);
        saveToFile();
    }

    public void remove(String id) {
        rentalMap.remove(id);
        saveToFile();
    }

    public void saveToFile() {
        File file = new File(DATA_FILE);
        file.getParentFile().mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (Rental r : rentalMap.values()) {
                pw.println(toCsv(r));
            }
        } catch (IOException e) {
            System.err.println("Error saving rentals: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        rentalMap.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Rental r = fromCsv(line);
                if (r != null) {
                    rentalMap.put(r.getRentalId(), r);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading rentals: " + e.getMessage());
        }
    }

    private String toCsv(Rental r) {
        RentalBill b = r.getBill();
        String returnDate = r.getReturnDate() == null ? "-" : r.getReturnDate().toString();
        String baseFee = b == null ? "-" : String.valueOf(b.getBaseFee());
        String discount = b == null ? "-" : String.valueOf(b.getDiscount());
        String penalty = b == null ? "-" : String.valueOf(b.getPenalty());
        String net = b == null ? "-" : String.valueOf(b.getNetPayable());

        return String.join(",",
                r.getRentalId(), r.getEquipmentId(), r.getUserId(),
                r.getRentDate().toString(), r.getDueDate().toString(), returnDate,
                String.valueOf(r.isDamaged()), r.getStatus().name(),
                baseFee, discount, penalty, net);
    }

    private Rental fromCsv(String line) {
        String[] p = line.split(",", -1);
        if (p.length < 12) return null;
        try {
            String rentalId = p[0];
            String equipmentId = p[1];
            String userId = p[2];
            LocalDate rentDate = LocalDate.parse(p[3]);
            LocalDate dueDate = LocalDate.parse(p[4]);
            LocalDate returnDate = p[5].equals("-") ? null : LocalDate.parse(p[5]);
            boolean damaged = Boolean.parseBoolean(p[6]);
            RentalStatus status = RentalStatus.valueOf(p[7]);

            RentalBill bill = null;
            if (!p[8].equals("-")) {
                double baseFee = Double.parseDouble(p[8]);
                double discount = Double.parseDouble(p[9]);
                double penalty = Double.parseDouble(p[10]);
                bill = new RentalBill(baseFee, discount, penalty);
            }

            return new Rental(rentalId, equipmentId, userId, rentDate, dueDate,
                    returnDate, damaged, status, bill);
        } catch (Exception ex) {
            System.err.println("Error parsing rental line: " + line);
            return null;
        }
    }
}
