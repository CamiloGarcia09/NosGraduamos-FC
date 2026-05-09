package co.edu.uco.core.domain.port.out;

import java.util.List;

public record Response<T>(List<T> data, List<String> errors) {

    public Response(List<T>  data) {
        this(data, List.of());
    }
}
