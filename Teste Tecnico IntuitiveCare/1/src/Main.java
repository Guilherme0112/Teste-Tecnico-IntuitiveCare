
import java.util.List;

import utils.PdfUtil;
import utils.Utils;

public class Main {

    public static void main(String[] args) throws Exception {

        try {

            // Retorna o HTML do site
            String html = Utils.getHtml(
                    "https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos");

            List<String> links = Utils.getLinksOfPage(html);

            PdfUtil.downloadPDFs(links);

        } catch (Exception e) {
           
            System.out.println(e.getMessage());
        }
    }

}