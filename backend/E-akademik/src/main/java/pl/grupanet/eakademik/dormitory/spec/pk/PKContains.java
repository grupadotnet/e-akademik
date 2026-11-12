package pl.grupanet.eakademik.dormitory.spec.pk;

import lombok.Getter;

@Getter
public enum PKContains {
    POLITECHNIKA_KRAKOWSKA("Politechnika Krakowska");

    private final String universityName;

    PKContains(String universityName) {
        this.universityName = universityName;
    }
}