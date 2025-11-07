package Controller;

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Node;
import org.w3c.dom.events.MouseEvent;

public class MainController {
    @FXML
    private StackPane mainPane;

    @FXML
    private StackPane specialCard;

    @FXML
    private StackPane monthlyCard;

    @FXML
    private StackPane dailyCard;

    // Card name and price labels
    @FXML
    private Label specialCardName;
    @FXML
    private Label specialCardPrice;
    @FXML
    private Label monthlyCardName;
    @FXML
    private Label monthlyCardPrice;
    @FXML
    private Label dailyCardName;
    @FXML
    private Label dailyCardPrice;

    private StackPane selectedCard = null;
    private Timeline pulseAnimation;
    // Map to track pulse animations by card to prevent accumulation
    private Map<StackPane, ScaleTransition> pulseAnimations = new HashMap<>();

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

        // Stop and remove any existing pulse animation for monthly card
        ScaleTransition existingPulse = pulseAnimations.get(monthlyCard);
        if (existingPulse != null) {
            existingPulse.stop();
            pulseAnimations.remove(monthlyCard);
        }

        // Animasi pulse yang berulang
        ScaleTransition pulse = new ScaleTransition(Duration.millis(1500), monthlyCard);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.03);
        pulse.setToY(1.03);
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
        
        // Store the animation reference to prevent accumulation
        pulseAnimations.put(monthlyCard, pulse);
    }

    // Setup efek hover (smooth scale)
    private void setupHoverEffect(StackPane card) {
        if (card == null) return;

        // Get the card name and price labels
        Label cardName = null;
        Label cardPrice = null;
        
        if (card == specialCard) {
            cardName = specialCardName;
            cardPrice = specialCardPrice;
        } else if (card == monthlyCard) {
            cardName = monthlyCardName;
            cardPrice = monthlyCardPrice;
        } else if (card == dailyCard) {
            cardName = dailyCardName;
            cardPrice = dailyCardPrice;
        }

        final Label nameLabel = cardName;
        final Label priceLabel = cardPrice;

        card.setOnMouseEntered(event -> {
            if (selectedCard != card) {
                // Scale the card
                ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), card);
                scaleUp.setToX(1.08);
                scaleUp.setToY(1.08);
                scaleUp.play();
                
                // Scale the card name and price labels
                if (nameLabel != null) {
                    ScaleTransition nameScaleUp = new ScaleTransition(Duration.millis(200), nameLabel);
                    nameScaleUp.setToX(1.1);
                    nameScaleUp.setToY(1.1);
                    nameScaleUp.play();
                }
                
                if (priceLabel != null) {
                    ScaleTransition priceScaleUp = new ScaleTransition(Duration.millis(200), priceLabel);
                    priceScaleUp.setToX(1.1);
                    priceScaleUp.setToY(1.1);
                    priceScaleUp.play();
                }
            }
        });

        card.setOnMouseExited(event -> {
            if (selectedCard != card) {
                // Scale down the card
                ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), card);
                scaleDown.setToX(1.0);
                scaleDown.setToY(1.0);
                scaleDown.play();
                
                // Scale down the card name and price labels
                if (nameLabel != null) {
                    ScaleTransition nameScaleDown = new ScaleTransition(Duration.millis(200), nameLabel);
                    nameScaleDown.setToX(1.0);
                    nameScaleDown.setToY(1.0);
                    nameScaleDown.play();
                }
                
                if (priceLabel != null) {
                    ScaleTransition priceScaleDown = new ScaleTransition(Duration.millis(200), priceLabel);
                    priceScaleDown.setToX(1.0);
                    priceScaleDown.setToY(1.0);
                    priceScaleDown.play();
                }
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
        // Contoh: showPaymentDialog(planName, price);
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
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
            mainPane.getChildren().setAll(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void memberAct(ActionEvent actionEvent) {
        setMainPane("/resources/fxml/Membership.fxml");
    }

    public void aboutAct(ActionEvent actionEvent) {
        setMainPane("/resources/fxml/About.fxml");
    }

    public void priceAct(ActionEvent actionEvent) {
        setMainPane("/resources/fxml/Price.fxml");
        // Set up hover effects for price cards after loading
        Platform.runLater(() -> {
            // Find cards and labels from the loaded node
            Node priceNode = mainPane.getChildren().isEmpty() ? null : mainPane.getChildren().get(0);
            if (priceNode != null) {
                StackPane special = (StackPane) priceNode.lookup("#specialCard");
                StackPane monthly = (StackPane) priceNode.lookup("#monthlyCard");
                StackPane daily = (StackPane) priceNode.lookup("#dailyCard");
                
                if (special != null) {
                    Label nameLabel = (Label) priceNode.lookup("#specialCardName");
                    Label priceLabel = (Label) priceNode.lookup("#specialCardPrice");
                    setupHoverEffectWithLabels(special, nameLabel, priceLabel);
                }
                if (monthly != null) {
                    Label nameLabel = (Label) priceNode.lookup("#monthlyCardName");
                    Label priceLabel = (Label) priceNode.lookup("#monthlyCardPrice");
                    setupHoverEffectWithLabels(monthly, nameLabel, priceLabel);
                    setupPulseAnimationForCard(monthly);
                }
                if (daily != null) {
                    Label nameLabel = (Label) priceNode.lookup("#dailyCardName");
                    Label priceLabel = (Label) priceNode.lookup("#dailyCardPrice");
                    setupHoverEffectWithLabels(daily, nameLabel, priceLabel);
                }
            }
        });
    }
    
    private void setupHoverEffectWithLabels(StackPane card, Label nameLabel, Label priceLabel) {
        if (card == null) return;

        card.setOnMouseEntered(event -> {
            if (selectedCard != card) {
                // Scale the card
                ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), card);
                scaleUp.setToX(1.08);
                scaleUp.setToY(1.08);
                scaleUp.play();
                
                // Scale the card name and price labels
                if (nameLabel != null) {
                    ScaleTransition nameScaleUp = new ScaleTransition(Duration.millis(200), nameLabel);
                    nameScaleUp.setToX(1.1);
                    nameScaleUp.setToY(1.1);
                    nameScaleUp.play();
                }
                
                if (priceLabel != null) {
                    ScaleTransition priceScaleUp = new ScaleTransition(Duration.millis(200), priceLabel);
                    priceScaleUp.setToX(1.1);
                    priceScaleUp.setToY(1.1);
                    priceScaleUp.play();
                }
            }
        });

        card.setOnMouseExited(event -> {
            if (selectedCard != card) {
                // Scale down the card
                ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), card);
                scaleDown.setToX(1.0);
                scaleDown.setToY(1.0);
                scaleDown.play();
                
                // Scale down the card name and price labels
                if (nameLabel != null) {
                    ScaleTransition nameScaleDown = new ScaleTransition(Duration.millis(200), nameLabel);
                    nameScaleDown.setToX(1.0);
                    nameScaleDown.setToY(1.0);
                    nameScaleDown.play();
                }
                
                if (priceLabel != null) {
                    ScaleTransition priceScaleDown = new ScaleTransition(Duration.millis(200), priceLabel);
                    priceScaleDown.setToX(1.0);
                    priceScaleDown.setToY(1.0);
                    priceScaleDown.play();
                }
            }
        });
    }
    
    private void setupPulseAnimationForCard(StackPane card) {
        if (card == null) return;
        
        // Stop and remove any existing pulse animation for this card
        ScaleTransition existingPulse = pulseAnimations.get(card);
        if (existingPulse != null) {
            existingPulse.stop();
            pulseAnimations.remove(card);
        }
        
        // Create and start new pulse animation
        ScaleTransition pulse = new ScaleTransition(Duration.millis(1500), card);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.03);
        pulse.setToY(1.03);
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
        
        // Store the animation reference to prevent accumulation
        pulseAnimations.put(card, pulse);
    }

    public void contactAct(ActionEvent actionEvent) {
    }

    public void mainAct(ActionEvent actionEvent) {
        setMainPane("/resources/fxml/Main.fxml");
    }
}
