package pl.grupanet.eakademik.controllers;

public enum ApiVersions {
    V1;

    public String getPath(){
        return ApiConstants.API_URL+this+"/";
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
