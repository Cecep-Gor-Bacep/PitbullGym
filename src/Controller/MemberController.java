package Controller;

import DataAccess.MemberDoA;
import Model.Member;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;

public class MemberController {

    private TableView<Member> memberTable;
    private TableColumn<Member, Integer> colID;
    private TableColumn<Member, String> colName;
    private TableColumn<Member, String> colPhone;
    private TableColumn<Member, String> colPlan;
    private TableColumn<Member, LocalDate> colStartDate;
    private TableColumn<Member, LocalDate> colEndDate;
    private TableColumn<Member, String> colStatus;
    private TableColumn<Member, Void> colAction;

    private TextField searchField;
    private Label totalMembersLabel;
    private Label activeMembersLabel;
    private Label expiredMembersLabel;

    private Button btnSearch;
    private Button btnAddMember;
    private Button btnRefresh;

    private MemberDoA memberDAO;
    private ObservableList<Member> memberList;
    // Default constructor (creates its own DAO) kept for backward compatibility
    public MemberController() {
        this(new MemberDoA());
    }

    // Constructor injection to allow sharing DAO instances across controllers
    public MemberController(MemberDoA memberDAO) {
        this.memberDAO = memberDAO;
    }

    // Setup table dari node yang di-lookup
    @SuppressWarnings("unchecked")
    public void setupMemberTable(Node memberNode) {
        memberTable = (TableView<Member>) memberNode.lookup("#memberTable");

        if (memberTable == null) return;

        // Get columns from table
        colID = (TableColumn<Member, Integer>) memberTable.getColumns().get(0);
        colName = (TableColumn<Member, String>) memberTable.getColumns().get(1);
        colPhone = (TableColumn<Member, String>) memberTable.getColumns().get(2);
        colPlan = (TableColumn<Member, String>) memberTable.getColumns().get(3);
        colStartDate = (TableColumn<Member, LocalDate>) memberTable.getColumns().get(4);
        colEndDate = (TableColumn<Member, LocalDate>) memberTable.getColumns().get(5);
        colStatus = (TableColumn<Member, String>) memberTable.getColumns().get(6);
        colAction = (TableColumn<Member, Void>) memberTable.getColumns().get(7);

        // Set member ID for ID column
        colID.setCellFactory(col -> new TableCell<Member, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    Member member = getTableView().getItems().get(getIndex());
                    setText(member.getMemberIdString());
                }
            }
        });

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colPlan.setCellValueFactory(new PropertyValueFactory<>("planType"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Custom cell for Status column (color coding)
        colStatus.setCellFactory(column -> new TableCell<Member, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if (status.equals("Active")) {
                        setStyle("-fx-text-fill: #2f7d32; -fx-font-weight: bold;");
                    } else if (status.equals("Expired")) {
                        setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                    }
                }
            }
        });

        addActionButtons();
    }

    // Add Edit & Delete buttons
    private void addActionButtons() {
        Callback<TableColumn<Member, Void>, TableCell<Member, Void>> cellFactory =
                new Callback<TableColumn<Member, Void>, TableCell<Member, Void>>() {
                    @Override
                    public TableCell<Member, Void> call(final TableColumn<Member, Void> param) {
                        final TableCell<Member, Void> cell = new TableCell<Member, Void>() {

                            private final Button btnEdit = new Button("📝");
                            private final Button btnDelete = new Button("🗑️");
                            private final Button btnCard = new Button("💳");
                            private final Button btnDetails = new Button("ℹ📓");

                            {
                                btnEdit.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                                        "-fx-background-radius: 5; -fx-padding: 5 10 5 10; -fx-cursor: hand;");
                                btnDelete.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; " +
                                        "-fx-background-radius: 5; -fx-padding: 5 10 5 10; -fx-cursor: hand;");
                                btnCard.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                                        "-fx-background-radius: 5; -fx-padding: 5 10 5 10; -fx-cursor: hand;");
                                btnDetails.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; " +
                                        "-fx-background-radius: 5; -fx-padding: 5 10 5 10; -fx-cursor: hand;");

                                btnEdit.setOnAction(event -> {
                                    Member member = getTableView().getItems().get(getIndex());
                                    editMember(member);
                                });

                                btnDelete.setOnAction(event -> {
                                    Member member = getTableView().getItems().get(getIndex());
                                    deleteMember(member);
                                });
                                
                                btnCard.setOnAction(event -> {
                                    Member member = getTableView().getItems().get(getIndex());
                                    generateMemberCard(member);
                                });
                                
                                btnDetails.setOnAction(event -> {
                                    Member member = getTableView().getItems().get(getIndex());
                                    showMemberDetails(member);
                                });
                            }

                            @Override
                            public void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    HBox buttons = new HBox(5, btnEdit, btnDetails, btnCard, btnDelete);
                                    setGraphic(buttons);
                                }
                            }
                        };
                        return cell;
                    }
                };

        colAction.setCellFactory(cellFactory);
    }

    // Setup controls (buttons & labels)
    public void setupMemberControls(Node memberNode) {
        searchField = (TextField) memberNode.lookup("#searchField");
        btnSearch = (Button) memberNode.lookup("#btnSearch");
        btnAddMember = (Button) memberNode.lookup("#btnAddMember");
        btnRefresh = (Button) memberNode.lookup("#btnRefresh");
        totalMembersLabel = (Label) memberNode.lookup("#totalMembersLabel");
        activeMembersLabel = (Label) memberNode.lookup("#activeMembersLabel");
        expiredMembersLabel = (Label) memberNode.lookup("#expiredMembersLabel");

        if (btnSearch != null) {
            btnSearch.setOnAction(e -> searchMembers());
        }
        if (btnAddMember != null) {
            btnAddMember.setOnAction(e -> showAddMemberDialog());
        }
        if (btnRefresh != null) {
            btnRefresh.setOnAction(e -> loadMemberData());
        }
    }

    // Load all member data
    public void loadMemberData() {
        if (memberTable == null) return;

        memberList = memberDAO.getAllMembers();
        memberTable.setItems(memberList);
        updateStatistics();
    }

    // Search members
    private void searchMembers() {
        if (searchField == null || memberTable == null) return;

        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadMemberData();
        } else {
            ObservableList<Member> searchResults = memberDAO.searchMembers(keyword);
            memberTable.setItems(searchResults);
            updateStatistics();
        }
    }

    // Show Add Member Dialog
    private void showAddMemberDialog() {
        Dialog<Member> dialog = new Dialog<>();
        dialog.setTitle("Add New Member");
        dialog.setHeaderText("Enter member information");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");

        ComboBox<String> planCombo = new ComboBox<>();
        planCombo.getItems().clear();
        planCombo.getItems().addAll("Monthly", "Special");
        planCombo.setValue("Monthly");

        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        DatePicker endDatePicker = new DatePicker(LocalDate.now().plusMonths(1));

        startDatePicker.setOnAction(event -> {
            LocalDate startDate = startDatePicker.getValue();
            if (startDate != null) {
                endDatePicker.setValue(startDate.plusMonths(1));
            }
        });

        grid.add(new Label("Name: *"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Phone: *"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Plan Type:"), 0, 2);
        grid.add(planCombo, 1, 2);
        grid.add(new Label("Start Date:"), 0, 3);
        grid.add(startDatePicker, 1, 3);
        grid.add(new Label("End Date:"), 0, 4);
        grid.add(endDatePicker, 1, 4);

        dialog.getDialogPane().setContent(grid);

        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            saveButton.setDisable(nameField.getText().trim().isEmpty() || phoneField.getText().trim().isEmpty());
        });

        phoneField.textProperty().addListener((obs, oldVal, newVal) -> {
            saveButton.setDisable(nameField.getText().trim().isEmpty() || phoneField.getText().trim().isEmpty());
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();

                if (name.isEmpty() || phone.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Name and Phone are required!");
                    return null;
                }

                return new Member(
                        0,
                        name,
                        phone,
                        planCombo.getValue(),
                        startDatePicker.getValue(),
                        endDatePicker.getValue(),
                        1 // Initial membership count
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(member -> {
            if (member != null) {
                if (memberDAO.addMember(member)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Member added successfully!");
                    loadMemberData();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to add member!");
                }
            }
        });
    }

    // Edit member
    private void editMember(Member member) {
        Dialog<Member> dialog = new Dialog<>();
        dialog.setTitle("Edit Member");
        dialog.setHeaderText("Edit member information or extend membership");

        ButtonType saveButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(member.getName());
        TextField phoneField = new TextField(member.getPhone());

        ComboBox<String> planCombo = new ComboBox<>();
        planCombo.getItems().clear();
        planCombo.getItems().addAll("Monthly", "Special");
        planCombo.setValue(member.getPlanType());

        DatePicker startDatePicker = new DatePicker(member.getStartDate());
        DatePicker endDatePicker = new DatePicker(member.getEndDate());

        // Simpan original end date untuk reference
        final LocalDate originalEndDate = member.getEndDate();

        ComboBox<String> extendCombo = new ComboBox<>();
        extendCombo.getItems().addAll(
                "No Extension",
                "Extend +1 Month",
                "Extend +2 Months",
                "Extend +3 Months",
                "Extend +6 Months",
                "Extend +1 Year"
        );
        extendCombo.setValue("No Extension");

        extendCombo.setOnAction(event -> {
            String selected = extendCombo.getValue();

            // Selalu calculate dari ORIGINAL end date, bukan dari end date yang sudah diubah
            switch (selected) {
                case "Extend +1 Month":
                    endDatePicker.setValue(originalEndDate.plusMonths(1));
                    break;
                case "Extend +2 Months":
                    endDatePicker.setValue(originalEndDate.plusMonths(2));
                    break;
                case "Extend +3 Months":
                    endDatePicker.setValue(originalEndDate.plusMonths(3));
                    break;
                case "Extend +6 Months":
                    endDatePicker.setValue(originalEndDate.plusMonths(6));
                    break;
                case "Extend +1 Year":
                    endDatePicker.setValue(originalEndDate.plusYears(1));
                    break;
                default:
                    // No Extension - kembalikan ke original
                    endDatePicker.setValue(originalEndDate);
                    break;
            }
        });

        startDatePicker.setOnAction(event -> {
            LocalDate startDate = startDatePicker.getValue();
            if (startDate != null) {
                endDatePicker.setValue(startDate.plusMonths(1));
                extendCombo.setValue("No Extension");
            }
        });

        grid.add(new Label("Name: *"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Phone: *"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Plan Type:"), 0, 2);
        grid.add(planCombo, 1, 2);
        grid.add(new Label("Start Date:"), 0, 3);
        grid.add(startDatePicker, 1, 3);
        grid.add(new Label("End Date:"), 0, 4);
        grid.add(endDatePicker, 1, 4);
        grid.add(new Label("Extend Membership:"), 0, 5);
        grid.add(extendCombo, 1, 5);

        Label infoLabel = new Label("💡 Tip: Use 'Extend Membership' to add months to current End Date");
        infoLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666;");
        grid.add(infoLabel, 0, 6, 2, 1);

        dialog.getDialogPane().setContent(grid);

        javafx.scene.Node updateButton = dialog.getDialogPane().lookupButton(saveButtonType);

        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateButton.setDisable(nameField.getText().trim().isEmpty() || phoneField.getText().trim().isEmpty());
        });

        phoneField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateButton.setDisable(nameField.getText().trim().isEmpty() || phoneField.getText().trim().isEmpty());
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();

                if (name.isEmpty() || phone.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Name and Phone are required!");
                    return null;
                }

                if (endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "End Date must be after Start Date!");
                    return null;
                }

                member.setName(name);
                member.setPhone(phone);
                member.setPlanType(planCombo.getValue());
                
                // If extending membership, increment count
                if (endDatePicker.getValue().isAfter(member.getEndDate())) {
                    member.incrementMembershipCount();
                }
                
                member.setStartDate(startDatePicker.getValue());
                member.setEndDate(endDatePicker.getValue());
                return member;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedMember -> {
            if (updatedMember != null) {
                if (memberDAO.updateMember(updatedMember)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Member updated successfully!");
                    loadMemberData();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update member!");
                }
            }
        });
    }

    // Delete member
    private void deleteMember(Member member) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Member");
        alert.setHeaderText("Are you sure you want to delete this member?");
        alert.setContentText(member.getName() + " (" + member.getPhone() + ")");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (memberDAO.deleteMember(member.getId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Member deleted successfully!");
                    loadMemberData();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete member!");
                }
            }
        });
    }

    // Update statistics
    private void updateStatistics() {
        if (memberList == null) return;

        int total = memberList.size();
        int active = (int) memberList.stream()
                .filter(m -> m.getStatus().equals("Active"))
                .count();
        int expired = total - active;

        if (totalMembersLabel != null) totalMembersLabel.setText(String.valueOf(total));
        if (activeMembersLabel != null) activeMembersLabel.setText(String.valueOf(active));
        if (expiredMembersLabel != null) expiredMembersLabel.setText(String.valueOf(expired));
    }

    // Refresh table (untuk auto-update status)
    public void refreshTable() {
        if (memberTable != null && memberTable.getItems() != null) {
            for (Member member : memberTable.getItems()) {
                member.updateStatus();
            }
            memberTable.refresh();
            updateStatistics();
        }
    }

    // Generate Member Card
    private void generateMemberCard(Member member) {
        // Create a dialog to display and save the member card
        Dialog<Void> cardDialog = new Dialog<>();
        cardDialog.setTitle("Member Card - " + member.getName());
        cardDialog.setHeaderText("Member Card");
        
        ButtonType saveButtonType = new ButtonType("Save as Image", ButtonBar.ButtonData.OK_DONE);
        cardDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CLOSE);
        
        // Create card layout
        VBox cardBox = new VBox(20);
        cardBox.setAlignment(javafx.geometry.Pos.CENTER);
        cardBox.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e); " +
            "-fx-background-radius: 20; " +
            "-fx-padding: 40; " +
            "-fx-border-color: #4CAF50; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 20; " +
            "-fx-min-width: 400; " +
            "-fx-min-height: 500;"
        );
        
        // Gym Logo/Title
        Label gymTitle = new Label("PITBULL'Z GYM");
        gymTitle.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        gymTitle.setFont(new Font(28));
        gymTitle.setTextAlignment(TextAlignment.CENTER);
        
        Label memberCardLabel = new Label("MEMBER CARD");
        memberCardLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.8); -fx-font-weight: bold;");
        memberCardLabel.setFont(new Font(16));
        memberCardLabel.setTextAlignment(TextAlignment.CENTER);
        
        // Member ID
        Label memberIdLabel = new Label("ID: " + member.getMemberIdString());
        memberIdLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        memberIdLabel.setFont(new Font(18));
        memberIdLabel.setTextAlignment(TextAlignment.CENTER);
        
        // Member Name
        Label nameLabel = new Label(member.getName());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        nameLabel.setFont(new Font(24));
        nameLabel.setTextAlignment(TextAlignment.CENTER);
        
        // Membership Details
        VBox detailsBox = new VBox(10);
        detailsBox.setAlignment(javafx.geometry.Pos.CENTER);
        
        Label planLabel = new Label("Plan: " + member.getPlanType());
        planLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.9);");
        planLabel.setFont(new Font(14));
        
        Label startLabel = new Label("Start: " + member.getStartDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        startLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.9);");
        startLabel.setFont(new Font(14));
        
        Label endLabel = new Label("Valid Until: " + member.getEndDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        endLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.9);");
        endLabel.setFont(new Font(14));
        
        Label statusLabel = new Label("Status: " + member.getStatus());
        statusLabel.setStyle(member.getStatus().equals("Active") ? 
            "-fx-text-fill: #4CAF50; -fx-font-weight: bold;" : 
            "-fx-text-fill: #f44336; -fx-font-weight: bold;");
        statusLabel.setFont(new Font(14));
        
        Label countLabel = new Label("Membership Count: " + member.getMembershipCount());
        countLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.8);");
        countLabel.setFont(new Font(12));
        
        detailsBox.getChildren().addAll(planLabel, startLabel, endLabel, statusLabel, countLabel);
        
        cardBox.getChildren().addAll(gymTitle, memberCardLabel, memberIdLabel, nameLabel, detailsBox);
        
        cardDialog.getDialogPane().setContent(cardBox);
        cardDialog.getDialogPane().setPrefSize(450, 550);
        
        // Handle save button
        Node saveButton = cardDialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setOnMouseClicked(event -> {
            try {
                // Create a scene with the card
                Scene scene = new Scene(cardBox);
                WritableImage image = cardBox.snapshot(null, null);
                
                // Save dialog
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Member Card");
                fileChooser.setInitialFileName("MemberCard_" + member.getMemberIdString() + ".png");
                fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PNG files", "*.png")
                );
                
                File file = fileChooser.showSaveDialog(cardDialog.getDialogPane().getScene().getWindow());
                if (file != null) {
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                    showAlert(Alert.AlertType.INFORMATION, "Success", 
                        "Member card saved successfully!\n" + file.getAbsolutePath());
                }
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save member card: " + e.getMessage());
            }
        });
        
        cardDialog.showAndWait();
    }
    
    // Show detailed member information dialog
    public void showMemberDetails(Member member) {
        Dialog<Void> detailsDialog = new Dialog<>();
        detailsDialog.setTitle("Member Details - " + member.getName());
        detailsDialog.setHeaderText("Detailed Member Information");
        
        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        detailsDialog.getDialogPane().getButtonTypes().addAll(closeButtonType);
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 40, 20, 40));
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(new Label(member.getName()), 1, 0);
        
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(new Label(member.getPhone()), 1, 1);
        
        grid.add(new Label("Member ID:"), 0, 2);
        grid.add(new Label(member.getMemberIdString()), 1, 2);
        
        grid.add(new Label("Plan Type:"), 0, 3);
        grid.add(new Label(member.getPlanType()), 1, 3);
        
        grid.add(new Label("Start Date:"), 0, 4);
        grid.add(new Label(member.getStartDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))), 1, 4);
        
        grid.add(new Label("End Date:"), 0, 5);
        grid.add(new Label(member.getEndDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))), 1, 5);
        
        grid.add(new Label("Status:"), 0, 6);
        Label statusLabel = new Label(member.getStatus());
        statusLabel.setStyle(member.getStatus().equals("Active") ? 
            "-fx-text-fill: #4CAF50; -fx-font-weight: bold;" : 
            "-fx-text-fill: #f44336; -fx-font-weight: bold;");
        grid.add(statusLabel, 1, 6);
        
        grid.add(new Label("Membership Count:"), 0, 7);
        grid.add(new Label(String.valueOf(member.getMembershipCount())), 1, 7);
        
        detailsDialog.getDialogPane().setContent(grid);
        detailsDialog.showAndWait();
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