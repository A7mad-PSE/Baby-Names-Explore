public class NameService {
    private final MyList<BabyName> names;

    public NameService(MyList<BabyName> names) {
        this.names = names;
    }

    public MyList<BabyName> getAll() {
        return names;
    }

    public BabyName search(String name, char gender) {
        if (name == null) return null;
        int index = names.indexOf(new BabyName(name.trim(), Character.toUpperCase(gender)));
        if (index < 0) return null;
        return names.get(index);
    }

    public MyList<BabyName> filter(char genderFilter, String nameQuery) {
        MyList<BabyName> result = new MyList<>();
        char g = Character.toUpperCase(genderFilter);
        String q = nameQuery == null ? "" : nameQuery.trim().toLowerCase();

        for (int i = 0; i < names.size(); i++) {
            BabyName b = names.get(i);
            if (g == 'M' || g == 'F') {
                if (b.getGender() != g) continue;
            }
            if (!q.isEmpty() && !b.getName().toLowerCase().contains(q)) continue;
            result.add(b);
        }
        return result;
    }

    public MyList<BabyName> sortAlphabetically(MyList<BabyName> source) {
        int n = source.size();
        BabyName[] arr = new BabyName[n];
        for (int i = 0; i < n; i++) {
            arr[i] = source.get(i);
        }

        for (int i = 1; i < n; i++) {
            BabyName key = arr[i];
            int j = i - 1;
            while (j >= 0 && compareNames(arr[j], key) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }

        MyList<BabyName> sorted = new MyList<>();
        for (int i = 0; i < n; i++) {
            sorted.add(arr[i]);
        }
        return sorted;
    }

    private int compareNames(BabyName a, BabyName b) {
        int byName = a.getName().compareToIgnoreCase(b.getName());
        if (byName != 0) return byName;
        return Character.compare(a.getGender(), b.getGender()); // F before M
    }

    public BabyName mostPopular(int year, char genderFilter) {
        validateYear(year);
        char g = Character.toUpperCase(genderFilter);
        BabyName best = null;
        int bestCount = -1;

        for (int i = 0; i < names.size(); i++) {
            BabyName b = names.get(i);
            if (g == 'M' || g == 'F') {
                if (b.getGender() != g) continue;
            }
            int count = b.getCount(year);
            if (count > bestCount) {
                bestCount = count;
                best = b;
            }
        }
        return best;
    }
    public MyList<BabyName> top10(int year, char genderFilter) {
        validateYear(year);
        MyList<BabyName> filtered = filter(genderFilter, "");
        int n = filtered.size();
        BabyName[] arr = new BabyName[n];
        for (int i = 0; i < n; i++) {
            arr[i] = filtered.get(i);
        }

        for (int i = 0; i < n - 1; i++) {
            int max = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j].getCount(year) > arr[max].getCount(year)) {
                    max = j;
                }
            }
            BabyName tmp = arr[i];
            arr[i] = arr[max];
            arr[max] = tmp;
        }

        MyList<BabyName> top = new MyList<>();
        int limit = Math.min(10, n);
        for (int i = 0; i < limit; i++) {
            top.add(arr[i]);
        }
        return top;
    }
    public YearCount namePeak(String name, char gender) {
        BabyName b = search(name, gender);
        if (b == null) return null;

        int peakYear = BabyName.START_YEAR;
        int peakCount = b.getCount(peakYear);
        for (int y = BabyName.START_YEAR + 1; y <= BabyName.END_YEAR; y++) {
            int c = b.getCount(y);
            if (c > peakCount) {
                peakCount = c;
                peakYear = y;
            }
        }
        return new YearCount(peakYear, peakCount);
    }

    public int[] yearComparison(String name, char gender, int year1, int year2) {
        validateYear(year1);
        validateYear(year2);
        BabyName b = search(name, gender);
        if (b == null) return null;
        int c1 = b.getCount(year1);
        int c2 = b.getCount(year2);
        return new int[]{c1, c2, c1 - c2};
    }
    public int[] genderTotals() {
        int f = 0;
        int m = 0;
        for (int i = 0; i < names.size(); i++) {
            char g = names.get(i).getGender();
            if (g == 'F') f++;
            else if (g == 'M') m++;
        }
        return new int[]{f, m};
    }

    private void validateYear(int year) {
        if (year < BabyName.START_YEAR || year > BabyName.END_YEAR) {
            throw new IllegalArgumentException(
                    "Year must be between " + BabyName.START_YEAR + " and " + BabyName.END_YEAR);
        }
    }
}
