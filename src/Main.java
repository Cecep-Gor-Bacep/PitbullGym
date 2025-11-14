import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage pStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/resources/fxml/MainMenu.fxml"));
        if (root instanceof Region) {
            Region region = (Region) root;
            // Make root resize with the window for responsive layout
            region.prefWidthProperty().bind(pStage.widthProperty());
            region.prefHeightProperty().bind(pStage.heightProperty());
        }
        Scene scene = new Scene(root);
        pStage.setTitle("Pitbull Gym");
        pStage.setScene(scene);
        // Fit to current device screen size
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        pStage.setX(bounds.getMinX());
        pStage.setY(bounds.getMinY());
        pStage.setWidth(bounds.getWidth());
        pStage.setHeight(bounds.getHeight());
        pStage.setMaximized(true);
        pStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
