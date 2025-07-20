package org.example;

class Lexer {
    private String buffer;
    private int pos;
    private int readPos;
    private char ch;

    public Lexer(String buffer) {
        this.buffer = buffer;
        this.pos = 0;
        this.readPos = 0;
        this.ch = 0;
        readChar();
    }

    private void readChar() {
        if (readPos >= buffer.length()) {
            ch = 0; // EOF
        } else {
            ch = buffer.charAt(readPos);
        }
        pos = readPos;
        readPos++;
    }

    private void skipWhitespaces() {
        while (Character.isWhitespace(ch)) {
            readChar();
        }
    }

    public Token nextToken() {
        skipWhitespaces();

        if (ch == 0) {
            readChar();
            return new Token(TokenKind.END, null);
        } else if (ch == '=') {
            readChar();
            return new Token(TokenKind.EQUAL, null);
        } else if (ch == '+') {
            readChar();
            return new Token(TokenKind.PLUS, null);
        } else if (ch == '<') {
            readChar();
            return new Token(TokenKind.LESS_THAN, null);
        } else if (ch == ':') {
            //label的情况
            readChar();
            StringBuilder sb = new StringBuilder();
            while (Character.isLetterOrDigit(ch) || ch == '_') {
                sb.append(ch);
                readChar();
            }
            return new Token(TokenKind.LABEL, sb.toString());
        } else if (Character.isDigit(ch)) {
            //数字
            StringBuilder sb = new StringBuilder();
            while (Character.isDigit(ch)) {
                sb.append(ch);
                readChar();
            }
            return new Token(TokenKind.INT, sb.toString());
        } else if (Character.isLetterOrDigit(ch) || ch == '_') {
            StringBuilder sb = new StringBuilder();
            while (Character.isLetterOrDigit(ch) || ch == '_') {
                sb.append(ch);
                readChar();
            }
            String value = sb.toString();
            switch (value) {
                case "input": return new Token(TokenKind.INPUT, null);
                case "output": return new Token(TokenKind.OUTPUT, null);
                case "goto": return new Token(TokenKind.GOTO, null);
                case "if": return new Token(TokenKind.IF, null);
                case "then": return new Token(TokenKind.THEN, null);
                default: return new Token(TokenKind.IDENT, value);
            }
        } else {
            String value = String.valueOf(ch);
            readChar();
            return new Token(TokenKind.INVALID, value);
        }
    }
}
