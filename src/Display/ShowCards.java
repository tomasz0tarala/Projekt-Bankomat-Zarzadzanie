package Display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ShowCards extends JDialog {
    public JList listaKart;
    private JLabel titleLabel;
    private JLabel iconLabel;
    private JPanel showCardsJPanel;
    private JPanel topJPanel;
    private JPanel middleJPanel;
    private JScrollPane listaKartJScrollPane;
    private JButton wróćButton;
    private JPanel bottomJPanel;

    public ShowCards() {
        setContentPane(showCardsJPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Lista kart");
        int width = 600;
        int height = 475;
        setSize(width, height);
        setResizable(false);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        wróćButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        DefaultListModel<String> listModel = new DefaultListModel<>();
        final String DB_URL = "jdbc:mysql://localhost/Bankomat?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            String sql = "SELECT nazwakarty FROM karty";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                listModel.addElement(resultSet.getString("nazwakarty"));
            }
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        listaKart.setModel(listModel);
        setVisible(true);
    }


}
