package Controller;

import DataAccess.DashboardDoA;
import Model.Dashboard;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Optional;

public class DashboardController {

    private Label currentDateLabel;
    private Label visitorCountLabel;
    private Label productsSoldLabel;
    private Label dailyIncomeLabel;
    private Label weeklyVisitorsLabel;
    private Label weeklyIncomeLabel;
    private Label monthlyVisitorsLabel;
    private Label monthlyIncomeLabel;

    private Button btnAddVisitor;
    private Button btnAddProduct;
    private Button btnAddIncome;
    private Button btnRefreshDashboard;

    private DashboardDoA dashboardDAO;
    private Dashboard todayData;

    public DashboardController() {
        this.dashboardDAO = new DashboardDoA();
        this.todayData = dashboardDAO.getTodayData();
    }

    // Setup dashboard dari node yang di-lookup
    public void setupDashboard(Node dashboardNode) {
        // Lookup labels
        currentDateLabel = (Label) dashboardNode.lookup("#currentDateLabel");
        visitorCountLabel = (Label) dashboardNode.lookup("#visitorCountLabel");
        productsSoldLabel = (Label) dashboardNode.lookup("#productsSoldLabel");
        dailyIncomeLabel = (Label) dashboardNode.lookup("#dailyIncomeLabel");
        weeklyVisitorsLabel = (Label) dashboardNode.lookup("#weeklyVisitorsLabel");
        weeklyIncomeLabel = (Label) dashboardNode.lookup("#weeklyIncomeLabel");
        monthlyVisitorsLabel = (Label) dashboardNode.lookup("#monthlyVisitorsLabel");
        monthlyIncomeLabel = (Label) dashboardNode.lookup("#monthlyIncomeLabel");

        // Lookup buttons
        btnAddVisitor = (Button) dashboardNode.lookup("#btnAddVisitor");
        btnAddProduct = (Button) dashboardNode.lookup("#btnAddProduct");
        btnAddIncome = (Button) dashboardNode.lookup("#btnAddIncome");
        btnRefreshDashboard = (Button) dashboardNode.lookup("#btnRefreshDashboard");

        // Setup button actions
        if (btnAddVisitor != null) {
            btnAddVisitor.setOnAction(e -> showAddVisitorDialog());
        }
        if (btnAddProduct != null) {
            btnAddProduct.setOnAction(e -> showAddProductDialog());
        }
        if (btnAddIncome != null) {
            btnAddIncome.setOnAction(e -> showAddIncomeDialog());
        }
        if (btnRefreshDashboard != null) {
            btnRefreshDashboard.setOnAction(e -> refreshDashboard());
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

        // Update today's statistics
        if (visitorCountLabel != null) {
            visitorCountLabel.setText(String.valueOf(todayData.getVisitorCount()));
        }
        if (productsSoldLabel != null) {
            productsSoldLabel.setText(String.valueOf(todayData.getProductsSold()));
        }
        if (dailyIncomeLabel != null) {
            dailyIncomeLabel.setText(String.format("Rp %,d", todayData.getDailyIncome()));
        }

        // Update weekly statistics
        if (weeklyVisitorsLabel != null) {
            weeklyVisitorsLabel.setText(String.valueOf(dashboardDAO.getWeeklyVisitors()));
        }
        if (weeklyIncomeLabel != null) {
            weeklyIncomeLabel.setText(String.format("Rp %,d", dashboardDAO.getWeeklyIncome()));
        }

        // Update monthly statistics
        if (monthlyVisitorsLabel != null) {
            monthlyVisitorsLabel.setText(String.valueOf(dashboardDAO.getMonthlyVisitors()));
        }
        if (monthlyIncomeLabel != null) {
            monthlyIncomeLabel.setText(String.format("Rp %,d", dashboardDAO.getMonthlyIncome()));
        }

        System.out.println("✓ Dashboard refreshed");
        dashboardDAO.printDashboardSummary();
    }

    // Show dialog to add visitor
    private void showAddVisitorDialog() {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("➕ Add Visitor");

        ButtonType addButtonType = new ButtonType("✅ Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Apply futuristic styling
        dialog.getDialogPane().setStyle(
                "-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e);" +
                        "-fx-border-color: #4CAF50;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;"
        );

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(30, 40, 30, 40));
        grid.setStyle("-fx-background-color: transparent;");

        Label infoLabel = new Label("Each visitor pays Rp 10,000 for daily gym access");
        infoLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 12; -fx-padding: 0 0 10 0;");

        Label countLabel = new Label("👥 Number of Visitors:");
        countLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold; -fx-font-size: 14;");

        Spinner<Integer> visitorSpinner = new Spinner<>(1, 100, 1);
        visitorSpinner.setEditable(true);
        visitorSpinner.setStyle(
                "-fx-background-color: rgba(76, 175, 80, 0.1);" +
                        "-fx-border-color: #4CAF50;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );

        Label previewLabel = new Label("💰 Total Income: Rp 10,000");
        previewLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold; -fx-font-size: 13;");

        // Update preview saat spinner berubah
        visitorSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            long income = newVal * Dashboard.DAILY_GYM_FEE;
            previewLabel.setText(String.format("💰 Total Income: Rp %,d", income));
        });

        grid.add(infoLabel, 0, 0, 2, 1);
        grid.add(countLabel, 0, 1);
        grid.add(visitorSpinner, 1, 1);
        grid.add(previewLabel, 0, 2, 2, 1);

        dialog.getDialogPane().setContent(grid);

        // Style buttons
        styleDialogButtons(dialog, addButtonType, "#4CAF50");

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return visitorSpinner.getValue();
            }
            return null;
        });

        Optional<Integer> result = dialog.showAndWait();
        result.ifPresent(count -> {
            if (dashboardDAO.addVisitors(count)) {
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        count + " visitor(s) added successfully!\nIncome: Rp " +
                                String.format("%,d", count * Dashboard.DAILY_GYM_FEE));
                refreshDashboard();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add visitors!");
            }
        });
    }

    // Show dialog to add product sale
    private void showAddProductDialog() {
        Dialog<Long> dialog = new Dialog<>();
        dialog.setTitle("🛒 Add Product Sale");

        ButtonType addButtonType = new ButtonType("✅ Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Apply styling
        dialog.getDialogPane().setStyle(
                "-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e);" +
                        "-fx-border-color: #2196F3;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;"
        );

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(30, 40, 30, 40));
        grid.setStyle("-fx-background-color: transparent;");

        Label productLabel = new Label("🏷️ Product Name:");
        productLabel.setStyle("-fx-text-fill: #2196F3; -fx-font-weight: bold; -fx-font-size: 14;");

        TextField productField = new TextField();
        productField.setPromptText("e.g. Protein Shake");
        productField.setStyle(
                "-fx-background-color: rgba(33, 150, 243, 0.1);" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: rgba(33, 150, 243, 0.5);" +
                        "-fx-border-color: #2196F3;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10;"
        );

        Label priceLabel = new Label("💵 Price (Rp):");
        priceLabel.setStyle("-fx-text-fill: #2196F3; -fx-font-weight: bold; -fx-font-size: 14;");

        TextField priceField = new TextField();
        priceField.setPromptText("e.g. 50000");
        priceField.setStyle(
                "-fx-background-color: rgba(33, 150, 243, 0.1);" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: rgba(33, 150, 243, 0.5);" +
                        "-fx-border-color: #2196F3;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10;"
        );

        grid.add(productLabel, 0, 0);
        grid.add(productField, 1, 0);
        grid.add(priceLabel, 0, 1);
        grid.add(priceField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Style buttons
        styleDialogButtons(dialog, addButtonType, "#2196F3");

        // Disable add button if price is empty or invalid
        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        priceField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                Long.parseLong(newVal);
                addButton.setDisable(false);
            } catch (NumberFormatException e) {
                addButton.setDisable(true);
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    return Long.parseLong(priceField.getText());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        Optional<Long> result = dialog.showAndWait();
        result.ifPresent(price -> {
            if (price != null && price > 0) {
                if (dashboardDAO.addProductSale(price)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success",
                            "Product sold successfully!\nPrice: Rp " + String.format("%,d", price));
                    refreshDashboard();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to add product sale!");
                }
            }
        });
    }

    // Show dialog to add custom income
    private void showAddIncomeDialog() {
        Dialog<Long> dialog = new Dialog<>();
        dialog.setTitle("💵 Add Custom Income");

        ButtonType addButtonType = new ButtonType("✅ Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Apply styling
        dialog.getDialogPane().setStyle(
                "-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e);" +
                        "-fx-border-color: #FF9800;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;"
        );

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(30, 40, 30, 40));
        grid.setStyle("-fx-background-color: transparent;");

        Label descLabel = new Label("📝 Description:");
        descLabel.setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold; -fx-font-size: 14;");

        TextField descField = new TextField();
        descField.setPromptText("e.g. Personal Training Session");
        descField.setStyle(
                "-fx-background-color: rgba(255, 152, 0, 0.1);" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: rgba(255, 152, 0, 0.5);" +
                        "-fx-border-color: #FF9800;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10;"
        );

        Label amountLabel = new Label("💰 Amount (Rp):");
        amountLabel.setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold; -fx-font-size: 14;");

        TextField amountField = new TextField();
        amountField.setPromptText("e.g. 100000");
        amountField.setStyle(
                "-fx-background-color: rgba(255, 152, 0, 0.1);" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: rgba(255, 152, 0, 0.5);" +
                        "-fx-border-color: #FF9800;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10;"
        );

        grid.add(descLabel, 0, 0);
        grid.add(descField, 1, 0);
        grid.add(amountLabel, 0, 1);
        grid.add(amountField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Style buttons
        styleDialogButtons(dialog, addButtonType, "#FF9800");

        // Disable add button if amount is empty or invalid
        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        amountField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                Long.parseLong(newVal);
                addButton.setDisable(false);
            } catch (NumberFormatException e) {
                addButton.setDisable(true);
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    return Long.parseLong(amountField.getText());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        Optional<Long> result = dialog.showAndWait();
        result.ifPresent(amount -> {
            if (amount != null && amount > 0) {
                if (dashboardDAO.addIncome(amount)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success",
                            "Income added successfully!\nAmount: Rp " + String.format("%,d", amount));
                    refreshDashboard();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to add income!");
                }
            }
        });
    }

    // Helper method to style dialog buttons
    private void styleDialogButtons(Dialog<?> dialog, ButtonType actionButton, String color) {
        Node actionBtn = dialog.getDialogPane().lookupButton(actionButton);
        Node cancelBtn = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);

        actionBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, " + color + ", " + adjustColor(color) + ");" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 12 30 12 30;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, " + color + "99, 10, 0, 0, 0);"
        );

        cancelBtn.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                        "-fx-text-fill: " + color + ";" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-padding: 12 30 12 30;" +
                        "-fx-cursor: hand;"
        );
    }

    // Helper to adjust color brightness
    private String adjustColor(String hex) {
        // Simple color adjustment (darker shade)
        return hex.replace("4CAF50", "45a049")
                .replace("2196F3", "1976D2")
                .replace("FF9800", "F57C00");
    }

    // Show alert
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}