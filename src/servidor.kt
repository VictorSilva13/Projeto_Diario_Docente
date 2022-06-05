import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.request.*
import java.io.*
import kotlin.io.*

class Aluno(val nome: String, val nota1: String, val nota2: String, val faltas: String)

class Professor(val nome: String, val faculdade: String, val disciplina: String)

val profs:MutableList<Professor> = mutableListOf()
val minhaListaAlunos:MutableList<Aluno> = mutableListOf()

fun salvarDadosProfs(){
    val conteudo = profs.joinToString(separator="\n") { p ->
        p.nome + ";" + p.faculdade + ";" + p.disciplina
    }
    File("dadosProf.txt").writeText(conteudo)
}

fun lerDadosProfs(){
   val file = File("dadosProf.txt")

   if (!file.exists()) {
      return
   }
   val conteudo = File("dadosProf.txt").readText()
   val dados = conteudo.split("\n").
    map { linha -> linha.split(";") } . 
    map { linha -> Professor(linha[0],linha[1],linha[2]) }
   profs.clear()
   profs.addAll(dados)

}

fun criarListaProfs(): String{ 
    if(!profs.any()) //caso o usuario acesse a lista sem antes cadastrar ninguem
        return "Ainda não temos professores cadastrados<br><br>"
    return profs.joinToString(separator="\n") {
        p -> "Nome: ${p.nome} --- Instituição: ${p.faculdade} --- Disciplina: ${p.disciplina}<br><br>"}
}

fun salvarDadosAlunos(){
    val conteudo = minhaListaAlunos.joinToString(separator="\n") { p ->
        p.nome + ";" + p.nota1 + ";" + p.nota2 + ";" + p.faltas
    }
    File("dadosAlunos.txt").writeText(conteudo)
}

fun lerDadosAlunos(){
   val file = File("dadosAlunos.txt")

   if (!file.exists()) {
      return
   }
   val conteudo = File("dadosAlunos.txt").readText()
   val dados = conteudo.split("\n").
    map { linha -> linha.split(";") } . 
    map { linha -> Aluno(linha[0],linha[1],linha[2],linha[3]) }
   minhaListaAlunos.clear()
   minhaListaAlunos.addAll(dados)
}

fun media(n1: String, n2: String): Double{
    val nota1 = n1.toDouble()
    val nota2 = n2.toDouble()

    return (nota1+nota2)/2
}

fun situacao(media: Double, faltas: String): String{ //MEDIA PARA APROVAÇÃO = 7 FALTAS MÁXIMAS = 4 
    val nFaltas = faltas.toInt()
    if(media>=7.0 && nFaltas<5){
        return "<b>APROVADO</b>"
    }else if(nFaltas>=5){
        return "REPROVADO POR FALTA"
    }else{
        return "REPROVADO"
    }
}

fun criarListaAlunos(): String{ 
    if(!minhaListaAlunos.any()) //caso o usuario acesse a lista sem antes cadastrar ninguem
        return "Ainda não temos alunos cadastrados<br><br>"
    return minhaListaAlunos.joinToString(separator="\n") {
        p -> "Nome: ${p.nome} --- 1ª Avaliação: ${p.nota1} --- 2ª Avaliação: ${p.nota2} --- Número de faltas: ${p.faltas} --- Média: ${media(p.nota1, p.nota2)} --- ${situacao(media(p.nota1, p.nota2),p.faltas)}<br><br>"}
}

