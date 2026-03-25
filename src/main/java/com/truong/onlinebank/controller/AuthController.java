package com.truong.onlinebank.controller;

import com.truong.onlinebank.model.Account;
import com.truong.onlinebank.repository.AccountRepository;
import com.truong.onlinebank.security.JwtUtil;
import com.truong.onlinebank.service.AccountService;
import com.truong.onlinebank.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil;
    private final AccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String cardNumber = body.get("cardNumber");
        String pin = body.get("pin");

        // tìm stk
        Account account = accountRepository.findByCardNumber(cardNumber)
                .orElse(null);

        if (account == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Không tìm thấy tài khoản / Card number not found"));

        }
        // so sánh mật khẩu
//        String pinHash = hashPin(pin);
        //debug
//        System.out.println("PIN entered:   " + pin);
//        System.out.println("Input hash:    " + pinHash);
//        System.out.println("Database hash: " + account.getPinHash());

        if (!account.getPinHash().equals(HashUtil.hashPin(pin))) {
            return ResponseEntity.status(401)
                    .body(Map.of("Error", "Sai mã pin / Wrong pin"));
        }

        // tạo token
        String token = jwtUtil.generateToken(cardNumber);
        return ResponseEntity.ok(Map.of("token", token, "name", account.getHolderName()));

    }
//    private String hashPin(String pin){
//        try{
//            java.security.MessageDigest md =
//                    java.security.MessageDigest.getInstance("SHA-256");
//            byte[] hash = md.digest(pin.getBytes());
//            StringBuilder sb = new StringBuilder();
//            for (byte b : hash){
//                sb.append(String.format("%02x",b));
//            }
//            return sb.toString();
//        }catch (Exception e){
//            throw new RuntimeException(" Hashing error", e);
//        }
//    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody Map<String, String> body){
        String cardNumber = body.get("cardNumber");
        String holderName = body.get("holderName");
        String pin = body.get("pin");

        try{
            accountService.signUp(cardNumber,holderName,pin);

            String token = jwtUtil.generateToken(cardNumber);
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "name", holderName,
                    "Message", "Tạo tài khoản thành công / Account created successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }
    }

