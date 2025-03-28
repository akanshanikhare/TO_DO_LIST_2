import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;

public class VibrantToDoList {
    private JFrame frame;
    private DefaultListModel<String> taskModel;
    private JList<String> taskList;
    private JTextField taskInput;
    private JButton addButton, deleteButton, updateButton, saveButton, darkModeButton;
    private boolean isDarkMode = false;
    private final String FILE_NAME = "tasks.txt";

    public VibrantToDoList() {
        frame = new JFrame("🌟 Colorful To-Do List 🌟");
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        GradientPanel panel = new GradientPanel();
        panel.setLayout(new BorderLayout());

        taskModel = new DefaultListModel<>();
        taskList = new JList<>(taskModel);
        taskList.setFont(new Font("SansSerif", Font.BOLD, 16));
        taskList.setBackground(new Color(255, 255, 204));
        taskList.setSelectionBackground(new Color(255, 140, 0));

        // Load tasks in a separate thread
        new LoadTasksWorker().execute();

        taskInput = new JTextField(20);
        taskInput.setFont(new Font("SansSerif", Font.BOLD, 16));

        addButton = createStyledButton("➕ Add Task", new Color(46, 204, 113));
        deleteButton = createStyledButton("❌ Delete", new Color(231, 76, 60));
        updateButton = createStyledButton("✏ Update", new Color(52, 152, 219));
        saveButton = createStyledButton("💾 Save", new Color(241, 196, 15));
        darkModeButton = createStyledButton("🌙 Dark Mode", new Color(155, 89, 182));

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(255, 230, 204));
        inputPanel.add(taskInput, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 204, 204));
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(darkModeButton);

        panel.add(new JScrollPane(taskList), BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);

        addButton.addActionListener(e -> addTask());
        deleteButton.addActionListener(e -> deleteTask());
        updateButton.addActionListener(e -> updateTask());
        saveButton.addActionListener(e -> new SaveTasksWorker().execute());
        darkModeButton.addActionListener(e -> toggleDarkMode(panel));
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return button;
    }

    private void addTask() {
        String task = taskInput.getText().trim();
        if (!task.isEmpty()) {
            taskModel.addElement(task);
            taskInput.setText("");
        } else {
            JOptionPane.showMessageDialog(frame, "Task cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            taskModel.remove(selectedIndex);
        } else {
            JOptionPane.showMessageDialog(frame, "Select a task to delete!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            String newTask = JOptionPane.showInputDialog(frame, "Update Task:", taskModel.getElementAt(selectedIndex));
            if (newTask != null && !newTask.trim().isEmpty()) {
                taskModel.set(selectedIndex, newTask);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Select a task to update!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleDarkMode(JPanel panel) {
        if (isDarkMode) {
            panel.setBackground(new Color(255, 230, 204));
            taskList.setBackground(new Color(255, 255, 204));
            darkModeButton.setText("🌙 Dark Mode");
            isDarkMode = false;
        } else {
            panel.setBackground(new Color(40, 40, 40));
            taskList.setBackground(new Color(60, 60, 60));
            taskList.setForeground(Color.WHITE);
            darkModeButton.setText("☀ Light Mode");
            isDarkMode = true;
        }
    }

    // Background Task for Saving Tasks
    private class SaveTasksWorker extends SwingWorker<Void, Void> {
        @Override
        protected Void doInBackground() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                for (int i = 0; i < taskModel.size(); i++) {
                    writer.write(taskModel.getElementAt(i));
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void done() {
            JOptionPane.showMessageDialog(frame, "Tasks saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Background Task for Loading Tasks
    private class LoadTasksWorker extends SwingWorker<Void, String> {
        @Override
        protected Void doInBackground() {
            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
                String task;
                while ((task = reader.readLine()) != null) {
                    publish(task);
                }
            } catch (IOException e) {
                System.out.println("No previous tasks found.");
            }
            return null;

        }

        @Override
        protected void process(List<String> tasks) {
            for (String task : tasks) {
                taskModel.addElement(task);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VibrantToDoList::new);
    }

    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gradient = new GradientPaint(0, 0, new Color(255, 153, 102), getWidth(), getHeight(), new Color(255, 102, 204));
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }
    }
}
