package org.example;

import java.util.ArrayList;
import java.util.List;

class Parser {
    private List<Token> tokens;
    private int index;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.index = 0;
    }

    private Token current() {
        return tokens.get(index);
    }

    private void advance() {
        index++;
    }

    private TermNode parseTerm() {
        Token token = current();
        advance();
        switch (token.kind) {
            case INPUT:
                return new InputTermNode();
            case INT:
                return new IntTermNode(token.value);
            case IDENT:
                return new IdentTermNode(token.value);
            default:
                throw new RuntimeException("Expected a term (input, int or ident) but found " + token.kind);
        }
    }

    private ExprNode parseExpr() {
        TermNode lhs = parseTerm();
        if (current().kind == TokenKind.PLUS) {
            advance();
            TermNode rhs = parseTerm();
            return new ExprNode(lhs, rhs);
        }
        return new ExprNode(lhs);
    }

    private RelNode parseRel() {
        TermNode lhs = parseTerm();
        if (current().kind == TokenKind.LESS_THAN) {
            advance();
            TermNode rhs = parseTerm();
            return new RelNode(lhs, rhs);
        }
        throw new RuntimeException("Expected rel (<) found " + current().kind);
    }

    private InstrNode parseInstr() {
        Token token = current();
        switch (token.kind) {
            case IDENT:
                String ident = token.value;
                advance();
                if (current().kind != TokenKind.EQUAL) {
                    throw new RuntimeException("Expected equal found " + current().kind);
                }
                advance();
                return new AssignNode(ident, parseExpr());
            case IF:
                advance();
                RelNode rel = parseRel();
                if (current().kind != TokenKind.THEN) {
                    throw new RuntimeException("Expected then found " + current().kind);
                }
                advance();
                return new IfNode(rel, parseInstr());
            case GOTO:
                advance();
                if (current().kind != TokenKind.LABEL) {
                    throw new RuntimeException("Expected label found " + current().kind);
                }
                String gotoLabel = current().value;
                advance();
                return new GotoNode(gotoLabel);
            case OUTPUT:
                advance();
                return new OutputNode(parseTerm());
            case LABEL:
                String label = current().value;
                advance();
                return new LabelNode(label);
            default:
                throw new RuntimeException("Unexpected token " + token.kind);
        }
    }

    ProgramNode parseProgram() {
        List<InstrNode> instrs = new ArrayList<>();
        while (current().kind != TokenKind.END) {
            instrs.add(parseInstr());
        }
        return new ProgramNode(instrs);
    }
}