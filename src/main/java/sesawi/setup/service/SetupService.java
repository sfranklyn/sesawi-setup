/*
 * Copyright 2013 Samuel Franklyn <sfranklyn@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sesawi.setup.service;

import java.lang.management.ManagementFactory;
import sesawi.setup.table.UsersTable;
import sesawi.setup.table.OwnersTable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import sesawi.setup.table.ComputersTable;
import sesawi.setup.table.ConfigsTable;
import sesawi.setup.table.LocationsTable;
import sesawi.setup.table.PricesTable;
import sesawi.setup.table.RolesTable;
import sesawi.setup.table.ShiftsTable;
import sesawi.setup.table.TicketsTable;
import sesawi.setup.table.UrlsRolesTable;
import sesawi.setup.table.UsersRolesTable;

/**
 *
 * @author Samuel Franklyn <sfranklyn@gmail.com>
 */
public class SetupService {

    private static final Logger log = Logger.getLogger(SetupService.class.getName());
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static MessageDigest messageDigest = null;
    private static String computerName = null;

    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA-512");
            Class.forName(DRIVER);
            computerName = ManagementFactory.getRuntimeMXBean().getName().split("@")[1];
        } catch (NoSuchAlgorithmException | ClassNotFoundException ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
        }

    }

    public static String hashPassword(final String password) {
        byte[] hash = null;
        try {
            messageDigest.reset();
            hash = messageDigest.digest(password.getBytes());
        } catch (Exception ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return getHexString(hash);
    }

    public static String getHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public void dropDb(String server, String port, String user, String password)
            throws ClassNotFoundException, SQLException {
        String jdbcUrl = "jdbc:mysql://" + server + ":" + port + "/mysql";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("drop user 'sesawi'@'localhost';");
            stmt.executeUpdate("drop database sesawi;");
        }
    }

    public void createDb(String server, String port, String user, String password)
            throws ClassNotFoundException, SQLException {
        String jdbcUrl = "jdbc:mysql://" + server + ":" + port + "/mysql";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("create database if not exists sesawi;");
            stmt.executeUpdate("create user 'sesawi'@'localhost' "
                    + "identified by 'sesawi';");
            stmt.executeUpdate("grant all on sesawi.* to "
                    + "'sesawi'@'localhost';");
        }
    }

    public void execute(String server, String port, String user, String password)
            throws ClassNotFoundException, SQLException {

        createDb(server, port, user, password);

        String jdbcUrl = "jdbc:mysql://" + server + ":" + port + "/sesawi";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, "sesawi", "sesawi");
                Statement stmt = conn.createStatement()) {

            OwnersTable ownersTable = new OwnersTable();
            UsersTable usersTable = new UsersTable();
            RolesTable rolesTable = new RolesTable();
            UsersRolesTable usersRolesTable = new UsersRolesTable();
            UrlsRolesTable urlsRolesTable = new UrlsRolesTable();
            ConfigsTable configsTable = new ConfigsTable();
            PricesTable pricesTable = new PricesTable();
            LocationsTable locationsTable = new LocationsTable();
            ComputersTable computersTable = new ComputersTable();
            TicketsTable ticketsTable = new TicketsTable();
            ShiftsTable shiftsTable = new ShiftsTable();

            ownersTable.drop(stmt);
            urlsRolesTable.drop(stmt);
            usersRolesTable.drop(stmt);
            usersTable.drop(stmt);
            rolesTable.drop(stmt);
            configsTable.drop(stmt);
            pricesTable.drop(stmt);
            computersTable.drop(stmt);
            locationsTable.drop(stmt);
            ticketsTable.drop(stmt);
            shiftsTable.drop(stmt);

            ownersTable.create(stmt);
            usersTable.create(stmt);
            rolesTable.create(stmt);
            usersRolesTable.create(stmt);
            urlsRolesTable.create(stmt);
            configsTable.create(stmt);
            pricesTable.create(stmt);
            locationsTable.create(stmt);
            computersTable.create(stmt);
            ticketsTable.create(stmt);
            shiftsTable.create(stmt);

            String roleName = "ADM";
            String roleDesc = "Administrator";
            String roleMenu = "adm.xhtml";
            final int adminRoleId = rolesTable.insert(
                    roleName, roleDesc, roleMenu, conn);

            roleName = "USR";
            roleDesc = "User";
            roleMenu = "user.xhtml";
            final int userRoleId = rolesTable.insert(
                    roleName, roleDesc, roleMenu, conn);

            roleName = "OWN";
            roleDesc = "Owner";
            roleMenu = "owner.xhtml";
            final int ownerRoleId = rolesTable.insert(
                    roleName, roleDesc, roleMenu, conn);

            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("owner_name", "SG");
            paramMap.put("owner_desc", "Sesawi Group");
            int ownerId1 = ownersTable.insert(paramMap, conn);

            paramMap = new HashMap<>();
            paramMap.put("owner_name", "SF");
            paramMap.put("owner_desc", "Samuel Franklyn");
            int ownerId2 = ownersTable.insert(paramMap, conn);

            paramMap = new HashMap<>();
            String adminName1 = "adm1";
            paramMap.put("user_name", adminName1);
            String userPassword = hashPassword("adm1");
            paramMap.put("user_password", userPassword);
            paramMap.put("user_full_name", "Administrator One");
            paramMap.put("user_email", "-");
            final int adminUserId1 = usersTable.insert(paramMap, conn);
            usersRolesTable.insert(adminUserId1, adminRoleId, conn);

            paramMap = new HashMap<>();
            String userName1 = "user1";
            userPassword = hashPassword("user1");
            paramMap.put("user_name", userName1);
            paramMap.put("user_password", userPassword);
            paramMap.put("user_full_name", "User One");
            paramMap.put("user_email", "-");
            final int userId1 = usersTable.insert(paramMap, conn);

            paramMap = new HashMap<>();
            String userName2 = "user2";
            userPassword = hashPassword("user2");
            paramMap.put("user_name", userName2);
            paramMap.put("user_password", userPassword);
            paramMap.put("user_full_name", "User Two");
            paramMap.put("user_email", "-");
            final int userId2 = usersTable.insert(paramMap, conn);

            paramMap = new HashMap<>();
            String userName3 = "owner1";
            userPassword = hashPassword("owner1");
            paramMap.put("user_name", userName3);
            paramMap.put("user_password", userPassword);
            paramMap.put("user_full_name", "Owner One");
            paramMap.put("user_email", "-");
            paramMap.put("owner_id", ownerId1);
            final int userId3 = usersTable.insert(paramMap, conn);

            paramMap = new HashMap<>();
            String userName4 = "owner2";
            userPassword = hashPassword("owner2");
            paramMap.put("user_name", userName4);
            paramMap.put("user_password", userPassword);
            paramMap.put("user_full_name", "Owner Two");
            paramMap.put("user_email", "-");
            paramMap.put("owner_id", ownerId2);
            final int userId4 = usersTable.insert(paramMap, conn);

            usersRolesTable.insert(userId1, userRoleId, conn);
            usersRolesTable.insert(userId2, userRoleId, conn);
            usersRolesTable.insert(userId3, ownerRoleId, conn);
            usersRolesTable.insert(userId4, ownerRoleId, conn);

            urlsRolesTable.insertAdmUrlsRoles(adminRoleId, conn);
            urlsRolesTable.insertUserUrlsRoles(userRoleId, conn);
            urlsRolesTable.insertOwnerUrlsRoles(ownerRoleId, conn);

            paramMap = new HashMap<>();
            paramMap.put("price_code", "MOTOR");
            BigDecimal priceEntry = new BigDecimal(1000, new MathContext(5)).setScale(0);
            BigDecimal priceEntryHour = new BigDecimal(1, new MathContext(1)).setScale(0);
            BigDecimal pricePerHour = new BigDecimal(1000, new MathContext(5)).setScale(0);
            BigDecimal priceLost = new BigDecimal(25000, new MathContext(5)).setScale(0);
            paramMap.put("price_entry", priceEntry);
            paramMap.put("price_entry_hour", priceEntryHour);
            paramMap.put("price_per_hour", pricePerHour);
            paramMap.put("price_lost", priceLost);
            pricesTable.insert(paramMap, conn);

            paramMap = new HashMap<>();
            paramMap.put("price_code", "MOBIL");
            priceEntry = new BigDecimal(3000, new MathContext(5)).setScale(0);
            priceEntryHour = new BigDecimal(2, new MathContext(1)).setScale(0);
            pricePerHour = new BigDecimal(2000, new MathContext(5)).setScale(0);
            priceLost = new BigDecimal(25000, new MathContext(5)).setScale(0);
            paramMap.put("price_entry", priceEntry);
            paramMap.put("price_entry_hour", priceEntryHour);
            paramMap.put("price_per_hour", pricePerHour);
            paramMap.put("price_lost", priceLost);
            pricesTable.insert(paramMap, conn);

            paramMap = new HashMap<>();
            paramMap.put("price_code", "TRUK");
            priceEntry = new BigDecimal(4000, new MathContext(5)).setScale(0);
            priceEntryHour = new BigDecimal(2, new MathContext(1)).setScale(0);
            pricePerHour = new BigDecimal(3000, new MathContext(5)).setScale(0);
            priceLost = new BigDecimal(25000, new MathContext(5)).setScale(0);
            paramMap.put("price_entry", priceEntry);
            paramMap.put("price_entry_hour", priceEntryHour);
            paramMap.put("price_per_hour", pricePerHour);
            paramMap.put("price_lost", priceLost);
            pricesTable.insert(paramMap, conn);

            paramMap = new HashMap<>();
            paramMap.put("location_name", "SFB1");
            paramMap.put("location_desc", "HEAVEN");
            paramMap.put("owner_id", ownerId2);
            int locationId1 = locationsTable.insertLocations(paramMap, conn);

            paramMap = new HashMap<>();
            paramMap.put("location_name", "SFB2");
            paramMap.put("location_desc", "EARTH");
            paramMap.put("owner_id", ownerId2);
            int locationId2 = locationsTable.insertLocations(paramMap, conn);

            paramMap = new HashMap<>();
            paramMap.put("computer_name", computerName);
            paramMap.put("computer_desc", "Working Laptop");
            paramMap.put("location_id", locationId1);
            computersTable.insert(paramMap, conn);

            paramMap = new HashMap<>();
            paramMap.put("computer_name", "sf-hp-pavilion");
            paramMap.put("computer_desc", "Multimedia Laptop");
            paramMap.put("location_id", locationId2);
            computersTable.insert(paramMap, conn);

        }
    }

}
