package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {
    private final Statement statement;

    public UserDaoJDBCImpl() {
        try {
            this.statement = new Util().getConnection().createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createUsersTable() {
        try {
            statement.execute("""
                    CREATE TABLE `mydbtest`.`users` (
                      `id` INT NOT NULL AUTO_INCREMENT,
                      `name` VARCHAR(45) NOT NULL,
                      `lastName` VARCHAR(45) NOT NULL,
                      `age` TINYINT NOT NULL,
                      PRIMARY KEY (`id`))
                    ENGINE = InnoDB
                    DEFAULT CHARACTER SET = utf8;""");
        } catch (SQLException ignored) {
        }
    }

    public void dropUsersTable() {
        try {
            statement.execute("DROP TABLE mydbtest.users;");
        } catch (SQLException ignored) {
        }
    }

    public void saveUser(String name, String lastName, byte age) {
        try {
            PreparedStatement ps = statement.getConnection().
                    prepareStatement("insert into mydbtest.users(name, lastName, age) values(?, ?, ?);");
            ps.setString(1, name);
            ps.setString(2, lastName);
            ps.setByte(3, age);
            ps.execute();
            System.out.println("User с именем - " + name + " добавлен в базу данных");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeUserById(long id) {
        try {
            statement.execute("DELETE FROM mydbtest.users where id = " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> getAllUsers() {
        List<User> usersList = new ArrayList<>();
        try {
            PreparedStatement ps = statement.getConnection().prepareStatement("SELECT * FROM mydbtest.users;");
            ps.execute();
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Long id = rs.getLong(1);
                String name = rs.getString(2);
                String lastName = rs.getString(3);
                Byte age = rs.getByte(4);
                User user = new User(name, lastName, age);
                user.setId(id);
                usersList.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println(usersList);
        return usersList;
    }

    public void cleanUsersTable() {
        try {
            statement.execute("DELETE FROM mydbtest.users");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            statement.getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
