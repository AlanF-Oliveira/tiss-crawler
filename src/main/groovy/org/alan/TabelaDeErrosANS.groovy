package org.alan

import groovyx.net.http.HttpBuilder
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class TabelaDeErrosANS {

    static final String URL_ANS = "https://www.gov.br/ans/pt-br"


    void buscarTabelaDeErros() {
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

            Element linkTabelasRelacionadas = paginaTiss.select("a")
                    .find {
                        it.text().contains("Clique aqui para acessar as planilhas")
                    }

            println linkTabelasRelacionadas.attr("href")
            String urlTabelasRelacionadas = linkTabelasRelacionadas.attr("href")

            Document paginaTabelas = HttpBuilder.configure {
                request.uri = urlTabelasRelacionadas
            }.get()

            Element linkTabelaErros = paginaTabelas.select("a")
                    .find {
                        it.text() ==~ /(?i).*tabela de erros.*\.xlsx.*/
                    }
            println linkTabelaErros.attr("href")
            String urlDownloadTabelaErros = linkTabelaErros.attr("href")
            baixarTabelaDeErros(urlDownloadTabelaErros)
        } catch (Exception e) {
            println "Erro ao buscar informações da TISS: ${e.message}"
        }
    }

    void baixarTabelaDeErros(String url) {
        byte[] arquivo = HttpBuilder.configure {
            request.uri = url
        }.get(byte[].class) {
            response.parser("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    { config, fromServer ->
                fromServer.inputStream.bytes
            }
        }
        Path dir = Paths.get("./Downloads/Aquivos_padrao_TISS")
        Files.createDirectories(dir)
        String nomeArquivo = new URI(url).path.tokenize("/").last()
        Path destino = dir.resolve(nomeArquivo)
        Files.write(destino, arquivo)
    }
}

