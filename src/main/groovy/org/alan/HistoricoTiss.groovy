package org.alan

import groovyx.net.http.HttpBuilder
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class HistoricoTiss {

    static final String URL_ANS = "https://www.gov.br/ans/pt-br"

    void buscarHistorico() {
        try {
            Document pagina = HttpBuilder.configure {
                request.uri = URL_ANS
            }.get()
            println pagina.title()

            Element linkPrestador = pagina.select("a")
                    .find { it.text().contains("Espaço do Prestador") }

            //println linkPrestador.text()
            String urlPrestadores = linkPrestador.attr("href")


            Document paginaPrestadores = HttpBuilder.configure {
                request.uri = urlPrestadores
            }.get()
            println paginaPrestadores.title()

            Element linkTiss = paginaPrestadores.select("a")
                    .find {
                        it.text().contains("TISS - Padrão para Troca")
                    }
            //println linkTiss.text()
            String urlTiss = linkTiss.attr("href")

            Document paginaTiss = HttpBuilder.configure {
                request.uri = urlTiss
            }.get()

            Element linkHistorico = paginaTiss.select("a")
                    .find {
                        it.text().contains("Clique aqui para acessar todas as versões dos Componentes")
                    }

            println linkHistorico.attr("href")
            String urlHistorico = linkHistorico.attr("href")

            Document paginaHistoricos = HttpBuilder.configure {
                request.uri = urlHistorico
            }.get()

            println paginaHistoricos.title()
            Element tabelaHistorico = paginaHistoricos.select("table").first()
            Elements linhas = tabelaHistorico.select("tr")
            boolean coletar = true
            def historico = []
            linhas.each { linha ->
                Elements colunas = linha.select("td")

                if (!colunas.isEmpty()) {
                    String competencia = colunas.get(0).text()
                    String publicacao = colunas.get(1).text()
                    String inicioVigencia = colunas.get(2).text()

                    if (coletar) {
                        historico << [competencia: competencia, publicacao: publicacao, inicioVigencia: inicioVigencia]
                    }

                    if (competencia.equalsIgnoreCase("Jan/2016")) {
                        coletar = false
                    }
                }
            }
            if (!historico.isEmpty()) {
                salvarHistorico(historico)
            } else {
                println "Nenhum histórico encontrado"
            }
        } catch (Exception e) {
            println "Erro ao buscar informações da TISS: ${e.message}"
        }
    }

    void salvarHistorico(List historico) {

        Path arquivo = Paths.get("./Downloads/historico_tiss.csv")
        Files.createDirectories(arquivo.getParent())
        arquivo.toFile().withWriter("UTF-8") { writer ->
            writer.writeLine("Competência,Publicação,Início de Vigência")
            historico.each { Map registro ->
                writer.writeLine("${registro.competencia},${registro.publicacao},${registro.inicioVigencia}")
            }
        }
    }
}