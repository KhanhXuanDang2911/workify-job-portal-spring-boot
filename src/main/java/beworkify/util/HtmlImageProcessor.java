package beworkify.util;

import beworkify.service.AzureBlobService;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;

@RequiredArgsConstructor
public class HtmlImageProcessor {

    private final AzureBlobService storageService;

    public String process(String html) {
        if (html == null || html.isBlank())
            return html;
        Document doc = Jsoup.parseBodyFragment(html);
        Elements imgs = doc.select("img");
        for (Element img : imgs) {
            String src = img.attr("src");
            if (src != null && src.startsWith("data:")) {
                int comma = src.indexOf(',');
                if (comma > 0) {
                    String meta = src.substring(5, comma);
                    String base64 = src.substring(comma + 1);
                    byte[] data = Base64.getDecoder().decode(base64);
                    String ext = "png";
                    if (meta.contains("image/")) {
                        ext = meta.substring(meta.indexOf("image/") + 6);
                        if (ext.contains(";"))
                            ext = ext.substring(0, ext.indexOf(';'));
                    }
                    byte[] uploadBytes = data;
                    try {
                        ByteArrayInputStream in = new ByteArrayInputStream(data);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Thumbnails.of(in)
                                .size(900, 900)
                                .outputFormat(ext.equalsIgnoreCase("png") ? "png" : "jpg")
                                .outputQuality(0.80)
                                .toOutputStream(baos);
                        uploadBytes = baos.toByteArray();
                    } catch (Exception e) {
                        uploadBytes = data;
                    }

                    String filename = UUID.randomUUID().toString() + "." + ext;
                    String url = storageService.uploadBytes(uploadBytes, filename, "image/" + ext);
                    img.attr("src", url);
                }
            }
        }
        return doc.body().html();
    }
}