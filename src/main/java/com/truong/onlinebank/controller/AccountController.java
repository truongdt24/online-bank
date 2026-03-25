package com.truong.onlinebank.controller;

import com.truong.onlinebank.model.Transaction;
import com.truong.onlinebank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AccountController {
    private final AccountService accountService;

    
    // GET so dư
    @GetMapping("/{cardNumber}/balance")
    public ResponseEntity<?> getBalance(@PathVariable String cardNumber){
        BigDecimal balance = accountService.getBalance(cardNumber);
        return ResponseEntity.ok(Map.of("balance", balance));
    }

    // POST nap tien
    @PostMapping("/{cardNumber}/deposit")
    public ResponseEntity<?> deposit(
            @PathVariable String cardNumber,
            @RequestBody Map<String, BigDecimal> body){
        accountService.deposit(cardNumber, body.get("amount"));
        return ResponseEntity.ok(Map.of("Message", "Nạp tiền thành công / Deposit successfull"));
    }

    //POST rut tien
    @PostMapping("/{cardNumber}/withdraw")
    public ResponseEntity<?> withdraw(
            @PathVariable String cardNumber,
            @RequestBody Map<String, BigDecimal> body){
                accountService.withdraw(cardNumber, body.get("amount"));
                return ResponseEntity.ok(Map.of("message", " Rút tiền thành công / Withdraw succesful"));
    }

    //POST chuyen tien
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody Map<String, String> body){
        accountService.transfer(
                body.get("fromCard"),
                body.get("toCard"),
                new BigDecimal(body.get("amount"))
        );
        return ResponseEntity.ok(Map.of("message", "Chuyển tiền thành công / Transfer successful"));
    }

    //GET lịch sử gd
    @GetMapping("/{cardNumber}/history")
    public ResponseEntity<List<Transaction>> getHistory(@PathVariable String cardNumber){
        return ResponseEntity.ok(accountService.getHistory(cardNumber));
    }



}
