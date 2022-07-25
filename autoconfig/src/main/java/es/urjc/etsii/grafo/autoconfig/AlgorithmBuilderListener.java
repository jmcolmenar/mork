package es.urjc.etsii.grafo.autoconfig;

import es.urjc.etsii.grafo.autoconfig.antlr.AlgorithmParser;
import es.urjc.etsii.grafo.autoconfig.antlr.AlgorithmParserBaseListener;
import es.urjc.etsii.grafo.autoconfig.service.AlgComponentService;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class AlgorithmBuilderListener extends AlgorithmParserBaseListener {
    private final Deque<Map<String, Object>> paramContext;
    private final AlgComponentService algorithmComponents;
    Object lastPropertyValue;

    public AlgorithmBuilderListener(AlgComponentService algorithmComponents) {
        this.algorithmComponents = algorithmComponents;
        paramContext = new ArrayDeque<>();
    }

    @Override
    public void enterComponent(AlgorithmParser.ComponentContext ctx) {
        paramContext.push(new HashMap<>());
    }

    @Override
    public void exitComponent(AlgorithmParser.ComponentContext ctx) {
        var params = paramContext.pop();
        String componentName = ctx.IDENT().getText();
        var componentClass = this.algorithmComponents.byName(componentName);
        var componentInstance = AlgorithmBuilderUtil.build(componentClass, params);
        lastPropertyValue = componentInstance;
    }

    @Override
    public void exitLiteral(AlgorithmParser.LiteralContext ctx) {
        if(ctx.arrayLiteral() != null){
            // TODO delete empty arrays, do not allow in parsing grammar
            // TODO Implement array support, with proper typing
            throw new UnsupportedOperationException("Array parsing not implemented yet");
        } else {
            lastPropertyValue = getValue(ctx);
        }
    }

    @Override
    public void exitProperty(AlgorithmParser.PropertyContext ctx) {
        String propertyName = ctx.IDENT().getText();
        if (lastPropertyValue == null) {
            throw new IllegalStateException();
        }
        currentContext().put(propertyName, lastPropertyValue);
        lastPropertyValue = null;
    }

    public Map<String, Object> currentContext() {
        return paramContext.getFirst();
    }

    public Object getValue(AlgorithmParser.LiteralContext ctx) {
        if (ctx.BooleanLiteral() != null) {
            return Boolean.valueOf(ctx.BooleanLiteral().getText());
        }
        if (ctx.CharacterLiteral() != null) {
            return ctx.CharacterLiteral().getText();
        }
        if (ctx.IntegerLiteral() != null) {
            return Integer.parseInt(ctx.IntegerLiteral().getText());
        }
        if (ctx.FloatingPointLiteral() != null) {
            return Double.parseDouble(ctx.FloatingPointLiteral().getText());
        }
        if (ctx.StringLiteral() != null) {
            return ctx.StringLiteral().getText();
        }
        if (ctx.NullLiteral() != null) {
            return null;
        }
        throw new UnsupportedOperationException("failed to process LiteralContext" + ctx);
    }

    public Object getLastPropertyValue() {
        return lastPropertyValue;
    }
}
