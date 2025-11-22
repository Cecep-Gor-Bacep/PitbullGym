package Controller;

import DataAccess.DashboardDoA;
import DataAccess.MemberDoA;
import Model.Dashboard;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class AnalyticsController {
    
    private Label totalMembersLabel;
    private Label totalDailyVisitorsLabel;
    private Label totalSubscriptionRevenueLabel;
    private Label totalDailyVisitorRevenueLabel;
    private Label totalBeverageRevenueLabel;
    private Label combinedTotalRevenueLabel;
    
    private DashboardDoA dashboardDAO;
    private MemberDoA memberDAO;

    // Default constructor (creates its own DAOs) kept for backward compatibility
    public AnalyticsController() {
        this(new DashboardDoA(), new MemberDoA());
    }

    // Constructor injection to allow sharing DAO instances across controllers
    public AnalyticsController(DashboardDoA dashboardDAO, MemberDoA memberDAO) {
        this.dashboardDAO = dashboardDAO;
        this.memberDAO = memberDAO;
    }

    // Setup analytics from node lookup
    public void setupAnalytics(Node analyticsNode) {
        // Lookup all labels
        totalMembersLabel = (Label) analyticsNode.lookup("#totalMembersLabel");
        totalDailyVisitorsLabel = (Label) analyticsNode.lookup("#totalDailyVisitorsLabel");
        totalSubscriptionRevenueLabel = (Label) analyticsNode.lookup("#totalSubscriptionRevenueLabel");
        totalDailyVisitorRevenueLabel = (Label) analyticsNode.lookup("#totalDailyVisitorRevenueLabel");
        totalBeverageRevenueLabel = (Label) analyticsNode.lookup("#totalBeverageRevenueLabel");
        combinedTotalRevenueLabel = (Label) analyticsNode.lookup("#combinedTotalRevenueLabel");
        
        // Initial load
        refreshAnalytics();
    }

    // Refresh analytics data
    public void refreshAnalytics() {
        // Total registered members
        if (totalMembersLabel != null) {
            int totalMembers = memberDAO.getTotalMembers();
            totalMembersLabel.setText(String.valueOf(totalMembers));
        }
        
        // Total daily visitors (all time - sum of all daily visitor counts)
        if (totalDailyVisitorsLabel != null) {
            // For now, we'll use today's data. In a real system, you'd sum all historical data
            Dashboard todayData = dashboardDAO.getTodayData();
            int totalVisitors = todayData.getVisitorCount(); // This would be sum of all time in production
            totalDailyVisitorsLabel.setText(String.valueOf(totalVisitors));
        }
        
        // Total subscription revenue (from members)
        if (totalSubscriptionRevenueLabel != null) {
            // Calculate based on member plans
            // Monthly: 150k, Special: 650k
            long subscriptionRevenue = calculateSubscriptionRevenue();
            totalSubscriptionRevenueLabel.setText(String.format("Rp %,d", subscriptionRevenue));
        }
        
        // Total daily visitor revenue (all time)
        if (totalDailyVisitorRevenueLabel != null) {
            Dashboard todayData = dashboardDAO.getTodayData();
            long dailyVisitorRevenue = todayData.getDailyVisitorIncome(); // Would sum all time in production
            totalDailyVisitorRevenueLabel.setText(String.format("Rp %,d", dailyVisitorRevenue));
        }
        
        // Total beverage revenue (all time)
        if (totalBeverageRevenueLabel != null) {
            long beverageRevenue = dashboardDAO.getTotalBeverageRevenue();
            totalBeverageRevenueLabel.setText(String.format("Rp %,d", beverageRevenue));
        }
        
        // Combined total revenue
        if (combinedTotalRevenueLabel != null) {
            long subscriptionRevenue = calculateSubscriptionRevenue();
            Dashboard todayData = dashboardDAO.getTodayData();
            long dailyVisitorRevenue = todayData.getDailyVisitorIncome();
            long beverageRevenue = dashboardDAO.getTotalBeverageRevenue();
            long combined = subscriptionRevenue + dailyVisitorRevenue + beverageRevenue;
            combinedTotalRevenueLabel.setText(String.format("Rp %,d", combined));
        }
    }
    
    // Calculate subscription revenue from members
    private long calculateSubscriptionRevenue() {
        long revenue = 0;
        var members = memberDAO.getAllMembers();
        for (var member : members) {
            if ("Monthly".equals(member.getPlanType())) {
                revenue += 150000; // Rp 150k per month
            } else if ("Special".equals(member.getPlanType())) {
                revenue += 650000; // Rp 650k per month
            }
        }
        return revenue;
    }
}

