
package com.project.gamevaultgui;

import javax.swing.*;
import java.awt.*;

public class ManageGamesPanel extends JPanel {

    public ManageGamesPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Manage Games (Admin)"));
        add(new JLabel("Admin Game Management Panel Placeholder", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}