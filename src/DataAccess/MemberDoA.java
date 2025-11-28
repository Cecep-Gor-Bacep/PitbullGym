package DataAccess;

import Model.Member;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

/**
 * MemberDoA - Data Access Object untuk Member
 * Menggunakan SQLite database untuk persistent storage
 */
public class MemberDoA {
    private static final String TABLE_NAME = "members";

    // Constructor
    public MemberDoA() {
        // Pastikan database sudah diinisialisasi
        if (!DatabaseManager.isConnected()) {
            DatabaseManager.initialize();
        }
    }

    /**
     * Get all members dari database
     */
    public ObservableList<Member> getAllMembers() {
        ObservableList<Member> members = FXCollections.observableArrayList();

        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY id ASC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Member member = mapResultSetToMember(rs);
                members.add(member);
            }
            System.out.println("✓ Retrieved " + members.size() + " members from database");
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving members: " + e.getMessage());
            e.printStackTrace();
        }

        return members;
    }

    // Add new member ke database
    public boolean addMember(Member member) {
        try {
            // Check apakah phone sudah terdaftar
            if (memberExistsByPhone(member.getPhone())) {
                System.err.println("✗ Member with phone " + member.getPhone() + " already exists");
                return false;
            }

            String query = "INSERT INTO " + TABLE_NAME +
                    " (name, phone, plan_type, start_date, end_date, status, membership_count) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            int rowsAffected = DatabaseManager.executeUpdate(query,
                    member.getName(),
                    member.getPhone(),
                    member.getPlanType(),
                    member.getStartDate().toString(),
                    member.getEndDate().toString(),
                    member.getStatus(),
                    member.getMembershipCount()
            );

            if (rowsAffected > 0) {
                System.out.println("✓ Member added successfully: " + member.getName());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error adding member: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Update member di database
    public boolean updateMember(Member member) {
        try {
            String query = "UPDATE " + TABLE_NAME +
                    " SET name=?, phone=?, plan_type=?, start_date=?, end_date=?, status=?, membership_count=? " +
                    "WHERE id=?";

            int rowsAffected = DatabaseManager.executeUpdate(query,
                    member.getName(),
                    member.getPhone(),
                    member.getPlanType(),
                    member.getStartDate().toString(),
                    member.getEndDate().toString(),
                    member.getStatus(),
                    member.getMembershipCount(),
                    member.getId()
            );

            if (rowsAffected > 0) {
                System.out.println("✓ Member updated successfully: " + member.getName());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error updating member: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Delete member dari database
    public boolean deleteMember(int id) {
        try {
            String query = "DELETE FROM " + TABLE_NAME + " WHERE id=?";

            int rowsAffected = DatabaseManager.executeUpdate(query, id);

            if (rowsAffected > 0) {
                System.out.println("✓ Member deleted successfully (ID: " + id + ")");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error deleting member: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Search members by name atau phone (Live Search)
    public ObservableList<Member> searchMembers(String keyword) {
        ObservableList<Member> results = FXCollections.observableArrayList();

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllMembers();
        }

        String searchPattern = "%" + keyword.toLowerCase() + "%";
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE LOWER(name) LIKE ? OR phone LIKE ? " +
                "ORDER BY id ASC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Member member = mapResultSetToMember(rs);
                    results.add(member);
                }
            }

            System.out.println("Search results for '" + keyword + "': " + results.size() + " found");
        } catch (SQLException e) {
            System.err.println("✗ Error searching members: " + e.getMessage());
            e.printStackTrace();
        }

        return results;
    }

    // Get total members count
    public int getTotalMembers() {
        return countMembersWithCondition(null);
    }

    // Helper method untuk count members
    private int countMembersWithCondition(String status) {
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME;
        if (status != null) {
            query += " WHERE status = ?";
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (status != null) {
                stmt.setString(1, status);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error counting members: " + e.getMessage());
        }

        return 0;
    }

    // Get active members count
    public int getActiveMembers() {
        return countMembersWithCondition("Active");
    }

    // Get expired members count
    public int getExpiredMembers() {
        return countMembersWithCondition("Expired");
    }

    // Get members by plan type
    public ObservableList<Member> getMembersByPlan(String planType) {
        ObservableList<Member> results = FXCollections.observableArrayList();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE plan_type = ? ORDER BY id ASC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, planType);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Member member = mapResultSetToMember(rs);
                    results.add(member);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving members by plan: " + e.getMessage());
        }

        return results;
    }

    // Get members by status
    public ObservableList<Member> getMembersByStatus(String status) {
        ObservableList<Member> results = FXCollections.observableArrayList();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE status = ? ORDER BY id ASC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Member member = mapResultSetToMember(rs);
                    results.add(member);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving members by status: " + e.getMessage());
        }

        return results;
    }

    // Check if member exists by phone
    public boolean memberExistsByPhone(String phone) {
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE phone = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phone);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error checking member existence: " + e.getMessage());
        }

        return false;
    }

    // Get member by ID
    public Member getMemberById(int id) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMember(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving member by ID: " + e.getMessage());
        }

        return null;
    }

    // Clear all members (for testing/reset)
    public void clearAllMembers() {
        // Prefer TRUNCATE to remove all rows and reset AUTO_INCREMENT.
        String truncateQuery = "TRUNCATE TABLE " + TABLE_NAME;
        try {
            DatabaseManager.executeUpdate(truncateQuery);
            System.out.println("✓ All members cleared from database (table truncated, AUTO_INCREMENT reset)");
            return;
        } catch (SQLException e) {
            // TRUNCATE may fail if there are foreign key constraints or insufficient privileges.
            System.err.println("⚠ TRUNCATE failed, falling back to DELETE + ALTER TABLE: " + e.getMessage());
        }

        // Fallback: DELETE all rows then reset AUTO_INCREMENT
        String deleteQuery = "DELETE FROM " + TABLE_NAME;
        try {
            int rows = DatabaseManager.executeUpdate(deleteQuery);
            // Reset AUTO_INCREMENT to 1
            String resetAi = "ALTER TABLE " + TABLE_NAME + " AUTO_INCREMENT = 1";
            try {
                DatabaseManager.executeUpdate(resetAi);
                System.out.println("✓ All members cleared from database (deleted: " + rows + ") and AUTO_INCREMENT reset");
            } catch (SQLException ex) {
                System.err.println("✗ Deleted rows, but failed to reset AUTO_INCREMENT: " + ex.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("✗ Error clearing members: " + e.getMessage());
        }
    }

    // Print all members to console (debugging)
    public void printAllMembers() {
        ObservableList<Member> members = getAllMembers();

        System.out.println("\n========== MEMBER LIST ==========");
        System.out.printf("%-5s %-20s %-15s %-10s %-12s %-12s %-10s%n",
                "ID", "Name", "Phone", "Plan", "Start", "End", "Status");
        System.out.println("=".repeat(85));

        for (Member member : members) {
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

    /**
     * Map ResultSet ke Member object
     */
    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String phone = rs.getString("phone");
        String planType = rs.getString("plan_type");
        LocalDate startDate = LocalDate.parse(rs.getString("start_date"));
        LocalDate endDate = LocalDate.parse(rs.getString("end_date"));
        int membershipCount = rs.getInt("membership_count");

        return new Member(id, name, phone, planType, startDate, endDate, membershipCount);
    }
}