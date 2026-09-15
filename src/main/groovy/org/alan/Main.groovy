package org.alan

class Main {
    static void main(String[] args) {
        TissCrawler tissCrawler = new TissCrawler()
        HistoricoTiss historicoTiss = new HistoricoTiss()
        TabelaDeErrosANS tabelaDeErrosANS = new TabelaDeErrosANS()
        tissCrawler.buscarTiss()
        historicoTiss.buscarHistorico()
        tabelaDeErrosANS.buscarTabelaDeErros()
    }
}