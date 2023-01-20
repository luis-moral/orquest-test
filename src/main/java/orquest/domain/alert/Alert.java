package orquest.domain.alert;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import orquest.domain.clockin.CreateClockIn;
import orquest.domain.clockin.UpdateClockIn;

import java.util.Optional;

public class Alert {

    private final static ExpressionParser PARSER = new SpelExpressionParser();

    private final long id;
    private final String businessId;
    private final Expression expression;
    private final String message;

    public Alert(long id, String businessId, String expression, String message) {
        this.id = id;
        this.businessId = businessId;
        this.expression = PARSER.parseExpression(expression);
        this.message = message;
    }

    public long id() {
        return id;
    }

    public String businessId() {
        return businessId;
    }

    public String expression() {
        return expression.getExpressionString();
    }

    public String message() {
        return message;
    }

    public boolean checkFor(CreateClockIn clockIn) {
        return executeExpression(clockIn);
    }

    public boolean checkFor(UpdateClockIn clockIn) {
        return executeExpression(clockIn);
    }

    private boolean executeExpression(Object parameter) {
        EvaluationContext context = new StandardEvaluationContext(parameter);
        context.setVariable("clockIn", parameter);

        return
            Optional
                .ofNullable(expression.getValue(context, Boolean.class))
                .orElse(false);
    }
}
