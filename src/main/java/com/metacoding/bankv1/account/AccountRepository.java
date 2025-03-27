package com.metacoding.bankv1.account;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class AccountRepository {
    private final EntityManager em;

    // 샘 왜???????? 재사용할려고!!
    public void updateByNumber(int balance, String password, int number) {
        Query query = em.createNativeQuery("update account_tb set balance = ?, password = ? where number = ?");
        query.setParameter(1, balance);
        query.setParameter(2, password);
        query.setParameter(3, number);
        query.executeUpdate();
    }

    public void save(Integer number, String password, Integer balance, int userId) {
        Query query = em.createNativeQuery("insert into account_tb(number, password, balance, user_id, created_at) values (?, ?, ?, ?, now())");
        query.setParameter(1, number);
        query.setParameter(2, password);
        query.setParameter(3, balance);
        query.setParameter(4, userId);
        query.executeUpdate();
    }

    public List<Account> findAllByUserId(Integer userId) {
        Query query = em.createNativeQuery("select * from account_tb where user_id = ? order by created_at desc", Account.class);
        query.setParameter(1, userId);
        return query.getResultList();
    }

    public Account findByNumber(Integer number) {
        Query query = em.createNativeQuery("select * from account_tb where number = ?", Account.class);
        query.setParameter(1, number);

        try {
            return (Account) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<AccountResponse.DetailDTO> findAllByNumber(int number, String type) {
        String sql = """
            select 
            dt.account_number,
            dt.account_balance,
            dt.account_owner,
            substr(created_at, 1, 16) created_at,
            withdraw_number w_number,
            deposit_number d_number,
            amount amount,
            case when withdraw_number = ? then withdraw_balance 
            else deposit_balance 
            end balance,
            case when withdraw_number = ? then '출금' 
            else '입금' 
            end type 
            from history_tb ht 
            inner join (select at.number account_number, at.balance account_balance, ut.fullname account_owner 
            from account_tb at 
            inner join user_tb ut on at.user_id = ut.id 
            where at.number = ?) dt on 1=1 
            """;
        String sql2 = "where deposit_number = ? or withdraw_number = ?;";
        String sql3 = "where deposit_number = ?;";
        String sql4 = "where withdraw_number = ?;";

        if(type.equals("입금")) sql += sql3;
        else if (type.equals("출금")) sql += sql4;
        else sql += sql2;

        Query query = em.createNativeQuery(sql);
        query.setParameter(1, number);
        query.setParameter(2, number);
        query.setParameter(3, number);
        query.setParameter(4, number);
        if(type.equals("전체")) query.setParameter(5, number);

        List<Object[]> obsList = query.getResultList();
        List<AccountResponse.DetailDTO> detailList = new ArrayList<>();

        for (Object[] obs : obsList) {
            AccountResponse.DetailDTO detail =
                    new AccountResponse.DetailDTO(
                            (int) obs[0],
                            (int) obs[1],
                            (String) obs[2],
                            (String) obs[3],
                            (int) obs[4],
                            (int) obs[5],
                            (int) obs[6],
                            (int) obs[7],
                            (String) obs[8]
                    );
            detailList.add(detail);
        }
        return detailList;
    }

    public AccountResponse.TransferUserDTO detail1(int number) {
        String sql = """
            select 
            at.balance account_balance,
            at.number account_number,
            ut.fullname account_owner
            from account_tb at
            inner join user_tb ut
            on at.user_id = ut.id
            where at.number = ?
            """;
        Query query = em.createNativeQuery(sql);
        query.setParameter(1, number);

        Object[] result = (Object[]) query.getSingleResult();

        AccountResponse.TransferUserDTO userInfo = new AccountResponse.TransferUserDTO(
                ((Number) result[1]).intValue(),     // account_number
                ((Number) result[0]).intValue(),     // account_balance
                (String) result[2]                   // account_owner
        );

        return userInfo;
    }


    public List<AccountResponse.TransferListDTO> detail2(int number, String type) {
        String sql = """
                select substr(created_at, 1, 16) created_at,
                withdraw_number w_number,
                deposit_number d_number,
                amount,
                case when withdraw_number = ? then withdraw_balance
                else deposit_balance
                end balance,
                case when withdraw_number = ? then '출금'
                else '입금'
                end type
                from history_tb
                """;
        String sql2 = "where deposit_number = ? or withdraw_number = ?;";
        String sql3 = "where deposit_number = ?;";
        String sql4 = "where withdraw_number = ?;";

        if(type.equals("입금")) sql += sql3;
        else if (type.equals("출금")) sql += sql4;
        else sql += sql2;

        Query query = em.createNativeQuery(sql);
        query.setParameter(1, number);
        query.setParameter(2, number);
        query.setParameter(3, number);
        if(type.equals("전체"))  query.setParameter(4, number);

        List<Object[]> obsList = query.getResultList();
        List<AccountResponse.TransferListDTO> detailList = new ArrayList<>();

        for (Object[] obs : obsList) {
            AccountResponse.TransferListDTO detail =
                    new AccountResponse.TransferListDTO(
                            (String) obs[0],
                            (int) obs[1],
                            (int) obs[2],
                            (int) obs[3],
                            (int) obs[4],
                            (String) obs[5]
                    );
            detailList.add(detail);
        }
        return detailList;
    }


    //    public void updateWithdraw(int amount, int number) {
//        Query query = em.createNativeQuery("update account_tb set balance = balance - ? where number = ?");
//        query.setParameter(1, amount);
//        query.setParameter(2, number);
//        query.executeUpdate();
//    }
//
//    public void updateDeposit(int amount, int number) {
//        Query query = em.createNativeQuery("update account_tb set balance = balance + ? where number = ?");
//        query.setParameter(1, amount);
//        query.setParameter(2, number);
//        query.executeUpdate();
//    }
//
//    public void updatePassword(String password, int number) {
//        Query query = em.createNativeQuery("update account_tb set password = ? where number = ?");
//        query.setParameter(1, password);
//        query.setParameter(2, number);
//        query.executeUpdate();
//    }
}
