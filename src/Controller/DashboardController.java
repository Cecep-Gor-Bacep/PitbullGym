package Controller;

import DataAccess.DashboardDoA;
import Model.Beverage;
import Model.Dashboard;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// JavaFX imports
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.util.Callback;



public class DashboardController {

    // Labels for Daily Visitor System
    private Label currentDateLabel;
    private Label dailyVisitorCountLabel;
    private Label dailyVisitorIncomeLabel;
    
    // Labels for Beverage Sales System
    private Label beverageRevenueLabel;
    private Label totalBeveragesSoldLabel;
    
    // Buttons
    private Button btnAddDailyVisitor;
    private Button btnConfirmBeverageSale;
    private Button btnRefreshDashboard;
    
    // Beverage selection
    private ComboBox<Beverage> beverageCombo;
    private Beverage selectedBeverage;
    
    // Available beverages
    private List<Beverage> availableBeverages;
    
    private DashboardDoA dashboardDAO;
    private Dashboard todayData;
    private Runnable onDataChangedCallback; // Callback to notify when data changes

    public DashboardController() {
        this.dashboardDAO = new DashboardDoA();
        this.todayData = dashboardDAO.getTodayData();
        initializeBeverages();
    }
    
    // Set callback to be notified when data changes
    public void setOnDataChangedCallback(Runnable callback) {
        this.onDataChangedCallback = callback;
    }
    
    // Initialize available beverages
    private void initializeBeverages() {
        availableBeverages = new ArrayList<>();
        availableBeverages.add(new Beverage("Water", 3000));
        availableBeverages.add(new Beverage("Pre-Workout", 5000));
        availableBeverages.add(new Beverage("Whey Protein", 5000));
        availableBeverages.add(new Beverage("Creatine", 5000));
        availableBeverages.add(new Beverage("Banana", 3000));
        availableBeverages.add(new Beverage("Protein Bar", 5000));
    }

    // Setup dashboard from node lookup
    public void setupDashboard(Node dashboardNode) {
        // Lookup Daily Visitor System labels
        currentDateLabel = (Label) dashboardNode.lookup("#currentDateLabel");
        dailyVisitorCountLabel = (Label) dashboardNode.lookup("#dailyVisitorCountLabel");
        dailyVisitorIncomeLabel = (Label) dashboardNode.lookup("#dailyVisitorIncomeLabel");
        
        // Lookup Beverage Sales System labels
        beverageRevenueLabel = (Label) dashboardNode.lookup("#beverageRevenueLabel");
        totalBeveragesSoldLabel = (Label) dashboardNode.lookup("#totalBeveragesSoldLabel");
        
        // Lookup buttons
        btnAddDailyVisitor = (Button) dashboardNode.lookup("#btnAddDailyVisitor");
        btnConfirmBeverageSale = (Button) dashboardNode.lookup("#btnConfirmBeverageSale");
        btnRefreshDashboard = (Button) dashboardNode.lookup("#btnRefreshDashboard");
        
        // Lookup beverage combo
        beverageCombo = (ComboBox<Beverage>) dashboardNode.lookup("#beverageCombo");
        
        // Setup beverage combo
        if (beverageCombo != null) {
            beverageCombo.getItems().addAll(availableBeverages);
            beverageCombo.setCellFactory(param -> new ListCell<Beverage>() {
                @Override
                protected void updateItem(Beverage item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.toString());
                    }
                }
            });
            beverageCombo.setButtonCell(new ListCell<Beverage>() {
                @Override
                protected void updateItem(Beverage item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Select Beverage");
                    } else {
                        setText(item.toString());
                    }
                }
            });
            beverageCombo.setOnAction(e -> {
                selectedBeverage = beverageCombo.getValue();
            });
        }

        // Setup button actions
        if (btnAddDailyVisitor != null) {
            btnAddDailyVisitor.setOnAction(e -> addDailyVisitor());
        }
        if (btnConfirmBeverageSale != null) {
            btnConfirmBeverageSale.setOnAction(e -> confirmBeverageSale());
            btnConfirmBeverageSale.setDisable(true);
        }
        if (btnRefreshDashboard != null) {
            btnRefreshDashboard.setOnAction(e -> refreshDashboard());
        }
        
        // Enable confirm button when beverage is selected
        if (beverageCombo != null && btnConfirmBeverageSale != null) {
            beverageCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                btnConfirmBeverageSale.setDisable(newVal == null);
            });
        }

        // Initial load
        refreshDashboard();
    }

    // Refresh dashboard data
    public void refreshDashboard() {
        todayData = dashboardDAO.getTodayData();

        // Update current date
        if (currentDateLabel != null) {
            LocalDate today = LocalDate.now();
            String dayOfWeek = today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            String formattedDate = today.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
            currentDateLabel.setText(dayOfWeek + ", " + formattedDate);
        }

        // Update Daily Visitor System
        if (dailyVisitorCountLabel != null) {
            dailyVisitorCountLabel.setText(String.valueOf(todayData.getVisitorCount()));
        }
        if (dailyVisitorIncomeLabel != null) {
            dailyVisitorIncomeLabel.setText(String.format("Rp %,d", todayData.getDailyVisitorIncome()));
        }
        
        // Update Beverage Sales System
        if (beverageRevenueLabel != null) {
            beverageRevenueLabel.setText(String.format("Rp %,d", todayData.getBeverageRevenue()));
        }
        if (totalBeveragesSoldLabel != null) {
            totalBeveragesSoldLabel.setText(String.valueOf(todayData.getProductsSold()));
        }

        System.out.println("✓ Dashboard refreshed");
    }

    // Add daily visitor (increases count by 1, adds Rp 10,000)
    private void addDailyVisitor() {
        if (dashboardDAO.addVisitor()) {
            refreshDashboard();
            notifyDataChanged(); // Notify analytics to refresh
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                    "Daily visitor added!\nIncome: Rp 10,000");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add daily visitor!");
        }
    }

    // Confirm beverage sale
    private void confirmBeverageSale() {
        if (selectedBeverage == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a beverage first!");
            return;
        }
        
        if (dashboardDAO.addBeverageSale(selectedBeverage.getName(), selectedBeverage.getPrice())) {
            refreshDashboard();
            notifyDataChanged(); // Notify analytics to refresh
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                    "Beverage sale confirmed!\n" + selectedBeverage.getName() + 
                    " - Rp " + String.format("%,d", selectedBeverage.getPrice()));
            
            // Reset selection
            beverageCombo.setValue(null);
            selectedBeverage = null;
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add beverage sale!");
        }
    }
    
    // Notify that data has changed (for analytics refresh)
    private void notifyDataChanged() {
        if (onDataChangedCallback != null) {
            onDataChangedCallback.run();
        }
    }

    // Show alert helper
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
