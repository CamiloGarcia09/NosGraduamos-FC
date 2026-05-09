package co.edu.uco.core.domain.validator.page;

import co.edu.uco.core.application.catalog.strategy.inmemory.enums.DetailMessageEnum;
import co.edu.uco.core.domain.port.out.repository.SimplePageRequest;
import co.edu.uco.core.domain.validator.Validator;
import co.edu.uco.utils.exception.BusinessRuleException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SortColumnValidator implements Validator<SimplePageRequest> {
    private static final Map<Class<?>, SortColumnValidator> INSTANCE_CACHE = new ConcurrentHashMap<>();
    private final List<String> validColumns;
    private SortColumnValidator(Class<?> modelClass) {
        this.validColumns = Arrays.stream(modelClass.getDeclaredFields())
                .map(Field::getName)
                .toList();
    }
    public static SortColumnValidator getInstance(Class<?> modelClass) {
        return INSTANCE_CACHE.computeIfAbsent(modelClass, SortColumnValidator::new);
    }
    @Override
    public void validate(SimplePageRequest data) throws BusinessRuleException {
        if (!validColumns.contains(data.getColumnSort())) {
             throw BusinessRuleException.buildUserException(String.format(DetailMessageEnum.FUN_030.getContent(),
                     data.getColumnSort()));
        }
    }
}