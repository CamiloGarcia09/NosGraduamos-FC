package co.edu.uco.utils.helper;

public final class UtilPagination {
    private UtilPagination() {}
    public static int toZeroBasedPage(int page) {
        return page - 1;
    }
    public static int toOneBasedPage(int page) {
        return page + 1;
    }
}