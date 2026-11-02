# Baby Names Explorer

**JavaFX desktop app that loads US baby-name data (`yob2020.txt` – `yob2025.txt`), combines records by name + gender, and provides browsing and analysis across 6 years.**

## Features

- **Tab 1 – Browse Names**: display names in a `TableView`, search by name, filter by gender (All/F/M), sort A–Z, open details, show match count.
- **Tab 2 – Name Details**: display selected name + gender, all 6 yearly counts, select a year to view its count, show peak year/count.
- **Tab 3 – Analysis**: most popular name, top 10 names, year comparison (counts + difference), gender totals.

## Data

- Folder: `/home/ahmad/Desktop/TEST2/Names+Data`
- Files: `yob2020.txt` … `yob2025.txt`, each line: `Name,Gender,Count`

## Classes

- `Main` – JavaFX entry point.
- `ExplorerGUI` – JavaFX UI (tabs, controls, event handlers).
- `NameDataLoader` – loads + validates + merges data files.
- `NameService` – search, filter, sort, top10, peak, comparison, totals.
- `BabyName`, `YearCount` – data models.
- `MyList` – custom generic array-based list (primary storage).

## Run

- IntelliJ: run `Main`.
- CLI: `java --module-path <javafx-lib> --add-modules javafx.controls,javafx.graphics -cp out/production/Data-Str-1 Main`

## Note

- Custom generic `MyList<E>` used as the main dataset storage (no ArrayList/LinkedList for the core data).
- Sorting uses custom insertion sort; top-10 uses selection sort.
