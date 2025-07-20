package org.example;

import java.io.PrintWriter;
import java.util.Map;

public class Sematic {
}

abstract class TermNode {
    abstract void generateAsm(PrintWriter writer, Map<String, Integer> variables);
}

class InputTermNode extends TermNode {
    @Override
    void generateAsm(PrintWriter writer, Map<String, Integer> variables) {
        writer.println("    read 0, line, LINE_MAX");
        writer.println("    mov rdi, line");
        writer.println("    call strlen");
        writer.println("    mov rdi, line");
        writer.println("    mov rsi, rax");
        writer.println("    call parse_uint");
    }
}

class IntTermNode extends TermNode {
    private String value;

    public IntTermNode(String value) {
        this.value = value;
    }

    @Override
    void generateAsm(PrintWriter writer, Map<String, Integer> variables) {
        writer.println("    mov rax, " + value);
    }
}

class IdentTermNode extends TermNode {
    private String ident;

    public IdentTermNode(String ident) {
        this.ident = ident;
    }

    @Override
    void generateAsm(PrintWriter writer, Map<String, Integer> variables) {
        Integer index = variables.get(ident);
        if (index == null) {
            throw new RuntimeException("Identifier not defined: " + ident);
        }
        writer.println("    mov rax, qword [rbp - " + (index * 8 + 8) + "]");
    }
}

class ExprNode {
    private TermNode term;
    private TermNode lhs, rhs;

    public ExprNode(TermNode term) {
        this.term = term;
    }

    public ExprNode(TermNode lhs, TermNode rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public void generateAsm(PrintWriter writer, Map<String, Integer> variables) {
        if (term != null) {
            term.generateAsm(writer, variables);
        } else {
            lhs.generateAsm(writer, variables);
            writer.println("    mov rdx, rax");
            rhs.generateAsm(writer, variables);
            writer.println("    add rax, rdx");
        }
    }
}

class RelNode {
    private TermNode lhs, rhs;

    public RelNode(TermNode lhs, TermNode rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public void generateAsm(PrintWriter writer, Map<String, Integer> variables) {
        lhs.generateAsm(writer, variables);
        writer.println("    mov rdx, rax");
        rhs.generateAsm(writer, variables);
        writer.println("    cmp rdx, rax");
        writer.println("    setl al");
        writer.println("    and al, 1");
        writer.println("    movzx rax, al");
    }
}

abstract class InstrNode {
    abstract void generateAsm(PrintWriter writer, Map<String, Integer> variables, int[] ifCount);
    abstract void declareVariables(Map<String, Integer> variables);
}

class AssignNode extends InstrNode {
    private String ident;
    private ExprNode expr;

    public AssignNode(String ident, ExprNode expr) {
        this.ident = ident;
        this.expr = expr;
    }

    @Override
    void generateAsm(PrintWriter writer, Map<String, Integer> variables, int[] ifCount) {
        expr.generateAsm(writer, variables);
        int index = variables.get(ident);
        writer.println("    mov qword [rbp - " + (index * 8 + 8) + "], rax");
    }

    @Override
    void declareVariables(Map<String, Integer> variables) {
        if (!variables.containsKey(ident)) {
            variables.put(ident, variables.size());
        }
    }
}

class IfNode extends InstrNode {
    private RelNode rel;
    private InstrNode instr;

    public IfNode(RelNode rel, InstrNode instr) {
        this.rel = rel;
        this.instr = instr;
    }

    @Override
    void generateAsm(PrintWriter writer, Map<String, Integer> variables, int[] ifCount) {
        rel.generateAsm(writer, variables);
        int label = ifCount[0]++;
        writer.println("    test rax, rax");
        writer.println("    jz .endif" + label);
        instr.generateAsm(writer, variables, ifCount);
        writer.println(".endif" + label + ":");
    }

    @Override
    void declareVariables(Map<String, Integer> variables) {
        instr.declareVariables(variables);
    }
}

class GotoNode extends InstrNode {
    private String label;

    public GotoNode(String label) {
        this.label = label;
    }

    @Override
    void generateAsm(PrintWriter writer, Map<String, Integer> variables, int[] ifCount) {
        writer.println("    jmp ." + label);
    }

    @Override
    void declareVariables(Map<String, Integer> variables) {}
}

class OutputNode extends InstrNode {
    private TermNode term;

    public OutputNode(TermNode term) {
        this.term = term;
    }

    @Override
    void generateAsm(PrintWriter writer, Map<String, Integer> variables, int[] ifCount) {
        term.generateAsm(writer, variables);
        writer.println("    mov rdi, 1");
        writer.println("    mov rsi, rax");
        writer.println("    call write_uint");
    }

    @Override
    void declareVariables(Map<String, Integer> variables) {}
}

class LabelNode extends InstrNode {
    private String label;

    public LabelNode(String label) {
        this.label = label;
    }

    @Override
    void generateAsm(PrintWriter writer, Map<String, Integer> variables, int[] ifCount) {
        writer.println("." + label + ":");
    }

    @Override
    void declareVariables(Map<String, Integer> variables) {}
}