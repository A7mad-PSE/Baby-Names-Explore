import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
public class NameDataLoader {
    public static MyList<BabyName> loadFromFolder(String folderPath) {
        MyList<BabyName> list = new MyList<>();

        for (int year = BabyName.START_YEAR; year <= BabyName.END_YEAR; year++) {
            File file = new File(folderPath, "yob" + year + ".txt");
            if (!file.exists()) {
                System.err.println("Missing file (skipped): " + file.getAbsolutePath());
                continue;
            }
            loadYearFile(file, year, list);
        }

        return list;
    }

    private static void loadYearFile(File file, int year, MyList<BabyName> list) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty()) continue;

                ParsedRecord record = parseLine(line);
                if (record == null) {
                    System.err.println("Malformed line " + lineNum + " in " + file.getName() + ": " + line);
                    continue;
                }

                BabyName existing = find(list, record.name, record.gender);
                if (existing == null) {
                    BabyName baby = new BabyName(record.name, record.gender);
                    baby.setCount(year, record.count);
                    list.add(baby);
                } else {
                    existing.setCount(year, record.count);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read " + file.getAbsolutePath() + ": " + e.getMessage());
        }
    }
    private static ParsedRecord parseLine(String line) {
        String[] parts = line.split(",");
        if (parts.length != 3) return null;

        String name = parts[0].trim();
        if (name.isEmpty()) return null;

        String genderStr = parts[1].trim();
        if (genderStr.length() != 1) return null;
        char gender = Character.toUpperCase(genderStr.charAt(0));
        if (gender != 'M' && gender != 'F') return null;

        int count;
        try {
            count = Integer.parseInt(parts[2].trim());
        } catch (NumberFormatException e) {
            return null;
        }
        if (count < 0) return null;

        return new ParsedRecord(name, gender, count);
    }

    private static BabyName find(MyList<BabyName> list, String name, char gender) {
        int index = list.indexOf(new BabyName(name, gender));
        if (index < 0) return null;
        return list.get(index);
    }

    private static class ParsedRecord {
        final String name;
        final char gender;
        final int count;

        ParsedRecord(String name, char gender, int count) {
            this.name = name;
            this.gender = gender;
            this.count = count;
        }
    }
}
