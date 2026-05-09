package co.edu.uco.core.domain.aggregate;

public abstract class Entity<I> {
    private I id;
    protected void setId(I i) {this.id = i;}
    public I getId() {
            return id;
        }
}