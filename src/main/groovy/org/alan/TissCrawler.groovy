package org.alan

import groovyx.net.http.HttpBuilder
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class TissCrawler {
    static final String URL_ANS = "https://www.gov.br/ans/pt-br"

    void buscarTiss() {
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
            println paginaTiss.title()

            Element linkPadraoTiss = paginaTiss.select("a")
                    .find {
                        it.text().contains("Clique aqui para acessar a versão")
                    }
            //println linkPadraoTiss.text()
            String urlPadraoTiss = linkPadraoTiss.attr("href")


            Document paginaPadraoTiss = HttpBuilder.configure {
                request.uri = urlPadraoTiss
            }.get()
            println paginaPadraoTiss.title()

            Element linkComponenteComunicacao = paginaPadraoTiss.select("a")
                    .find {
                        it.text() ==~ /(?i).*Componente de Comunicação.*\.zip.*/
                    }
            //print linkComponenteComunicacao.text()
            String downloadCompComunicacao = linkComponenteComunicacao.attr("href")
            baixarComponenteComunicacao(downloadCompComunicacao)
        } catch (Exception e) {
            println "Erro ao buscar informações da TISS: ${e.message}"
        }
    }

    void baixarComponenteComunicacao(String downloadCompComunicacao) {

        byte[] arquivo = HttpBuilder.configure {
            request.uri = downloadCompComunicacao
        }.get(byte[].class) {
            response.parser("application/zip") { config, fromServer ->
                fromServer.inputStream.bytes
            }
        }
        Path dir = Paths.get("./Downloads/Aquivos_padrao_TISS")
        Files.createDirectories(dir)
        String nomeArquivo = new URI(downloadCompComunicacao).path.tokenize("/").last()
        Path destino = dir.resolve(nomeArquivo)
        Files.write(destino, arquivo)
    }
}



