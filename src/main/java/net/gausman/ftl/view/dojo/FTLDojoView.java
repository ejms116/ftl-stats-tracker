package net.gausman.ftl.view.dojo;

import net.gausman.ftl.data.Database;
import net.gausman.ftl.model.SaveMetadata;
import net.gausman.ftl.model.table.DojoSaveTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;

public class FTLDojoView extends JFrame {
    private static final Logger log = LoggerFactory.getLogger(FTLDojoView.class);
    private Database db;
    private DojoSaveTableModel tableModel;
    private JTable table;
    private TableRowSorter<DojoSaveTableModel> sorter;
    private Path dojoDir;
    private File continueSaveFile;

    public FTLDojoView(Path dojoDir, File saveFile){
        this.dojoDir = dojoDir;
        this.continueSaveFile = saveFile;
        try {
            this.db = new Database(dojoDir);

            List<SaveMetadata> saves = db.getAllSaves();
            tableModel = new DojoSaveTableModel(saves);

            tableModel.addTableModelListener(e -> {
                int row = e.getFirstRow();
                int col = e.getColumn();

                if (row < 0 || col < 0) return;

                SaveMetadata changedSave = tableModel.getSaveAt(row);

                try {
                    db.updateMetadata(changedSave);
                } catch (SQLException ex){
                    JOptionPane.showMessageDialog(this,
                            "Error saving changes:\n" + ex.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            table = new JTable(tableModel);

            setTitle("FTL Dojo");
            setSize(1200, 1000);

            initUI();

            setVisible(true);
        } catch (SQLException e){

        }

    }

    private void initUI() {
        setLayout(new BorderLayout());

        // --- Tool Bar ---
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false); // prevent dragging off the window
        add(toolbar, BorderLayout.NORTH);

        // --- Search Bar ---
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() {
                String text = searchField.getText();
                if (text.trim().isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });

//        add(searchField, BorderLayout.AFTER_LAST_LINE);
        toolbar.add(searchField);


        JButton loadButton = new JButton("Load");

        toolbar.add(loadButton);


        // --- Table ---
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        add(new JScrollPane(table), BorderLayout.CENTER);


        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] columnWidths = {350, 120, 60, 60, 500}; // adjust per column
        for (int i = 0; i < columnWidths.length; i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setMinWidth(columnWidths[i]);
            column.setPreferredWidth(columnWidths[i]);
        }

        loadButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;

            int modelRow = table.convertRowIndexToModel(row);
            SaveMetadata metadata = tableModel.getSaveAt(modelRow);
            Path selectedSave = dojoDir.resolve("saves").resolve(metadata.getFilename());

            copySelectedSave(selectedSave);
        });
    }

    private void copySelectedSave(Path selectedFilePath){
        try {
            Files.copy(selectedFilePath, continueSaveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            log.info("Save copied successfully.");
        } catch (Exception e) {
            log.error("Failed to copy save file.");
        }
    }

}
