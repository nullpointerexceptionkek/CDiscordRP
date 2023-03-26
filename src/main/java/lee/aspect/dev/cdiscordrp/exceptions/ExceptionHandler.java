/*
 *
 * MIT License
 *
 * Copyright (c) 2023 lee
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package lee.aspect.dev.cdiscordrp.exceptions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        /*
            ██╗░░░░░███╗░░░███╗███████╗░█████╗░░█████╗░
            ██║░░░░░████╗░████║██╔════╝██╔══██╗██╔══██╗
            ██║░░░░░██╔████╔██║█████╗░░███████║██║░░██║
            ██║░░░░░██║╚██╔╝██║██╔══╝░░██╔══██║██║░░██║
            ███████╗██║░╚═╝░██║██║░░░░░██║░░██║╚█████╔╝
            ╚══════╝╚═╝░░░░░╚═╝╚═╝░░░░░╚═╝░░╚═╝░╚════╝░
            if this happen = im bad at coding and have no idea what im doing
         */

        //there is no need to every touch this code again unless for gui improvements
        //the code below is for grabbing the uncaught exception and displaying it in a gui
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();

        JTextArea textArea = new JTextArea(stackTrace);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(800, 600));

        JButton copyButton = new JButton("Copy to Clipboard");
        copyButton.addActionListener((event) -> {
            StringSelection selection = new StringSelection(stackTrace);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            JOptionPane.showMessageDialog(null, "Error message copied to clipboard.", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);
        UIManager.put("Button.background", Color.WHITE);
        UIManager.put("Button.border", BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        UIManager.put("Button.font", new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        UIManager.put("Label.font", new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        UIManager.put("OptionPane.messageFont", new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
        UIManager.put("TextArea.border", BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(10, 0, 0, 0);
        buttonPanel.add(copyButton, gbc);

        JLabel messageLabel = new JLabel("<html><body><p style='text-align:center'>An unexpected error has occurred.<br>Please copy the error message below and send it to the developer.</p></body></html>");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(messageLabel, BorderLayout.NORTH);
        messagePanel.add(scrollPane, BorderLayout.CENTER);
        messagePanel.add(buttonPanel, BorderLayout.SOUTH);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        JOptionPane.showMessageDialog(null, messagePanel, "Error", JOptionPane.ERROR_MESSAGE);

        System.exit(1);
    }

}