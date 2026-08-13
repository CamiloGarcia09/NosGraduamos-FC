package co.edu.uco.core.domain.validator.page;

import co.edu.uco.core.application.dto.page.PageRequestDTO;
import co.edu.uco.core.domain.port.out.catalog.CatalogPort;
import co.edu.uco.core.domain.validator.Validator;
import co.edu.uco.utils.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import static co.edu.uco.core.CrosswordsConstant.*;
import static co.edu.uco.utils.helper.UtilObject.isNullObject;
import static co.edu.uco.utils.helper.UtilText.*;

@Component
public final class PageRequestTypeValidator implements Validator<PageRequestDTO> {
    private final CatalogPort catalogPort;
    public PageRequestTypeValidator(CatalogPort catalogPort) {
        this.catalogPort = catalogPort;
    }
    @Override
    public void validate(PageRequestDTO data) throws BusinessRuleException {
        if (isNullObject(data)) {
            return;
        }
        validatePage(data.getPage());
        validateSize(data.getSize());
        validateColumnSort(data.getColumnSort());
        validateSort(data.getSort());
    }
    private void validatePage(String page) {
        if (!isNullObject(page) && !page.isEmpty() && !validMatch(page, ONLY_NUMBERS)) {
            throw BusinessRuleException.buildUserException(String.format(catalogPort.getMessage("FUN_033"), PAGE_ATTRIBUTE));
        }
    }
    private void validateSize(String size) {
        if (!isNullObject(size) && !size.isEmpty() && !validMatch(size, ONLY_NUMBERS)) {
            throw BusinessRuleException.buildUserException(String.format(catalogPort.getMessage("FUN_033"), SIZE_ATTRIBUTE));
        }
    }
    private void validateColumnSort(String columnSort) {
        if (!isNullObject(columnSort) && !columnSort.isEmpty() && !validMatch(columnSort, ONLY_LETTERS)) {
            throw BusinessRuleException.buildUserException(
                    String.format(catalogPort.getMessage("FUN_043"), COLUMN_SORT_ATTRIBUTE));
        }
    }
    private void validateSort(String sort) {
        if (!isNullObject(sort) && !sort.isEmpty() && !validMatch(sort, ONLY_LETTERS)) {
            throw BusinessRuleException.buildUserException(
                    String.format(catalogPort.getMessage("FUN_043"), SORT_ATTRIBUTE));
        }
    }
}