package org.ieselgrao.hibernatepractica.model;

public class UniverseException extends IllegalArgumentException {

    // Constants for error messages
    public static final String INVALID_NAME = "[ERROR] Name cannot be null or empty";
    public static final String INVALID_NUMBER_OF_MOONS = "[ERROR] Number of moons cannot be negative";
    public static final String INVALID_MASS = "[ERROR] Mass cannot be less than 10e23 kg";
    public static final String INVALID_RADIUS = "[ERROR] Radius cannot be less than 500 km";
    public static final String INVALID_GRAVITY = "[ERROR] Gravity cannot be negative or zero";
    public static final String INVALID_DISTANCE = "[ERROR] Distance cannot be negative or zero";
    public static final String INVALID_LAST_ALBEDO_MEASUREMENT = "[ERROR] Last albedo measurement cannot be null or in the future";
    public static final String INVALID_PLANET_LIST = "[ERROR] Planet list cannot be empty or null";

    public UniverseException(String message) {
        super(message);
    }
}
