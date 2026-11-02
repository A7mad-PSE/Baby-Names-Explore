import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
public class ExplorerGUI {
    private static final String DEFAULT_DATA_FOLDER = "/home/ahmad/Desktop/TEST2/Names+Data";

    private NameService service;
    private BabyName selected;

    private final Label statusLabel = new Label("Load a data folder to begin.");
    private final TableView<BabyName> browseTable = new TableView<>();
    private final Label matchCountLabel = new Label("Matches: 0");
    private final TextField searchField = new TextField();
    private final ComboBox<String> genderFilter = new ComboBox<>();
    private final CheckBox sortCheck = new CheckBox("Sort A–Z");

    private final Label detailNameLabel = new Label("—");
    private final TableView<YearCount> detailTable = new TableView<>();
    private final ComboBox<Integer> detailYearBox = new ComboBox<>();
    private final Label detailYearCountLabel = new Label("Count: —");
    private final Label detailPeakLabel = new Label("Peak: —");

    private final ComboBox<Integer> popularYearBox = new ComboBox<>();
    private final ComboBox<String> popularGenderBox = new ComboBox<>();
    private final Label popularResultLabel = new Label("—");
    private final TableView<BabyName> top10Table = new TableView<>();
    private final TextField compareNameField = new TextField();
    private final ComboBox<String> compareGenderBox = new ComboBox<>();
    private final ComboBox<Integer> compareYear1Box = new ComboBox<>();
    private final ComboBox<Integer> compareYear2Box = new ComboBox<>();
    private final Label compareResultLabel = new Label("—");
    private final Label genderTotalsLabel = new Label("—");

    public void build(Stage stage) {
        TabPane tabs = new TabPane();
        tabs.getTabs().addAll(
                tab("Browse Names", buildBrowseTab()),
                tab("Name Details", buildDetailsTab()),
                tab("Analysis", buildAnalysisTab())
        );

        Button loadBtn = new Button("Load Data Folder…");
        loadBtn.setOnAction(e -> chooseAndLoad(stage));

        HBox top = new HBox(10, loadBtn, statusLabel);
        top.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(tabs);

        stage.setTitle("Baby Names Explorer");
        stage.setScene(new Scene(root, 900, 600));
        stage.show();


        new Thread(() -> {
            MyList<BabyName> list = NameDataLoader.loadFromFolder(DEFAULT_DATA_FOLDER);
            Platform.runLater(() -> {
                service = new NameService(list);
                statusLabel.setText("Loaded " + list.size() + " name+gender records.");
                refreshBrowse();
                if (!browseTable.getItems().isEmpty()) {
                    browseTable.getSelectionModel().select(0);
                }
                showGenderTotals();
            });
        }, "data-loader").start();
    }

    private Tab tab(String title, javafx.scene.Node content) {
        Tab t = new Tab(title, content);
        t.setClosable(false);
        return t;
    }


