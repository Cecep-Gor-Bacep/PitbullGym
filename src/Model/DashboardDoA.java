package Model;

import Model.DashboardData;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DashboardDoA {

    // In-memory storage untuk dashboard data (key = date, value = data)
    private Map<LocalDate, DashboardData> dashboardStorage;
    private DashboardData todayData;

    public DashboardDoA() {
        dashboardStorage = new HashMap<>();

        // Initialize today's data
        LocalDate today = LocalDate.now();
        todayData = new DashboardData(today, 0, 0, 0);
        dashboardStorage.put(today, todayData);

        // Load dummy historical data for testing
        loadDummyData();
    }

    private void loadDummyData() {
        // Data kemarin
        LocalDate yesterday = LocalDate.now().minusDays(1);
        DashboardData yesterdayData = new DashboardData(yesterday, 25, 8, 350000);
        dashboardStorage.put(yesterday, yesterdayData);

        // Data 2 hari lalu
        LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
        DashboardData twoDaysAgoData = new DashboardData(twoDaysAgo, 30, 12, 480000);
        dashboardStorage.put(twoDaysAgo, twoDaysAgoData);

        // Data 3 hari lalu
        LocalDate threeDaysAgo = LocalDate.now().minusDays(3);
        DashboardData threeDaysAgoData = new DashboardData(threeDaysAgo, 28, 10, 420000);
        dashboardStorage.put(threeDaysAgo, threeDaysAgoData);
    }

    // Get today's data
    public DashboardData getTodayData() {
        LocalDate today = LocalDate.now();

        // Check if date changed (new day)
        if (!todayData.getDate().equals(today)) {
            // Save yesterday's data
            dashboardStorage.put(todayData.getDate(), todayData);

            // Create new today's data
            todayData = new DashboardData(today, 0, 0, 0);
            dashboardStorage.put(today, todayData);
        }

        return todayData;
    }

    // Get data by date
    public DashboardData getDataByDate(LocalDate date) {
        return dashboardStorage.getOrDefault(date, new DashboardData(date, 0, 0, 0));
    }

    // Add visitor to today
    public boolean addVisitor() {
        try {
            getTodayData().addVisitor();
            System.out.println("✓ Visitor added. New count: " + todayData.getVisitorCount());
            return true;
        } catch (Exception e) {
            System.err.println("✗ Failed to add visitor: " + e.getMessage());
            return false;
        }
    }

    // Add multiple visitors
    public boolean addVisitors(int count) {
        try {
            for (int i = 0; i < count; i++) {
                getTodayData().addVisitor();
            }
            System.out.println("✓ " + count + " visitors added. Total: " + todayData.getVisitorCount());
            return true;
        } catch (Exception e) {
            System.err.println("✗ Failed to add visitors: " + e.getMessage());
            return false;
        }
    }

    // Add product sale
    public boolean addProductSale(long productPrice) {
        try {
            getTodayData().addProductSale(productPrice);
            System.out.println("✓ Product sale added. Price: Rp " + String.format("%,d", productPrice));
            return true;
        } catch (Exception e) {
            System.err.println("✗ Failed to add product sale: " + e.getMessage());
            return false;
        }
    }

    // Add custom income
    public boolean addIncome(long amount) {
        try {
            getTodayData().addIncome(amount);
            System.out.println("✓ Income added. Amount: Rp " + String.format("%,d", amount));
            return true;
        } catch (Exception e) {
            System.err.println("✗ Failed to add income: " + e.getMessage());
            return false;
        }
    }

    // Get total visitors this week
    public int getWeeklyVisitors() {
        int total = 0;
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            DashboardData data = dashboardStorage.get(date);
            if (data != null) {
                total += data.getVisitorCount();
            }
        }

        return total;
    }

    // Get total income this week
    public long getWeeklyIncome() {
        long total = 0;
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            DashboardData data = dashboardStorage.get(date);
            if (data != null) {
                total += data.getDailyIncome();
            }
        }

        return total;
    }

    // Get total products sold this week
    public int getWeeklyProductsSold() {
        int total = 0;
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            DashboardData data = dashboardStorage.get(date);
            if (data != null) {
                total += data.getProductsSold();
            }
        }

        return total;
    }

    // Get total visitors this month
    public int getMonthlyVisitors() {
        int total = 0;
        LocalDate today = LocalDate.now();
        int daysInMonth = today.lengthOfMonth();

        for (int i = 0; i < daysInMonth; i++) {
            LocalDate date = today.minusDays(i);
            if (date.getMonth() != today.getMonth()) break;

            DashboardData data = dashboardStorage.get(date);
            if (data != null) {
                total += data.getVisitorCount();
            }
        }

        return total;
    }

    // Get total income this month
    public long getMonthlyIncome() {
        long total = 0;
        LocalDate today = LocalDate.now();
        int daysInMonth = today.lengthOfMonth();

        for (int i = 0; i < daysInMonth; i++) {
            LocalDate date = today.minusDays(i);
            if (date.getMonth() != today.getMonth()) break;

            DashboardData data = dashboardStorage.get(date);
            if (data != null) {
                total += data.getDailyIncome();
            }
        }

        return total;
    }

    // Print dashboard summary
    public void printDashboardSummary() {
        System.out.println("\n========== DASHBOARD SUMMARY ==========");
        System.out.println("Date: " + LocalDate.now());
        System.out.println("Today's Visitors: " + todayData.getVisitorCount());
        System.out.println("Today's Products Sold: " + todayData.getProductsSold());
        System.out.println("Today's Income: " + todayData.getFormattedIncome());
        System.out.println("---------------------------------------");
        System.out.println("Weekly Visitors: " + getWeeklyVisitors());
        System.out.println("Weekly Income: Rp " + String.format("%,d", getWeeklyIncome()));
        System.out.println("Monthly Visitors: " + getMonthlyVisitors());
        System.out.println("Monthly Income: Rp " + String.format("%,d", getMonthlyIncome()));
        System.out.println("=======================================\n");
    }
}