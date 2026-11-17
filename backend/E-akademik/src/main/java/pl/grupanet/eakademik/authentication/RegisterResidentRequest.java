package pl.grupanet.eakademik.authentication;

record RegisterResidentRequest(
        String universityName,
        String dormitoryName,
        String roomNumber,
        String fullName,
        String email,
        String password
) {}
