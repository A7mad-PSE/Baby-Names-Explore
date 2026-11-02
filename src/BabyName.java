public class BabyName {
    public static final int START_YEAR = 2020;
    public static final int END_YEAR = 2025;
    public static final int YEAR_COUNT = END_YEAR - START_YEAR + 1;

    private String name;
    private char gender;
    private YearCount[] yearlyCounts;

    public BabyName(String name, char gender) {
        this.name = name;
        this.gender = gender;
        this.yearlyCounts = new YearCount[YEAR_COUNT];
        for (int i = 0; i < YEAR_COUNT; i++) {
            yearlyCounts[i] = new YearCount(START_YEAR + i, 0);
        }
    }

    public BabyName(String name, char gender, YearCount[] yearlyCounts) {
        this.name = name;
        this.gender = gender;
        this.yearlyCounts = yearlyCounts;
    }

    private int indexOfYear(int year) {
        if (year < START_YEAR || year > END_YEAR) {
            throw new IllegalArgumentException("Year must be between " + START_YEAR + " and " + END_YEAR);
        }
        return year - START_YEAR;
    }

    public int getCount(int year) {
        return yearlyCounts[indexOfYear(year)].getCount();
    }

    public void setCount(int year, int count) {
        yearlyCounts[indexOfYear(year)].setCount(count);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public YearCount[] getYearlyCounts() {
        return yearlyCounts;
    }

    public void setYearlyCounts(YearCount[] yearlyCounts) {
        this.yearlyCounts = yearlyCounts;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BabyName other = (BabyName) obj;
        return gender == other.gender
                && name != null
                && name.equalsIgnoreCase(other.name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" (").append(gender).append("): ");
        for (int i = 0; i < yearlyCounts.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(yearlyCounts[i]);
        }
        return sb.toString();
    }
}
