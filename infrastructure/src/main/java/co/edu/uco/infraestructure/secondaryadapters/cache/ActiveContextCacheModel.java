package co.edu.uco.infraestructure.secondaryadapters.cache;

final class ActiveContextCacheModel {

    private String id;
    private String externalIdentityId;
    private String organizationId;
    private String applicationId;
    private String environmentId;
    private String updatedAt;

    public ActiveContextCacheModel() {
    }

    ActiveContextCacheModel(final String id, final String externalIdentityId, final String organizationId,
                            final String applicationId, final String environmentId, final String updatedAt) {
        this.id = id;
        this.externalIdentityId = externalIdentityId;
        this.organizationId = organizationId;
        this.applicationId = applicationId;
        this.environmentId = environmentId;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getExternalIdentityId() {
        return externalIdentityId;
    }

    public void setExternalIdentityId(final String externalIdentityId) {
        this.externalIdentityId = externalIdentityId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(final String organizationId) {
        this.organizationId = organizationId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(final String applicationId) {
        this.applicationId = applicationId;
    }

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(final String environmentId) {
        this.environmentId = environmentId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
