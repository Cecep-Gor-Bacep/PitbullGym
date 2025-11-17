package Controller;

import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

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

    // ============================================
    // Controllers
    // ============================================
    private MemberController membershipController;

    // Timeline untuk auto-refresh status membership
    private Timeline statusRefreshTimeline;

    // ============================================
    // Initialize Method
    // ============================================
    @FXML
    public void initialize() {
        // Initialize membership controller
        membershipController = new MemberController();

        // Setup price cards hover effects
        if (specialCard != null) setupHoverEffect(specialCard);
        if (monthlyCard != null) {
            setupHoverEffect(monthlyCard);
            setupPulseAnimation();
        }
        if (dailyCard != null) setupHoverEffect(dailyCard);

        // Setup auto-refresh status setiap menit
        startStatusRefreshTimer();
    }

    // Auto-refresh membership status setiap menit
    private void startStatusRefreshTimer() {
        statusRefreshTimeline = new Timeline(
                new javafx.animation.KeyFrame(Duration.minutes(1), event -> {
                    // Refresh membership status jika membership page sedang aktif
                    if (membershipController != null) {
                        membershipController.refreshTable();
                    }
                })
        );
        statusRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        statusRefreshTimeline.play();
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

                // Setup membership table and controls using MembershipController
                membershipController.setupMemberTable(memberNode);
                membershipController.setupMemberControls(memberNode);
                membershipController.loadMemberData();
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

