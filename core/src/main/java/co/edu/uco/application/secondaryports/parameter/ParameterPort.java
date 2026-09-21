package co.edu.uco.application.secondaryports.parameter;

import co.edu.uco.application.secondaryports.entity.ParameterData;

import java.util.Optional;

public interface ParameterPort {

    Optional<ParameterData> findParameter(String name);
}