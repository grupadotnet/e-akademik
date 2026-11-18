package pl.grupanet.eakademik.dormitory.spec.pk;

public enum UniversityNames {
    POLITECHNIKA_KRAKOWSKA("Politechnika Krakowska");

    private final String universityName;

    UniversityNames(String universityName) {
        this.universityName = universityName;
    }

    @Override
    public String toString() {
        return universityName;
    }
}