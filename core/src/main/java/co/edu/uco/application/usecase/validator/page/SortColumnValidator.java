package co.edu.uco.application.usecase.validator.page;

import co.edu.uco.application.common.catalog.CatalogPortStaticRef;
import co.edu.uco.application.secondaryports.repository.SimplePageRequest;
import co.edu.uco.application.usecase.validator.Validator;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;

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
             throw BusinessRuleException.buildUserException(String.format(
                     CatalogPortStaticRef.getMessage("FUN_030"),
                     data.getColumnSort()));
        }
    }
}