package com.metacoding.bankv1.account;

import com.metacoding.bankv1.account.history.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final HistoryRepository historyRepository;

    @Transactional
    public void 계좌생성(AccountRequest.SaveDTO saveDTO, int userId) {
        accountRepository.save(saveDTO.getNumber(), saveDTO.getPassword(), saveDTO.getBalance(), userId);
    }

    public List<Account> 나의계좌목록(Integer userId) {
        return accountRepository.findAllByUserId(userId);
    }

    @Transactional
    public void 계좌이체(AccountRequest.TransferDTO transferDTO, int sessionUserId) {
        Account withdrawAccount = accountRepository.findByNumber(transferDTO.getWithdrawNumber());
        if (withdrawAccount == null) throw new RuntimeException("출금 계좌가 존재하지 않습니다");

        Account depositAccount = accountRepository.findByNumber(transferDTO.getDepositNumber());
        if (depositAccount == null) throw new RuntimeException("입금 계좌가 존재하지 않습니다");

        withdrawAccount.잔액검사(transferDTO.getAmount());

        withdrawAccount.비밀번호검사(transferDTO.getWithdrawPassword());

        withdrawAccount.계좌주인검사(sessionUserId);

        withdrawAccount.출금(transferDTO.getAmount());
        accountRepository.updateByNumber(withdrawAccount.getBalance(), withdrawAccount.getPassword(), withdrawAccount.getNumber());

        depositAccount.입금(transferDTO.getAmount());
        accountRepository.updateByNumber(depositAccount.getBalance(), depositAccount.getPassword(), depositAccount.getNumber());
        
        historyRepository.save(transferDTO.getWithdrawNumber(), transferDTO.getDepositNumber(), transferDTO.getAmount(), withdrawAccount.getBalance(), depositAccount.getBalance());
    }

    public List<AccountResponse.DetailDTO> 계좌상세보기(int number, String type, Integer sessionUserId) {
        // 1. 계좌 존재 확인
        Account account = accountRepository.findByNumber(number);
        if (account == null) throw new RuntimeException("계좌가 존재하지 않습니다");

        // 2. 계좌 주인 확인
        account.계좌주인검사(sessionUserId);

        // 3. 조회해서 주면 됨
        List<AccountResponse.DetailDTO> detailList = accountRepository.findAllByNumber(number, type);
        return detailList;
    }
}
