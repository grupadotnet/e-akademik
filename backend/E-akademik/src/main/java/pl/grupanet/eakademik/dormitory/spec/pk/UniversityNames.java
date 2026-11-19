package pl.grupanet.eakademik.dormitory.spec.pk;

public enum UniversityNames {
    POLITECHNIKA_KRAKOWSKA("Politechnika Krakowska"),
    AGH("Akademia Górniczo-Hutnicza");
    private final String universityName;

    UniversityNames(String universityName) {
        this.universityName = universityName;
    }

    @Override
    public String toString() {
        return universityName;
    }
}