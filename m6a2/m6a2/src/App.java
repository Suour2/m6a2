/* Bailey Garrett
 * module 6 assignment 2 
 * 2/25/24 
 * batch update speed efficency test 
 */

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class App extends Application {

    private TextArea logTextArea;

    public static void main(String[] args) {
        // Launch app
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Title
        primaryStage.setTitle("Batch Update Speed Test");

        // Create borderpane to hold gui elements
        BorderPane borderPane = new BorderPane();

        // Create text area for log messages
        logTextArea = new TextArea();
        logTextArea.setEditable(false);

        // Button to connect to database
        Button connectButton = new Button("Connect to Database");
        connectButton.setOnAction(e -> connectToDatabase());

        // Create box for hold button and log text
        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(10));
        vBox.getChildren().addAll(connectButton, logTextArea);

        borderPane.setCenter(vBox);

        // Create scene set stage
        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void connectToDatabase() {
        // Create dialog box DB connection
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Connect to Database");

        // Create a button type for connecting
        ButtonType connectButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(connectButtonType, ButtonType.CANCEL);

        // Create box for DBConnectionPane
        VBox content = new VBox();
        DBConnectionPane connectionPane = new DBConnectionPane();
        content.getChildren().add(connectionPane);
        dialog.getDialogPane().setContent(content);

        // Result convert for dialog
        dialog.setResultConverter(buttonType -> {
            if (buttonType == connectButtonType) {
                try {
                    // Get connection from DBConnectionPane
                    Connection connection = connectionPane.getConnection();
                    if (connection != null) {
                        log("Connected to database.");

                        // Perform batch updates
                        long start = System.currentTimeMillis();
                        insertRecords(connection, false);
                        long end = System.currentTimeMillis();
                        log("Time taken without batch update: " + (end - start) + " ms");

                        // Perform without batch updates
                        start = System.currentTimeMillis();
                        insertRecords(connection, true);
                        end = System.currentTimeMillis();
                        log("Time taken with batch update: " + (end - start) + " ms");
                    } else {
                        log("Error connecting to database.");
                    }
                } catch (SQLException ex) {
                    log("Error connecting to database: " + ex.getMessage());
                }
            }
            return null;
        });

        // show dialog and wait for user inputs
        dialog.showAndWait();
    }

    private void insertRecords(Connection connection, boolean useBatchUpdate) throws SQLException {
        // prepare insert query
        String insertQuery = "INSERT INTO Temp(num1, num2, num3) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            // generate random numbers
            Random random = new Random();
            for (int i = 0; i < 1000; i++) {
                preparedStatement.setDouble(1, random.nextDouble());
                preparedStatement.setDouble(2, random.nextDouble());
                preparedStatement.setDouble(3, random.nextDouble());

                // Add to batch or update
                if (useBatchUpdate) {
                    preparedStatement.addBatch();
                } else {
                    preparedStatement.executeUpdate();
                }
            }

            // Execute batch
            if (useBatchUpdate) {
                preparedStatement.executeBatch();
            }
        }
    }

    private void log(String message) {
        // Append message to log text
        logTextArea.appendText(message + "\n");
    }
}