fun main() {
    println("Iniciando Servidor...")
    lerDadosProfs()
    lerDadosAlunos()
    embeddedServer(Netty, port = 7654){
        routing{
            post("/cria_professor") {
                val parameters = call.receiveParameters()
                val nome = parameters["nome_prof"] ?: "<blank>"
                val instituicao = parameters["nome_instituicao"] ?: "<blank>"
                val disciplina = parameters["nome_disciplina"] ?: "<blank>"
                profs.add(Professor(nome, instituicao, disciplina))
                salvarDadosProfs()
                call.respondText("""
                <html>
                <head>
                <title>Perfil</title>
                <link rel="stylesheet" href="st/stylesheet.css">
                <link rel="icon" href="st/welcome.png">
                </head>

                <body class="page">     
                    <div style="text-align:center">
                        <br>
                        <h2>Docente cadastrado com sucesso<p></h2>
                        <h4>Seus dados agora estão em nosso banco de dados . . . <img src="st/form.png" height="35px" width="35px" ></h4>
                        <b>Nome:</b> ${nome} <br>
                        <b>Instituição:</b> ${instituicao} <br>
                        <b>Disciplina ministrada:</b> ${disciplina} <br> <br> <br>
                                
                        <div class="box">
                            <a href="st/cadastroA.html" style="text-decoration:none">Cadastrar turma/aluno</a>
                        </div><br>
                        <div class="box">
                            <a href="st/index.html" style="text-decoration:none">Sair</a>
                        </div>
                        
                    </div>
                </body>
                </html>
                """, ContentType.Text.Html)
            }

            post("/cria_aluno"){
                val parameters = call.receiveParameters()
                val nomeAluno = parameters["nome_aluno"] ?: "<blank>"
                val nota1 = parameters["nota_1"] ?: "0.0"
                val nota2 = parameters["nota_2"] ?: "0.0"
                val faltas = parameters["faltas"] ?: "0"

                minhaListaAlunos.add(Aluno(nomeAluno, nota1, nota2, faltas))
                salvarDadosAlunos()

                call.respondRedirect("st/cadastroA.html", permanent = true)
            }

            get("/help"){
                call.respondText("""
                <html>
                <head>
                    <title>Ajuda</title>
                    <link rel="stylesheet" href="st/stylesheet.css">
                    <link rel="icon" href="st/help.png">
                </head>

                <body class="page">
                
                    <div style="text-align: center;"><br><br>
                        <h2>Descrição:</h2> 
                        <h4>Esta aplicação tem como objetivo permitir a criação de boletins para 
                        uso do docente.<br>Após realizar o cadastro próprio, é possível entrar em seu perfil
                        e inscrever novos alunos com suas respectivas informações. <br>Pode-se consultar
                        o corpo de docentes e discentes cadastrados. <br>A média e situação de aprovação ou 
                        reprovação de cada aluno é determinada automaticamente.
                        </h4>
                    </div>
                   
                    <div style="text-align: center" class="box">
                        <a href="st/index.html" style="text-decoration:none">Voltar</a>               
                    </div>
                
                </body>

                </html>
                """, ContentType.Text.Html)
            }

            get("/lista_profs"){
                call.respondText("""
                <html>
                <head>
                <title>Lista de Professores</title>
                <link rel="stylesheet" href="st/stylesheet.css">
                <link rel="icon" href="st/lista.png">
                </head>
                <body class="page">
                    <div style="text-align:center"><br>
                        <h1>Docentes Cadastrados:</h1>
                    </div><br>
                    
                    <div class="lista" style="text-align:center"><br>
                        ${criarListaProfs()}
                    </div><br>
                    
                    <div style="text-align: center" class="box">
                    <a href="st/cadastroP.html" style="text-decoration:none">Cadastrar Professor</a><br>
                    </div><br>

                    <div style="text-align: center" class="box">
                    <a href="st/index.html" style="text-decoration:none">Voltar</a>               
                    </div>
                </body>
                </html>
                """, ContentType.Text.Html)
            }

            get("/lista_alunos"){
                call.respondText("""
                <html>
                <title>Lista de Alunos</title>
                <link rel="stylesheet" href="st/stylesheet.css">
                <link rel="icon" href="st/lista.png">

                <body class="page">
                <div style="text-align:center"><br>
                    <h1>Alunos Cadastrados:</h1>
                </div><br>

                <div class="lista" style="text-align:center"><br>
                ${criarListaAlunos()}
                </div><br>
               
                <div style="text-align: center" class="box">
                <a href="st/cadastroA.html" style="text-decoration:none">Cadastrar Alunos</a><br>
                </div><br>

                <div style="text-align: center" class="box">
                <a href="st/index.html": style="text-decoration:none">Sair</a>               
                </div><br>

                </body>
                </html>
                """, ContentType.Text.Html)
            }

            post("/login"){
                val parameters = call.receiveParameters()
                val nome = parameters["prof"] ?: "<blank>"
                val instituicao = parameters["instituicao"] ?: "<blank>"
                val disciplina = parameters["disciplina"] ?: "<blank>"

                val lista = File("dadosProf.txt").readText()
                val listaDadosUsuarioAtual = nome+";"+instituicao+";"+disciplina
                if(lista.contains(listaDadosUsuarioAtual)){
                    call.respondText("""
                    <html>
                    <head>
                        <title>Professor Logado</title>
                        <link rel="stylesheet" href="st/stylesheet.css">
                        <link rel="icon" href="st/welcome.png">
                    </head>
                    <body class="page">
                        <div style="text-align:center"><br>
                        <h2>Abram espaço pois o(a) nosso(a) querido(a) docente ${nome} acabou de chegar</h2><br>
                        <h3>O que vamos fazer agora?</h3>
                        <img src="st/lookdown.png" width="300px" height="150px">
                        </div>
                        
                        <div style="text-align: center" class="box">
                        <a href="st/cadastroA.html" style="text-decoration:none">Cadastrar turma/aluno</a>
                        </div><br>

                        <div style="text-align: center" class="box">
                        <a href="lista_alunos" style="text-decoration:none">Minha lista de alunos</a>
                        </div><br>

                        <div style="text-align: center" class="box">
                        <a href="st/index.html" style="text-decoration:none">Sair</a>
                        </div><br>
                        
                    </body>
                    </html>
                    """, ContentType.Text.Html)
                }else{
                    call.respondText("""
                    <html>
                    <head>
                        <title>Sem Cadastro</title>
                        <link rel="stylesheet" href="st/stylesheet.css">
                        <link rel="icon" href="st/bug.png">
                    </head>
                    <body class="page">
                        <div style="text-align:center"><br><br><br>
                        <h2>Usuário não foi encontrado</h2>
                        <img src="st/erro.png" height="150px"><br><br>
                        </div>

                        <div style="text-align: center" class="box">
                        <a href="st/cadastroP.html">Cadastrar Professor</a><br>
                        </div><br>

                        <div style="text-align: center" class="box">
                        <a href="st/index.html":>Sair</a>
                        </div><br>
 
                    </body>
                    </html>
                    """, ContentType.Text.Html)
                }
            }

            static("/st"){
                files("static/")
            }
        }
    }.start(wait=true)
}
