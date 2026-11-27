package dev.kairo.installer;

import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.*;

final class InstallerGui {
    private InstallerGui() {}

    static void show(Path defaultDir) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Kairo Client - Instalador");
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            JTextField dir = new JTextField(defaultDir.toString(), 36);
            JButton go = new JButton("Instalar");
            JTextArea log = new JTextArea(12, 48);
            log.setEditable(false);
            JPanel top = new JPanel();
            top.add(new JLabel("Pasta .minecraft:"));
            top.add(dir);
            top.add(go);
            f.add(top, java.awt.BorderLayout.NORTH);
            f.add(new JScrollPane(log), java.awt.BorderLayout.CENTER);
            go.addActionListener(e -> {
                go.setEnabled(false);
                new Thread(() -> {
                    try {
                        Installer.install(Paths.get(dir.getText().trim()),
                            m -> SwingUtilities.invokeLater(() -> log.append(m + "\n")));
                    } catch (Exception ex) {
                        SwingUtilities.invokeLater(() -> log.append("ERRO: " + ex + "\n"));
                    }
                    SwingUtilities.invokeLater(() -> go.setEnabled(true));
                }).start();
            });
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
