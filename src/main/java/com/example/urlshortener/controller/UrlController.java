// package com.example.urlshortener.controller;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.servlet.view.RedirectView;

// import com.example.urlshortener.service.UrlService;

// @RestController
// public class UrlController {

//     private final UrlService service;

//     public UrlController(UrlService service) {
//         this.service = service;
//     }

//     // API to shorten URL
//     @PostMapping("/shorten")
//     public ResponseEntity<String> shortenUrl(@RequestBody String url) {

//         String shortCode = service.shortenUrl(url);

//         return ResponseEntity.ok("http://localhost:8080/" + shortCode);
//     }

//     // Redirect to original URL
//     @GetMapping("/{shortCode}")
//     public RedirectView redirect(@PathVariable String shortCode) {

//         String originalUrl = service.getOriginalUrl(shortCode);

//         return new RedirectView(originalUrl);
//     }
// }



// package com.example.urlshortener.controller;
// 
// import java.util.List;
// 
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.servlet.view.RedirectView;
// 
// import com.example.urlshortener.model.UrlMapping; // ✅ new import
// import com.example.urlshortener.model.UrlRequest;
// import com.example.urlshortener.service.UrlService;
//import com.example.urlshortener.model.UrlMapping;
// 
// 
// 
// 
// 
// @RestController
// public class UrlController {
// 
    // private final UrlService service;
// 
    // public UrlController(UrlService service) {
        // this.service = service;
    // }
// 
   // API to shorten URL
    // @PostMapping("/shorten")
    // public ResponseEntity<String> shortenUrl(@RequestBody UrlRequest request) { // ✅ fixed
        // String shortCode = service.shortenUrl(request.getUrl()); // ✅ fixed
        // return ResponseEntity.ok("http://localhost:8080/" + shortCode);
    // }
// 
    //Redirect to original URL
    // @GetMapping("/{shortCode}")
    // public RedirectView redirect(@PathVariable String shortCode) {
        // String originalUrl = service.getOriginalUrl(shortCode);
        // return new RedirectView(originalUrl);
    // }
// @GetMapping("/all")
// public List<UrlMapping> getAllUrls() {
    // return service.getAllUrls();
// }
// 
// @DeleteMapping("/delete/{id}")
// public void deleteUrl(@PathVariable Long id) {
    // service.deleteUrl(id);
// }
// 
// @GetMapping("/search")
// public List<UrlMapping> searchUrls(
        // @RequestParam String keyword) {
// 
    // return service.searchUrls(keyword);
// }
// 
// }



package com.example.urlshortener.controller;

import java.util.List;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.model.UrlRequest;
import com.example.urlshortener.service.QrCodeService;
import com.example.urlshortener.service.UrlService;

@RestController
public class UrlController {

    private final UrlService service;
    private final QrCodeService qrCodeService;

    public UrlController(
            UrlService service,
            QrCodeService qrCodeService
    ) {
        this.service = service;
        this.qrCodeService = qrCodeService;
    }

    // Shorten URL
    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(
            @RequestBody UrlRequest request,
            HttpServletRequest httpRequest
    ) {

        String shortCode =
                service.shortenUrl(
                        request.getUrl()
                );

        String baseUrl = getBaseUrl(httpRequest);

        return ResponseEntity.ok(
                baseUrl + "/" + shortCode
        );
    }

        // Simple homepage
        @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
        public ResponseEntity<String> home() {
        String html = "<html><head><title>URL Shortener</title></head>"
            + "<body><h1>URL Shortener</h1>"
            + "<p>Available endpoints:</p>"
            + "<ul>"
            + "<li>POST /shorten</li>"
            + "<li>GET /all</li>"
            + "<li>GET /search?keyword=...</li>"
            + "<li>GET /{shortCode} (redirect)</li>"
            + "</ul></body></html>";

        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(html);
        }

    // Redirect to original URL
    @GetMapping("/{shortCode}")
    public RedirectView redirect(
            @PathVariable String shortCode
    ) {

        String originalUrl =
                service.getOriginalUrl(shortCode);

        return new RedirectView(originalUrl);
    }

    // Get all URLs
    @GetMapping("/all")
    public List<UrlMapping> getAllUrls() {

        return service.getAllUrls();
    }

    // Delete URL
    @DeleteMapping("/delete/{id}")
    public void deleteUrl(
            @PathVariable Long id
    ) {

        service.deleteUrl(id);
    }

    // Search URL
    @GetMapping("/search")
    public List<UrlMapping> searchUrls(
            @RequestParam String keyword
    ) {

        return service.searchUrls(keyword);
    }

    // Generate QR Code
    @GetMapping(
            value = "/qr/{shortCode}",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    public ResponseEntity<byte[]> generateQr(
            @PathVariable String shortCode,
            HttpServletRequest httpRequest
    ) throws Exception {

        String shortUrl =
                getBaseUrl(httpRequest) + "/" + shortCode;

        byte[] qr =
                qrCodeService.generateQrCode(shortUrl);

        return ResponseEntity.ok(qr);
    }

    private String getBaseUrl(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        int endIndex = requestURL.length() - requestURI.length() + contextPath.length();
        return requestURL.substring(0, endIndex);
    }
}