package org.example;

class Token {
    public TokenKind kind;
    public String value;

    public Token(TokenKind kind, String value) {
        this.kind = kind;
        this.value = value;
    }

    @Override
    public String toString() {
        return value != null ? kind + "(" + value + ")" : kind.toString();
    }
}