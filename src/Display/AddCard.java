package Display;

import Content.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;


public class AddCard extends JDialog {
    private Card card;
    private JLabel titleLabel;
    private JLabel iconLabel;
    private JRadioButton kartaPłatniczaRadioButton;
    private JRadioButton kartaKredytowaRadioButton;
    private JRadioButton kartaBankomatowaRadioButton;
    private JButton addCardButton;
    private JButton backButton;
    private JLabel titleRightLabel;
    private JLabel titleLeftLabel;
    private JPanel addCardJPanel;
    private JPanel topJPanel;
    private JPanel bottomJPanel;
    private JPanel middleJpanel;
    private JPanel middleLeftJPanel;
    private JPanel middleRightJPanel;
    private JTextField cardBalanceField;
    private JPasswordField cardPinField;
    private JTextField cardNameField;

    public AddCard() {
        setTitle("Dodaj kartę");
        setContentPane(addCardJPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        int width = 600;
        int height = 475;
        setSize(width, height);
        setResizable(false);
        setLocationRelativeTo(null);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        setVisible(true);
        addCardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerCard();
            }
        });

    }

    private void registerCard() {
        String cardName = cardNameField.getText();
        String cardPin = String.valueOf(cardPinField.getPassword());
        String balance = cardBalanceField.getText();
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(kartaPłatniczaRadioButton);
        buttonGroup.add(kartaKredytowaRadioButton);
        buttonGroup.add(kartaBankomatowaRadioButton);
        String cardType = "";
        if (kartaPłatniczaRadioButton.isSelected()) {
            cardType = "kartaPłatnicza";
        } else if (kartaKredytowaRadioButton.isSelected()) {
            cardType = "kartaKredytowa";
        } else if (kartaBankomatowaRadioButton.isSelected()) {
            cardType = "kartaBankomatowa";
        }
        System.out.println(cardType);
        if (cardName.isEmpty() || cardPin.isEmpty() || balance.isEmpty() || cardType.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Uzupełnij wszytkie pola aby dodać kartę", "Try again", JOptionPane.ERROR_MESSAGE);
        } else if (!cardPin.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Pin musi składać się z czterech cyfr", "Try again", JOptionPane.ERROR_MESSAGE);
            cardPinField.setText("");
        } else if (!balance.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Saldo musi być dodatnią liczbą", "Try again", JOptionPane.ERROR_MESSAGE);
            cardBalanceField.setText("");
        } else if (Double.parseDouble(balance) < 0) {
            JOptionPane.showMessageDialog(this, "Saldo nie może być ujemne", "Try again", JOptionPane.ERROR_MESSAGE);
            cardBalanceField.setText("");
        } else {
            card = addCardToDatabase(cardType, cardName, cardPin, balance);
            if (card != null) {
                JOptionPane.showMessageDialog(this, "Dodano kartę", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Dodawanie karty nie powiodło się. Spróbój ponownie ", "Try again", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public Card card1;

    private Card addCardToDatabase(String cardType, String cardName, String cardPin, String balance) {
        Card card1 = null;
        final String DB_URL = "jdbc:mysql://localhost/Bankomat?serverTimezone=UTC";
        final String NAZWAKARTY = "root";
        final String PINKARTY = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, NAZWAKARTY, PINKARTY);
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO karty (typkarty, nazwakarty, pinkarty, saldokarty) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, cardType);
            ps.setString(2, cardName);
            ps.setString(3, cardPin);
            ps.setString(4, balance);
            //insert rows into the table
            int addedRows = ps.executeUpdate();
            if (addedRows > 0) {
                card = new Card();
                card.cardType = cardType;
                card.cardName = cardName;
                card.cardPin = cardPin;
                card.cardBalance = balance;
            }

            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return card;
    }
}
