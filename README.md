# Projeto Diário Docente
## Projeto da Disciplina de Linguagem de Programação Funcional
Aplicação web voltada ao cadastro de docentes que terão disponíveis as seguintes funcionalidades: <br><br>
1. Auto-cadastro <br>
2. Inscrição de novos alunos <br>
3. Cálculo automático de média <br>
4. Definição automática de aprovação <br>
5. Definição automática de reprovação <br>
6. Em caso de reprovação o motivo será expresso com base nas informações passadas<br><br>

Média mínima para aprovação = 7 <br>
Número máximo de faltas para aprovação = 4 <br>

O primeiro acesso pode ser feito ao utilizar os seguintes comandos: <br>
-> Para compilar o servidor ``` kotlinc -cp "ktor.jar;." servidor.kt ```<br>
-> Para executar o servidor ``` kotlin -cp "ktor.jar;." ServidorKt ``` <br>
-> Página Inicial está no link ```http://localhost:7654/st/index.html```<br>

O servidor "servidor.kt" está contido na pasta src e toda a parte estática (páginas HTML e imagens) na pasta static


