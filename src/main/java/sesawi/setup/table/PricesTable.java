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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author Samuel Franklyn <sfranklyn@gmail.com>
 */
public class PricesTable {

    private static final String pricesCreateSql
            = "create table prices ("
            + "price_id int not null auto_increment,"
            + "price_code char(5) not null,"
            + "price_entry decimal(5,0) default 0,"
            + "price_entry_hour decimal(1,0) default 0,"
            + "price_per_hour decimal(5,0) default 0,"
            + "price_lost decimal(5,0) default 0,"
            + "price_last_change datetime,"
            + "constraint primary key (price_id),"
            + "constraint nk_prices unique (price_code)"
            + ");";

    private static final String pricesInsertSql
            = "insert into prices ("
            + "price_code," //1
            + "price_entry," //2
            + "price_entry_hour," //3
            + "price_per_hour," //4
            + "price_lost," //5
            + "price_last_change" //6
            + ") values ("
            + "?," //1
            + "?," //2
            + "?," //3
            + "?," //4
            + "?," //5
            + "?" //6
            + ")";

    public void drop(Statement stmt) throws SQLException {
        stmt.executeUpdate("drop table if exists prices;");
    }

    public void create(Statement stmt) throws SQLException {
        stmt.executeUpdate(pricesCreateSql);
    }

    public void insert(final Map paramMap,
            final Connection conn)
            throws SQLException {
        Date now = new Date();
        try (PreparedStatement pricesStmt = conn.
                prepareStatement(pricesInsertSql)) {
            pricesStmt.setString(1, (String) paramMap.get("price_code"));
            pricesStmt.setBigDecimal(2, (BigDecimal) paramMap.get("price_entry"));
            pricesStmt.setBigDecimal(3, (BigDecimal) paramMap.get("price_entry_hour"));
            pricesStmt.setBigDecimal(4, (BigDecimal) paramMap.get("price_per_hour"));
            pricesStmt.setBigDecimal(5, (BigDecimal) paramMap.get("price_lost"));
            pricesStmt.setTimestamp(6, new Timestamp(now.getTime()));
            pricesStmt.executeUpdate();
        }
    }

}
