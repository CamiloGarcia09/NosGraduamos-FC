package co.edu.uco.infrastructure.adapter.secondary.repository.data;

public interface DataMapper <D, A> {
    D mapperData(A model);
    A mapperModel(D data);
}