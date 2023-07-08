package com.sasicode.fullStackApp.customer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
@RequiredArgsConstructor
@Slf4j
public class CustomerJdbcTemplateAccessService implements CustomerDao{

    private final JdbcTemplate jdbcTemplate;

    private final CustomerRowMapper customerRowMapper;
    @Override
    public List<Customer> selectAllCustomers() {
        var sql = """
                SELECT * FROM customer;
                """;
        return jdbcTemplate.query(sql,customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        var sql = """
                SELECT * FROM customer where id = ?
                """;
        return jdbcTemplate.query(sql, customerRowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
                INSERT INTO customer(name, email, password, age) 
                VALUES(?, ?, ?, ?)
                """;
        int update = jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getPassword(), customer.getAge());
        log.info("jdbcTemplate insert query" + " " +update);
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        var sql = """
                SELECT COUNT(id) FROM customer where email = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsCustomerWithId(Integer id) {
        var sql = """
                SELECT COUNT(id) FROM customer where id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void updateCustomer(Customer customerUpdate) {
        if(customerUpdate.getName() != null) {
            var sql = """
                UPDATE customer SET name = ? WHERE id = ?
                """;
            int update = jdbcTemplate.update(sql, customerUpdate.getName(), customerUpdate.getId());
            log.info("jdbcTemplate update customer name query" + " " +update);
        }

        if(customerUpdate.getEmail() != null) {
            var sql = """
                UPDATE customer SET email = ? WHERE id = ?
                """;
            int update = jdbcTemplate.update(sql, customerUpdate.getEmail(), customerUpdate.getId());
            log.info("jdbcTemplate update customer email query" + " " +update);
        }

        if(customerUpdate.getAge() != null) {
            var sql = """
                UPDATE customer SET age = ? WHERE id = ?
                """;
            int update = jdbcTemplate.update(sql, customerUpdate.getAge(), customerUpdate.getId());
            log.info("jdbcTemplate update customer age query" + " " +update);
        }
    }

    @Override
    public void deleteCustomer() {
        var sql = """
                DELETE FROM CUSTOMER
                """;
        jdbcTemplate.execute(sql);
    }

    @Override
    public Optional<Customer> selectUserByEmail(String email) {
        var sql = """
                SELECT id, name, email, password, age from customer as c where email = ? 
                """;
        Optional<Customer> customer = jdbcTemplate.query(sql, customerRowMapper, email).stream().findFirst();
        return customer;
    }

    @Override
    public void deleteCustomerById(Integer customerId) {
        var sql = """
                DELETE from customer where id = ?
                """;
        int update = jdbcTemplate.update(sql, customerId);
        log.info("jdbcTemplate deleted customer %s{id}" + " " +update);
    }
}
