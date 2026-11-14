package pl.grupanet.eakademik.authentication;

record RegisterStaffRequest(
        String universityName,
        String dormitoryName,
        String fullName,
        String email,
        String password
) {}
