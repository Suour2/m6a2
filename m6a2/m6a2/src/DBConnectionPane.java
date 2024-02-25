
/* Bailey Garrett
 * module 6 assignment null
 * 2/25/24 
 * DB conection entry fields
 */
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.*;

public class DBConnectionPane extends BorderPane {
    private TextField driverField;
    private TextField urlField;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button connectButton;
    private Label statusLabel;
    private Connection connection;

    public DBConnectionPane() {
        initializeUI();
        setupEventHandlers();
    }

    // Create text filds
    private void initializeUI() {
        driverField = new TextField();
        urlField = new TextField();
        usernameField = new TextField();
        passwordField = new PasswordField();
        connectButton = new Button("Connect to DB");
        statusLabel = new Label();

        VBox vbox = new VBox(
                new Label("JDBC Driver:"),
                driverField,
                new Label("URL:"),
                urlField,
                new Label("Username:"),
                usernameField,
                new Label("Password:"),
                passwordField,
                connectButton,
                statusLabel);

        this.setCenter(vbox);
    }

    // connect button event
    private void setupEventHandlers() {
        connectButton.setOnAction(e -> {
            try {
                // Getters
                String driver = driverField.getText();
                String url = urlField.getText();
                String username = usernameField.getText();
                String password = passwordField.getText();

                // Load jdbc driver and connect to db
                Class.forName(driver);
                connection = DriverManager.getConnection(url, username, password);

                // Handle connection success
                statusLabel.setText("Connected to database.");
            } catch (ClassNotFoundException | SQLException ex) {
                // Handle connection failure
                statusLabel.setText("Error connecting to database: " + ex.getMessage());
            }
        });
    }

    public Connection getConnection() {
        return connection;
    }
}
