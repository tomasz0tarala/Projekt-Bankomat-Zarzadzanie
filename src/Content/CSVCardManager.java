package Content;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.sql.*;

public class CSVCardManager {
    private static final String DB_URL = "jdbc:mysql://localhost/Bankomat?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";

    public void importFromCSV() {
        LookAndFeel originalLookAndFeel = UIManager.getLookAndFeel();
        UIDefaults originalDefaults = new UIDefaults();
        originalDefaults.putAll(UIManager.getDefaults());

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Wybierz plik do importu");
        fileChooser.setPreferredSize(new Dimension(600, 475));

        // Add a file filter for .csv files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
        fileChooser.setFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(";");
                    String cardType = values[0];
                    String cardName = values[1];
                    String cardPin = values[2];
                    String cardBalance = values[3];

                    Card card = new Card(cardName, cardPin, cardBalance);
                    card.cardType = cardType;

                    addCardToDatabase(card);
                }
                JOptionPane.showMessageDialog(null, "Importowanie z pliku powiodło się!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            UIManager.setLookAndFeel(originalLookAndFeel);
            UIManager.getLookAndFeelDefaults().putAll(originalDefaults);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Importowanie z pliku nie powiodło się: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addCardToDatabase(Card card) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "INSERT INTO karty (typkarty, nazwakarty, pinkarty, saldokarty) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, card.cardType);
            statement.setString(2, card.cardName);
            statement.setString(3, card.cardPin);
            statement.setString(4, card.cardBalance);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void exportToCSV() {
        LookAndFeel originalLookAndFeel = UIManager.getLookAndFeel();
        UIDefaults originalDefaults = new UIDefaults();
        originalDefaults.putAll(UIManager.getDefaults());

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Wybierz miejsce zapisu pliku");
        fileChooser.setPreferredSize(new Dimension(600, 475));
        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }

            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 FileWriter writer = new FileWriter(filePath)) {
                String sql = "SELECT * FROM karty";
                PreparedStatement statement = conn.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String cardType = resultSet.getString("typkarty");
                    String cardName = resultSet.getString("nazwakarty");
                    String cardPin = resultSet.getString("pinkarty");
                    String cardBalance = resultSet.getString("saldokarty");

                    writer.append(cardType);
                    writer.append(";");
                    writer.append(cardName);
                    writer.append(";");
                    writer.append(cardPin);
                    writer.append(";");
                    writer.append(cardBalance);
                    writer.append("\n");
                }
                JOptionPane.showMessageDialog(null, "Eksportowanie do pliku powiodło się!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException | IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Eksportowanie do pliku nie powiodło się: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        try {
            UIManager.setLookAndFeel(originalLookAndFeel);
            UIManager.getLookAndFeelDefaults().putAll(originalDefaults);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}