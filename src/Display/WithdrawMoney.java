package Display;

import Content.Card;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class WithdrawMoney extends JDialog {
    private Card card;
    private JTextField cardNameField;
    private JPasswordField cardPinField;
    private JButton cashOutButton;
    private JButton backButton;
    private JPanel withdrawMoneyJPanel;
    private JPanel topJPanel;
    private JPanel bottomJPanel;
    private JPanel middleJPanel;
    private JLabel titleLabel;
    private JLabel isconLabel;
    private JLabel cardNameLabel;
    private JLabel cardPinLabel;
    private JTextField amountField;

    public WithdrawMoney() {
        setTitle("Wypłać pieniądze");
        setContentPane(withdrawMoneyJPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        int width = 500;
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
        cashOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cardName = cardNameField.getText();
                String cardPin = cardPinField.getText();
                String balance = amountField.getText();

                card = getAuthentificationCard(cardName, cardPin, balance);
                if (card != null) {
                    dispose();
                }

            }
        });
        setVisible(true);
    }

    private Card getAuthentificationCard(String cardName, String cardPin, String amount) {
        final String DB_URL = "jdbc:mysql://localhost/Bankomat?serverTimezone=UTC";
        final String NAZWAKARTY = "root";
        final String PINKARTY = "";
        try {
            Connection conn = DriverManager.getConnection(DB_URL, NAZWAKARTY, PINKARTY);
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM karty WHERE NAZWAKARTY=? AND PINKARTY=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, cardName);
            ps.setString(2, cardPin);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                card = new Card();
                card.cardType = resultSet.getString("typkarty");
                card.cardName = resultSet.getString("nazwakarty");
                card.cardPin = resultSet.getString("pinkarty");
                card.cardBalance = resultSet.getString("saldokarty");
//                    card.cardBalance = String.valueOf(Integer.parseInt(card.cardBalance) - Integer.parseInt(amount));
                if (card.cardType.equals("kartaKredytowa")) {
                    if (Integer.parseInt(card.cardBalance) >= Integer.parseInt(amount)) {
                        card.cardBalance = String.valueOf(Integer.parseInt(card.cardBalance) - Integer.parseInt(amount));
                        String updateSql = "UPDATE karty SET saldokarty = ? WHERE nazwakarty = ?";
                        PreparedStatement updatePs = conn.prepareStatement(updateSql);
                        updatePs.setString(1, card.cardBalance);
                        updatePs.setString(2, card.cardName);
                        updatePs.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Wypłacono pieniądze obecny balans to: " + (Integer.parseInt(card.cardBalance) - Integer.parseInt(amount)), "Sukces", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        if (Integer.parseInt(card.cardBalance) == 0) {
                            card.cardBalance = String.valueOf(Integer.parseInt(card.cardBalance) - Integer.parseInt(amount)) ;
                            JOptionPane.showMessageDialog(this, "Kwota zadłużenia wynosi: " + amount, "Sukces", JOptionPane.INFORMATION_MESSAGE);
                            String updateSql = "UPDATE karty SET saldokarty = ? WHERE nazwakarty = ?";
                            PreparedStatement updatePs = conn.prepareStatement(updateSql);
                            updatePs.setString(1, card.cardBalance);
                            updatePs.setString(2, card.cardName);
                            updatePs.executeUpdate();
                        } else {
                            card.cardBalance = String.valueOf(Integer.parseInt(card.cardBalance) - Integer.parseInt(amount)) ;
                            JOptionPane.showMessageDialog(this, "Kwota zadłużenia wynosi: " + Math.abs(Integer.parseInt(card.cardBalance) - Integer.parseInt(amount)), "Sukces", JOptionPane.INFORMATION_MESSAGE);
                            String updateSql = "UPDATE karty SET saldokarty = ? WHERE nazwakarty = ?";
                            PreparedStatement updatePs = conn.prepareStatement(updateSql);
                            updatePs.setString(1, card.cardBalance);
                            updatePs.setString(2, card.cardName);
                            updatePs.executeUpdate();
                        }
                    }
                } else if (Integer.parseInt(card.cardBalance) < Integer.parseInt(amount)) {
                    JOptionPane.showMessageDialog(this, "Nie masz wystarczających środków na koncie", "Try again", JOptionPane.ERROR_MESSAGE);
                    cardNameField.setText("");
                    cardPinField.setText("");
                    amountField.setText("");
                    return null;
                } else if (Integer.parseInt(amount) < 0) {
                    JOptionPane.showMessageDialog(this, "Nie możesz wypłacić ujemnej kwoty", "Try again", JOptionPane.ERROR_MESSAGE);
                    cardNameField.setText("");
                    cardPinField.setText("");
                    amountField.setText("");
                    return null;
                } else {
                    card.cardBalance = String.valueOf(Integer.parseInt(card.cardBalance) - Integer.parseInt(amount));
                    JOptionPane.showMessageDialog(this, "Wypłacono pieniądze obecny balans to: " + String.valueOf(Integer.parseInt(card.cardBalance) - Integer.parseInt(amount)), "Sukces", JOptionPane.INFORMATION_MESSAGE);
                    String updateSql = "UPDATE karty SET saldokarty = ? WHERE nazwakarty = ?";
                    PreparedStatement updatePs = conn.prepareStatement(updateSql);
                    updatePs.setString(1, card.cardBalance);
                    updatePs.setString(2, card.cardName);
                    updatePs.executeUpdate();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Niepoprawna nazwa karty lub pin", "Try again", JOptionPane.ERROR_MESSAGE);
                cardNameField.setText("");
                cardPinField.setText("");
                amountField.setText("");
            }
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return card;
    }
}