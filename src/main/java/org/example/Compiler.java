package org.example;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Compiler {
    public static void main(String[] args) throws IOException {
        String buffer = new String(Files.readAllBytes(Paths.get("/Users/lynn/IdeaProjects/compilertoy/src/main/resources/source.txt")));
        Lexer lexer = new Lexer(buffer);
        List<Token> tokens = new ArrayList<>();
        //生成 token 序列
        Token token;
        do {
            token = lexer.nextToken();
            tokens.add(token);
        } while (token.kind != TokenKind.END);

        // 语法
        Parser parser = new Parser(tokens);
        ProgramNode program = parser.parseProgram();

        //生成 asm
        try (PrintWriter writer = new PrintWriter("output.asm")) {
            program.generateAsm(writer);
        }
    }
}