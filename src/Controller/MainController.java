package Controller;

import Model.MemberData;
import Model.Member;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;

import java.time.LocalDate;

public class MainController {
    // ============================================
    // FXML Fields - Main Menu
    // ============================================
    @FXML
    private StackPane mainPane;

    // ============================================
    // FXML Fields - Price Cards
    // ============================================
    @FXML
    private StackPane specialCard;
    @FXML
    private StackPane monthlyCard;
    @FXML
    private StackPane dailyCard;

    private StackPane selectedCard = null;
    private Timeline pulseAnimation;

    // ============================================
    // FXML Fields - Membership
    // ============================================
    @FXML
    private TableView<Member> memberTable;
    @FXML
    private TableColumn<Member, Integer> colID;
    @FXML
    private TableColumn<Member, String> colName;
    @FXML
    private TableColumn<Member, String> colPhone;
    @FXML
    private TableColumn<Member, String> colPlan;
    @FXML
    private TableColumn<Member, LocalDate> colStartDate;
    @FXML
    private TableColumn<Member, LocalDate> colEndDate;
    @FXML
    private TableColumn<Member, String> colStatus;
    @FXML
    private TableColumn<Member, Void> colAction;

    @FXML
    private TextField searchField;
    @FXML
    private Label totalMembersLabel;
    @FXML
    private Label activeMembersLabel;
    @FXML
    private Label expiredMembersLabel;

    @FXML
    private Button btnSearch;
    @FXML
    private Button btnAddMember;
    @FXML
    private Button btnRefresh;

    private MemberData memberDAO;
    private ObservableList<Member> memberList;

    // ============================================
    // Initialize Method
    // ============================================
    @FXML
    public void initialize() {
        // Initialize DAO
        memberDAO = new MemberData();

        // Setup price cards hover effects
        if (specialCard != null) setupHoverEffect(specialCard);
        if (monthlyCard != null) {
            setupHoverEffect(monthlyCard);
            setupPulseAnimation();
        }
        if (dailyCard != null) setupHoverEffect(dailyCard);
    }

