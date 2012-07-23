ORG 0000

;This program calculates
;n numbers of the fibonacci sequence
;where n is read in by the user

a DC 0
b DC 1
c DC 1
n DC 0
int EQU 7
BEG
JSR prompt	;display prompt
INI		;get number from user
SBI 2		;first 2 numbers are preset
STA n		;store number
JSR prntln	;print line
LDA a		;load a
OTI		;print a
JSR prntspx	;print space

LDA b		;load b
OTI		;print b
JSR prntspc	;print space

loop: CLA	;clear register
ADD a		;add a
ADD b		;add b
STA c		;c = a + b
OTI		;print c to screen
JSR prntspc	;print space

LDA b		;get b
STA a		;a = b

LDA c		;get c
STA b		;b = c

LDA n		
DEC		
STA n		;n = n-1
BNZ loop

HLT

prntspc: LDI 32	;space
OTA
RET

prntln: LDI 10	;carriage return
OTA
RET

prompt: LDI 69  ;E
OTA
LDI 110 ;n
OTA
LDI 116 ;t
OTA
LDI 101 ;e
OTA
LDI 114 ;r
OTA
LDI 32  ;space
OTA
LDI 110 ;n
OTA
LDI 117 ;u
OTA
LDI 109 ;m
OTA
LDI 98  ;b
OTA
LDI 101 ;e
OTA
LDI 114 ;r
OTA
LDI 58  ;:
OTA
 
RET
 
END
