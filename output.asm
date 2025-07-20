format ELF64 executable
LINE_MAX equ 1024
segment readable executable
include "linux.inc"
include "utils.inc"
entry _start
_start:
    mov rbp, rsp
    sub rsp, 16
    read 0, line, LINE_MAX
    mov rdi, line
    call strlen
    mov rdi, line
    mov rsi, rax
    call parse_uint
    mov qword [rbp - 8], rax
    mov rax, 0
    mov qword [rbp - 16], rax
.label:
    mov rax, qword [rbp - 16]
    mov rdi, 1
    mov rsi, rax
    call write_uint
    mov rax, qword [rbp - 16]
    mov rdx, rax
    mov rax, 1
    add rax, rdx
    mov qword [rbp - 16], rax
    mov rax, qword [rbp - 16]
    mov rdx, rax
    mov rax, qword [rbp - 8]
    cmp rdx, rax
    setl al
    and al, 1
    movzx rax, al
    test rax, rax
    jz .endif0
    jmp .label
.endif0:
    mov rax, qword [rbp - 16]
    mov rdx, rax
    mov rax, 10
    cmp rdx, rax
    setl al
    and al, 1
    movzx rax, al
    test rax, rax
    jz .endif1
    jmp .label2
.endif1:
    mov rax, 69
    mov rdi, 1
    mov rsi, rax
    call write_uint
.label2:
    add rsp, 16
    mov rax, 60
    xor rdi, rdi
    syscall
segment readable writeable
line rb LINE_MAX
