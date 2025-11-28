package Controller;

import DataAccess.MemberDoA;
import Model.Member;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.util.Callback;
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
    private MemberDoA memberDAO = new MemberDoA();
    private ObservableList<Member> memberList;

    public void setupMemberTable(Node memberNode) {
        this.memberTable = (TableView)memberNode.lookup("#memberTable");
        if (this.memberTable != null) {
            this.colID = (TableColumn)this.memberTable.getColumns().get(0);
            this.colName = (TableColumn)this.memberTable.getColumns().get(1);
            this.colPhone = (TableColumn)this.memberTable.getColumns().get(2);
            this.colPlan = (TableColumn)this.memberTable.getColumns().get(3);
            this.colStartDate = (TableColumn)this.memberTable.getColumns().get(4);
            this.colEndDate = (TableColumn)this.memberTable.getColumns().get(5);
            this.colStatus = (TableColumn)this.memberTable.getColumns().get(6);
            this.colAction = (TableColumn)this.memberTable.getColumns().get(7);

            // PERUBAHAN 1: Atur lebar kolom Action agar semua tombol terlihat
            this.colAction.setPrefWidth(260);
            this.colAction.setMinWidth(260);

            // Display formatted UI row number (e.g., "PBG-0001") in the ID column
            // while keeping the DB id property unchanged.
            this.colID.setCellFactory((col) -> new TableCell<Member, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        int displayIndex = getIndex() + 1; // 1-based row number in current view
                        setText(String.format("PBG-%04d", displayIndex));
                    }
                }
            });
            this.colName.setCellValueFactory(new PropertyValueFactory("name"));
            this.colPhone.setCellValueFactory(new PropertyValueFactory("phone"));
            this.colPlan.setCellValueFactory(new PropertyValueFactory("planType"));
            this.colStartDate.setCellValueFactory(new PropertyValueFactory("startDate"));
            this.colEndDate.setCellValueFactory(new PropertyValueFactory("endDate"));
            this.colStatus.setCellValueFactory(new PropertyValueFactory("status"));
            this.colStatus.setCellFactory((column) -> new TableCell<Member, String>() {
                protected void updateItem(String status, boolean empty) {
                    super.updateItem(status, empty);
                    if (!empty && status != null) {
                        this.setText(status);
                        if (status.equals("Active")) {
                            this.setStyle("-fx-text-fill: #2f7d32; -fx-font-weight: bold;");
                        } else if (status.equals("Expired")) {
                            this.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                        }
                    } else {
                        this.setText((String)null);
                        this.setStyle("");
                    }
                }
            });
            this.addActionButtons();
        }
    }

    private void addActionButtons() {
        Callback<TableColumn<Member, Void>, TableCell<Member, Void>> cellFactory = new Callback<TableColumn<Member, Void>, TableCell<Member, Void>>() {
            public TableCell<Member, Void> call(TableColumn<Member, Void> param) {
                TableCell<Member, Void> cell = new TableCell<Member, Void>() {
                    private final Button btnEdit = new Button("Edit");
                    private final Button btnDelete = new Button("Delete");
                    private final Button btnCard = new Button("Card");
                    private final Button btnDetails = new Button("Details");

                    {
                        this.btnEdit.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10 5 10; -fx-cursor: hand;");
                        this.btnDelete.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10 5 10; -fx-cursor: hand;");
                        this.btnCard.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10 5 10; -fx-cursor: hand;");
                        this.btnDetails.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10 5 10; -fx-cursor: hand;");
                        this.btnEdit.setOnAction((event) -> {
                            Member member = (Member)this.getTableView().getItems().get(this.getIndex());
                            MemberController.this.editMember(member);
                        });
                        this.btnDelete.setOnAction((event) -> {
                            Member member = (Member)this.getTableView().getItems().get(this.getIndex());
                            MemberController.this.deleteMember(member);
                        });
                        this.btnCard.setOnAction((event) -> {
                            Member member = (Member)this.getTableView().getItems().get(this.getIndex());
                            MemberController.this.generateMemberCard(member);
                        });
                        this.btnDetails.setOnAction((event) -> {
                            Member member = (Member)this.getTableView().getItems().get(this.getIndex());
                            MemberController.this.showMemberDetails(member);
                        });
                    }

                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            this.setGraphic((Node)null);
                        } else {
                            HBox buttons = new HBox((double)5.0F, new Node[]{this.btnEdit, this.btnDetails, this.btnCard, this.btnDelete});
                            this.setGraphic(buttons);
                        }
                    }
                };
                return cell;
            }
        };
        this.colAction.setCellFactory(cellFactory);
    }

    public void setupMemberControls(Node memberNode) {
        this.searchField = (TextField)memberNode.lookup("#searchField");
        this.btnSearch = (Button)memberNode.lookup("#btnSearch");
        this.btnAddMember = (Button)memberNode.lookup("#btnAddMember");
        this.btnRefresh = (Button)memberNode.lookup("#btnRefresh");
        this.totalMembersLabel = (Label)memberNode.lookup("#totalMembersLabel");
        this.activeMembersLabel = (Label)memberNode.lookup("#activeMembersLabel");
        this.expiredMembersLabel = (Label)memberNode.lookup("#expiredMembersLabel");
        if (this.btnSearch != null) {
            this.btnSearch.setOnAction((e) -> this.searchMembers());
        }

        if (this.btnAddMember != null) {
            this.btnAddMember.setOnAction((e) -> this.showAddMemberDialog());
        }

        if (this.btnRefresh != null) {
            this.btnRefresh.setOnAction((e) -> this.loadMemberData());
        }

        // LIVE SEARCH: Add text property listener untuk real-time search
        if (this.searchField != null) {
            this.searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                // Trigger search saat user mengetik
                if (this.memberTable != null) {
                    if (newVal.trim().isEmpty()) {
                        // Jika search field kosong, tampilkan semua members
                        this.loadMemberData();
                    } else {
                        // Perform live search dengan keyword yang diketik
                        ObservableList<Member> searchResults = this.memberDAO.searchMembers(newVal.trim());
                        this.memberTable.setItems(searchResults);
                        this.updateStatistics();
                    }
                }
            });
        }
    }

    public void loadMemberData() {
        if (this.memberTable != null) {
            this.memberList = this.memberDAO.getAllMembers();
            this.memberTable.setItems(this.memberList);
            this.updateStatistics();
        }
    }

    private void searchMembers() {
        if (this.searchField != null && this.memberTable != null) {
            String keyword = this.searchField.getText().trim();
            if (keyword.isEmpty()) {
                this.loadMemberData();
            } else {
                ObservableList<Member> searchResults = this.memberDAO.searchMembers(keyword);
                this.memberTable.setItems(searchResults);
                this.updateStatistics();
            }
        }
    }

    private void showAddMemberDialog() {
        Dialog<Member> dialog = new Dialog();
        dialog.setTitle("Add New Member");
        dialog.setHeaderText("Enter member information");
        ButtonType saveButtonType = new ButtonType("Save", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(new ButtonType[]{saveButtonType, ButtonType.CANCEL});
        GridPane grid = new GridPane();
        grid.setHgap((double)10.0F);
        grid.setVgap((double)10.0F);
        grid.setPadding(new Insets((double)20.0F, (double)150.0F, (double)10.0F, (double)10.0F));
        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        ComboBox<String> planCombo = new ComboBox();
        planCombo.getItems().clear();
        planCombo.getItems().addAll(new String[]{"Monthly", "Special"});
        planCombo.setValue("Monthly");
        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        DatePicker endDatePicker = new DatePicker(LocalDate.now().plusMonths(1L));
        startDatePicker.setOnAction((event) -> {
            LocalDate startDate = (LocalDate)startDatePicker.getValue();
            if (startDate != null) {
                endDatePicker.setValue(startDate.plusMonths(1L));
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
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);
        nameField.textProperty().addListener((obs, oldVal, newVal) -> saveButton.setDisable(nameField.getText().trim().isEmpty() || phoneField.getText().trim().isEmpty()));
        phoneField.textProperty().addListener((obs, oldVal, newVal) -> saveButton.setDisable(nameField.getText().trim().isEmpty() || phoneField.getText().trim().isEmpty()));
        dialog.setResultConverter((dialogButton) -> {
            if (dialogButton == saveButtonType) {
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();
                if (!name.isEmpty() && !phone.isEmpty()) {
                    return new Member(0, name, phone, (String)planCombo.getValue(), (LocalDate)startDatePicker.getValue(), (LocalDate)endDatePicker.getValue(), 1);
                } else {
                    this.showAlert(AlertType.ERROR, "Validation Error", "Name and Phone are required!");
                    return null;
                }
            } else {
                return null;
            }
        });
        dialog.showAndWait().ifPresent((member) -> {
            if (member != null) {
                if (this.memberDAO.addMember(member)) {
                    this.showAlert(AlertType.INFORMATION, "Success", "Member added successfully!");
                    this.loadMemberData();
                } else {
                    this.showAlert(AlertType.ERROR, "Error", "Failed to add member!");
                }
            }
        });
    }

    private void editMember(Member member) {
        Dialog<Member> dialog = new Dialog();
        dialog.setTitle("Edit Member");
        dialog.setHeaderText("Edit member information or extend membership");
        ButtonType saveButtonType = new ButtonType("Update", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(new ButtonType[]{saveButtonType, ButtonType.CANCEL});
        GridPane grid = new GridPane();
        grid.setHgap((double)10.0F);
        grid.setVgap((double)10.0F);
        grid.setPadding(new Insets((double)20.0F, (double)150.0F, (double)10.0F, (double)10.0F));
        TextField nameField = new TextField(member.getName());
        TextField phoneField = new TextField(member.getPhone());
        ComboBox<String> planCombo = new ComboBox();
        planCombo.getItems().clear();
        planCombo.getItems().addAll(new String[]{"Monthly", "Special"});
        planCombo.setValue(member.getPlanType());
        DatePicker startDatePicker = new DatePicker(member.getStartDate());
        DatePicker endDatePicker = new DatePicker(member.getEndDate());
        LocalDate originalEndDate = member.getEndDate();
        ComboBox<String> extendCombo = new ComboBox();
        extendCombo.getItems().addAll(new String[]{"No Extension", "Extend +1 Month", "Extend +2 Months", "Extend +3 Months", "Extend +6 Months", "Extend +1 Year"});
        extendCombo.setValue("No Extension");
        extendCombo.setOnAction((event) -> {
            switch ((String)extendCombo.getValue()) {
                case "Extend +1 Month" -> endDatePicker.setValue(originalEndDate.plusMonths(1L));
                case "Extend +2 Months" -> endDatePicker.setValue(originalEndDate.plusMonths(2L));
                case "Extend +3 Months" -> endDatePicker.setValue(originalEndDate.plusMonths(3L));
                case "Extend +6 Months" -> endDatePicker.setValue(originalEndDate.plusMonths(6L));
                case "Extend +1 Year" -> endDatePicker.setValue(originalEndDate.plusYears(1L));
                default -> endDatePicker.setValue(originalEndDate);
            }
        });
        startDatePicker.setOnAction((event) -> {
            LocalDate startDate = (LocalDate)startDatePicker.getValue();
            if (startDate != null) {
                endDatePicker.setValue(startDate.plusMonths(1L));
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
        Node updateButton = dialog.getDialogPane().lookupButton(saveButtonType);
        nameField.textProperty().addListener((obs, oldVal, newVal) -> updateButton.setDisable(nameField.getText().trim().isEmpty() || phoneField.getText().trim().isEmpty()));
        phoneField.textProperty().addListener((obs, oldVal, newVal) -> updateButton.setDisable(nameField.getText().trim().isEmpty() || phoneField.getText().trim().isEmpty()));
        dialog.setResultConverter((dialogButton) -> {
            if (dialogButton == saveButtonType) {
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();
                if (!name.isEmpty() && !phone.isEmpty()) {
                    if (((LocalDate)endDatePicker.getValue()).isBefore((ChronoLocalDate)startDatePicker.getValue())) {
                        this.showAlert(AlertType.ERROR, "Validation Error", "End Date must be after Start Date!");
                        return null;
                    } else {
                        member.setName(name);
                        member.setPhone(phone);
                        member.setPlanType((String)planCombo.getValue());
                        if (((LocalDate)endDatePicker.getValue()).isAfter(member.getEndDate())) {
                            member.incrementMembershipCount();
                        }
                        member.setStartDate((LocalDate)startDatePicker.getValue());
                        member.setEndDate((LocalDate)endDatePicker.getValue());
                        return member;
                    }
                } else {
                    this.showAlert(AlertType.ERROR, "Validation Error", "Name and Phone are required!");
                    return null;
                }
            } else {
                return null;
            }
        });
        dialog.showAndWait().ifPresent((updatedMember) -> {
            if (updatedMember != null) {
                if (this.memberDAO.updateMember(updatedMember)) {
                    this.showAlert(AlertType.INFORMATION, "Success", "Member updated successfully!");
                    this.loadMemberData();
                } else {
                    this.showAlert(AlertType.ERROR, "Error", "Failed to update member!");
                }
            }
        });
    }

    private void deleteMember(Member member) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete Member");
        alert.setHeaderText("Are you sure you want to delete this member?");
        String var10001 = member.getName();
        alert.setContentText(var10001 + " (" + member.getPhone() + ")");
        alert.showAndWait().ifPresent((response) -> {
            if (response == ButtonType.OK) {
                if (this.memberDAO.deleteMember(member.getId())) {
                    this.showAlert(AlertType.INFORMATION, "Success", "Member deleted successfully!");
                    this.loadMemberData();
                } else {
                    this.showAlert(AlertType.ERROR, "Error", "Failed to delete member!");
                }
            }
        });
    }

    private void updateStatistics() {
        if (this.memberList != null) {
            int total = this.memberList.size();
            int active = (int)this.memberList.stream().filter((m) -> m.getStatus().equals("Active")).count();
            int expired = total - active;
            if (this.totalMembersLabel != null) {
                this.totalMembersLabel.setText(String.valueOf(total));
            }
            if (this.activeMembersLabel != null) {
                this.activeMembersLabel.setText(String.valueOf(active));
            }
            if (this.expiredMembersLabel != null) {
                this.expiredMembersLabel.setText(String.valueOf(expired));
            }
        }
    }

    public void refreshTable() {
        if (this.memberTable != null && this.memberTable.getItems() != null) {
            for(Member member : this.memberTable.getItems()) {
                member.updateStatus();
            }
            this.memberTable.refresh();
            this.updateStatistics();
        }
    }

    private void generateMemberCard(Member member) {
        Dialog<Void> cardDialog = new Dialog();
        String var10001 = member.getName();
        cardDialog.setTitle("Member Card - " + var10001);
        cardDialog.setHeaderText("Member Card");
        ButtonType saveButtonType = new ButtonType("Save as Image", ButtonData.OK_DONE);
        cardDialog.getDialogPane().getButtonTypes().addAll(new ButtonType[]{saveButtonType, ButtonType.CLOSE});
        VBox cardBox = new VBox((double)20.0F);
        cardBox.setAlignment(Pos.CENTER);
        cardBox.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e); -fx-background-radius: 20; -fx-padding: 40; -fx-border-color: #4CAF50; -fx-border-width: 3; -fx-border-radius: 20; -fx-min-width: 400; -fx-min-height: 500;");
        Label gymTitle = new Label("PITBULL'Z GYM");
        gymTitle.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        gymTitle.setFont(new Font((double)28.0F));
        gymTitle.setTextAlignment(TextAlignment.CENTER);
        Label memberCardLabel = new Label("MEMBER CARD");
        memberCardLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.8); -fx-font-weight: bold;");
        memberCardLabel.setFont(new Font((double)16.0F));
        memberCardLabel.setTextAlignment(TextAlignment.CENTER);
        String var10002 = member.getMemberIdString();
        Label memberIdLabel = new Label("ID: " + var10002);
        memberIdLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        memberIdLabel.setFont(new Font((double)18.0F));
        memberIdLabel.setTextAlignment(TextAlignment.CENTER);
        Label nameLabel = new Label(member.getName());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        nameLabel.setFont(new Font((double)24.0F));
        nameLabel.setTextAlignment(TextAlignment.CENTER);
        VBox detailsBox = new VBox((double)10.0F);
        detailsBox.setAlignment(Pos.CENTER);
        var10002 = member.getPlanType();
        Label planLabel = new Label("Plan: " + var10002);
        planLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.9);");
        planLabel.setFont(new Font((double)14.0F));
        var10002 = member.getStartDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        Label startLabel = new Label("Start: " + var10002);
        startLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.9);");
        startLabel.setFont(new Font((double)14.0F));
        var10002 = member.getEndDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        Label endLabel = new Label("Valid Until: " + var10002);
        endLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.9);");
        endLabel.setFont(new Font((double)14.0F));
        var10002 = member.getStatus();
        Label statusLabel = new Label("Status: " + var10002);
        statusLabel.setStyle(member.getStatus().equals("Active") ? "-fx-text-fill: #4CAF50; -fx-font-weight: bold;" : "-fx-text-fill: #f44336; -fx-font-weight: bold;");
        statusLabel.setFont(new Font((double)14.0F));
        Label countLabel = new Label("Membership Count: " + member.getMembershipCount());
        countLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.8);");
        countLabel.setFont(new Font((double)12.0F));
        detailsBox.getChildren().addAll(new Node[]{planLabel, startLabel, endLabel, statusLabel, countLabel});
        cardBox.getChildren().addAll(new Node[]{gymTitle, memberCardLabel, memberIdLabel, nameLabel, detailsBox});
        cardDialog.getDialogPane().setContent(cardBox);
        cardDialog.getDialogPane().setPrefSize((double)450.0F, (double)550.0F);

        // PERUBAHAN 2: Tambahkan FileChooser untuk memilih lokasi penyimpanan
        // Gunakan setResultConverter untuk handle button action
        cardDialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    // PERBAIKAN: Hapus line new Scene(cardBox) karena cardBox sudah di dalam scene
                    // Langsung snapshot tanpa membuat scene baru
                    WritableImage image = cardBox.snapshot((SnapshotParameters)null, (WritableImage)null);

                    // FileChooser untuk memilih lokasi save
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save Member Card");
                    fileChooser.setInitialFileName("MemberCard_" + member.getMemberIdString() + "_" + member.getName().replace(" ", "_") + ".png");
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files", new String[]{"*.png"}));

                    // Set default directory ke Downloads atau Home
                    String userHome = System.getProperty("user.home");
                    File defaultDirectory = new File(userHome, "Downloads");
                    if (!defaultDirectory.exists()) {
                        defaultDirectory = new File(userHome);
                    }
                    fileChooser.setInitialDirectory(defaultDirectory);

                    // Show save dialog - user bisa pilih lokasi
                    File file = fileChooser.showSaveDialog(cardDialog.getDialogPane().getScene().getWindow());

                    if (file != null) {
                        ImageIO.write(SwingFXUtils.fromFXImage(image, (BufferedImage)null), "png", file);
                        this.showAlert(AlertType.INFORMATION, "Success", "Member card saved successfully!\n\nLocation:\n" + file.getAbsolutePath());
                    }
                } catch (IOException e) {
                    this.showAlert(AlertType.ERROR, "Error", "Failed to save member card: " + e.getMessage());
                }
            }
            return null;
        });

        cardDialog.showAndWait();
    }

    public void showMemberDetails(Member member) {
        Dialog<Void> detailsDialog = new Dialog();
        String var10001 = member.getName();
        detailsDialog.setTitle("Member Details - " + var10001);
        detailsDialog.setHeaderText("Detailed Member Information");
        ButtonType closeButtonType = new ButtonType("Close", ButtonData.CANCEL_CLOSE);
        detailsDialog.getDialogPane().getButtonTypes().addAll(new ButtonType[]{closeButtonType});
        GridPane grid = new GridPane();
        grid.setHgap((double)15.0F);
        grid.setVgap((double)15.0F);
        grid.setPadding(new Insets((double)20.0F, (double)40.0F, (double)20.0F, (double)40.0F));
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
        statusLabel.setStyle(member.getStatus().equals("Active") ? "-fx-text-fill: #4CAF50; -fx-font-weight: bold;" : "-fx-text-fill: #f44336; -fx-font-weight: bold;");
        grid.add(statusLabel, 1, 6);
        grid.add(new Label("Membership Count:"), 0, 7);
        grid.add(new Label(String.valueOf(member.getMembershipCount())), 1, 7);
        detailsDialog.getDialogPane().setContent(grid);
        detailsDialog.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText((String)null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}