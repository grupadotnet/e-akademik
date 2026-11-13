    package pl.grupanet.eakademik.dormitory.spec.pk;

    import lombok.Getter;

    @Getter
    public enum UniversityNames {
        POLITECHNIKA_KRAKOWSKA("Politechnika Krakowska");

        private final String universityName;

        UniversityNames(String universityName) {
            this.universityName = universityName;
        }
    }