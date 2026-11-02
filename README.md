<div align="center">

# Baby Names Explorer

**Explore and analyze 6 years of US baby names — 100% local, built with pure JavaFX & custom data structures.**

A desktop JavaFX application that loads, validates, and merges US baby-name data (`yob2020.txt` – `yob2025.txt`), combining records by name and gender, and providing powerful browsing, searching, and statistical analysis.

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/GUI-JavaFX-0076D6?logo=java&logoColor=white)
![Data Structures](https://img.shields.io/badge/Core-Custom%20MyList-success)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

<img src="assets/demo.png" alt="Baby Names Explorer UI Preview" width="720">

</div>

## How It Works

```mermaid
flowchart LR
    A[Data Files yob2020-2025] --> B[NameDataLoader]
    B --> C[Custom MyList<BabyName>]
    C --> D[NameService]
    D --> E[Tab 1: Browse & Filter]
    D --> F[Tab 2: Name Details & Peaks]
    D --> G[Tab 3: Top 10 & Comparisons]
