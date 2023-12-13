package Display;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class EditOrDeleteCard extends JDialog {
    private JTextField newNameField;
    private JPasswordField newPinField;
    private JTextField newBalanceField;
    private JLabel titleLabel;
    private JPanel topJPanel;
    private JLabel leftTitleLabel;
    private JLabel middleTitleLabel;
    private JLabel rightTitleLabel;
    private JButton editButton;
    private JButton deleteButton;
    private JButton backButton;
    private JTable cardsTable;
    private JPanel editOrDeleteJPanel;
    private JLabel iconLabel;
    private JPanel bottomJPanel;
    private JPanel middleBottomJPanel;
    private JPanel middleTopJPanel;
    private JScrollPane scrollPane;
    private JButton clearButton;

    public EditOrDeleteCard() {
        setTitle("Edytuj lub usuń kartę");
        setContentPane(editOrDeleteJPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        int width = 600;
        int height = 475;
        setSize(width, height);
        setResizable(false);
        setLocationRelativeTo(null);
        createTable();
        populateTableFromDatabase();
        cardsTable.setFillsViewportHeight(true);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel tblModel = (DefaultTableModel) cardsTable.getModel();
                if (newNameField.getText().equals("") ||
                        newPinField.getText().equals("") ||
                        newBalanceField.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Wypełnij wszytkie pola", "Try again", JOptionPane.ERROR_MESSAGE);
                } else if (!newPinField.getText().matches("\\d{4}")) {
                    JOptionPane.showMessageDialog(null, "PIN musi składać się z 4 cyfr", "Try again", JOptionPane.ERROR_MESSAGE);
                    newPinField.setText("");
                } else if (!newBalanceField.getText().matches("\\d+")) {
                    JOptionPane.showMessageDialog(null, "Saldo musi być liczbą", "Try again", JOptionPane.ERROR_MESSAGE);
                    newBalanceField.setText("");
                } else {
                    if (cardsTable.getSelectedRowCount() == 1) {
                        String cardNamePole = newNameField.getText();
                        String newPin = newPinField.getText();
                        String newBalance = newBalanceField.getText();
                        String cardNameStare = (String) tblModel.getValueAt(cardsTable.getSelectedRow(), 0);
                        tblModel.setValueAt(cardNamePole, cardsTable.getSelectedRow(), 0);
                        tblModel.setValueAt(newPin, cardsTable.getSelectedRow(), 1);
                        tblModel.setValueAt(newBalance, cardsTable.getSelectedRow(), 2);

                        // Update the database
                        final String DB_URL = "jdbc:mysql://localhost/Bankomat?serverTimezone=UTC";
                        final String USER = "root";
                        final String PASS = "";
                        try {
                            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                            Statement stmt = conn.createStatement();
                            int cardId = getCardId(cardNameStare);
                            // Assuming the ID of the card is stored in the fourth column of the table
                            String sql = "UPDATE karty SET nazwakarty = '" + cardNamePole + "', pinkarty = '" + newPin + "', saldokarty = '" + newBalance + "' WHERE id = " + cardId;
                            stmt.executeUpdate(sql);
                            stmt.close();
                            conn.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        JOptionPane.showMessageDialog(null, "Updated successfully");
                        newNameField.setText("");
                        newPinField.setText("");
                        newBalanceField.setText("");
                    } else {
                        if (cardsTable.getRowCount() == 0) {
                            JOptionPane.showMessageDialog(null, "Table is empty");
                        } else {
                            JOptionPane.showMessageDialog(null, "Please select single row for delete");
                        }
                    }
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel tblModel = (DefaultTableModel) cardsTable.getModel();
                if (cardsTable.getSelectedRowCount() == 1) {
                    String cardNameDelete = (String) tblModel.getValueAt(cardsTable.getSelectedRow(), 0);
                    tblModel.removeRow(cardsTable.getSelectedRow());

                    // Delete from the database
                    final String DB_URL = "jdbc:mysql://localhost/Bankomat?serverTimezone=UTC";
                    final String USER = "root";
                    final String PASS = "";
                    try {
                        Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                        Statement stmt = conn.createStatement();
                        int cardId = getCardId(cardNameDelete);
                        String sql = "DELETE FROM karty WHERE id = " + cardId;
                        stmt.executeUpdate(sql);
                        stmt.close();
                        conn.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    JOptionPane.showMessageDialog(null, "Usunięto kartę");
                } else {
                    if (cardsTable.getRowCount() == 0) {
                        JOptionPane.showMessageDialog(null, "Tabela jest pusta");
                    } else {
                        JOptionPane.showMessageDialog(null, "Wybierz jedną kartę do usunięcia");
                    }
                }
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newNameField.setText("");
                newPinField.setText("");
                newBalanceField.setText("");
            }
        });
        setVisible(true);
    }

    private void createTable() {
        Object[][] data = {

        };

        cardsTable.setModel(new DefaultTableModel(
                data,
                new String[]{"Nazwa karty",
                        "PIN",
                        "Saldo"
                }

        ));

        TableColumnModel columnModel = (TableColumnModel) cardsTable.getColumnModel();

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        columnModel.getColumn(0).setCellRenderer(centerRenderer);
        columnModel.getColumn(1).setCellRenderer(centerRenderer);
        columnModel.getColumn(2).setCellRenderer(centerRenderer);
    }

    private void populateTableFromDatabase() {
        DefaultTableModel model = (DefaultTableModel) cardsTable.getModel();
        model.setRowCount(0); // Clear the table
        final String DB_URL = "jdbc:mysql://localhost/Bankomat?serverTimezone=UTC";
        final String USER = "root";
        final String PASS = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String sql = "SELECT nazwakarty, pinkarty, saldokarty FROM karty";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String cardName = rs.getString("nazwakarty");
                String cardPin = rs.getString("pinkarty");
                String cardBalance = rs.getString("saldokarty");

                model.addRow(new Object[]{cardName, cardPin, cardBalance});
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCardId(String cardName) {
        int cardId = -1;
        final String DB_URL = "jdbc:mysql://localhost/Bankomat?serverTimezone=UTC";
        final String USER = "root";
        final String PASS = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String sql = "SELECT id FROM karty WHERE nazwakarty = '" + cardName + "'";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                cardId = rs.getInt("id");
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cardId;
    }


}
