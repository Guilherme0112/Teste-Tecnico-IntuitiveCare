package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exceptions.LinkException;

public class Utils {

    /***
     * Faz a busca do site e o retorna em String
     * 
     * @param link Link do site que será buscado
     * @return String do html do site
     * @throws Exception
     */
    public static String getHtml(String link) throws Exception {

        try {

            System.out.println("Buscando o HTML do site...");

            // Inicializa a variavel que receberá o HTML do site
            StringBuilder htmlContent = new StringBuilder();

            // Converte de URI para URL
            URI uri = new URI(link);
            URL url = uri.toURL();

            // Tenta fazer a conexão com o site
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            // Lê a resposta da requisição
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            // Inicializa a variavel de índice
            String line;

            // Condição para passar todo conteúdo do HTML para o
            // htmlContent
            while ((line = reader.readLine()) != null) {
                htmlContent.append(line);
            }

            System.out.println("HTML Encontrado");

            return htmlContent.toString();

        } catch (Exception e) {

            System.out.println(e.getMessage());
            return null;
        }
    }


    /**
     * Pega os links da página do gov
     * 
     * @param html HTML da página
     * @return Uma lista com os 2 links que serão baixados
     */
    public static List<String> getLinksOfPage(String html) throws Exception, LinkException{
        
        System.out.println("Obtendo os links dos Pdfs do site...");

        // Inicializa a List de retorno
        List<String> links = new ArrayList<>();

        try {      

            // Pega os elementos dentro da desta div específica
            String regex = "<div class=\"cover-richtext-tile tile-content\".*?>(.*?)</div>";
            Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(html);
            
            // Verifica se existe alguma correspondência ao regex que busca a div pai
            if (!matcher.find()) {
                
                throw new LinkException("Não foi possível encontrar a div pai");
            }

            // Pega o conteúdo que está dentro da div
            String divContent = matcher.group(1); 
            
            // Busca o elemento <ol> dentro da div divContent
            String olRegex = "<ol.*?>(.*?)</ol>";
            Pattern olPattern = Pattern.compile(olRegex, Pattern.DOTALL);
            Matcher olMatcher = olPattern.matcher(divContent);

            // Verifica se existe alguma correspondência ao regex que busca a <ol>
            if (!olMatcher.find()) {

                throw new LinkException("Não foi possível encontrar os elementos <ol> dentro da div pai");
            }

            // Pega o conteúdo que está dentro da <ol>
            String olContent = olMatcher.group(1);
            
            // Busca todas as tags <a> dentro da <ol>
            String aTagsRegex = "<a.*?href=\"(.*?)\".*?>(.*?)</a>";
            Pattern aPattern = Pattern.compile(aTagsRegex, Pattern.DOTALL);
            Matcher aMatcher = aPattern.matcher(olContent);

            // Enquanto ele encontrar conteudo dentro das tags <a>
            // Ele pega o href adiciona em uma lista
            while (aMatcher.find()) {

                String href = aMatcher.group(1); 
                links.add(href); 
            }
            
            // Remove o 2° item pois ele é a versão .xls do anexo
            // Apenas busco a versão em PDF
            links.remove(1);

            System.out.println("Os links foram encontrados com sucesso");

        } catch (LinkException e){
            
            links.add(e.getMessage());
            System.out.println(e.getMessage());
            
        } catch (Exception e) {
            
            links.add(e.getMessage());
            System.out.println(e.getMessage());
        }

        return links;
    }
}
