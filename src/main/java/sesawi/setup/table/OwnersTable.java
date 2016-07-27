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
public class OwnersTable {
    
    private static final String ownersCreateSql
            = "create table owners ("
            + "owner_id int not null auto_increment,"
            + "owner_name varchar(30) not null,"
            + "owner_desc varchar(200),"
            + "constraint primary key (owner_id),"
            + "constraint nk_owners unique (owner_name)"
            + ");";

    private static final String ownersInsertSql
            = "insert into owners ("
            + "owner_name," //1
            + "owner_desc" //2
            + ") values ("
            + "?," //1
            + "?" //2
            + ")";
    
    public void drop(Statement stmt) throws SQLException {
        stmt.executeUpdate("drop table if exists owners;");        
    }

    public void create(Statement stmt) throws SQLException {
        stmt.executeUpdate(ownersCreateSql);
    }

    public int insert(final Map paramMap,
            final Connection conn)
            throws SQLException {
        int ownerId = 0;
        try (PreparedStatement ownersStmt
                = conn.prepareStatement(ownersInsertSql,
                        Statement.RETURN_GENERATED_KEYS)) {
            ownersStmt.setString(1, (String) paramMap.get("owner_name"));
            ownersStmt.setString(2, (String) paramMap.get("owner_desc"));
            ownersStmt.executeUpdate();
            ResultSet resultSet = ownersStmt.getGeneratedKeys();
            if (resultSet.next()) {
                ownerId = resultSet.getInt(1);
            }
        }
        return ownerId;
    }

}
