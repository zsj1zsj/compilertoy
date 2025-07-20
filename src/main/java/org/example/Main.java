package org.example;

public class Main {
    public static void main(String[] args) {
        Lexer lexer = new Lexer("n = input\n" +
                "i = 0\n" +
                ":label\n" +
                "output i\n" +
                "i = i + 1\n" +
                "if i < n then goto :label\n" +
                "if i < 10 then goto :label2\n" +
                "output 69\n" +
                ":label2");

        Token token = null;
        while ((token = lexer.nextToken()) != null && token.kind != TokenKind.END) {
            System.out.println(token);
        }
    }
}