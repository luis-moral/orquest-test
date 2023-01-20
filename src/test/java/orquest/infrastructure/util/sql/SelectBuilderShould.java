package orquest.infrastructure.util.sql;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SelectBuilderShould {

    @Test
    public void clear_all_fields() {
        SelectBuilder query =
            new SelectBuilder()
                .field("user.id", "user.display_name")
                .from("user", "tag")
                .where("user.id = user_tag.user_id", "user.id = 3", "tag.id = user_tag.tag_id")
                .order("user.id ASC", "tag.color")
                .group("user.id")
                .limit("1");
        query.clear();

        Assertions.assertThat(query).usingRecursiveComparison().isEqualTo(new SelectBuilder());
    }

    @Test
    public void build_sql_with_no_filters() {
        SelectBuilder query = new SelectBuilder().field("user.id", "user.display_name").from("user");

        Assertions.assertThat(query.toString()).isEqualTo("SELECT user.id, user.display_name FROM user");

        query.from("tag");
        Assertions.assertThat(query.toString()).isEqualTo("SELECT user.id, user.display_name FROM user, tag");
    }

    @Test
    public void build_sql_where() {
        SelectBuilder query =
            new SelectBuilder()
                .field("user.id", "user.display_name")
                .from("user", "tag")
                .where("user.id = user_tag.user_id", "user.id = 3", "tag.id = user_tag.tag_id");

        Assertions
            .assertThat(query.toString())
            .isEqualTo(
                "SELECT user.id, user.display_name FROM user, tag WHERE user.id = user_tag.user_id AND user.id = 3 AND tag.id = user_tag.tag_id"
            );
    }

    @Test
    public void build_sql_order() {
        SelectBuilder query =
            new SelectBuilder()
                .field("user.id", "user.display_name")
                .from("user", "tag")
                .order("user.id ASC", "tag.color");

        Assertions
            .assertThat(query.toString())
            .isEqualTo("SELECT user.id, user.display_name FROM user, tag ORDER BY user.id ASC, tag.color");
    }

    @Test
    public void build_sql_group_by() {
        SelectBuilder query =
            new SelectBuilder()
                .field("user.id", "user.display_name", "tag.id")
                .from("user", "tag")
                .group("user.id", "tag.id");

        Assertions
            .assertThat(query.toString())
            .isEqualTo("SELECT user.id, user.display_name, tag.id FROM user, tag GROUP BY user.id, tag.id");
    }

    @Test
    public void build_sql_limit() {
        SelectBuilder query =
            new SelectBuilder().field("user.id", "user.display_name", "tag.id").from("user", "tag").limit("1");

        Assertions
            .assertThat(query.toString())
            .isEqualTo("SELECT user.id, user.display_name, tag.id FROM user, tag LIMIT 1");
    }

    @Test
    public void build_sql_where_and_order_and_group_and_limit() {
        SelectBuilder query =
            new SelectBuilder()
                .field("user.id", "user.display_name")
                .from("user", "tag")
                .where("user.id = user_tag.user_id", "user.id = 3", "tag.id = user_tag.tag_id")
                .order("user.id ASC", "tag.color")
                .group("user.id")
                .limit("1");

        Assertions
            .assertThat(query.toString())
            .isEqualTo(
                "SELECT user.id, user.display_name FROM user, tag WHERE user.id = user_tag.user_id AND user.id = 3 AND tag.id = user_tag.tag_id ORDER BY user.id ASC, tag.color GROUP BY user.id LIMIT 1"
            );
    }

    @Test
    public void error_if_invalid_parameters() {
        SelectBuilder query = new SelectBuilder();

        Assertions
            .assertThatThrownBy(() -> query.toString())
            .hasMessage("Missing fields or from")
            .isInstanceOf(IllegalArgumentException.class);

        Assertions
            .assertThatThrownBy(() -> query.where())
            .hasMessage("Where must have a value")
            .isInstanceOf(IllegalArgumentException.class);

        Assertions
            .assertThatThrownBy(() -> query.order())
            .hasMessage("Order must have a value")
            .isInstanceOf(IllegalArgumentException.class);
    }
}