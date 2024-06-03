package todolistapp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class ToDoListApp {

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    protected ArrayList<Task> tasks = new ArrayList<>();

    public ToDoListApp() {
        frame = new JFrame("To-Do List Application");
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add all panels to the mainPanel
        mainPanel.add(new MainMenuPanel(), "MainMenu");
        mainPanel.add(new ViewTasksPanel(), "ViewTasks");
        mainPanel.add(new AddTaskPanel(), "AddTask");
        mainPanel.add(new EditTaskPanel(), "EditTask");
        mainPanel.add(new DeleteTaskPanel(), "DeleteTask");

        frame.add(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 900);
        frame.setVisible(true);

        showPanel("MainMenu");
    }

    public void showPanel(String panelName) {
        // Ensure tasks are refreshed in all panels
        if (panelName.equals("ViewTasks")) {
            ((ViewTasksPanel) mainPanel.getComponent(1)).loadTasks();
        } else if (panelName.equals("EditTask")) {
            ((EditTaskPanel) mainPanel.getComponent(3)).loadTasks();
        } else if (panelName.equals("DeleteTask")) {
            ((DeleteTaskPanel) mainPanel.getComponent(4)).loadTasks();
        }
        cardLayout.show(mainPanel, panelName);
    }

    public static void main(String[] args) {
        new ToDoListApp();
    }

    class MainMenuPanel extends JPanel {

        MainMenuPanel() {
            setLayout(new GridLayout(8, 1));
            setBackground(Color.WHITE);  // Set background color for the full body

            Dimension buttonSize = new Dimension(300, 80);

            JButton viewTasksButton = new StyledButton("View Tasks", Color.BLUE, buttonSize);
            JButton addTaskButton = new StyledButton("Add Task", Color.lightGray, buttonSize);
            JButton editTaskButton = new StyledButton("Edit Task", Color.BLACK, buttonSize);
            JButton deleteTaskButton = new StyledButton("Delete Task", Color.RED, buttonSize);

            viewTasksButton.addActionListener(e -> showPanel("ViewTasks"));
            addTaskButton.addActionListener(e -> showPanel("AddTask"));
            editTaskButton.addActionListener(e -> showPanel("EditTask"));
            deleteTaskButton.addActionListener(e -> showPanel("DeleteTask"));

            add(createButtonPanel(viewTasksButton));
            add(createButtonPanel(addTaskButton));
            add(createButtonPanel(editTaskButton));
            add(createButtonPanel(deleteTaskButton));
        }

        private JPanel createButtonPanel(JButton button) {
            JPanel panel = new JPanel();
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add margin
            panel.setLayout(new BorderLayout());
            panel.setOpaque(false);  // Make the panel transparent to show the background color of the parent panel
            panel.add(button, BorderLayout.CENTER);
            return panel;
        }
    }

    class ViewTasksPanel extends JPanel {

        private JTable taskTable;
        private DefaultTableModel taskTableModel;

        ViewTasksPanel() {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);  // Set background color for the full body

            taskTableModel = new DefaultTableModel(new Object[]{"Title", "Description"}, 0);
            taskTable = new JTable(taskTableModel);

            add(new JScrollPane(taskTable), BorderLayout.CENTER);

            JButton backButton = new StyledButton("Back", Color.BLUE, new Dimension(100, 40));

            backButton.addActionListener(e -> showPanel("MainMenu"));
            add(backButton, BorderLayout.SOUTH);
        }

        private void loadTasks() {
            taskTableModel.setRowCount(0);  // Clear existing data
            for (Task task : tasks) {
                taskTableModel.addRow(new Object[]{task.getTitle(), task.getDescription()});
            }
        }
    }

    class AddTaskPanel extends JPanel {

        AddTaskPanel() {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);  // Set background color for the full body
            JPanel inputPanel = new JPanel(new GridLayout(20, 10));
            inputPanel.setOpaque(false);  // Make the panel transparent to show the background color of the parent panel

            JLabel titleLabel = new JLabel("Task:");
            JTextField titleField = new JTextField();
            JLabel descriptionLabel = new JLabel("Description:");
            JTextArea descriptionArea = new JTextArea();

            JButton addButton = new StyledButton("Add Task", Color.BLUE, new Dimension(100, 40));
            addButton.addActionListener(e -> {
                String title = titleField.getText().trim();
                String description = descriptionArea.getText().trim();
                if (title.isEmpty() || description.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Title and Description cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                tasks.add(new Task(title, description));
                showPanel("MainMenu");
            });

            inputPanel.add(titleLabel);
            inputPanel.add(titleField);
            inputPanel.add(descriptionLabel);
            inputPanel.add(new JScrollPane(descriptionArea));

            add(inputPanel, BorderLayout.CENTER);
            add(addButton, BorderLayout.SOUTH);
        }
    }

    class EditTaskPanel extends JPanel {

        private JList<Task> taskList;
        private DefaultListModel<Task> taskListModel;

        EditTaskPanel() {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);  // Set background color for the full body

            taskListModel = new DefaultListModel<>();
            taskList = new JList<>(taskListModel);

            taskList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && !taskList.isSelectionEmpty()) {
                    Task selectedTask = taskList.getSelectedValue();
                    editTask(selectedTask);
                }
            });

            add(new JScrollPane(taskList), BorderLayout.CENTER);

            JButton backButton = new StyledButton("Back", Color.BLUE, new Dimension(100, 40));
            backButton.addActionListener(e -> showPanel("MainMenu"));
            add(backButton, BorderLayout.SOUTH);
        }

        private void loadTasks() {
            taskListModel.clear();
            for (Task task : tasks) {
                taskListModel.addElement(task);
            }
        }

        private void editTask(Task task) {
            String newTitle = JOptionPane.showInputDialog(this, "Edit Title", task.getTitle());
            if (newTitle != null && !newTitle.trim().isEmpty()) {
                task.setTitle(newTitle.trim());
            }

            String newDescription = JOptionPane.showInputDialog(this, "Edit Description", task.getDescription());
            if (newDescription != null && !newDescription.trim().isEmpty()) {
                task.setDescription(newDescription.trim());
            }

            loadTasks();
        }
    }

    class DeleteTaskPanel extends JPanel {

        private JList<Task> taskList;
        private DefaultListModel<Task> taskListModel;

        DeleteTaskPanel() {
            setLayout(new BorderLayout());
            setBackground(Color.LIGHT_GRAY);  // Set background color for the full body

            taskListModel = new DefaultListModel<>();
            taskList = new JList<>(taskListModel);

            JButton deleteButton = new StyledButton("Delete Selected Task", Color.BLUE, new Dimension(200, 40));
            deleteButton.addActionListener(e -> {
                Task selectedTask = taskList.getSelectedValue();
                if (selectedTask != null) {
                    tasks.remove(selectedTask);
                    loadTasks();
                }
            });

            add(new JScrollPane(taskList), BorderLayout.CENTER);
            add(deleteButton, BorderLayout.NORTH);

            JButton backButton = new StyledButton("Back", Color.BLUE, new Dimension(100, 40));
            backButton.addActionListener(e -> showPanel("MainMenu"));
            add(backButton, BorderLayout.SOUTH);
        }

        private void loadTasks() {
            taskListModel.clear();
            for (Task task : tasks) {
                taskListModel.addElement(task);
            }
        }
    }

    class StyledButton extends JButton {

        StyledButton(String text, Color bgColor, Dimension size) {
            super(text);
            setFont(new Font("Arial", Font.PLAIN, 16));
            setBackground(bgColor); // Set background color
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setPreferredSize(size); // Set width and height
            // Set padding (top, left, bottom, right)
            setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        }
    }

}
