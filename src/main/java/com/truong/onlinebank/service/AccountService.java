package com.truong.onlinebank.service;

import com.truong.onlinebank.model.Account;
import com.truong.onlinebank.model.Transaction;
import com.truong.onlinebank.repository.AccountRepository;
import com.truong.onlinebank.repository.TransactionRepository;
import com.truong.onlinebank.util.HashUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    // Đăng ký
    public void signUp(String cardNumber, String holderName, String pin){
        if (accountRepository.findByCardNumber(cardNumber).isPresent()){
            throw new RuntimeException(" Số tài khoản đã tồn tại / Card number is already exists ");
        }

        Account account = new Account();
        account.setCardNumber(cardNumber);
        account.setHolderName(holderName);
        account.setPinHash(HashUtil.hashPin(pin));
        account.setBalance(new BigDecimal("0.00"));
        account.setActive(true);
        account.setCreatedAt(LocalDateTime.now());

        accountRepository.save(account);
    }

    // kiểm tra số tiền
    public BigDecimal getBalance(String cardNumber){
        Account account = accountRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản / Account not found") );
        return account.getBalance();
    }

    // nạp tiền
    public void deposit(String cardNumber, BigDecimal amount){
        Account account = accountRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản / Account not found"));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        saveTransaction(account, "DEPOSIT", amount, null);
    }

    // Rút tiền
    @Transactional
    public void withdraw(String cardNumber, BigDecimal amount){
        Account account = accountRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản / Account not found"));

        if (account.getBalance().compareTo(amount) < 0){
            throw new RuntimeException("Số dư không đủ / Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        saveTransaction(account, "WITHDRAW", amount, null);
    }

    // chuyển tiền
    @Transactional
    public void transfer(String fromCard, String toCard, BigDecimal amount){
        Account sender = accountRepository.findByCardNumber(fromCard)
                .orElseThrow(() -> new RuntimeException(" Không tìm thấy tài khoản / Sender not found"));

        Account receiver = accountRepository.findByCardNumber(toCard)
                .orElseThrow(() -> new RuntimeException(" Không tìm thấy tài khoản / Receiver not found"));

        if (sender.getBalance().compareTo(amount) < 0){
            throw new RuntimeException(" Số dư không đủ / Insufficient balance");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        accountRepository.save(sender);
        accountRepository.save(receiver);
        saveTransaction(sender, "TRANSFER_OUT", amount, sender.getHolderName());
        saveTransaction(receiver, "TRANSFER_IN", amount, receiver.getHolderName() );
    }

    // lịch sử giao dịch
    public List<Transaction> getHistory(String cardNumber){
        Account account = accountRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException(" Không tìm thấy tài khoản / Account not found"));
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(account.getId());
    }

    // lưu lịch sử gd

    private void saveTransaction(Account account, String type, BigDecimal amount, String relatedName){
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setRelatedName(relatedName);
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

}
