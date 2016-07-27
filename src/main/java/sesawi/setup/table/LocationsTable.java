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
public class LocationsTable {

    private static final String locationsCreateSql
            = "create table locations ("
            + "location_id int not null auto_increment,"
            + "owner_id int not null,"
            + "location_name varchar(30) not null,"
            + "location_desc varchar(200),"
            + "constraint fk_locations1 foreign key (owner_id)"
            + "  references owners (owner_id),"
            + "constraint primary key (location_id),"
            + "constraint nk_locations unique (location_name)"
            + ");";

    private static final String locationsInsertSql
            = "insert into locations ("
            + "location_name," //1
            + "location_desc," //2
            + "owner_id" //3
            + ") values ("
            + "?," //1
            + "?," //2
            + "?" //3
            + ")";

    public void drop(Statement stmt) throws SQLException {
        stmt.executeUpdate("drop table if exists locations;");
    }

    public void create(Statement stmt) throws SQLException {
        stmt.executeUpdate(locationsCreateSql);
    }

    public int insertLocations(final Map paramMap,
            final Connection conn)
            throws SQLException {
        int locationId = 0;
        try (PreparedStatement locationsStmt
                = conn.prepareStatement(locationsInsertSql,
                        Statement.RETURN_GENERATED_KEYS)) {
            locationsStmt.setString(1, (String) paramMap.get("location_name"));
            locationsStmt.setString(2, (String) paramMap.get("location_desc"));
            locationsStmt.setInt(3, (Integer) paramMap.get("owner_id"));
            locationsStmt.executeUpdate();
            ResultSet resultSet = locationsStmt.getGeneratedKeys();
            if (resultSet.next()) {
                locationId = resultSet.getInt(1);
            }
        }
        return locationId;
    }

}
