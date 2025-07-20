package org.example;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgramNode {
    private List<InstrNode> instrs;

    public ProgramNode(List<InstrNode> instrs) {
        this.instrs = instrs;
    }

    public void generateAsm(PrintWriter writer) {
        Map<String, Integer> variables = new HashMap<>();
        int[] ifCount = new int[]{0}; // 使用数组模拟 C 中的指针

        // 收集变量
        for (InstrNode instr : instrs) {
            instr.declareVariables(variables);
        }

        writer.println("format ELF64 executable");
        writer.println("LINE_MAX equ 1024");
        writer.println("segment readable executable");
        writer.println("include \"linux.inc\"");
        writer.println("include \"utils.inc\"");
        writer.println("entry _start");
        writer.println("_start:");

        writer.println("    mov rbp, rsp");
        writer.println("    sub rsp, " + (variables.size() * 8));

        for (InstrNode instr : instrs) {
            instr.generateAsm(writer, variables, ifCount);
        }

        writer.println("    add rsp, " + (variables.size() * 8));
        writer.println("    mov rax, 60");
        writer.println("    xor rdi, rdi");
        writer.println("    syscall");

        writer.println("segment readable writeable");
        writer.println("line rb LINE_MAX");
    }
}