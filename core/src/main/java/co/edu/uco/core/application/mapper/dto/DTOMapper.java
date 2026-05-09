package co.edu.uco.core.application.mapper.dto;

public interface DTOMapper<T,D> {
    D mapperDomain(T dto);
    T mapperDTO(D domain);
}