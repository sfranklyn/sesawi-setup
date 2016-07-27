/*
 * Copyright 2015 Samuel Franklyn <sfranklyn@gmail.com>.
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
package sesawi.setup.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 *
 * @author Samuel Franklyn <sfranklyn@gmail.com>
 */
public class ComputersTable {

    private static final String COMPUTERS_CREATE_SQL
            = "create table computers ("
            + "computer_id int not null auto_increment,"
            + "location_id int not null,"
            + "computer_name varchar(50) not null,"
            + "computer_desc varchar(200),"
            + "constraint fk_computers1 foreign key (location_id)"
            + "  references locations (location_id),"
            + "constraint primary key (computer_id),"
            + "constraint nk_computers unique (computer_name)"
            + ");";

    private static final String COMPUTERS_INSERT_SQL
            = "insert into computers ("
            + "computer_name," //1
            + "computer_desc," //2
            + "location_id" //3
            + ") values ("
            + "?," //1
            + "?," //2
            + "?" //3
            + ")";

    public void drop(Statement stmt) throws SQLException {
        stmt.executeUpdate("drop table if exists computers;");
    }

    public void create(Statement stmt) throws SQLException {
        stmt.executeUpdate(COMPUTERS_CREATE_SQL);
    }

    public int insert(final Map paramMap,
            final Connection conn)
            throws SQLException {
        int computerId = 0;
        try (PreparedStatement computersStmt
                = conn.prepareStatement(COMPUTERS_INSERT_SQL,
                        Statement.RETURN_GENERATED_KEYS)) {
            computersStmt.setString(1, (String) paramMap.get("computer_name"));
            computersStmt.setString(2, (String) paramMap.get("computer_desc"));
            computersStmt.setInt(3, (Integer) paramMap.get("location_id"));
            computersStmt.executeUpdate();
            ResultSet resultSet = computersStmt.getGeneratedKeys();
            if (resultSet.next()) {
                computerId = resultSet.getInt(1);
            }
        }
        return computerId;
    }

}
