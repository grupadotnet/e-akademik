package org.commons;

import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;

@Getter
public class DormitoryId {
    public static class Builder {
        private final String universityName;
        private final String dormitoryName;

        public Builder(@NonNull String universityName, @NonNull String dormitoryName) {
            this.universityName = universityName;
            this.dormitoryName = dormitoryName;
        }

        public DormitoryId build() {
            return new DormitoryId(new Name(universityName), new Name(dormitoryName));
        }
    }

    private final Name universityName;
    private final Name dormitoryName;

    public DormitoryId(@NonNull final Name universityName, @NonNull final Name dormitoryName) {
        this.universityName = universityName;
        this.dormitoryName = dormitoryName;
    }

    @Override
    public String toString() {
        return universityName + "-" + dormitoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DormitoryId that = (DormitoryId) o;
        return Objects.equals(universityName, that.universityName) && Objects.equals(dormitoryName, that.dormitoryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(universityName, dormitoryName);
    }
}
