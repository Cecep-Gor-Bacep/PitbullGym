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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MainController {
    @FXML
    private StackPane mainPane;

    @FXML
    private StackPane specialCard;

    @FXML
    private StackPane monthlyCard;

    @FXML
    private StackPane dailyCard;

    private StackPane selectedCard = null;
    private Timeline pulseAnimation;

    @FXML
    public void initialize() {
        // Tambahkan efek hover untuk setiap card
        if (specialCard != null) setupHoverEffect(specialCard);
        if (monthlyCard != null) {
            setupHoverEffect(monthlyCard);
            setupPulseAnimation(); // Animasi khusus untuk Monthly card
        }
        if (dailyCard != null) setupHoverEffect(dailyCard);
    }

    private void setupPulseAnimation() {
        if (monthlyCard == null) return;

        // Animasi pulse yang berulang
        ScaleTransition pulse = new ScaleTransition(Duration.millis(1500), monthlyCard);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.03);
        pulse.setToY(1.03);
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
    }

    // Setup efek hover (smooth scale)
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

    // Handler untuk Special Card
    @FXML
    private void selectSpecial(MouseEvent event) {
        if (specialCard != null) {
            System.out.println("Special card clicked!");
            selectCard(specialCard, "Special", "650k/month");
        }
    }

    // Handler untuk Monthly Card
    @FXML
    private void selectMonthly(MouseEvent event) {
        if (monthlyCard != null) {
            System.out.println("Monthly card clicked!");
            selectCard(monthlyCard, "Monthly", "150k/month");
        }
    }

    // Handler untuk Daily Card
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

        // Reset card yang sebelumnya dipilih
        if (selectedCard != null && selectedCard != card) {
            resetCardStyle(selectedCard);
        }

        // Set card baru sebagai terpilih
        selectedCard = card;
        highlightSelectedCard(card);

        // Print pilihan
        System.out.println("=================================");
        System.out.println("Selected Plan: " + planName);
        System.out.println("Price: " + price);
        System.out.println("=================================");

        // TODO: Tambahkan logic Anda di sini
    }

    // Highlight card yang dipilih
    private void highlightSelectedCard(StackPane card) {
        if (card == null || card.getChildren().isEmpty()) return;

        // Tambahkan CSS class 'selected'
        card.getStyleClass().add("selected");

        VBox vbox = (VBox) card.getChildren().get(0);
        String currentStyle = vbox.getStyle();

        // Tambahkan border hijau untuk card terpilih
        if (currentStyle.contains("-fx-border-color")) {
            currentStyle = currentStyle.replaceAll("-fx-border-color:[^;]+;", "-fx-border-color: #2f7d32;");
            currentStyle = currentStyle.replaceAll("-fx-border-width:[^;]+;", "-fx-border-width: 4;");
        } else {
            currentStyle += "; -fx-border-color: #2f7d32; -fx-border-width: 4;";
        }

        vbox.setStyle(currentStyle);
    }

    // Reset style card yang tidak terpilih
    private void resetCardStyle(StackPane card) {
        if (card == null || card.getChildren().isEmpty()) return;

        // Hapus CSS class 'selected'
        card.getStyleClass().remove("selected");

        VBox vbox = (VBox) card.getChildren().get(0);
        String currentStyle = vbox.getStyle();

        // Kembalikan border ke original
        if (card == specialCard || card == dailyCard) {
            currentStyle = currentStyle.replaceAll("-fx-border-color:[^;]+;", "-fx-border-color: rgba(0,0,0,0.06);");
            currentStyle = currentStyle.replaceAll("-fx-border-width:[^;]+;", "-fx-border-width: 2;");
        } else if (card == monthlyCard) {
            currentStyle = currentStyle.replaceAll("-fx-border-color:[^;]+;", "-fx-border-color: rgba(247,181,0,0.35);");
            currentStyle = currentStyle.replaceAll("-fx-border-width:[^;]+;", "-fx-border-width: 2;");
        }

        vbox.setStyle(currentStyle);
    }

    private void setMainPane(String fxml) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxml));

            if (node instanceof Region) {
                Region region = (Region) node;

                // Bind width dan height agar mengikuti parent
                region.prefWidthProperty().bind(mainPane.widthProperty());
                region.prefHeightProperty().bind(mainPane.heightProperty());

                // Set min/max agar benar-benar memenuhi area
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

        // Setup background image binding after loading
        Platform.runLater(() -> {
            Node memberNode = mainPane.getChildren().isEmpty() ? null : mainPane.getChildren().get(0);
            if (memberNode != null) {
                // Setup background image binding
                ImageView backgroundImage = (ImageView) memberNode.lookup("#memberBackgroundImage");

                if (backgroundImage != null) {
                    // Bind ke ukuran mainPane (StackPane parent)
                    backgroundImage.fitWidthProperty().bind(mainPane.widthProperty());
                    backgroundImage.fitHeightProperty().bind(mainPane.heightProperty());
                    backgroundImage.setPreserveRatio(false);
                }
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