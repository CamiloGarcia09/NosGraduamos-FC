package co.edu.uco.application.usecase.validator.context;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;

public final class SelectActiveContextCompositeValidator {

    private final SelectActiveContextIdentifiersRule identifiersRule;
    private final SelectActiveContextHierarchyRule hierarchyRule;

    public SelectActiveContextCompositeValidator(final SelectActiveContextIdentifiersRule identifiersRule,
                                                 final SelectActiveContextHierarchyRule hierarchyRule) {
        this.identifiersRule = identifiersRule;
        this.hierarchyRule = hierarchyRule;
    }

    public void validate(final SelectActiveContextDTO context) {
        identifiersRule.validate(context);
        hierarchyRule.validate(context);
    }
}