    private VBox buildBrowseTab() {
        TableColumn<BabyName, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(220);

        TableColumn<BabyName, Character> genderCol = new TableColumn<>("Gender");
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        genderCol.setPrefWidth(80);

        browseTable.getColumns().add(nameCol);
        browseTable.getColumns().add(genderCol);
        browseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        browseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) showDetails(newV);
        });

        searchField.setPromptText("Search by name");
        genderFilter.getItems().addAll("All", "F", "M");
        genderFilter.setValue("All");

        Button applyBtn = new Button("Apply");
        applyBtn.setOnAction(e -> refreshBrowse());
        Button openDetailsBtn = new Button("Open Details");
        openDetailsBtn.setOnAction(e -> {
            BabyName sel = browseTable.getSelectionModel().getSelectedItem();
            if (sel == null) {
                alert(Alert.AlertType.INFORMATION, "Select a name in the table first.");
                return;
            }
            showDetails(sel);
            alert(Alert.AlertType.INFORMATION, "Opened details for " + sel.getName() + " (" + sel.getGender() + "). Switch to the Name Details tab.");
        });

        HBox controls = new HBox(10,
                new Label("Name:"), searchField,
                new Label("Gender:"), genderFilter,
                sortCheck, applyBtn, openDetailsBtn, matchCountLabel);
        controls.setPadding(new Insets(10));

        VBox box = new VBox(8, controls, browseTable);
        box.setPadding(new Insets(10));
        VBox.setVgrow(browseTable, javafx.scene.layout.Priority.ALWAYS);
        return box;
    }


    private VBox buildDetailsTab() {
        TableColumn<YearCount, Integer> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        TableColumn<YearCount, Integer> countCol = new TableColumn<>("Count");
        countCol.setCellValueFactory(new PropertyValueFactory<>("count"));
        detailTable.getColumns().add(yearCol);
        detailTable.getColumns().add(countCol);
        detailTable.setPrefHeight(200);

        for (int y = BabyName.START_YEAR; y <= BabyName.END_YEAR; y++) {
            detailYearBox.getItems().add(y);
        }
        detailYearBox.setValue(BabyName.START_YEAR);
        detailYearBox.setOnAction(e -> updateSelectedYearCount());

        HBox yearRow = new HBox(10, new Label("Selected year:"), detailYearBox, detailYearCountLabel);
        VBox box = new VBox(10,
                new Label("Selected name:"), detailNameLabel,
                new Label("Yearly counts:"), detailTable,
                yearRow, detailPeakLabel);
        box.setPadding(new Insets(10));
        return box;
    }


    private VBox buildAnalysisTab() {
        for (int y = BabyName.START_YEAR; y <= BabyName.END_YEAR; y++) {
            popularYearBox.getItems().add(y);
            compareYear1Box.getItems().add(y);
            compareYear2Box.getItems().add(y);
        }
        popularYearBox.setValue(BabyName.START_YEAR);
        compareYear1Box.setValue(2020);
        compareYear2Box.setValue(2025);
        popularGenderBox.getItems().addAll("All", "F", "M");
        popularGenderBox.setValue("All");
        compareGenderBox.getItems().addAll("F", "M");
        compareGenderBox.setValue("F");

        TableColumn<BabyName, String> nCol = new TableColumn<>("Name");
        nCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<BabyName, Character> gCol = new TableColumn<>("Gender");
        gCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        top10Table.getColumns().add(nCol);
        top10Table.getColumns().add(gCol);
        top10Table.setPrefHeight(220);

        Button popularBtn = new Button("Most Popular");
        popularBtn.setOnAction(e -> showMostPopular());
        Button top10Btn = new Button("Top 10");
        top10Btn.setOnAction(e -> showTop10());
        Button compareBtn = new Button("Compare Years");
        compareBtn.setOnAction(e -> showYearComparison());
        Button totalsBtn = new Button("Gender Totals");
        totalsBtn.setOnAction(e -> showGenderTotals());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Year:"), 0, 0);
        grid.add(popularYearBox, 1, 0);
        grid.add(new Label("Gender:"), 2, 0);
        grid.add(popularGenderBox, 3, 0);
        grid.add(popularBtn, 4, 0);
        grid.add(top10Btn, 5, 0);
        grid.add(popularResultLabel, 0, 1, 6, 1);
        grid.add(new Label("Top 10:"), 0, 2);
        grid.add(top10Table, 0, 3, 6, 1);

        grid.add(new Label("Name:"), 0, 4);
        grid.add(compareNameField, 1, 4);
        grid.add(new Label("Gender:"), 2, 4);
        grid.add(compareGenderBox, 3, 4);
        grid.add(new Label("Year 1:"), 0, 5);
        grid.add(compareYear1Box, 1, 5);
        grid.add(new Label("Year 2:"), 2, 5);
        grid.add(compareYear2Box, 3, 5);
        grid.add(compareBtn, 4, 5);
        grid.add(compareResultLabel, 0, 6, 6, 1);

        grid.add(totalsBtn, 0, 7);
        grid.add(genderTotalsLabel, 1, 7, 5, 1);

        return new VBox(grid);
    }

    private void chooseAndLoad(Stage stage) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select folder with yob2020.txt … yob2025.txt");
        File defaultParent = new File(DEFAULT_DATA_FOLDER).getParentFile();
        chooser.setInitialDirectory(defaultParent != null ? defaultParent : new File(System.getProperty("user.home"), "Desktop"));
        File folder = chooser.showDialog(stage);
        if (folder == null) return;

        statusLabel.setText("Loading from " + folder.getAbsolutePath() + " …");
        new Thread(() -> {
            MyList<BabyName> list = NameDataLoader.loadFromFolder(folder.getAbsolutePath());
            Platform.runLater(() -> {
                service = new NameService(list);
                statusLabel.setText("Loaded " + list.size() + " name+gender records.");
                refreshBrowse();
                if (!browseTable.getItems().isEmpty()) {
                    browseTable.getSelectionModel().select(0);
                }
                showGenderTotals();
            });
        }, "data-loader").start();
    }

    private void refreshBrowse() {
        if (service == null) {
            alert(Alert.AlertType.WARNING, "Load a data folder first.");
            return;
        }
        char g = genderChar(genderFilter.getValue());
        MyList<BabyName> filtered = service.filter(g, searchField.getText());
        if (sortCheck.isSelected()) {
            filtered = service.sortAlphabetically(filtered);
        }
        ObservableList<BabyName> rows = FXCollections.observableArrayList();
        for (int i = 0; i < filtered.size(); i++) {
            rows.add(filtered.get(i));
        }
        browseTable.setItems(rows);
        matchCountLabel.setText("Matches: " + filtered.size());
    }

    private void showDetails(BabyName baby) {
        selected = baby;
        detailNameLabel.setText(baby.getName() + " (" + baby.getGender() + ")");
        ObservableList<YearCount> years = FXCollections.observableArrayList();
        YearCount[] arr = baby.getYearlyCounts();
        for (YearCount yc : arr) years.add(yc);
        detailTable.setItems(years);
        updateSelectedYearCount();

        YearCount peak = service.namePeak(baby.getName(), baby.getGender());
        if (peak != null) {
            detailPeakLabel.setText("Peak: " + peak.getYear() + " with count " + peak.getCount());
        }
    }

    private void updateSelectedYearCount() {
        if (selected == null || detailYearBox.getValue() == null) return;
        int year = detailYearBox.getValue();
        detailYearCountLabel.setText("Count: " + selected.getCount(year));
    }

    private void showMostPopular() {
        if (!ready()) return;
        try {
            BabyName best = service.mostPopular(popularYearBox.getValue(), genderChar(popularGenderBox.getValue()));
            if (best == null) {
                popularResultLabel.setText("No matching record found.");
            } else {
                int y = popularYearBox.getValue();
                popularResultLabel.setText("Most popular: " + best.getName() + " (" + best.getGender()
                        + ") — count " + best.getCount(y));
            }
        } catch (IllegalArgumentException ex) {
            alert(Alert.AlertType.ERROR, ex.getMessage());
        }
    }

    private void showTop10() {
        if (!ready()) return;
        try {
            MyList<BabyName> top = service.top10(popularYearBox.getValue(), genderChar(popularGenderBox.getValue()));
            ObservableList<BabyName> rows = FXCollections.observableArrayList();
            for (int i = 0; i < top.size(); i++) rows.add(top.get(i));
            top10Table.setItems(rows);
            // show counts in status via name column isn't enough — add tooltip via result label
            StringBuilder sb = new StringBuilder("Top " + top.size() + ": ");
            int y = popularYearBox.getValue();
            for (int i = 0; i < top.size(); i++) {
                if (i > 0) sb.append(", ");
                BabyName b = top.get(i);
                sb.append(b.getName()).append("=").append(b.getCount(y));
            }
            popularResultLabel.setText(sb.toString());
        } catch (IllegalArgumentException ex) {
            alert(Alert.AlertType.ERROR, ex.getMessage());
        }
    }

    private void showYearComparison() {
        if (!ready()) return;
        String name = compareNameField.getText();
        if (name == null || name.trim().isEmpty()) {
            alert(Alert.AlertType.WARNING, "Enter a name to compare.");
            return;
        }
        char g = compareGenderBox.getValue().charAt(0);
        try {
            int[] r = service.yearComparison(name.trim(), g, compareYear1Box.getValue(), compareYear2Box.getValue());
            if (r == null) {
                compareResultLabel.setText("No matching record found.");
                return;
            }
            compareResultLabel.setText(
                    compareYear1Box.getValue() + "=" + r[0]
                            + ", " + compareYear2Box.getValue() + "=" + r[1]
                            + ", difference=" + r[2]);
        } catch (IllegalArgumentException ex) {
            alert(Alert.AlertType.ERROR, ex.getMessage());
        }
    }

    private void showGenderTotals() {
        if (service == null) return;
        int[] t = service.genderTotals();
        genderTotalsLabel.setText("Distinct F: " + t[0] + "   Distinct M: " + t[1]);
    }

    private boolean ready() {
        if (service == null) {
            alert(Alert.AlertType.WARNING, "Load a data folder first.");
            return false;
        }
        return true;
    }

    private char genderChar(String value) {
        if (value == null || value.equalsIgnoreCase("All")) return 'A';
        return Character.toUpperCase(value.charAt(0));
    }

    private void alert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