    // ============================================
    // Price Card Methods
    // ============================================
    private void setupPulseAnimation() {
        if (monthlyCard == null) return;

        ScaleTransition pulse = new ScaleTransition(Duration.millis(1500), monthlyCard);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.03);
        pulse.setToY(1.03);
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
    }

    private void setupHoverEffect(StackPane card) {
        if (card == null) return;

        card.setOnMouseEntered(event -> {
            if (selectedCard != card) {
                ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), card);
                scaleUp.setToX(1.08);
                scaleUp.setToY(1.08);
                scaleUp.play();
            }
        });

        card.setOnMouseExited(event -> {
            if (selectedCard != card) {
                ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), card);
                scaleDown.setToX(1.0);
                scaleDown.setToY(1.0);
                scaleDown.play();
            }
        });
    }

    @FXML
    private void selectSpecial(MouseEvent event) {
        if (specialCard != null) {
            System.out.println("Special card clicked!");
            selectCard(specialCard, "Special", "650k/month");
        }
    }

    @FXML
    private void selectMonthly(MouseEvent event) {
        if (monthlyCard != null) {
            System.out.println("Monthly card clicked!");
            selectCard(monthlyCard, "Monthly", "150k/month");
        }
    }

    @FXML
    private void selectDaily(MouseEvent event) {
        if (dailyCard != null) {
            System.out.println("Daily card clicked!");
            selectCard(dailyCard, "Daily", "10k/day");
        }
    }

    private void selectCard(StackPane card, String planName, String price) {
        if (card == null) {
            System.out.println("ERROR: Card is null!");
            return;
        }

        if (selectedCard != null && selectedCard != card) {
            resetCardStyle(selectedCard);
        }

        selectedCard = card;
        highlightSelectedCard(card);

        System.out.println("=================================");
        System.out.println("Selected Plan: " + planName);
        System.out.println("Price: " + price);
        System.out.println("=================================");
    }

    private void highlightSelectedCard(StackPane card) {
        if (card == null || card.getChildren().isEmpty()) return;

        card.getStyleClass().add("selected");

        VBox vbox = (VBox) card.getChildren().get(0);
        String currentStyle = vbox.getStyle();

        if (currentStyle.contains("-fx-border-color")) {
            currentStyle = currentStyle.replaceAll("-fx-border-color:[^;]+;", "-fx-border-color: #2f7d32;");
            currentStyle = currentStyle.replaceAll("-fx-border-width:[^;]+;", "-fx-border-width: 4;");
        } else {
            currentStyle += "; -fx-border-color: #2f7d32; -fx-border-width: 4;";
        }

        vbox.setStyle(currentStyle);
    }

    private void resetCardStyle(StackPane card) {
        if (card == null || card.getChildren().isEmpty()) return;

        card.getStyleClass().remove("selected");

        VBox vbox = (VBox) card.getChildren().get(0);
        String currentStyle = vbox.getStyle();

        if (card == specialCard || card == dailyCard) {
            currentStyle = currentStyle.replaceAll("-fx-border-color:[^;]+;", "-fx-border-color: rgba(0,0,0,0.06);");
            currentStyle = currentStyle.replaceAll("-fx-border-width:[^;]+;", "-fx-border-width: 2;");
        } else if (card == monthlyCard) {
            currentStyle = currentStyle.replaceAll("-fx-border-color:[^;]+;", "-fx-border-color: rgba(247,181,0,0.35);");
            currentStyle = currentStyle.replaceAll("-fx-border-width:[^;]+;", "-fx-border-width: 2;");
        }

        vbox.setStyle(currentStyle);
    }

    // ============================================
    // Membership Methods
    // ============================================
    @SuppressWarnings("unchecked")
    private void setupMemberTable(Node memberNode) {
        memberTable = (TableView<Member>) memberNode.lookup("#memberTable");

        if (memberTable == null) return;

        // Get columns from table instead of lookup
        colID = (TableColumn<Member, Integer>) memberTable.getColumns().get(0);
        colName = (TableColumn<Member, String>) memberTable.getColumns().get(1);
        colPhone = (TableColumn<Member, String>) memberTable.getColumns().get(2);
        colPlan = (TableColumn<Member, String>) memberTable.getColumns().get(3);
        colStartDate = (TableColumn<Member, LocalDate>) memberTable.getColumns().get(4);
        colEndDate = (TableColumn<Member, LocalDate>) memberTable.getColumns().get(5);
        colStatus = (TableColumn<Member, String>) memberTable.getColumns().get(6);
        colAction = (TableColumn<Member, Void>) memberTable.getColumns().get(7);

        // Set row number for ID column (1, 2, 3, ...)
        colID.setCellFactory(col -> new TableCell<Member, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colPlan.setCellValueFactory(new PropertyValueFactory<>("planType"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Custom cell for Status column
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

    private void addActionButtons() {
        Callback<TableColumn<Member, Void>, TableCell<Member, Void>> cellFactory =
                new Callback<TableColumn<Member, Void>, TableCell<Member, Void>>() {
                    @Override
                    public TableCell<Member, Void> call(final TableColumn<Member, Void> param) {
                        final TableCell<Member, Void> cell = new TableCell<Member, Void>() {

                            private final Button btnEdit = new Button("Edit");
                            private final Button btnDelete = new Button("Delete");

                            {
                                btnEdit.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                                        "-fx-background-radius: 5; -fx-padding: 5 10 5 10; -fx-cursor: hand;");
                                btnDelete.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; " +
                                        "-fx-background-radius: 5; -fx-padding: 5 10 5 10; -fx-cursor: hand;");

                                btnEdit.setOnAction(event -> {
                                    Member member = getTableView().getItems().get(getIndex());
                                    editMember(member);
                                });

                                btnDelete.setOnAction(event -> {
                                    Member member = getTableView().getItems().get(getIndex());
                                    deleteMember(member);
                                });
                            }

                            @Override
                            public void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    HBox buttons = new HBox(5, btnEdit, btnDelete);
                                    setGraphic(buttons);
                                }
                            }
                        };
                        return cell;
                    }
                };

        colAction.setCellFactory(cellFactory);
    }

    private void setupMemberControls(Node memberNode) {
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

    private void loadMemberData() {
        if (memberTable == null) return;

        memberList = memberDAO.getAllMembers();
        memberTable.setItems(memberList);
        updateStatistics();
    }

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
        planCombo.getItems().addAll("Daily", "Monthly", "Special");
        planCombo.setValue("Monthly");
        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        DatePicker endDatePicker = new DatePicker(LocalDate.now().plusMonths(1));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Plan Type:"), 0, 2);
        grid.add(planCombo, 1, 2);
        grid.add(new Label("Start Date:"), 0, 3);
        grid.add(startDatePicker, 1, 3);
        grid.add(new Label("End Date:"), 0, 4);
        grid.add(endDatePicker, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Member(
                        0,
                        nameField.getText(),
                        phoneField.getText(),
                        planCombo.getValue(),
                        startDatePicker.getValue(),
                        endDatePicker.getValue()
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(member -> {
            if (memberDAO.addMember(member)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Member added successfully!");
                loadMemberData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add member!");
            }
        });
    }

    private void editMember(Member member) {
        Dialog<Member> dialog = new Dialog<>();
        dialog.setTitle("Edit Member");
        dialog.setHeaderText("Edit member information");

        ButtonType saveButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(member.getName());
        TextField phoneField = new TextField(member.getPhone());
        ComboBox<String> planCombo = new ComboBox<>();
        planCombo.getItems().addAll("Daily", "Monthly", "Special");
        planCombo.setValue(member.getPlanType());
        DatePicker startDatePicker = new DatePicker(member.getStartDate());
        DatePicker endDatePicker = new DatePicker(member.getEndDate());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Plan Type:"), 0, 2);
        grid.add(planCombo, 1, 2);
        grid.add(new Label("Start Date:"), 0, 3);
        grid.add(startDatePicker, 1, 3);
        grid.add(new Label("End Date:"), 0, 4);
        grid.add(endDatePicker, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                member.setName(nameField.getText());
                member.setPhone(phoneField.getText());
                member.setPlanType(planCombo.getValue());
                member.setStartDate(startDatePicker.getValue());
                member.setEndDate(endDatePicker.getValue());
                return member;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedMember -> {
            if (memberDAO.updateMember(updatedMember)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Member updated successfully!");
                loadMemberData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update member!");
            }
        });
    }

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

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // ============================================
    // Navigation Methods
    // ============================================
    private void setMainPane(String fxml) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxml));

            if (node instanceof Region) {
                Region region = (Region) node;
                region.prefWidthProperty().bind(mainPane.widthProperty());
                region.prefHeightProperty().bind(mainPane.heightProperty());
                region.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
                region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            }

            mainPane.getChildren().setAll(node);

        } catch (Exception e) {
            System.err.println("Error loading FXML: " + fxml);
            e.printStackTrace();
        }
    }

    public void memberAct(ActionEvent actionEvent) {
        setMainPane("/resources/fxml/Membership.fxml");

        Platform.runLater(() -> {
            Node memberNode = mainPane.getChildren().isEmpty() ? null : mainPane.getChildren().get(0);
            if (memberNode != null) {
                // Setup background image
                ImageView backgroundImage = (ImageView) memberNode.lookup("#memberBackgroundImage");
                if (backgroundImage != null) {
                    backgroundImage.fitWidthProperty().bind(mainPane.widthProperty());
                    backgroundImage.fitHeightProperty().bind(mainPane.heightProperty());
                    backgroundImage.setPreserveRatio(false);
                }

                // Setup table and controls
                setupMemberTable(memberNode);
                setupMemberControls(memberNode);
                loadMemberData();
            }
        });
    }

    public void aboutAct(ActionEvent actionEvent) {
        setMainPane("/resources/fxml/About.fxml");
    }

    public void priceAct(ActionEvent actionEvent) {
        setMainPane("/resources/fxml/Price.fxml");
    }

    public void contactAct(ActionEvent actionEvent) {
        // TODO: Implement contact page
    }

    public void mainAct(ActionEvent actionEvent) {
        setMainPane("/resources/fxml/Main.fxml");
    }
}