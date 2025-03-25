package utils;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PdfUtil {

    /***
     * Método que retorna o arquivo .zip com os 2 anexos dentro
     * 
     * @param links Links dos pdfs
     * @return Status se rodou ou se deu erro
     * @throws Exception Erro genérico
     */
    public static void downloadPDFs(List<String> links) throws Exception {

        System.out.println("Baixando e zipando os PDFs...");

        // Nome do arquivo .zip
        String zipFilename = "arquivos.zip";

        // Bloco try que vai baixar e zipar os anexos
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFilename))) {

            // Nome do anexo 1
            String filename = "ANEXO I";

            // Cada anexo passará por este fluxo (é espero 2 anexos)
            for (String link : links) {

                // Cria uma instância URI e faz a requisição
                // Depois busca a resposta
                URI uri = new URI(link);
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder(uri).build();
                HttpResponse<InputStream> response = client.send(request, BodyHandlers.ofInputStream());

                // Cria a entrada paras os pdfs no arquivo .zip
                ZipEntry zipEntry = new ZipEntry(filename + ".pdf");
                zipOut.putNextEntry(zipEntry);

                // Lê os dados do conteúdo do PDF em pedaços para 
                // evitar problemas de memória
                try (InputStream in = response.body()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    // Grava os pdfs dentro do arquivo .zip
                    while ((bytesRead = in.read(buffer)) != -1) {
                        zipOut.write(buffer, 0, bytesRead);
                    }
                }

                // Fecha a instância
                zipOut.closeEntry();

                // Atualiza o nome do próximo PDF
                filename = "ANEXO II";
            }

            System.out.println("Todos os PDFs foram baixados e zipados com sucesso");

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }
    }
}
