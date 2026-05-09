package co.edu.uco.core.application.mapper.entity;

public interface DataMapper<E,D,T> {
    D mapperDomain(E entity);
    E mapperData(D data);
    T mapperDTO(E entity);
}