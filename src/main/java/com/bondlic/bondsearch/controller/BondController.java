package com.bondlic.bondsearch.controller;

import com.bondlic.bondsearch.domain.dto.Bond;
import com.bondlic.bondsearch.domain.dto.SearchCondition;
import com.bondlic.bondsearch.util.TempFileUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

@RestController
@RequestMapping("/bond")
public class BondController {


    private final List<String> allowApiKeys;

    public BondController(@Value("${bondlic.allowApiKeys}") String allowApiKeys) {
        this.allowApiKeys = Arrays.stream(allowApiKeys.split(";"))
                .toList();
    }

    @GetMapping("/popular")
    @CrossOrigin(origins = {"http://localhost:4200", "http://csrf.com:4200"}, allowCredentials = "true")
    public ResponseEntity<List<Bond>> popularBond(HttpServletRequest request, HttpServletResponse response) {
        String apiKey = request.getHeader("Api-Key");
        if (!allowApiKeys.contains(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
        Bond first = Bond.builder()
                .isin("123")
                .name("First")
                .build();
        return ResponseEntity.ok(List.of(first));
    }

    @PostMapping("/search")
    @CrossOrigin(origins = {"http://localhost:4200", "http://csrf.com:4200", "http://localhost:8080"}, allowCredentials = "true")
    public ResponseEntity<List<Bond>> searchBond(@RequestBody SearchCondition condition,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {
        Cookie jwtTokenCookie = new Cookie("hello_new1", "nono_come");
        response.addCookie(jwtTokenCookie);
        Optional<Cookie[]> optionalCookies = Optional.ofNullable(request.getCookies());
        String token = request.getHeader("token");
        boolean isSameSite = Arrays.stream(optionalCookies.orElse(new Cookie[]{}))
                .filter(cookie -> "token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .anyMatch(val -> val.equals(token));
        if (!isSameSite) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
        Bond first = Bond.builder()
                .isin("456")
                .name("Second")
                .build();
        return ResponseEntity.ok(List.of(first));
    }

    @PostMapping("/upload")
    @CrossOrigin(origins = {"http://localhost:4200", "http://csrf.com:4200", "http://localhost:8080"}, allowCredentials = "true")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file,

                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws IOException {

        String position = request.getParameter("position");

        String fileName = file.getOriginalFilename() + "_" + position ;
        Path tempFile = TempFileUtil.writeFileToTemp(file.getInputStream(), fileName);
        FileSystemResource systemResource = new FileSystemResource(tempFile);

//        Map map = new ObjectMapper().readValue(request.getParameter("json"), Map.class);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("fileName", systemResource.getFile().getName());
        return ResponseEntity.ok(hashMap);
    }

    @PostMapping("/combine")
    @CrossOrigin(origins = {"http://localhost:4200", "http://csrf.com:4200", "http://localhost:8080"}, allowCredentials = "true")
    public ResponseEntity<Map<String, String>> combine(
                                                @RequestBody Map<String, List<String>> map
                                                 ) throws IOException {

        List<String> names = map.get("names");

        Path todayPath = TempFileUtil.getTodayPath();
        Path tempFile = Files.createTempFile(todayPath, UUID.randomUUID().toString().replaceAll("-", ""), ".tmp");

        names.stream().sorted((i1, i2) -> {
            int i1Position = Integer.parseInt(i1.split("_")[1]);
            int i2Position = Integer.parseInt(i2.split("_")[1]);
            return Integer.compare(i1Position, i2Position);
        }).forEach(fileName -> {
            try {
                byte[] bytes = TempFileUtil.readFileFromTemp(fileName, true)
                        .orElseThrow(() -> new RuntimeException("Not found file in temp folder: " + fileName));
                Files.write(tempFile, bytes, StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });



        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("fileName", tempFile.toFile()
                .getName());
        return ResponseEntity.ok(hashMap);
    }


}
