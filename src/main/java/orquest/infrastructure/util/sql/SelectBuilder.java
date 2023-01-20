package orquest.infrastructure.util.sql;

import java.util.LinkedList;
import java.util.List;

public class SelectBuilder {

    private List<String> queryFields;
    private List<String> queryTables;
    private List<String> queryWhere;
    private List<String> queryOrder;
    private List<String> queryGroup;
    private String queryLimit;

    public SelectBuilder() {}

    public void clear() {
        queryFields = null;
        queryTables = null;
        queryWhere = null;
        queryOrder = null;
        queryGroup = null;
        queryLimit = null;
    }

    public SelectBuilder field(String...field) {
        queryFields = checkAndAdd(queryFields, "Field", field);

        return this;
    }

    public SelectBuilder from(String...table) {
        queryTables = checkAndAdd(queryTables, "From", table);

        return this;
    }

    public SelectBuilder where(String...where) {
        queryWhere = checkAndAdd(queryWhere, "Where", where);

        return this;
    }

    public SelectBuilder order(String...order) {
        queryOrder = checkAndAdd(queryOrder, "Order", order);

        return this;
    }

    public SelectBuilder group(String...group) {
        queryGroup = checkAndAdd(queryGroup, "Group", group);

        return this;
    }

    public SelectBuilder limit(String limit) {
        queryLimit = limit;

        return this;
    }

    private List<String> checkAndAdd(List<String> list, String error, String...values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException(error + " must have a value");
        }

        if (list == null) {
            list = new LinkedList<>();
        }

        list.addAll(List.of(values));

        return list;
    }

    public String toString() {
        if (queryFields == null || queryTables == null) {
            throw new IllegalArgumentException("Missing fields or from");
        }

        StringBuilder builder =
            new StringBuilder("SELECT ")
                .append(String.join(", ", queryFields))
                .append(" FROM ")
                .append(String.join(", ", queryTables));

        if (queryWhere != null) {
            builder.append(" WHERE ").append(String.join(" AND ", queryWhere));
        }

        if (queryOrder != null) {
            builder.append(" ORDER BY ").append(String.join(", ", queryOrder));
        }

        if (queryGroup != null) {
            builder.append(" GROUP BY ").append(String.join(", ", queryGroup));
        }

        if (queryLimit != null) {
            builder.append(" LIMIT ").append(queryLimit);
        }

        return builder.toString();
    }
}
