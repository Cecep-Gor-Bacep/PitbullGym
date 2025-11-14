package Model;

import Model.Member;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MemberData {
    // In-memory storage (tanpa database)
    private List<Member> memberStorage;
    private int currentId;

    // Constructor
    public MemberData() {
        memberStorage = new ArrayList<>();
        currentId = 1;

        // Load dummy data untuk testing
        loadDummyData();
    }

    // Load dummy data
    private void loadDummyData() {
        memberStorage.add(new Member(currentId++, "John Doe", "081234567890", "Monthly",
                LocalDate.now().minusDays(10), LocalDate.now().plusDays(20)));
        memberStorage.add(new Member(currentId++, "Jane Smith", "081234567891", "Special",
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(25)));
        memberStorage.add(new Member(currentId++, "Bob Johnson", "081234567892", "Daily",
                LocalDate.now().minusDays(2), LocalDate.now().minusDays(1)));
        memberStorage.add(new Member(currentId++, "Alice Williams", "081234567893", "Monthly",
                LocalDate.now().minusDays(15), LocalDate.now().plusDays(15)));
        memberStorage.add(new Member(currentId++, "Charlie Brown", "081234567894", "Special",
                LocalDate.now().minusDays(3), LocalDate.now().plusDays(27)));
    }

    // Get all members
    public ObservableList<Member> getAllMembers() {
        ObservableList<Member> members = FXCollections.observableArrayList();

        // Copy semua member dari storage
        for (Member member : memberStorage) {
            members.add(member);
        }

        // Sort by ID descending (newest first)
        members.sort((m1, m2) -> Integer.compare(m2.getId(), m1.getId()));

        return members;
    }

    // Add new member
    public boolean addMember(Member member) {
        try {
            // Set ID otomatis
            member.setId(currentId++);

            // Add ke storage
            memberStorage.add(member);

            System.out.println("✓ Member added successfully: " + member.getName());
            return true;
        } catch (Exception e) {
            System.err.println("✗ Failed to add member: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Update member
    public boolean updateMember(Member updatedMember) {
        try {
            for (int i = 0; i < memberStorage.size(); i++) {
                if (memberStorage.get(i).getId() == updatedMember.getId()) {
                    memberStorage.set(i, updatedMember);
                    System.out.println("✓ Member updated successfully: " + updatedMember.getName());
                    return true;
                }
            }
            System.err.println("✗ Member not found with ID: " + updatedMember.getId());
            return false;
        } catch (Exception e) {
            System.err.println("✗ Failed to update member: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Delete member
    public boolean deleteMember(int id) {
        try {
            for (int i = 0; i < memberStorage.size(); i++) {
                if (memberStorage.get(i).getId() == id) {
                    Member removed = memberStorage.remove(i);
                    System.out.println("✓ Member deleted successfully: " + removed.getName());
                    return true;
                }
            }
            System.err.println("✗ Member not found with ID: " + id);
            return false;
        } catch (Exception e) {
            System.err.println("✗ Failed to delete member: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Search members by name or phone
    public ObservableList<Member> searchMembers(String keyword) {
        ObservableList<Member> results = FXCollections.observableArrayList();

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllMembers();
        }

        String searchKey = keyword.toLowerCase().trim();

        for (Member member : memberStorage) {
            if (member.getName().toLowerCase().contains(searchKey) ||
                    member.getPhone().contains(searchKey)) {
                results.add(member);
            }
        }

        // Sort by ID descending
        results.sort((m1, m2) -> Integer.compare(m2.getId(), m1.getId()));

        System.out.println("Search results for '" + keyword + "': " + results.size() + " found");
        return results;
    }

    // Get total members count
    public int getTotalMembers() {
        return memberStorage.size();
    }

    // Get active members count
    public int getActiveMembers() {
        LocalDate today = LocalDate.now();
        return (int) memberStorage.stream()
                .filter(m -> m.getEndDate().isAfter(today) || m.getEndDate().isEqual(today))
                .count();
    }

    // Get expired members count
    public int getExpiredMembers() {
        LocalDate today = LocalDate.now();
        return (int) memberStorage.stream()
                .filter(m -> m.getEndDate().isBefore(today))
                .count();
    }

    // Get members by plan type
    public ObservableList<Member> getMembersByPlan(String planType) {
        ObservableList<Member> results = FXCollections.observableArrayList();

        for (Member member : memberStorage) {
            if (member.getPlanType().equalsIgnoreCase(planType)) {
                results.add(member);
            }
        }

        return results;
    }

    // Get members by status
    public ObservableList<Member> getMembersByStatus(String status) {
        ObservableList<Member> results = FXCollections.observableArrayList();

        for (Member member : memberStorage) {
            if (member.getStatus().equalsIgnoreCase(status)) {
                results.add(member);
            }
        }

        return results;
    }

    // Check if member exists by phone
    public boolean memberExistsByPhone(String phone) {
        for (Member member : memberStorage) {
            if (member.getPhone().equals(phone)) {
                return true;
            }
        }
        return false;
    }

    // Get member by ID
    public Member getMemberById(int id) {
        for (Member member : memberStorage) {
            if (member.getId() == id) {
                return member;
            }
        }
        return null;
    }

    // Clear all members (for testing)
    public void clearAllMembers() {
        memberStorage.clear();
        currentId = 1;
        System.out.println("✓ All members cleared");
    }

    // Print all members to console (for debugging)
    public void printAllMembers() {
        System.out.println("\n========== MEMBER LIST ==========");
        System.out.printf("%-5s %-20s %-15s %-10s %-12s %-12s %-10s%n",
                "ID", "Name", "Phone", "Plan", "Start", "End", "Status");
        System.out.println("=".repeat(85));

        for (Member member : memberStorage) {
            System.out.printf("%-5d %-20s %-15s %-10s %-12s %-12s %-10s%n",
                    member.getId(),
                    member.getName(),
                    member.getPhone(),
                    member.getPlanType(),
                    member.getStartDate(),
                    member.getEndDate(),
                    member.getStatus());
        }

        System.out.println("=".repeat(85));
        System.out.println("Total: " + getTotalMembers() +
                " | Active: " + getActiveMembers() +
                " | Expired: " + getExpiredMembers());
        System.out.println("================================\n");
    }

    // Note: Method close() tidak diperlukan karena tidak ada koneksi database
}